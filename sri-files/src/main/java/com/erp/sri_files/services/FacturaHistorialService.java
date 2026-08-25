package com.erp.sri_files.services;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.sri_files.models.Factura;
import com.erp.sri_files.models.FacturaLog;
import com.erp.sri_files.repositories.FacturaLogR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacturaHistorialService {
    
    private static final Logger log = LoggerFactory.getLogger(FacturaHistorialService.class);
    
    private final FacturaLogR facturaLogR;
    
    @Transactional
    public void registrarCambioEstado(Factura factura, String estadoAnterior, String estadoNuevo, String mensaje) {
        FacturaLog facturaLog = new FacturaLog();
        facturaLog.setIdfactura(factura.getIdfactura());
        facturaLog.setEstado(estadoNuevo);
        facturaLog.setMensaje(mensaje);
        facturaLog.setFecha(LocalDateTime.now());
        
        facturaLogR.save(facturaLog);
        
        log.debug("Factura {} cambio estado: {} -> {} [{}]", 
            factura.getIdfactura(), estadoAnterior, estadoNuevo, mensaje);
    }
    
    @Transactional
    public void registrarIntentoAutorizacion(Factura factura, String resultado, String mensaje) {
        FacturaLog facturaLog = new FacturaLog();
        facturaLog.setIdfactura(factura.getIdfactura());
        facturaLog.setEstado(factura.getEstado());
        facturaLog.setMensaje(String.format("Intento autorización: %s - %s", resultado, mensaje));
        facturaLog.setFecha(LocalDateTime.now());
        
        facturaLogR.save(facturaLog);
        
        log.debug("Factura {} intento autorización: {}", factura.getIdfactura(), resultado);
    }
    
    @Transactional
    public void registrarError(Factura factura, String etapa, String error) {
        FacturaLog facturaLog = new FacturaLog();
        facturaLog.setIdfactura(factura.getIdfactura());
        facturaLog.setEstado(factura.getEstado());
        facturaLog.setMensaje(String.format("Error en %s: %s", etapa, error));
        facturaLog.setFecha(LocalDateTime.now());
        
        facturaLogR.save(facturaLog);
        
        log.warn("Factura {} error en {}: {}", factura.getIdfactura(), etapa, error);
    }
}
