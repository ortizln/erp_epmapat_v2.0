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

    /** 1 = PRUEBAS, 2 = PRODUCCIÓN */
    private int ambiente = 1;

    // === WSDL locales (classpath) ===
    @Value("${sri.wsdl.local.recepcion:wsdl/RecepcionComprobantesOffline.wsdl}")
    private String wsdlLocalRecepcion;

    @Value("${sri.wsdl.local.autorizacion:wsdl/AutorizacionComprobantesOffline.wsdl}")
    private String wsdlLocalAutorizacion;

    // === Endpoints reales (SIN ?wsdl) ===
    @Value("${sri.endpoint.recepcion.pruebas:https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline}")
    private String epRecepcionPruebas;

    @Value("${sri.endpoint.autorizacion.pruebas:https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline}")
    private String epAutorizacionPruebas;

    @Value("${sri.endpoint.recepcion.produccion:https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline}")
    private String epRecepcionProd;

    @Value("${sri.endpoint.autorizacion.produccion:https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline}")
    private String epAutorizacionProd;

    // Ambiente por properties (1|2)
    @Value("${sri.ambiente:1}")
    public void setAmbiente(int ambiente) { this.ambiente = (ambiente == 2) ? 2 : 1; }
    public int getAmbiente() { return ambiente; }

    @PostConstruct
    void init() {
        // Fuerza TLS 1.2 (necesario para SRI)
        System.setProperty("https.protocols", "TLSv1.2");
    }

    // ======================================================
    // Helpers
    // ======================================================
    private URL classpathUrl(String resourcePath) {
        var url = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        if (url == null) throw new IllegalStateException("No se encontró recurso en classpath: " + resourcePath);
        return url;
    }

    private void applyTimeouts(Object port) {
        Map<String, Object> ctx = ((BindingProvider) port).getRequestContext();
        // Metro
        ctx.put("com.sun.xml.ws.connect.timeout", 15000);
        ctx.put("com.sun.xml.ws.request.timeout", 30000);
        // JAX-WS interno (por compatibilidad)
        ctx.put("com.sun.xml.internal.ws.connect.timeout", 15000);
        ctx.put("com.sun.xml.internal.ws.request.timeout", 30000);
        // Estándar
        ctx.put("javax.xml.ws.client.connectionTimeout", "15000");
        ctx.put("javax.xml.ws.client.receiveTimeout", "30000");
    }

    /** Aplica el endpoint real (celcer/cel), NO el WSDL local */
    private void overrideEndpoint(Object port, boolean autorizacion) {
        String endpoint = (ambiente == 2)
                ? (autorizacion ? epAutorizacionProd : epRecepcionProd)
                : (autorizacion ? epAutorizacionPruebas : epRecepcionPruebas);

        ((BindingProvider) port).getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

        System.out.println("[SRI] Ambiente=" + (ambiente == 2 ? "PRODUCCIÓN" : "PRUEBAS")
                + " | Servicio=" + (autorizacion ? "AUTORIZACIÓN" : "RECEPCIÓN")
                + " | Endpoint=" + endpoint);
    }

    private String stripBom(String s) {
        if (s == null || s.isEmpty()) return s;
        return (s.charAt(0) == '\uFEFF') ? s.substring(1) : s;
    }

    /** Extrae <claveAcceso> del XML firmado */
    private String extraerClaveAcceso(String xml) throws Exception {
        String cleaned = stripBom(xml);
        var dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        var doc = dbf.newDocumentBuilder()
                .parse(new org.xml.sax.InputSource(new java.io.StringReader(cleaned)));
        var list = doc.getElementsByTagName("claveAcceso");
        if (list.getLength() == 0) throw new IllegalStateException("No se encontró <claveAcceso> en el XML");
        return list.item(0).getTextContent().replaceAll("\\s+", "");
    }

    /** Setea ambiente leyendo <infoTributaria><ambiente> del XML firmado */
    public void setAmbienteFromXml(String xmlFirmado) {
        try {
            String xml = stripBom(xmlFirmado);
            var dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            var doc = dbf.newDocumentBuilder()
                    .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
            var list = doc.getElementsByTagName("ambiente");
            if (list.getLength() > 0) {
                int amb = Integer.parseInt(list.item(0).getTextContent().trim());
                setAmbiente(amb == 2 ? 2 : 1);
            }
        } catch (Exception ignore) {
            // conserva el ambiente actual
        }
    }

    // ======================================================
    // Recepción
    // ======================================================
    /** Enviar XML firmado como bytes a Recepción */
    public RespuestaSolicitud enviarFacturaFirmada(byte[] xmlBytes) throws Exception {
        // Construcción del Service con WSDL local
        URL wsdlURL = classpathUrl(wsdlLocalRecepcion);
        QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");

        RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService(wsdlURL, qname);
        RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();

        applyTimeouts(port);
        overrideEndpoint(port, /* autorizacion */ false);

        return port.validarComprobante(xmlBytes);
    }

    /** Enviar XML firmado como String (UTF-8) */
    public RespuestaSolicitud enviarFacturaFirmadaTxt(String xmlFirmado) throws Exception {
        String xml = stripBom(xmlFirmado);
        return enviarFacturaFirmada(xml.getBytes(StandardCharsets.UTF_8));
    }

    // ======================================================
    // Autorización
    // ======================================================
    /** Consultar autorización por clave de acceso */
    public RespuestaComprobante consultarAutorizacion(String claveAcceso) throws Exception {
        URL wsdlURL = classpathUrl(wsdlLocalAutorizacion);
        QName qname = new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService");

        AutorizacionComprobantesOfflineService service =
                new AutorizacionComprobantesOfflineService(wsdlURL, qname);
        AutorizacionComprobantesOffline port =
                service.getAutorizacionComprobantesOfflinePort();

        applyTimeouts(port);
        overrideEndpoint(port, /* autorizacion */ true);

        return port.autorizacionComprobante(claveAcceso.trim());
    }

    /**
     * Polling por autorización usando una función de consulta (infiere la clave desde el XML)
     */
    public RespuestaComprobante consultarAutorizacionConEspera(
            String xmlFirmado,
            Function<String, RespuestaComprobante> consultaAutorizacionFn,
            int maxIntentos,
            long sleepMillis) throws Exception {

        String clave = extraerClaveAcceso(xmlFirmado).trim();
        RespuestaComprobante ultimo = null;

        for (int i = 1; i <= maxIntentos; i++) {
            ultimo = consultaAutorizacionFn.apply(clave);

            String numStr = (ultimo.getNumeroComprobantes() == null) ? "0" : ultimo.getNumeroComprobantes().trim();
            int n;
            try { n = Integer.parseInt(numStr); } catch (NumberFormatException e) { n = 0; }

            if (n > 0 && ultimo.getAutorizaciones() != null) {
                return ultimo; // ya hay respuesta útil
            }
            Thread.sleep(sleepMillis);
        }
        return ultimo; // podría venir sin autorizaciones (pendiente)
    }

    /**
     * Polling por autorización **directo** con clave (más simple de usar)
     */
    public RespuestaComprobante consultarAutorizacionConEsperaPorClave(
            String claveAcceso,
            int maxAttempts,
            long initialSleepMs,
            long maxSleepMs,
            double backoffFactor,
            boolean stopOnNoAutorizado
    ) throws Exception {
        long sleep = initialSleepMs;
        RespuestaComprobante ultimo = null;

        for (int i = 1; i <= maxAttempts; i++) {

            ultimo = consultarAutorizacion(claveAcceso);

            // ¿SRI ya regresó algo?
            int n = 0;
            try { n = (ultimo.getNumeroComprobantes() == null) ? 0 : Integer.parseInt(ultimo.getNumeroComprobantes().trim()); }
            catch (NumberFormatException ignore) {}

            if (n > 0) {
                var a = getFirstAuth(ultimo);
                String estado = (a == null ? "" : s(a.getEstado()));

                // Corte inmediato en estados definitivos
                if ("AUTORIZADO".equalsIgnoreCase(estado)) {
                    return ultimo; // listo: úsalo y extrae el XML
                }
                if ("NO AUTORIZADO".equalsIgnoreCase(estado) && stopOnNoAutorizado) {
                    return ultimo; // devolver diagnóstico (mensajes/identificadores)
                }
            }

            // Espera con backoff + jitter antes del próximo intento
            long jitter = (long) (Math.random() * 400); // 0-400ms
            Thread.sleep(Math.min(sleep, maxSleepMs) + jitter);
            sleep = (long) Math.max(sleep * backoffFactor, sleep + 1000); // crece suavemente
        }
        return ultimo; // puede llegar aún PENDIENTE: decide en el controlador qué responder
    }
    // ---- Helper: obtener la primera autorización “útil” ----
    private ec.gob.sri.ws.autorizacion.Autorizacion getFirstAuth(RespuestaComprobante rc) {
        if (rc == null || rc.getAutorizaciones() == null || rc.getAutorizaciones().getAutorizacion() == null) return null;
        return rc.getAutorizaciones().getAutorizacion().stream().findFirst().orElse(null);
    }

    private static String s(String v){ return v == null ? "" : v.trim(); }
    // ---- Helper: extraer XML AUTORIZADO (CDATA de <comprobante>) ----
    public String extraerXmlAutorizado(RespuestaComprobante rc) {
        var a = getFirstAuth(rc);
        if (a == null) return null;
        if (!"AUTORIZADO".equalsIgnoreCase(s(a.getEstado()))) return null;
        var comp = a.getComprobante();
        if (comp == null) return null;
        String xml = comp.trim();
        // algunos servicios retornan con CDATA; limpiar
        if (xml.startsWith("<![CDATA[")) xml = xml.substring(9);
        if (xml.endsWith("]]>")) xml = xml.substring(0, xml.length()-3);
        return xml.trim();
    }
}
