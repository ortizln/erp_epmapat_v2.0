package com.erp.emails.repository;
import com.erp.emails.model.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;
import static org.junit.jupiter.api.Assertions.*;
class EmailOutboxQueryTest {
    @Test void invoiceRetryQueriesMatchTheRealEntityMappings() {
        var registry=new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect","org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access",false)
                .applySetting("hibernate.hbm2ddl.auto","none").build();
        try(var factory=new MetadataSources(registry).addAnnotatedClass(EmailMessage.class)
                .addAnnotatedClass(EmailAccount.class).addAnnotatedClass(EmailAttachment.class)
                .buildMetadata().buildSessionFactory();var session=factory.openSession()) {
            for(var method:EmailMessageR.class.getMethods()) {
                var query=method.getAnnotation(Query.class);
                if(query==null)continue;
                if(query.value().startsWith("update"))
                    assertDoesNotThrow(()->session.createMutationQuery(query.value()),method.getName());
                else assertDoesNotThrow(()->session.createQuery(query.value(),Object.class),method.getName());
            }
        } finally {StandardServiceRegistryBuilder.destroy(registry);}
    }
}
