package com.erp.sri_files.config;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyFilter.class);

    @Value("${app.apikey.enabled:false}")
    private boolean enabled;

    @Value("${app.apikey.header:X-API-Key}")
    private String apiKeyHeader;

    @Value("${app.apikey.value:}")
    private String expectedApiKey;

    @Value("${app.apikey.whitelisted-paths:/api/v1/erp/ping,/swagger-ui/**,/v3/api-docs/**,/actuator/health}")
    private String whitelistedPaths;

    private Set<String> whitelisted;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if (!enabled || expectedApiKey == null || expectedApiKey.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (whitelisted == null) {
            whitelisted = Set.of(whitelistedPaths.split(","));
        }

        String path = request.getRequestURI();
        for (String whitelistedPath : whitelisted) {
            if (path.matches(whitelistedPath.replace("*", ".*"))) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String providedKey = request.getHeader(apiKeyHeader);
        if (providedKey == null || !providedKey.equals(expectedApiKey)) {
            log.warn("API Key inválida o faltante para path={}", path);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"API Key inválida o faltante\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
