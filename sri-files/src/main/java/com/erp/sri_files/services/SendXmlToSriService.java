package com.erp.sri_files.services;

import ec.gob.sri.ws.autorizacion.AutorizacionComprobantesOffline;
import ec.gob.sri.ws.autorizacion.AutorizacionComprobantesOfflineService;
import ec.gob.sri.ws.autorizacion.RespuestaComprobante;
import ec.gob.sri.ws.recepcion.RecepcionComprobantesOffline;
import ec.gob.sri.ws.recepcion.RecepcionComprobantesOfflineService;
import ec.gob.sri.ws.recepcion.RespuestaSolicitud;
import jakarta.annotation.PostConstruct;
import jakarta.xml.ws.BindingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

@Service
public class SendXmlToSriService {

    /** 1 = PRUEBAS, 2 = PRODUCCIÓN. */
    private int ambiente = 2;

    // --- CONFIGURABLES ---
    // WSDL locales (classpath). Por defecto apuntan al directorio resources/wsdl
    @Value("${sri.wsdl.local.recepcion:wsdl/RecepcionComprobantesOffline.wsdl}")
    private String wsdlLocalRecepcion;

    @Value("${sri.wsdl.local.autorizacion:wsdl/AutorizacionComprobantesOffline.wsdl}")
    private String wsdlLocalAutorizacion;

    // Endpoints reales (SIN ?wsdl), se eligen según ambiente
    @Value("${sri.endpoint.recepcion.pruebas:https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline}")
    private String epRecepcionPruebas;

    @Value("${sri.endpoint.autorizacion.pruebas:https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline}")
    private String epAutorizacionPruebas;

    @Value("${sri.endpoint.recepcion.produccion:https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline}")
    private String epRecepcionProd;

    @Value("${sri.endpoint.autorizacion.produccion:https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline}")
    private String epAutorizacionProd;

    // Permite configurar ambiente por properties (sri.ambiente=1|2)
    @Value("${sri.ambiente:1}")
    public void setAmbiente(int ambiente) { this.ambiente = (ambiente == 2) ? 2 : 1; }
    public int getAmbiente() { return ambiente; }

    @PostConstruct
    void init() {
        // Fuerza TLS 1.2 (por compatibilidad con servidores SRI)
        System.setProperty("https.protocols", "TLSv1.2");
    }

    // ===================== Helpers base =====================

    private URL classpathUrl(String resourcePath) {
        var url = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        if (url == null) throw new IllegalStateException("No se encontró recurso en classpath: " + resourcePath);
        return url;
    }

    private void applyTimeouts(Object port) {
        Map<String, Object> ctx = ((BindingProvider) port).getRequestContext();
        ctx.put("com.sun.xml.ws.connect.timeout", 15000); // 15s conexión
        ctx.put("com.sun.xml.ws.request.timeout", 30000); // 30s lectura
    }

    private void overrideEndpoint(Object port, boolean autorizacion) {
        String endpoint = (ambiente == 2)
                ? (autorizacion ? epAutorizacionProd : epRecepcionProd)
                : (autorizacion ? epAutorizacionPruebas : epRecepcionPruebas);
        ((BindingProvider) port).getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
    }

    // ===================== Recepción =====================

    /** Enviar XML firmado como bytes. */
    public RespuestaSolicitud enviarFacturaFirmada(byte[] xmlBytes) throws Exception {
        URL wsdlURL = classpathUrl(wsdlLocalRecepcion);
        QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");

        RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService(wsdlURL, qname);
        RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();

        applyTimeouts(port);
        overrideEndpoint(port, /* autorizacion */ false);

        return port.validarComprobante(xmlBytes);
    }

    /** Enviar XML firmado como String. */
    public RespuestaSolicitud enviarFacturaFirmadaTxt(String xmlFirmado) throws Exception {
        return enviarFacturaFirmada(xmlFirmado.getBytes(StandardCharsets.UTF_8));
    }

    // ===================== Autorización =====================

    /** Consultar autorización con clave de acceso. */
    public RespuestaComprobante consultarAutorizacion(String claveAcceso) throws Exception {
        URL wsdlURL = classpathUrl(wsdlLocalAutorizacion);
        QName qname = new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService");

        AutorizacionComprobantesOfflineService service =
                new AutorizacionComprobantesOfflineService(wsdlURL, qname);
        AutorizacionComprobantesOffline port =
                service.getAutorizacionComprobantesOfflinePort();

        applyTimeouts(port);
        overrideEndpoint(port, /* autorizacion */ true);

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

    /** Helper: fija ambiente leyendo <infoTributaria><ambiente> del XML firmado. */
    public void setAmbienteFromXml(String xmlFirmado) {
        try {
            var dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            var doc = dbf.newDocumentBuilder()
                    .parse(new org.xml.sax.InputSource(new java.io.StringReader(xmlFirmado)));
            var list = doc.getElementsByTagName("ambiente");
            if (list.getLength() > 0) {
                int amb = Integer.parseInt(list.item(0).getTextContent().trim());
                setAmbiente(amb == 2 ? 2 : 1);
            }
        } catch (Exception ignore) {
            // si falla, conserva el ambiente actual
        }
    }

    /** Extrae <claveAcceso> del XML firmado. */
    private String extraerClaveAcceso(String xml) throws Exception {
        var dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        var doc = dbf.newDocumentBuilder()
                .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        var list = doc.getElementsByTagName("claveAcceso");
        if (list.getLength() == 0) throw new IllegalStateException("No se encontró <claveAcceso> en el XML");
        return list.item(0).getTextContent().replaceAll("\\s+", "");
    }
}
