package com.erp.sri_files.repositories;

import com.erp.sri_files.models.Factura;
import com.erp.sri_files.models.FacturaDetalle;
import com.erp.sri_files.models.FacturaDetalleImpuesto;
import com.erp.sri_files.models.FacturaPago;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;
import static org.junit.jupiter.api.Assertions.*;

class FacturaRecoveryQueryTest {
    @Test void conditionalUpdatesAreValidForRealEntityMappingsWithoutConnectingToProduction() {
        var registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", false)
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .build();
        try (var factory = new MetadataSources(registry)
                .addAnnotatedClass(Factura.class).addAnnotatedClass(FacturaDetalle.class)
                .addAnnotatedClass(FacturaDetalleImpuesto.class).addAnnotatedClass(FacturaPago.class)
                .buildMetadata().buildSessionFactory(); var session = factory.openSession()) {
            for (var method : FacturaR.class.getMethods()) {
                var annotation = method.getAnnotation(Query.class);
                if (annotation != null && annotation.value().startsWith("UPDATE Factura")) {
                    assertDoesNotThrow(() -> session.createMutationQuery(annotation.value()), method.getName());
                }
            }
        } finally { StandardServiceRegistryBuilder.destroy(registry); }
    }
}
