package com.erp.emails.config;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
@Component
public class InternalEmailApiFilter extends OncePerRequestFilter {
    private final OperationsGuard guard;
    public InternalEmailApiFilter(OperationsGuard guard){this.guard=guard;}
    @Override protected boolean shouldNotFilter(HttpServletRequest r){return !r.getRequestURI().startsWith("/api/v1/") || "OPTIONS".equals(r.getMethod());}
    @Override protected void doFilterInternal(HttpServletRequest r,HttpServletResponse s,FilterChain chain)throws ServletException,IOException{
        try {guard.check(r.getHeader("X-Operations-Key"),r.getHeader("X-Operations-Actor"));}
        catch(ResponseStatusException denied){s.setStatus(denied.getStatusCode().value());s.setContentType("application/json");s.getWriter().write("{\"error\":\"Acceso interno no autorizado o no configurado\"}");return;}
        chain.doFilter(r,s);
    }
}
