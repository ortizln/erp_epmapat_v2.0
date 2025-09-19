package com.erp.sri_files.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;


@Service
public class SriEnvio {
    private static final String WSDL_RECEPCION = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
    private static final String WSDL_AUTORIZACION = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

    public static void enviarFacturaFirmada(String rutaXmlFirmado) throws Exception {
        // 1. Leer archivo firmado
        byte[] xmlBytes = Files.readAllBytes(new File(rutaXmlFirmado).toPath());


    }
}
