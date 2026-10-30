package com.erp.sri_files.services;

import com.erp.sri_files.models.*;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.utils.FirmaComprobantesService;
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
import org.springframework.test.util.ReflectionTestUtils;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIfEnvironmentVariable(named="EPMAPA_TEST_PG_ADMIN",matches="jdbc:postgresql://127\\.0\\.0\\.1:55439/postgres")
class WorkflowPostgresTest {
    static DriverManagerDataSource ds;static org.hibernate.SessionFactory emf;
    private AnnotationConfigApplicationContext context;private JdbcTemplate jdbc;private TransactionTemplate tx;
    private FacturaOutboxService outbox;private FacturaR repo;private String db;
    static final FacturaXmlGeneratorService gen=mock(FacturaXmlGeneratorService.class);
    static final FirmaComprobantesService firm=mock(FirmaComprobantesService.class);
    static final SendXmlToSriService soap=mock(SendXmlToSriService.class);
    static final XmlToPdfService pdf=mock(XmlToPdfService.class);
    static final MailService mail=mock(MailService.class);
    static final EmailMsClientService emailClient=mock(EmailMsClientService.class);
    static final String KEY="1009202601046002881000120010130004585995621446311";
    @Configuration @EnableTransactionManagement static class Config {
        @Bean DriverManagerDataSource dataSource(){return ds;}
        @Bean JdbcTemplate jdbcTemplate(){return new JdbcTemplate(ds);}
        @Bean PlatformTransactionManager transactionManager(){var t=new JpaTransactionManager(emf);t.setDataSource(ds);t.setJpaDialect(new HibernateJpaDialect());return t;}
        @Bean FacturaR facturas(){return new JpaRepositoryFactory(SharedEntityManagerCreator.createSharedEntityManager(emf)).getRepository(FacturaR.class);}
        @Bean FacturaOutboxService outbox(JdbcTemplate j){return new FacturaOutboxService(j);}
        @Bean FacturaOperationsService operations(FacturaR f,FacturaOutboxService o,JdbcTemplate j){return new FacturaOperationsService(f,o,j,emailClient);}
        @Bean SriAutorizacionRetryPolicy retry(){return new SriAutorizacionRetryPolicy();}
        @Bean FacturaPreparationService prep(FacturaR f,SriAutorizacionRetryPolicy r,FacturaOutboxService o){return new FacturaPreparationService(f,gen,firm,r,o);}
        @Bean EnvioSriBatchService envio(FacturaR f,PlatformTransactionManager t,SriAutorizacionRetryPolicy r){return new EnvioSriBatchService(f,t,gen,firm,soap,pdf,mail,r);}

    }
    @BeforeAll void start()throws Exception {
        String admin=System.getenv("EPMAPA_TEST_PG_ADMIN");db="epmapa_workflow_"+UUID.randomUUID().toString().replace("-","");
        try(var c=DriverManager.getConnection(admin,"postgres","");var s=c.createStatement()){s.execute("CREATE DATABASE "+db);}
        ds=new DriverManagerDataSource(admin.replace("/postgres","/"+db),"postgres","");
        var registry=new StandardServiceRegistryBuilder().applySetting("hibernate.connection.datasource",ds)
                .applySetting("hibernate.hbm2ddl.auto","create").applySetting("hibernate.physical_naming_strategy","org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy").build();
        emf=new MetadataSources(registry).addAnnotatedClass(Factura.class).addAnnotatedClass(FacturaDetalle.class)
                .addAnnotatedClass(FacturaDetalleImpuesto.class).addAnnotatedClass(FacturaPago.class).buildMetadata().buildSessionFactory();
        var scripts=new ResourceDatabasePopulator(new ClassPathResource("sql/2026-10-30_flujo_persistente.sql"));scripts.execute(ds);scripts.execute(ds);
        context=new AnnotationConfigApplicationContext(Config.class);jdbc=context.getBean(JdbcTemplate.class);
        tx=new TransactionTemplate(context.getBean(PlatformTransactionManager.class));outbox=context.getBean(FacturaOutboxService.class);repo=context.getBean(FacturaR.class);
    }
    @AfterAll void stop()throws Exception {
        if(context!=null)context.close();if(emf!=null)emf.close();
        if(db!=null && db.matches("epmapa_workflow_[a-f0-9]+"))try(var c=DriverManager.getConnection(System.getenv("EPMAPA_TEST_PG_ADMIN"),"postgres","");var s=c.createStatement()){s.execute("DROP DATABASE "+db+" WITH (FORCE)");}
    }
    @BeforeEach void clean(){jdbc.execute("TRUNCATE public.fec_factura CASCADE");reset(soap,mail,gen,firm);}
    private Factura invoice(String estado){
        return tx.execute(st->{var f=new Factura();f.setEstado(estado);f.setClaveacceso(KEY);return repo.saveAndFlush(f);});
    }
    private void authorize(Long id){tx.executeWithoutResult(st->{var f=repo.findParaProcesar(id).orElseThrow();f.setEstado("O");f.setXmlautorizado("<autorizacion/>");repo.saveAndFlush(f);outbox.registrar(f);});}
    @Test void authorizationAndNotificationRollbackTogether(){
        var f=invoice("C");
        assertThrows(IllegalStateException.class,()->tx.executeWithoutResult(st->{
            var current=repo.findParaProcesar(f.getIdfactura()).orElseThrow();current.setEstado("O");current.setXmlautorizado("<autorizacion/>");repo.saveAndFlush(current);outbox.registrar(current);throw new IllegalStateException("crash");
        }));
        assertEquals("C",repo.findById(f.getIdfactura()).orElseThrow().getEstado());
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM public.evento_outbox",Integer.class));
    }
    @Test void repeatedAuthorizationCreatesOneNotificationAndVersionedCorrectionsCreateAnother(){
        var f=invoice("C");authorize(f.getIdfactura());authorize(f.getIdfactura());
        assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM public.evento_outbox",Integer.class));
        tx.executeWithoutResult(st->{var current=repo.findParaProcesar(f.getIdfactura()).orElseThrow();current.setNotificacionVersion(2);repo.saveAndFlush(current);outbox.registrar(current);});
        assertEquals(2,jdbc.queryForObject("SELECT count(*) FROM public.evento_outbox",Integer.class));
    }
    @Test void concurrentWorkersClaimEachNotificationOnlyOnce()throws Exception {
        var f=invoice("C");authorize(f.getIdfactura());
        var pool=Executors.newFixedThreadPool(8);var start=new CountDownLatch(1);
        try{List<Future<Map<String,Object>>> futures=new ArrayList<>();
            for(int i=0;i<8;i++)futures.add(pool.submit(()->{start.await();return outbox.reclamar();}));
            start.countDown();int winners=0;for(var future:futures)if(future.get(15,TimeUnit.SECONDS)!=null)winners++;
            assertEquals(1,winners);
        }finally{pool.shutdownNow();}
    }
    @Test void abandonedOutboxIsRecoveredAndStaleWorkerCannotOverwriteNewLease(){
        var f=invoice("C");authorize(f.getIdfactura());var old=outbox.reclamar();
        jdbc.update("UPDATE public.evento_outbox SET reservado_hasta=now()-interval '1 minute'");
        var fresh=outbox.reclamar();assertNotNull(fresh);assertNotEquals(old.get("token"),fresh.get("token"));
        outbox.terminar(old,"COMPLETE",null);
        assertEquals("PROCESSING",jdbc.queryForObject("SELECT estado FROM public.evento_outbox",String.class));
        outbox.terminar(fresh,"COMPLETE",null);
        assertEquals("COMPLETE",jdbc.queryForObject("SELECT estado FROM public.evento_outbox",String.class));
    }
    @Test void signedXmlAndDispatchIntentAreCommittedBeforeNetwork()throws Exception{
        var f=invoice("I");var sri=soap;
        when(gen.generarXmlFactura(any())).thenReturn("<factura/>");
        when(firm.firmarFactura(anyString(),any())).thenReturn("<signed-original/>");
        when(sri.inferAmbienteFromXml(anyString())).thenReturn(2);
        when(sri.enviarFacturaFirmadaTxt(anyString(),eq(2))).thenAnswer(call->{
            assertEquals("P",jdbc.queryForObject("SELECT estado FROM public.fec_factura WHERE idfactura=?",String.class,f.getIdfactura()));
            assertEquals("<signed-original/>",jdbc.queryForObject("SELECT xml_firmado_envio FROM public.fec_factura WHERE idfactura=?",String.class,f.getIdfactura()));
            throw new java.net.SocketTimeoutException("SRI unavailable");
        });
        context.getBean(EnvioSriBatchService.class).procesarFacturaEnNuevaTx(f.getIdfactura());
        assertEquals("C",repo.findById(f.getIdfactura()).orElseThrow().getEstado());
        verify(sri,never()).consultar_Autorizacion(anyString());
    }
    @Test void lateAuthorizationSchedulesNotificationWithoutCallingMail()throws Exception{
        var f=invoice("C");var auth=new com.erp.sri_files.dto.AutorizacionSriResult();auth.setAutorizado(true);
        auth.setXmlAutorizado("<factura><claveAcceso>"+KEY+"</claveAcceso></factura>");auth.setNumeroAutorizacion("real");auth.setFechaAutorizacion(java.time.LocalDateTime.now());
        when(soap.consultar_Autorizacion(KEY)).thenReturn(auth);
        assertEquals(Boolean.TRUE,tx.execute(st->context.getBean(EnvioSriBatchService.class).consultarPendiente(f.getIdfactura())));
        assertEquals("O",repo.findById(f.getIdfactura()).orElseThrow().getEstado());
        assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM public.evento_outbox",Integer.class));
        verifyNoInteractions(mail);
    }

    @Test void correctedRecipientCreatesANewVersionWithoutChangingTaxIdentity(){
        var f=invoice("C");authorize(f.getIdfactura());
        context.getBean(FacturaOperationsService.class).ejecutar(f.getIdfactura(),"corregir-correo",
                new FacturaOperationsService.Command("corrected@example.invalid",null,false,false),"test","operator-1");
        var updated=repo.findById(f.getIdfactura()).orElseThrow();
        assertEquals(KEY,updated.getClaveacceso());assertEquals("corrected@example.invalid",updated.getEmailcomprador());assertEquals(2,updated.getNotificacionVersion());
        assertEquals(2,jdbc.queryForObject("SELECT count(*) FROM public.evento_outbox",Integer.class));
        assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM public.factura_operacion",Integer.class));
    }
    @Test void manualCorrectionCannotRaceAnActiveWorker(){
        var f=invoice("C");authorize(f.getIdfactura());assertNotNull(outbox.reclamar());
        assertThrows(org.springframework.web.server.ResponseStatusException.class,()->context.getBean(FacturaOperationsService.class).ejecutar(f.getIdfactura(),"corregir-correo",
                new FacturaOperationsService.Command("corrected@example.invalid",null,false,false),"test","operator-1"));
        assertEquals(1,repo.findById(f.getIdfactura()).orElseThrow().getNotificacionVersion());
    }
}
