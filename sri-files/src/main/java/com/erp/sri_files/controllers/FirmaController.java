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
import org.springframework.web.bind.annotation.CrossOrigin;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/singsend")
@CrossOrigin(origins = "*")
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
            path = "/enviar"
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


    // ===================== 3) SOLO FIRMAR → XML =====================
    @PostMapping(
            path = "/factura/firmar/upload.xml"
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
                : firmaService.firmarFactura(xmlPlano, mf);
    }

    // ===================== 4) FIRMAR Y ENVIAR EN UNO SOLO =====================
    @PostMapping(
            path = "/factura/xml"
    )
    public ResponseEntity<?> firmarYEnviarFacturaUloadXml(
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
                    : firmaService.firmarFactura(xmlPlano, mf);

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
                        "autorizacion", autorizacion
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
            path = "/factura/string"
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
                    : firmaService.firmarFactura(xmlPlano, mf);

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
                        "autorizacion", autorizacion
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

    // ===================== 6) FIRMAR Y ENVIAR RETENCIÓN (solo retorna XML autorizado) =====================
    @PostMapping(
            path = "/retencion",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<?> firmarYEnviarRetencion(
            @RequestPart("xml") MultipartFile xmlFile,
            @RequestParam(value = "modo", required = false, defaultValue = "XADES_BES") String modo,
            @RequestParam(value = "definirId", required = false, defaultValue = "1") Long definirId,
            @RequestParam(value = "ambiente", required = false) Integer ambienteForzado,
            @RequestParam(value = "download", required = false, defaultValue = "false") boolean download // si quieres forzar descarga
    ) {
        try {
            // 1) XML en String (limpiando BOM por si acaso)
            String xmlPlano = toUtf8String(xmlFile);

            // 2) Modo firma
            ModoFirma mf = "XMLDSIG".equalsIgnoreCase(modo) ? ModoFirma.XMLDSIG : ModoFirma.XADES_BES;

            // 3) Firmar usando el definirId indicado
            String xmlFirmado = firmaService.firmarFactura(xmlPlano, mf);

            // 2) Ambiente
            if (ambienteForzado != null) {
                envioComprobantesWs.setAmbiente(ambienteForzado == 2 ? 2 : 1);
            } else {
                envioComprobantesWs.setAmbienteFromXml(xmlFirmado);
            }


            // 5) Enviar a recepción
            RespuestaSolicitud recepcion = envioComprobantesWs.enviarFacturaFirmadaTxt(xmlFirmado);

            // 6) Si recepción NO fue RECIBIDA -> 400 con errores
            if (!"RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "estado", recepcion.getEstado(),
                        "errores", resumenErroresRecepcion(recepcion)
                ));
            }

            // 7) Polling de autorización
            RespuestaComprobante rc = envioComprobantesWs.consultarAutorizacionConEspera(
                    xmlFirmado,
                    clave -> {
                        try { return envioComprobantesWs.consultarAutorizacion(clave); }
                        catch (Exception e) { throw new RuntimeException(e); }
                    },
                    10, 4000
            );

            // 8) Extraer solo el XML autorizado del SRI
            String xmlAutorizado = extraerXmlAutorizado(rc);
            if (xmlAutorizado != null) {
                if (download) {
                    // respuesta como adjunto para que el navegador descargue
                    String fileName = "retencion-" + System.currentTimeMillis() + ".xml";
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_XML)
                            .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                            .body(xmlAutorizado);
                }
                // o solo el XML (texto) con application/xml
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_XML)
                        .body(xmlAutorizado);
            }

            // 9) Aún no autorizado
            return ResponseEntity.status(202).body(Map.of(
                    "estado", "PENDIENTE",
                    "detalle", "La autorización aún no está disponible."
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error al firmar/enviar comprobante de retención",
                    "detalle", e.getMessage()
            ));
        }
    }


    // ===================== 7) FIRMAR Y ENVIAR RECIBIENDO XML EN STRING =====================
@PostMapping(
        path = "/factura"
)
public ResponseEntity<?> firmarYEnviarFactura(
        @RequestBody String xmlPlano,
        @RequestParam(value = "modo", required = false, defaultValue = "XADES_BES") String modo,
        @RequestParam(value = "definirId", required = false, defaultValue = "1") Long definirId,
        @RequestParam(value = "ambiente", required = false) Integer ambienteForzado
) {
    try {
        // Limpia BOM si existe (importante para parseo/firma)
        if (xmlPlano != null && !xmlPlano.isEmpty() && xmlPlano.charAt(0) == '\uFEFF') {
            xmlPlano = xmlPlano.substring(1);
        }

        // 1) Elegir modo de firma
        ModoFirma mf = "XMLDSIG".equalsIgnoreCase(modo) ? ModoFirma.XMLDSIG : ModoFirma.XADES_BES;

        // 2) Firmar (usa definirId correcto)
        String xmlFirmado = firmaService.firmarFactura(xmlPlano, mf);

        // 2) Ambiente
        if (ambienteForzado != null) {
            envioComprobantesWs.setAmbiente(ambienteForzado == 2 ? 2 : 1);
        } else {
            envioComprobantesWs.setAmbienteFromXml(xmlFirmado);
        }

        // 4) Enviar a recepción
        RespuestaSolicitud recepcion = envioComprobantesWs.enviarFacturaFirmadaTxt(xmlFirmado);

        // Si NO fue recibida, devolvemos error resumido (JSON)
        if (!"RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "estado", recepcion.getEstado(),
                    "errores", resumenErroresRecepcion(recepcion)
            ));
        }

        // 5) Polling/consulta de autorización
        RespuestaComprobante rc = envioComprobantesWs.consultarAutorizacionConEspera(
                xmlFirmado,
                clave -> {
                    try { return envioComprobantesWs.consultarAutorizacion(clave); }
                    catch (Exception e) { throw new RuntimeException(e); }
                },
                10,   // intentos
                4000  // ms entre intentos
        );

        // 6) Extraer el XML autorizado (si existe)
        String xmlAutorizado = extraerXmlAutorizado(rc);
        if (xmlAutorizado != null) {
            // ✅ SOLO devolvemos el XML autorizado
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xmlAutorizado);
        }

        // Si no hay autorización aún, devuelve 202 con mensaje breve
        return ResponseEntity.status(202).body(Map.of(
                "estado", "PENDIENTE",
                "detalle", "La autorización aún no está disponible."
        ));

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(Map.of(
                "error", "Error al firmar/enviar comprobante",
                "detalle", e.getMessage()
        ));
    }
}

    /** Toma el primer <autorizacion> con <estado>AUTORIZADO</estado> y devuelve su <comprobante> (XML) o null. */
    private static String extraerXmlAutorizado(ec.gob.sri.ws.autorizacion.RespuestaComprobante rc) {
        if (rc == null || rc.getAutorizaciones() == null || rc.getAutorizaciones().getAutorizacion() == null)
            return null;

        for (var aut : rc.getAutorizaciones().getAutorizacion()) {
            if (aut != null && "AUTORIZADO".equalsIgnoreCase(aut.getEstado())) {
                // OJO: en algunos casos el SRI devuelve el XML dentro de CDATA; aquí lo retornamos tal cual
                return aut.getComprobante();
            }
        }
        return null;
    }

    /** Convierte los detalles de errores de recepción en una lista simple de mensajes. */
    private static List<String> resumenErroresRecepcion(ec.gob.sri.ws.recepcion.RespuestaSolicitud r) {
        List<String> out = new ArrayList<>();
        if (r != null && r.getComprobantes() != null && r.getComprobantes().getComprobante() != null) {
            r.getComprobantes().getComprobante().forEach(c -> {
                if (c.getMensajes() != null && c.getMensajes().getMensaje() != null) {
                    c.getMensajes().getMensaje().forEach(m -> {
                        StringBuilder sb = new StringBuilder();
                        if (m.getMensaje() != null) sb.append(m.getMensaje());
                        if (m.getInformacionAdicional() != null) sb.append(" - ").append(m.getInformacionAdicional());
                        out.add(sb.toString());
                    });
                }
            });
        }
        if (out.isEmpty() && r != null && r.getEstado() != null) {
            out.add("Estado: " + r.getEstado());
        }
        return out;
    }



    // ===== Manejador de errores comunes =====
    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<String> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
