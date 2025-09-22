package com.erp.sri_files.services;
import ec.gob.sri.ws.recepcion.Comprobante;
import ec.gob.sri.ws.recepcion.Mensaje;
import ec.gob.sri.ws.recepcion.RecepcionComprobantesOffline;
import ec.gob.sri.ws.recepcion.RespuestaSolicitud;
import jakarta.xml.ws.WebServiceException;
import org.springframework.stereotype.Service;
import recepcion.ws.sri.gob.ec.RecepcionComprobantesOfflineService;

import javax.xml.namespace.QName;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class EnvioComprobantesWs {
    private static RecepcionComprobantesOfflineService service;
    private static final String VERSION = "1.0.0";
    public static final String ESTADO_RECIBIDA = "RECIBIDA";
    public static final String ESTADO_DEVUELTA = "DEVUELTA";

    public EnvioComprobantesWs(String wsdlLocation) throws MalformedURLException, WebServiceException {
        URL url = new URL(wsdlLocation);
        QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
        service = new RecepcionComprobantesOfflineService(url, qname);
    }

    public static final Object webService(String wsdlLocation) {
        try {
            QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
            URL url = new URL(wsdlLocation);
            service = new RecepcionComprobantesOfflineService(url, qname);
            return null;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return ex;
        } catch (WebServiceException ws) {
            ws.printStackTrace();
            return ws;
        }
    }


    public RespuestaSolicitud enviarComprobante(String ruc, File xmlFile, String tipoComprobante, String versionXsd) {
        RespuestaSolicitud response = new RespuestaSolicitud();

        try {
            // Obtener el port del servicio
            RecepcionComprobantesOffline port = (RecepcionComprobantesOffline) service.getRecepcionComprobantesOfflinePort();

            // Leer archivo XML a bytes
            Path path = xmlFile.toPath();
            byte[] xmlBytes = Files.readAllBytes(path);

            // Enviar al SRI
            response = port.validarComprobante(xmlBytes);

        } catch (Exception e) {
            e.printStackTrace();
            response.setEstado("ERROR: " + e.getMessage());
        }

        return response;
    }

    public RespuestaSolicitud enviarComprobanteLotes(String ruc, byte[] xml, String tipoComprobante, String versionXsd) {
        RespuestaSolicitud response = null;

        try {
            RecepcionComprobantesOffline port = (RecepcionComprobantesOffline) service.getRecepcionComprobantesOfflinePort();
            response = port.validarComprobante(xml);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response = new RespuestaSolicitud();
            response.setEstado(e.getMessage());
            return response;
        }
    }


    public static RespuestaSolicitud obtenerRespuestaEnvio(File archivo, String ruc, String tipoComprobante, String claveDeAcceso, String urlWsdl) {
        RespuestaSolicitud respuesta = new RespuestaSolicitud();
        EnvioComprobantesWs cliente = null;

        try {
            cliente = new EnvioComprobantesWs(urlWsdl);
        } catch (Exception ex) {
            ex.printStackTrace();
            respuesta.setEstado(ex.getMessage());
            return respuesta;
        }

        respuesta = cliente.enviarComprobante(ruc, archivo, tipoComprobante, "1.0.0");
        return respuesta;
    }

    public static RespuestaSolicitud obtenerRespuestaEnvio1(File archivo, String ruc, String tipoComprobante, String claveDeAcceso, String urlWsdl) {
        RespuestaSolicitud respuesta = new RespuestaSolicitud();
        EnvioComprobantesWs cliente = null;

        try {
            cliente = new EnvioComprobantesWs(urlWsdl);
        } catch (Exception ex) {
            ex.printStackTrace();
            respuesta.setEstado(ex.getMessage());
            return respuesta;
        }

        respuesta = cliente.enviarComprobante(ruc, archivo, tipoComprobante, "1.0.0");
        return respuesta;
    }

    public static String obtenerMensajeRespuesta(RespuestaSolicitud respuesta) {
        StringBuilder mensajeDesplegable = new StringBuilder();
        if (respuesta.getEstado().equals("DEVUELTA")) {
            RespuestaSolicitud.Comprobantes comprobantes = respuesta.getComprobantes();

            for(Comprobante comp : comprobantes.getComprobante()) {
                mensajeDesplegable.append(comp.getClaveAcceso());
                mensajeDesplegable.append("\n");

                for(Mensaje m : comp.getMensajes().getMensaje()) {
                    mensajeDesplegable.append(m.getMensaje()).append(" :\n");
                    mensajeDesplegable.append(m.getInformacionAdicional() != null ? m.getInformacionAdicional() : "");
                    mensajeDesplegable.append("\n");
                }

                mensajeDesplegable.append("\n");
            }
        }

        return mensajeDesplegable.toString();
    }
}
