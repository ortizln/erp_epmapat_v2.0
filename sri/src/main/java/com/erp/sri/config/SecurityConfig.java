package com.erp.sri.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesOffline;


@Configuration
public class SecurityConfig {
    @Bean
    public RecepcionComprobantesOffline recepcionComprobantesClient() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(RecepcionComprobantesOffline.class);
        factory.setAddress("https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline");

        RecepcionComprobantesOffline client = (RecepcionComprobantesOffline) factory.create();

        // Configurar autenticación básica si es necesario
        Client proxy = ClientProxy.getClient(client);
        HTTPConduit conduit = (HTTPConduit) proxy.getConduit();

        AuthorizationPolicy authorizationPolicy = new AuthorizationPolicy();
        authorizationPolicy.setUserName("tu_usuario");
        authorizationPolicy.setPassword("tu_contraseña");
        authorizationPolicy.setAuthorizationType("Basic");

        conduit.setAuthorization(authorizationPolicy);

        return client;
    }
}
