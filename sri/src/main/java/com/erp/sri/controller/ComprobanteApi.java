package com.erp.sri.controller;

import com.erp.sri.service.ComprobanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class ComprobanteApi {
    @Autowired
    private ComprobanteService comprobanteService;

    @PostMapping("/enviar")
    public ResponseEntity<?> enviarComprobante(@RequestBody String xmlComprobante) {
        try {
            RespuestaSolicitud respuesta = comprobanteService.enviarComprobante(xmlComprobante);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/enviar-archivo")
    public ResponseEntity<?> enviarComprobanteDesdeArchivo(@RequestBody String rutaArchivo) {
        try {
            String xmlComprobante = comprobanteService.leerArchivoXml(rutaArchivo);
            RespuestaSolicitud respuesta = comprobanteService.enviarComprobante(xmlComprobante);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
