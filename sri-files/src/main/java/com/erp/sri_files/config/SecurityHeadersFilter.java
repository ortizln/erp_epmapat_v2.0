package com.erp.sri_files.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Value("${app.security.headers.enabled:true}")
    private boolean enabled;

    @Value("${app.security.headers.content-type-options:true}")
    private boolean contentTypeOptions;

    @Value("${app.security.headers.xss-protection:true}")
    private boolean xssProtection;

    @Value("${app.security.headers.frame-options:true}")
    private boolean frameOptions;

    @Value("${app.security.headers.hsts:true}")
    private boolean hsts;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        if (contentTypeOptions) {
            response.setHeader("X-Content-Type-Options", "nosniff");
        }

        if (xssProtection) {
            response.setHeader("X-XSS-Protection", "1; mode=block");
        }

        if (frameOptions) {
            response.setHeader("X-Frame-Options", "DENY");
        }

        if (hsts) {
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }

        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        filterChain.doFilter(request, response);
    }
}
