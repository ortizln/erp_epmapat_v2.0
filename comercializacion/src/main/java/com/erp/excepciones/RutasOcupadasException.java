package com.erp.excepciones;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class RutasOcupadasException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final List<Long> rutasOcupadas;

    public RutasOcupadasException(String message, List<Long> rutasOcupadas) {
        super(message);
        this.rutasOcupadas = rutasOcupadas;
    }
}
