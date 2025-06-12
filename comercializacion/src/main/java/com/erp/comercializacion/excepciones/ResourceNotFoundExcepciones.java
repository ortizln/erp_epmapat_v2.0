package com.erp.comercializacion.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.NOT_FOUND ) 
public class ResourceNotFoundExcepciones extends RuntimeException{
	@Serial
	private static final long serialVersionUID = 1L;
	public ResourceNotFoundExcepciones (String mensaje) {
		super (mensaje);
	}

}
