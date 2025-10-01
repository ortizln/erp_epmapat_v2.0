package com.erp.sri_files.services;

import ec.gob.sri.ws.autorizacion.AutorizacionComprobantesOffline;
import ec.gob.sri.ws.autorizacion.AutorizacionComprobantesOfflineService;
import ec.gob.sri.ws.autorizacion.RespuestaComprobante;
import ec.gob.sri.ws.recepcion.RecepcionComprobantesOffline;
import ec.gob.sri.ws.recepcion.RecepcionComprobantesOfflineService;
import ec.gob.sri.ws.recepcion.RespuestaSolicitud;
import jakarta.xml.ws.BindingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

@Service
public class EnvioComprobantesWs {

    /** 1 = PRUEBAS, 2 = PRODUCCIÓN. */
    private int ambiente;

    // Permite configurar por application.properties (opcionales, con defaults SRI)
    @Value("${sri.ambiente:1}")
    public void setAmbiente(int ambiente) { this.ambiente = (ambiente == 2) ? 2 : 1; }
    public int getAmbiente() { return ambiente; }

    @Value("${sri.wsdl.recepcion.pruebas:https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl}")
    private String recepcionPruebas;

    @Value("${sri.wsdl.autorizacion.pruebas:https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl}")
    private String autorizacionPruebas;

    @Value("${sri.wsdl.recepcion.produccion:https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl}")
    private String recepcionProd;

    @Value("${sri.wsdl.autorizacion.produccion:https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl}")
    private String autorizacionProd;

    /** Selecciona WSDL de recepción según ambiente. */
    private String recepcionWsdl() {
        return ambiente == 1 ? recepcionPruebas : recepcionProd;
    }

    /** Selecciona WSDL de autorización según ambiente. */
    private String autorizacionWsdl() {
        return ambiente == 1 ? autorizacionPruebas : autorizacionProd;
    }

    /** Timeouts para JAX-WS (Metro). */
    private void applyTimeouts(Object port) {
        Map<String, Object> ctx = ((BindingProvider) port).getRequestContext();
        ctx.put("com.sun.xml.ws.connect.timeout", 15000);  // conexión
        ctx.put("com.sun.xml.ws.request.timeout", 30000);  // lectura
    }

    /** Enviar XML firmado como bytes. */
    public RespuestaSolicitud enviarFacturaFirmada(byte[] xmlBytes) throws Exception {
        URL wsdlURL = new URL(recepcionWsdl());
        QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");

        RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService(wsdlURL, qname);
        RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();
        applyTimeouts(port);

        return port.validarComprobante(xmlBytes);
    }

    /** Enviar XML firmado como String (se convierte a UTF-8 bytes). */
    public RespuestaSolicitud enviarFacturaFirmadaTxt(String xmlFirmado) throws Exception {
        byte[] xmlBytes = xmlFirmado.getBytes(StandardCharsets.UTF_8);
        return enviarFacturaFirmada(xmlBytes);
    }

    /** Consultar autorización con clave de acceso. */
    public RespuestaComprobante consultarAutorizacion(String claveAcceso) throws Exception {
        URL wsdlURL = new URL(autorizacionWsdl());
        QName qname = new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService");

        AutorizacionComprobantesOfflineService service =
                new AutorizacionComprobantesOfflineService(wsdlURL, qname);
        AutorizacionComprobantesOffline port =
                service.getAutorizacionComprobantesOfflinePort();
        applyTimeouts(port);

        return port.autorizacionComprobante(claveAcceso);
    }

    /** Polling: reintenta autorización hasta que haya resultados o se agoten intentos. */
    public RespuestaComprobante consultarAutorizacionConEspera(String xmlFirmado,
                                                               Function<String, RespuestaComprobante> consultaAutorizacionFn,
                                                               int maxIntentos,
                                                               long sleepMillis) throws Exception {
        String clave = extraerClaveAcceso(xmlFirmado).trim();
        RespuestaComprobante ultimo = null;

        for (int i = 1; i <= maxIntentos; i++) {
            ultimo = consultaAutorizacionFn.apply(clave);

            String num = (ultimo.getNumeroComprobantes() == null) ? "0" : ultimo.getNumeroComprobantes().trim();
            int n;
            try { n = Integer.parseInt(num); } catch (NumberFormatException e) { n = 0; }

            if (n > 0 && ultimo.getAutorizaciones() != null) {
                return ultimo;
            }
            Thread.sleep(sleepMillis);
        }
        return ultimo; // posiblemente n=0
    }

    /** Extrae <claveAcceso> del XML. */
    private String extraerClaveAcceso(String xml) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        org.w3c.dom.Document doc = dbf.newDocumentBuilder()
                .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        org.w3c.dom.NodeList list = doc.getElementsByTagName("claveAcceso");
        if (list.getLength() == 0) throw new IllegalStateException("No se encontró <claveAcceso> en el XML");
        return list.item(0).getTextContent().replaceAll("\\s+", "");
    }

    /** Helper: fija ambiente leyendo <infoTributaria><ambiente> del XML firmado. */
    public void setAmbienteFromXml(String xmlFirmado) {
        try {
            javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            org.w3c.dom.Document doc = dbf.newDocumentBuilder()
                    .parse(new org.xml.sax.InputSource(new java.io.StringReader(xmlFirmado)));
            org.w3c.dom.NodeList list = doc.getElementsByTagName("ambiente");
            if (list.getLength() > 0) {
                int amb = Integer.parseInt(list.item(0).getTextContent().trim());
                setAmbiente(amb == 2 ? 2 : 1);
            }
        } catch (Exception ignore) {
            // si falla, conserva el ambiente actual (por defecto PRUEBAS)
        }
    }
}
