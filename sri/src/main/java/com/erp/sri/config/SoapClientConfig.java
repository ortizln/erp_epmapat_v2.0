package com.erp.sri.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesOffline;


@Configuration
public class SoapClientConfig {
    @Bean
    public RecepcionComprobantesOffline recepcionComprobantesClient() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(RecepcionComprobantesOffline.class);
        factory.setAddress("https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline");

        // Configuración adicional para seguridad si es necesario
        // ...

        return (RecepcionComprobantesOffline) factory.create();
    }
}
