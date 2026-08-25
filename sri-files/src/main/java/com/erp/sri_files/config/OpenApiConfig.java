package com.erp.sri_files.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("SRI Files API")
                .description("""
                    Microservicio para la gestión de comprobantes electrónicos del SRI.
                    
                    Soporta 6 tipos de documentos:
                    - Facturas (01)
                    - Retenciones (07)
                    - Notas de crédito (04)
                    - Notas de débito (05)
                    - Liquidaciones de compra (03)
                    - Guías de remisión (06)
                    
                    Endpoints V1 centralizados bajo `/api/v1/`.
                    Flujo completo: recepción → validación → firma → envío SRI → autorización.
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("EPMAPA-T")
                    .email("soporte@epmapa.gob.ec"))
                .license(new License()
                    .name("Interno EPMAPA-T")))
            .addServersItem(new Server()
                .url("http://localhost:8080")
                .description("Local"));
    }
}
