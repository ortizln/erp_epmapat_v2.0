package com.erp.sri_files.controllers;

import com.erp.sri_files.services.EnvioComprobantesWs;
import com.erp.sri_files.utils.FirmaComprobantesService;
import com.erp.sri_files.utils.FirmaComprobantesService.ModoFirma;
import ec.gob.sri.ws.autorizacion.RespuestaComprobante;
import ec.gob.sri.ws.recepcion.RespuestaSolicitud;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/firmas")
@CrossOrigin("*")
public class FirmaController {

    private final EnvioComprobantesWs envioComprobantesWs;
    private final FirmaComprobantesService firmaService;

    public FirmaController(EnvioComprobantesWs envioComprobantesWs,
                           FirmaComprobantesService firmaService) {
        this.envioComprobantesWs = envioComprobantesWs;
        this.firmaService = firmaService;
    }

    // ===== Helpers / DTOs =====
    public record FirmarResp(String xmlFirmado) {}

    private static String toUtf8String(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Archivo XML vacío o no enviado (parte 'xml').");
        String s = new String(file.getBytes(), StandardCharsets.UTF_8);
        // elimina BOM si existe (importante para firmas)
        if (!s.isEmpty() && s.charAt(0) == '\uFEFF') s = s.substring(1);
        return s;
    }

    private static String extraerTag(String xml, String tagName) throws Exception {
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        var doc = dbf.newDocumentBuilder()
                .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        var list = doc.getElementsByTagName(tagName);
        if (list.getLength() == 0) return null;
        return list.item(0).getTextContent().replaceAll("\\s+", "");
    }

    // ===================== 1) ENVIAR XML YA FIRMADO =====================
    @PostMapping(
            path = "/enviar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> enviarComprobante(@RequestParam("file") MultipartFile file,
                                               @RequestParam(value = "ambiente", required = false) Integer ambienteForzado) {
        try {
            // 1) Leer el XML firmado (String y bytes)
            String xmlFirmado = toUtf8String(file);
            byte[] xmlBytes = xmlFirmado.getBytes(StandardCharsets.UTF_8);

            // 2) Ambiente: si te lo pasan, lo usas; sino lo deduces del XML
            if (ambienteForzado != null) {
                envioComprobantesWs.setAmbiente(ambienteForzado == 2 ? 2 : 1);
            } else {
                envioComprobantesWs.setAmbienteFromXml(xmlFirmado);
            }

            // 3) Recepción
            RespuestaSolicitud respuesta = envioComprobantesWs.enviarFacturaFirmada(xmlBytes);

            // 4) Si RECIBIDA, intenta autorización con polling
            if ("RECIBIDA".equalsIgnoreCase(respuesta.getEstado())) {
                RespuestaComprobante autorizacion = envioComprobantesWs.consultarAutorizacionConEspera(
                        xmlFirmado,
                        clave -> {
                            try { return envioComprobantesWs.consultarAutorizacion(clave); }
                            catch (Exception e) { throw new RuntimeException(e); }
                        },
                        10,   // intentos
                        4000  // ms entre intentos
                );
                return ResponseEntity.ok(autorizacion);
            } else {
                return ResponseEntity.badRequest().body(respuesta);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error al enviar comprobante firmado",
                    "detalle", e.getMessage()
            ));
        }
    }

    // ===================== 2) SOLO FIRMAR → JSON =====================
    @PostMapping(
            path = "/factura/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public FirmarResp firmarFacturaUploadJson(
            @RequestPart("xml") MultipartFile xmlFile,
            @RequestParam(value = "modo", required = false, defaultValue = "XADES_BES") String modo,
            @RequestParam(value = "definirId", required = false, defaultValue = "1") Long definirId
            // @RequestParam(value = "password", required = false) String password // <- agrega si tu servicio lo necesita
    ) throws Exception {

        String xmlPlano = toUtf8String(xmlFile);
        ModoFirma mf = "XMLDSIG".equalsIgnoreCase(modo) ? ModoFirma.XMLDSIG : ModoFirma.XADES_BES;

        String firmado = (definirId == null)
                ? firmaService.firmarFactura(xmlPlano, mf)
                : firmaService.firmarFactura(xmlPlano, definirId, mf);

        return new FirmarResp(firmado);
    }

    // ===================== 3) SOLO FIRMAR → XML =====================
    @PostMapping(
            path = "/factura/firmar/upload.xml",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public String firmarFacturaUploadXml(
            @RequestPart("xml") MultipartFile xmlFile,
            @RequestParam(value = "modo", required = false, defaultValue = "XADES_BES") String modo,
            @RequestParam(value = "definirId", required = false) Long definirId
            // @RequestParam(value = "password", required = false) String password // <- agrega si tu servicio lo necesita
    ) throws Exception {

        String xmlPlano = toUtf8String(xmlFile);
        ModoFirma mf = "XMLDSIG".equalsIgnoreCase(modo) ? ModoFirma.XMLDSIG : ModoFirma.XADES_BES;

        return (definirId == null)
                ? firmaService.firmarFactura(xmlPlano, mf)
                : firmaService.firmarFactura(xmlPlano, definirId, mf);
    }

    // ===================== 4) FIRMAR Y ENVIAR EN UNO SOLO =====================
    @PostMapping(
            path = "/factura/upload.xml",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> firmarYEnviarFacturaUploadXml(
            @RequestPart("xml") MultipartFile xmlFile,
            @RequestParam(value = "modo", required = false, defaultValue = "XADES_BES") String modo,
            @RequestParam(value = "definirId", required = false, defaultValue = "1") Long definirId,
            @RequestParam(value = "ambiente", required = false) Integer ambienteForzado
            // @RequestParam(value = "password", required = false) String password
    ) {
        try {
            // 1) Convertir el archivo a String
            String xmlPlano = toUtf8String(xmlFile);
            // 2) Elegir modo de firma
            ModoFirma mf = "XMLDSIG".equalsIgnoreCase(modo) ? ModoFirma.XMLDSIG : ModoFirma.XADES_BES;
            // 3) Firmar
            String xmlFirmado = (definirId == null)
                    ? firmaService.firmarFactura(xmlPlano, mf)
                    : firmaService.firmarFactura(xmlPlano, definirId, mf);

            // 4) Ambiente: forzado si lo pasan; si no, deducir del XML firmado
            if (ambienteForzado != null) {
                envioComprobantesWs.setAmbiente(ambienteForzado == 2 ? 2 : 1);
            } else {
                envioComprobantesWs.setAmbienteFromXml(xmlFirmado);
            }

            // 5) Enviar a recepción
            RespuestaSolicitud recepcion = envioComprobantesWs.enviarFacturaFirmadaTxt(xmlFirmado);

            // 6) Si RECIBIDA, aplicar polling hasta autorización
            if ("RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
                RespuestaComprobante autorizacion = envioComprobantesWs.consultarAutorizacionConEspera(
                        xmlFirmado,
                        clave -> {
                            try { return envioComprobantesWs.consultarAutorizacion(clave); }
                            catch (Exception e) { throw new RuntimeException(e); }
                        },
                        10,   // intentos
                        4000  // ms
                );
                return ResponseEntity.ok(Map.of(
                        "recepcion", recepcion,
                        "autorizacion", autorizacion,
                        "xmlFirmado", xmlFirmado
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "recepcion", recepcion,
                        "xmlFirmado", xmlFirmado
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error al firmar/enviar comprobante",
                    "detalle", e.getMessage()
            ));
        }
    }

    // ===================== 5) FIRMAR Y ENVIAR RECIBIENDO XML EN STRING =====================
    @PostMapping(
            path = "/factura/send-xml",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_PLAIN_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> firmarYEnviarFacturaXml(
            @RequestBody String xmlPlano,
            @RequestParam(value = "modo", required = false, defaultValue = "XADES_BES") String modo,
            @RequestParam(value = "definirId", required = false, defaultValue = "1") Long definirId,
            @RequestParam(value = "ambiente", required = false) Integer ambienteForzado
            // @RequestParam(value = "password", required = false) String password
    ) {
        try {
            // Limpia BOM si existe (muy importante para evitar invalidar la firma)
            if (!xmlPlano.isEmpty() && xmlPlano.charAt(0) == '\uFEFF') {
                xmlPlano = xmlPlano.substring(1);
            }

            // 1) Elegir modo de firma
            ModoFirma mf = "XMLDSIG".equalsIgnoreCase(modo) ? ModoFirma.XMLDSIG : ModoFirma.XADES_BES;

            // 2) Firmar
            String xmlFirmado = (definirId == null)
                    ? firmaService.firmarFactura(xmlPlano, mf)
                    : firmaService.firmarFactura(xmlPlano, definirId, mf);

            // 3) Ambiente: forzado si lo pasan; si no, deducir del XML firmado
            if (ambienteForzado != null) {
                envioComprobantesWs.setAmbiente(ambienteForzado == 2 ? 2 : 1);
            } else {
                envioComprobantesWs.setAmbienteFromXml(xmlFirmado);
            }

            // 4) Enviar a recepción
            RespuestaSolicitud recepcion = envioComprobantesWs.enviarFacturaFirmadaTxt(xmlFirmado);

            // 5) Si RECIBIDA, aplicar polling hasta autorización
            if ("RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
                RespuestaComprobante autorizacion = envioComprobantesWs.consultarAutorizacionConEspera(
                        xmlFirmado,
                        clave -> {
                            try { return envioComprobantesWs.consultarAutorizacion(clave); }
                            catch (Exception e) { throw new RuntimeException(e); }
                        },
                        10,   // intentos
                        4000  // ms
                );
                return ResponseEntity.ok(Map.of(
                        "recepcion", recepcion,
                        "autorizacion", autorizacion,
                        "xmlFirmado", xmlFirmado
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "recepcion", recepcion,
                        "xmlFirmado", xmlFirmado
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error al firmar/enviar comprobante",
                    "detalle", e.getMessage()
            ));
        }
    }


    // ===== Manejador de errores comunes =====
    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<String> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
