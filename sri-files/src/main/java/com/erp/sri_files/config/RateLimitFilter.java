package com.erp.sri_files.config;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    @Value("${app.ratelimit.enabled:true}")
    private boolean enabled;

    @Value("${app.ratelimit.max-requests-per-minute:60}")
    private int maxRequestsPerMinute;

    @Value("${app.ratelimit.max-requests-per-hour:1000}")
    private int maxRequestsPerHour;

    private final Map<String, RateWindow> minuteWindows = new ConcurrentHashMap<>();
    private final Map<String, RateWindow> hourWindows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        long now = System.currentTimeMillis();

        RateWindow minuteWindow = minuteWindows.compute(clientIp, (key, existing) -> {
            if (existing == null || now - existing.windowStart > 60_000) {
                return new RateWindow(now);
            }
            return existing;
        });

        if (minuteWindow.counter.incrementAndGet() > maxRequestsPerMinute) {
            log.warn("Rate limit excedido (minuto) para IP={}", clientIp);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("X-Rate-Limit-Remaining", "0");
            response.setHeader("X-Rate-Limit-Retry-After-Seconds", 
                String.valueOf(60 - (now - minuteWindow.windowStart) / 1000));
            response.getWriter().write("{\"error\":\"Rate limit excedido. Intente en un minuto.\"}");
            response.setContentType("application/json");
            return;
        }

        RateWindow hourWindow = hourWindows.compute(clientIp, (key, existing) -> {
            if (existing == null || now - existing.windowStart > 3_600_000) {
                return new RateWindow(now);
            }
            return existing;
        });

        if (hourWindow.counter.incrementAndGet() > maxRequestsPerHour) {
            log.warn("Rate limit excedido (hora) para IP={}", clientIp);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("X-Rate-Limit-Remaining", "0");
            response.setHeader("X-Rate-Limit-Retry-After-Seconds", 
                String.valueOf(3600 - (now - hourWindow.windowStart) / 1000));
            response.getWriter().write("{\"error\":\"Rate limit excedido. Intente en una hora.\"}");
            response.setContentType("application/json");
            return;
        }

        response.setHeader("X-Rate-Limit-Remaining", 
            String.valueOf(maxRequestsPerMinute - minuteWindow.counter.get()));

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    private static class RateWindow {
        final long windowStart;
        final AtomicInteger counter;

        RateWindow(long windowStart) {
            this.windowStart = windowStart;
            this.counter = new AtomicInteger(0);
        }
    }
}
