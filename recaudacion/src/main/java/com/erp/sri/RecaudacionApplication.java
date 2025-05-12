package com.erp.sri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RecaudacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecaudacionApplication.class, args);
	}

}
