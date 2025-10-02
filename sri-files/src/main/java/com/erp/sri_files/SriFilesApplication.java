package com.erp.sri_files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@SpringBootApplication
public class SriFilesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SriFilesApplication.class, args);
	}

    @Bean
   // @LoadBalanced
    RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
    static {
        org.apache.xml.security.Init.init();
        System.setProperty("org.apache.xml.security.ignoreLineBreaks", "true");
    }

}
