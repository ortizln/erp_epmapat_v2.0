package com.erp.gestiondocumental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.erp.gestiondocumental", "com.erp.pagosonline"})
@EnableDiscoveryClient
public class GestionDocumentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionDocumentalApplication.class, args);
    }
}
