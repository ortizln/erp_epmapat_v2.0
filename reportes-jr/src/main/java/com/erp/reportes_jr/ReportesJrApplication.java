package com.erp.reportes_jr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ReportesJrApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportesJrApplication.class, args);
	}

}
