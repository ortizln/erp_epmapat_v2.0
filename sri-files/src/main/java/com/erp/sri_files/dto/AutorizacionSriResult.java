package com.erp.sri_files.dto;

import java.time.LocalDateTime;

public class AutorizacionSriResult {

    private boolean autorizado;
    private String xmlAutorizado;
    private String mensaje;
    private LocalDateTime fechaAutorizacion;
    private String numeroAutorizacion;

    public AutorizacionSriResult() {}

    public boolean isAutorizado() { return autorizado; }
    public void setAutorizado(boolean autorizado) { this.autorizado = autorizado; }

    public String getXmlAutorizado() { return xmlAutorizado; }
    public void setXmlAutorizado(String xmlAutorizado) { this.xmlAutorizado = xmlAutorizado; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getFechaAutorizacion() { return fechaAutorizacion; }
    public void setFechaAutorizacion(LocalDateTime fechaAutorizacion) { this.fechaAutorizacion = fechaAutorizacion; }

    public String getNumeroAutorizacion() { return numeroAutorizacion; }
    public void setNumeroAutorizacion(String numeroAutorizacion) { this.numeroAutorizacion = numeroAutorizacion; }
}
