package com.erp.pagosonline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@EnableDiscoveryClient
@SpringBootApplication
public class PagosonlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(PagosonlineApplication.class, args);
	}

}
