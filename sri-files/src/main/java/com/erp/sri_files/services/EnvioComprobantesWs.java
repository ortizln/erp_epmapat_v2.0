package com.erp.sri_files.services;

import ec.gob.sri.ws.autorizacion.AutorizacionComprobantesOffline;
import ec.gob.sri.ws.autorizacion.AutorizacionComprobantesOfflineService;
import ec.gob.sri.ws.autorizacion.RespuestaComprobante;
import ec.gob.sri.ws.recepcion.RecepcionComprobantesOffline;
import ec.gob.sri.ws.recepcion.RecepcionComprobantesOfflineService;
import ec.gob.sri.ws.recepcion.RespuestaSolicitud;
import jakarta.xml.ws.BindingProvider;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
@Service
public class EnvioComprobantesWs {

    private static final String WSDL_RECEPCION =
            "classpath:wsdl/RecepcionComprobantesOffline.wsdl";
    private static final String WSDL_AUTORIZACION =
            "classpath:wsdl/AutorizacionComprobantesOffline.wsdl";

    // Si prefieres URL absolutas:
    private URL wsdlUrl(String classpathLocation) throws Exception {
        return Thread.currentThread()
                .getContextClassLoader()
                .getResource(classpathLocation.replace("classpath:", ""));
    }

    private void applyTimeouts(Object port) {
        // Metro usa estas properties para timeouts (en ms)
        Map<String, Object> ctx = ((BindingProvider) port).getRequestContext();
        ctx.put("com.sun.xml.ws.connect.timeout", 15000);
        ctx.put("com.sun.xml.ws.request.timeout", 30000);
    }

    public ec.gob.sri.ws.recepcion.RespuestaSolicitud enviarFactura_Firmada(String rutaXml) throws Exception {
        URL wsdlURL = wsdlUrl(WSDL_RECEPCION);
        QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");

        RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService(wsdlURL, qname);
        RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();
        applyTimeouts(port);

        byte[] xmlBytes = Files.readAllBytes(Paths.get(rutaXml));
        return port.validarComprobante(xmlBytes);
    }

    public RespuestaSolicitud enviarFacturaFirmada(byte[] xmlBytes) throws Exception {
        URL wsdlURL = wsdlUrl(WSDL_RECEPCION);
        QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");

        RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService(wsdlURL, qname);
        RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();
        applyTimeouts(port);

        return port.validarComprobante(xmlBytes);
    }

    public RespuestaComprobante consultarAutorizacion(String claveAcceso) throws Exception {
        URL wsdlURL = wsdlUrl(WSDL_AUTORIZACION);
        QName qname = new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService");

        AutorizacionComprobantesOfflineService service =
                new AutorizacionComprobantesOfflineService(wsdlURL, qname);
        AutorizacionComprobantesOffline port =
                service.getAutorizacionComprobantesOfflinePort();
        applyTimeouts(port);

        return port.autorizacionComprobante(claveAcceso);
    }
}
