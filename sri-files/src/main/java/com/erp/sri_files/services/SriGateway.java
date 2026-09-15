package com.erp.sri_files.services;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import ec.gob.sri.ws.autorizacion.RespuestaComprobante;
import ec.gob.sri.ws.recepcion.RespuestaSolicitud;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SriGateway {

    private static final Logger log = LoggerFactory.getLogger(SriGateway.class);

    private final SendXmlToSriService sendXmlToSriService;

    public enum Ambiente {
        PRUEBAS(1),
        PRODUCCION(2);
        
        private final int codigo;
        Ambiente(int codigo) { this.codigo = codigo; }
        public int getCodigo() { return codigo; }
    }

    public enum ResultadoRecepcion {
        RECIBIDA,
        NO_RECIBIDA,
        ERROR
    }

    public record SriRecepcionResult(
        ResultadoRecepcion resultado,
        RespuestaSolicitud respuesta,
        String mensaje
    ) {}

    public record SriAutorizacionResult(
        boolean autorizado,
        String xmlAutorizado,
        String numeroAutorizacion,
        String fechaAutorizacion,
        String mensaje,
        RespuestaComprobante respuestaCompleta
    ) {}

    public void setAmbiente(Ambiente ambiente) {
        sendXmlToSriService.setAmbiente(ambiente.getCodigo());
    }

    public void setAmbienteFromXml(String xmlFirmado) {
        sendXmlToSriService.setAmbienteFromXml(xmlFirmado);
    }

    public int inferAmbienteFromXml(String xmlFirmado) {
        return sendXmlToSriService.inferAmbienteFromXml(xmlFirmado);
    }

    public SriRecepcionResult enviarRecepcion(String xmlFirmado) {
        return enviarRecepcion(xmlFirmado, null);
    }

    public SriRecepcionResult enviarRecepcion(String xmlFirmado, Integer ambienteForzado) {
        String requestId = MDC.get("requestId");
        log.info("Enviando comprobante a Recepción SRI [requestId={}]", requestId);
        
        try {
            if (ambienteForzado != null) {
                sendXmlToSriService.setAmbiente(ambienteForzado == 2 ? 2 : 1);
            } else {
                sendXmlToSriService.setAmbienteFromXml(xmlFirmado);
            }

            int ambienteSolicitud = ambienteForzado != null ? ambienteForzado : sendXmlToSriService.inferAmbienteFromXml(xmlFirmado);
            RespuestaSolicitud recepcion = sendXmlToSriService.enviarFacturaFirmadaTxt(xmlFirmado, ambienteSolicitud);
            
            if ("RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
                log.info("Comprobante RECIBIDO por SRI [requestId={}]", requestId);
                return new SriRecepcionResult(ResultadoRecepcion.RECIBIDA, recepcion, "Recibido correctamente");
            } else {
                log.warn("Comprobante NO recibido: {} [requestId={}]", recepcion.getEstado(), requestId);
                return new SriRecepcionResult(ResultadoRecepcion.NO_RECIBIDA, recepcion, 
                    "Estado: " + recepcion.getEstado() + " | " + mensajesRecepcion(recepcion));
            }
        } catch (Exception e) {
            log.error("Error enviando a Recepción SRI [requestId={}]", requestId, e);
            return new SriRecepcionResult(ResultadoRecepcion.ERROR, null, e.getMessage());
        }
    }

    private String mensajesRecepcion(RespuestaSolicitud rs) {
        if (rs.getComprobantes() == null) return "Sin mensajes";
        return rs.getComprobantes().getComprobante().stream().filter(c -> c.getMensajes() != null)
                .flatMap(c -> c.getMensajes().getMensaje().stream())
                .map(m -> "[" + m.getIdentificador() + "] " + m.getMensaje() + " "
                        + java.util.Objects.toString(m.getInformacionAdicional(), ""))
                .collect(java.util.stream.Collectors.joining(" | "));
    }

    public SriAutorizacionResult consultarAutorizacion(String claveAcceso) {
        String requestId = MDC.get("requestId");
        log.info("Consultando autorización SRI: {} [requestId={}]", claveAcceso, requestId);
        
        try {
            RespuestaComprobante rc = sendXmlToSriService.consultarAutorizacion(claveAcceso);
            return procesarRespuestaAutorizacion(rc);
        } catch (Exception e) {
            log.error("Error consultando autorización [requestId={}]", requestId, e);
            return new SriAutorizacionResult(false, null, null, null, e.getMessage(), null);
        }
    }

    public SriAutorizacionResult consultarAutorizacionConPolling(String xmlFirmado, int maxIntentos, long sleepMs) {
        String requestId = MDC.get("requestId");
        log.info("Consultando autorización con polling (max={} intentos) [requestId={}]", maxIntentos, requestId);
        
        try {
            RespuestaComprobante rc = sendXmlToSriService.consultarAutorizacionConEspera(
                xmlFirmado,
                clave -> {
                    try { return sendXmlToSriService.consultarAutorizacion(clave); }
                    catch (Exception e) { throw new RuntimeException(e); }
                },
                maxIntentos,
                sleepMs
            );
            return procesarRespuestaAutorizacion(rc);
        } catch (Exception e) {
            log.error("Error en polling de autorización [requestId={}]", requestId, e);
            return new SriAutorizacionResult(false, null, null, null, e.getMessage(), null);
        }
    }

    private SriAutorizacionResult procesarRespuestaAutorizacion(RespuestaComprobante rc) {
        if (rc == null || rc.getAutorizaciones() == null || rc.getAutorizaciones().getAutorizacion() == null) {
            return new SriAutorizacionResult(false, null, null, null, "Sin respuesta del SRI", rc);
        }
        
        var lista = rc.getAutorizaciones().getAutorizacion();
        if (lista.isEmpty()) {
            return new SriAutorizacionResult(false, null, null, null, "Sin autorizaciones", rc);
        }
        
        var autorizada = lista.stream()
            .filter(a -> "AUTORIZADO".equalsIgnoreCase(a.getEstado()))
            .findFirst()
            .orElse(lista.get(0));
        
        boolean estadoAutorizado = "AUTORIZADO".equalsIgnoreCase(autorizada.getEstado());
        String xmlAutorizado = estadoAutorizado
            ? extraerComprobanteXml(autorizada.getComprobante())
            : null;
        boolean autorizado = estadoAutorizado && xmlAutorizado != null && !xmlAutorizado.isBlank();
        
        String numeroAutorizacion = autorizada.getNumeroAutorizacion();
        String fechaAutorizacion = autorizada.getFechaAutorizacion() != null 
            ? autorizada.getFechaAutorizacion().toXMLFormat() 
            : null;
        
        if (autorizado) {
            log.info("Comprobante AUTORIZADO: nro={} fecha={}", numeroAutorizacion, fechaAutorizacion);
        } else if (estadoAutorizado) {
            log.warn("SRI autorizo el comprobante nro={}, pero no devolvio un XML valido", numeroAutorizacion);
        } else {
            log.warn("Comprobante NO AUTORIZADO: estado={}", autorizada.getEstado());
        }
        
        return new SriAutorizacionResult(
            autorizado,
            xmlAutorizado,
            numeroAutorizacion,
            fechaAutorizacion,
            autorizado ? "AUTORIZADO" : estadoAutorizado
                ? "SRI autorizo el comprobante, pero no devolvio XML autorizado"
                : autorizada.getEstado(),
            rc
        );
    }

    private String extraerComprobanteXml(String comprobante) {
        if (comprobante == null || comprobante.isBlank()) {
            return null;
        }

        String valor = comprobante.trim();
        // JAXB ya entrega como texto el contenido que el SRI envia en CDATA.
        if (valor.startsWith("<")) {
            return valor;
        }

        try {
            String decodificado = new String(
                java.util.Base64.getDecoder().decode(valor),
                java.nio.charset.StandardCharsets.UTF_8
            ).trim();
            return decodificado.startsWith("<") ? decodificado : null;
        } catch (IllegalArgumentException e) {
            log.warn("El comprobante autorizado no contiene XML ni Base64 valido");
            return null;
        }
    }
}
