package com.erp.sri_files.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/singsend/**")
                        .allowedOrigins("*") // 👈 tu frontend Angular
                        .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS")
                        .allowedHeaders("*")                 // o específicos si quieres restringir
                        .exposedHeaders("*")
                        .allowCredentials(false)              // ponlo en false si NO usas cookies/sesión
                        .maxAge(3600);
            }
        };
    }
}
