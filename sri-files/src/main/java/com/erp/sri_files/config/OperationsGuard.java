package com.erp.sri_files.config;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
@Component
public class OperationsGuard {
    @Value("${OPERATIONS_API_KEY:}") private String expected;
    public String check(String key,String actor) {
        if(expected==null || expected.isBlank())throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Operaciones administrativas no configuradas");
        if(key==null || !MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),key.getBytes(StandardCharsets.UTF_8)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Operacion no autorizada");
        if(actor==null || actor.isBlank() || actor.length()>100)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Actor de auditoria requerido");
        return actor;
    }
}
