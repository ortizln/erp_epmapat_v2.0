package com.erp.sri.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesOffline;

@Service
public class ComprobanteService {
    private final RecepcionComprobantesOffline recepcionClient;

    @Autowired
    public ComprobanteService(RecepcionComprobantesOffline recepcionClient) {
        this.recepcionClient = recepcionClient;
    }

    public RespuestaSolicitud enviarComprobante(String xmlComprobante) {
        try {
            // Validar el XML antes de enviar si es necesario

            // Enviar el comprobante al SRI
            RespuestaSolicitud respuesta = recepcionClient.validarComprobante(xmlComprobante);

            // Procesar la respuesta
            return respuesta;
        } catch (Exception e) {
            // Manejar errores
            throw new RuntimeException("Error al enviar comprobante al SRI", e);
        }
    }

    // Método adicional para leer archivos XML desde disco
    public String leerArchivoXml(String rutaArchivo) {
        try {
            return new String(Files.readAllBytes(Paths.get(rutaArchivo)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo XML", e);
        }
    }
}
