package com.erp.emails.service;
import com.erp.emails.model.*;
import com.erp.emails.repository.EmailMessageR;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.transaction.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIfEnvironmentVariable(named="EPMAPA_TEST_PG_ADMIN",matches="jdbc:postgresql://127\\.0\\.0\\.1:55439/postgres")
class EmailPostgresTest {
    static DriverManagerDataSource ds;static org.hibernate.SessionFactory emf;
    static final MailSenderService sender=mock(MailSenderService.class);
    private AnnotationConfigApplicationContext context;private JdbcTemplate jdbc;private TransactionTemplate tx;
    private EmailMessageR repo;private EmailProcessorService processor;private EmailAttemptService history;private String db;
    @Configuration @EnableTransactionManagement static class Config {
        @Bean DriverManagerDataSource dataSource(){return ds;}
        @Bean JdbcTemplate jdbcTemplate(){return new JdbcTemplate(ds);}
        @Bean PlatformTransactionManager transactionManager(){var t=new JpaTransactionManager(emf);t.setDataSource(ds);t.setJpaDialect(new HibernateJpaDialect());return t;}
        @Bean EmailMessageR emails(){return new JpaRepositoryFactory(SharedEntityManagerCreator.createSharedEntityManager(emf)).getRepository(EmailMessageR.class);}
        @Bean EmailAttemptService history(JdbcTemplate j){return new EmailAttemptService(j);}
        @Bean EmailProcessorService processor(EmailMessageR r,PlatformTransactionManager t){return new EmailProcessorService(sender,r,t);}
    }
    @BeforeAll void start()throws Exception{
        String admin=System.getenv("EPMAPA_TEST_PG_ADMIN");db="epmapa_email_"+UUID.randomUUID().toString().replace("-","");
        try(var c=DriverManager.getConnection(admin,"postgres","");var s=c.createStatement()){s.execute("CREATE DATABASE "+db);}
        ds=new DriverManagerDataSource(admin.replace("/postgres","/"+db),"postgres","");
        var reg=new StandardServiceRegistryBuilder().applySetting("hibernate.connection.datasource",ds).applySetting("hibernate.hbm2ddl.auto","create").applySetting("hibernate.physical_naming_strategy","org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy").build();
        emf=new MetadataSources(reg).addAnnotatedClass(EmailMessage.class).addAnnotatedClass(EmailAttachment.class).addAnnotatedClass(EmailAccount.class).buildMetadata().buildSessionFactory();
        var scripts=new ResourceDatabasePopulator(new ClassPathResource("sql/2026-10-29_email_retry.sql"),new ClassPathResource("sql/2026-10-30_correo_intentos.sql"));scripts.execute(ds);scripts.execute(ds);
        context=new AnnotationConfigApplicationContext(Config.class);jdbc=context.getBean(JdbcTemplate.class);tx=new TransactionTemplate(context.getBean(PlatformTransactionManager.class));
        repo=context.getBean(EmailMessageR.class);processor=context.getBean(EmailProcessorService.class);history=context.getBean(EmailAttemptService.class);
    }
    @AfterAll void stop()throws Exception{
        if(context!=null)context.close();if(emf!=null)emf.close();
        if(db!=null && db.matches("epmapa_email_[a-f0-9]+"))try(var c=DriverManager.getConnection(System.getenv("EPMAPA_TEST_PG_ADMIN"),"postgres","");var s=c.createStatement()){s.execute("DROP DATABASE "+db+" WITH (FORCE)");}
    }
    @BeforeEach void clean(){jdbc.execute("TRUNCATE public.email_message CASCADE");reset(sender);}
    private EmailMessage message(){return tx.execute(s->{var m=new EmailMessage();m.setType(EmailType.DOC_ELECTRONICO);m.setStatus(EmailStatus.PENDING);m.setSubject("Prueba aislada");m.setToRecipients("test@example.invalid");return repo.saveAndFlush(m);});}
    @Test void simultaneousWorkersSendOnlyOnceAndRecordOneAttempt()throws Exception{
        var m=message();var entered=new CountDownLatch(1);var release=new CountDownLatch(1);var pool=Executors.newSingleThreadExecutor();
        doAnswer(call->{entered.countDown();if(!release.await(10,TimeUnit.SECONDS))throw new IllegalStateException("timeout");return null;}).when(sender).sendNow(any());
        try{var job=pool.submit(()->processor.procesarEmail(m.getId(),5));assertTrue(entered.await(8,TimeUnit.SECONDS));
            processor.procesarEmail(m.getId(),5);release.countDown();job.get(10,TimeUnit.SECONDS);
            verify(sender,times(1)).sendNow(any());assertEquals(EmailStatus.SENT,repo.findById(m.getId()).orElseThrow().getStatus());
            assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM public.correo_intento WHERE resultado='SENT'",Integer.class));
        }finally{release.countDown();pool.shutdownNow();}
    }
    @Test void permanentRecipientFailureIsSuspendedAndHistoryIsDurable(){
        var m=message();doThrow(new EmailDeliveryException(EmailDeliveryException.Kind.PERMANENT,"550 recipient rejected",null)).when(sender).sendNow(any());
        processor.procesarEmail(m.getId(),5);processor.procesarEmail(m.getId(),5);
        verify(sender,times(1)).sendNow(any());assertEquals(EmailStatus.FAILED,repo.findById(m.getId()).orElseThrow().getStatus());
        assertEquals("PERMANENT",jdbc.queryForObject("SELECT resultado FROM public.correo_intento",String.class));
    }
    @Test void lostSmtpResponseIsNotAutomaticallyRetried(){
        var m=message();doThrow(new EmailDeliveryException(EmailDeliveryException.Kind.UNCERTAIN,"EOF despues de DATA",null)).when(sender).sendNow(any());
        processor.procesarEmail(m.getId(),5);processor.procesarEmail(m.getId(),5);
        assertEquals(EmailStatus.UNCERTAIN,repo.findById(m.getId()).orElseThrow().getStatus());verify(sender,times(1)).sendNow(any());
    }
    @Test void acceptedSmtpThenDatabaseFailureBecomesUncertainInsteadOfResending(){
        var m=message();
        jdbc.execute("CREATE FUNCTION reject_sent() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN IF NEW.status='SENT' THEN RAISE EXCEPTION 'simulated persistence outage'; END IF; RETURN NEW; END $$");
        jdbc.execute("CREATE TRIGGER fail_sent BEFORE UPDATE ON public.email_message FOR EACH ROW EXECUTE FUNCTION reject_sent()");
        try{processor.procesarEmail(m.getId(),5);}finally{jdbc.execute("DROP TRIGGER fail_sent ON public.email_message");jdbc.execute("DROP FUNCTION reject_sent()");}
        assertEquals(EmailStatus.PENDING,repo.findById(m.getId()).orElseThrow().getStatus());
        jdbc.update("UPDATE public.email_message SET processing_until=now()-interval '1 minute'");assertEquals(1,history.recuperarAbandonados());
        processor.procesarEmail(m.getId(),5);verify(sender,times(1)).sendNow(any());
        assertEquals(EmailStatus.UNCERTAIN,repo.findById(m.getId()).orElseThrow().getStatus());
        assertEquals("UNCERTAIN",jdbc.queryForObject("SELECT resultado FROM public.correo_intento",String.class));
    }
    @Test void temporaryFailureKeepsNextAttemptAndDoesNotCleanAttachments(){
        var m=message();doThrow(new EmailDeliveryException(EmailDeliveryException.Kind.TRANSIENT,"421 unavailable",null)).when(sender).sendNow(any());
        processor.procesarEmail(m.getId(),5);var stored=repo.findById(m.getId()).orElseThrow();
        assertEquals(EmailStatus.PENDING,stored.getStatus());assertNotNull(stored.getNextAttemptAt());assertNull(stored.getProcessingToken());
        verify(sender,never()).cleanupAttachments(any());
    }

    @Test void correctingAccountConfigurationResumesOnlyItsSuspendedMessages(){
        var m=message();
        Long accountId=tx.execute(st->{
            var a=new EmailAccount();a.setCode("test-account-"+UUID.randomUUID());a.setName("Test");a.setFromAddress("sender@example.invalid");a.setTransportType(EmailAccountTransportType.SMTP);a.setActive(true);
            var em=SharedEntityManagerCreator.createSharedEntityManager(emf);em.persist(a);em.flush();
            var row=repo.findById(m.getId()).orElseThrow();row.setAccount(a);repo.saveAndFlush(row);return a.getId();
        });
        doThrow(new EmailDeliveryException(EmailDeliveryException.Kind.CONFIGURATION,"535",null)).when(sender).sendNow(any());
        processor.procesarEmail(m.getId(),5);assertEquals(EmailStatus.FAILED,repo.findById(m.getId()).orElseThrow().getStatus());
        reset(sender);history.reactivarConfiguracion(accountId);processor.procesarEmail(m.getId(),5);
        assertEquals(EmailStatus.SENT,repo.findById(m.getId()).orElseThrow().getStatus());
        assertEquals(2,jdbc.queryForObject("SELECT count(*) FROM public.correo_intento",Integer.class));
    }
}
