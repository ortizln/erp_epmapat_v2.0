package com.erp.sri_files.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Permitir cualquier origen
        config.setAllowedOriginPatterns(List.of("*"));

        // Métodos permitidos
        config.setAllowedMethods(
                Arrays.stream(allowedMethods.split(","))
                        .map(String::trim)
                        .toList()
        );

        // Headers permitidos
        config.setAllowedHeaders(
                Arrays.stream(allowedHeaders.split(","))
                        .map(String::trim)
                        .toList()
        );

        config.setAllowCredentials(allowCredentials);
        config.setMaxAge(maxAge);

        config.setExposedHeaders(List.of(
                "X-Request-Id",
                "X-Rate-Limit-Remaining",
                "X-Rate-Limit-Retry-After-Seconds"
        ));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}