package com.epmapat.erp_epmapat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@EnableDiscoveryClient
@SpringBootApplication
public class ComercializacionApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(ComercializacionApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder aplication) {
		return aplication.sources(ComercializacionApplication.class);
	}

}