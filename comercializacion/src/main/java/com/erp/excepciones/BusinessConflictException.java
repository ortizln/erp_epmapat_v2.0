package com.erp.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BusinessConflictException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BusinessConflictException(String mensaje) {
        super(mensaje);
    }
}
