package com.erp.comercializacion.servicio;

import com.erp.comercializacion.modelo.Fec_factura_log;
import com.erp.comercializacion.repositorio.Fec_factura_logR;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FecFacturaLogService {
    private final Fec_factura_logR repository;

    public FecFacturaLogService(Fec_factura_logR repository) {
        this.repository = repository;
    }

    public void registrar(Long idfactura, String estado, String mensaje) {
        Fec_factura_log log = new Fec_factura_log();
        log.setIdfactura(idfactura);
        log.setEstado(estado);
        log.setMensaje(mensaje);
        log.setFecha(LocalDateTime.now());
        repository.save(log);
    }
}
