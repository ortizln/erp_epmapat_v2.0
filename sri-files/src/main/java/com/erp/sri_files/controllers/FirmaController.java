package com.erp.sri_files.controllers;

import com.erp.sri_files.config.AESUtil;
import com.erp.sri_files.dto.SendMailRequest;
import com.erp.sri_files.dto.SendMailResponse;
import com.erp.sri_files.dto.TemplateMailRequest;
import com.erp.sri_files.models.Definir;
import com.erp.sri_files.models.EmailAttachment;
import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.DefinirR;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.services.*;
import com.erp.sri_files.utils.FirmaComprobantesService;
import com.erp.sri_files.utils.FirmaComprobantesService.ModoFirma;
import ec.gob.sri.ws.autorizacion.RespuestaComprobante;
import ec.gob.sri.ws.recepcion.RespuestaSolicitud;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.sf.jasperreports.repo.InputStreamResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/singsend")
@CrossOrigin(origins = "*")
public class FirmaController {

    private final EnvioComprobantesWs envioComprobantesWs;
    private final FirmaComprobantesService firmaService;
    private final RestTemplate restTemplate;

    @Autowired
    private FacturaR fecFacturaR;
    @Autowired
    private FacturaSRIService facturaSRIService;
    @Autowired
    private FacturasService facturasService;
    @Autowired
    private XmlToPdfService xmlToPdfService;
    @Autowired
    private DefinirR definirService;


    @Autowired
    private MailService mailService;


    @Value("${eureka.service-url}")
    private String eurekaServiceUrl;

    public FirmaController(EnvioComprobantesWs envioComprobantesWs,
                           FirmaComprobantesService firmaService, RestTemplate restTemplate) {
        this.envioComprobantesWs = envioComprobantesWs;
        this.firmaService = firmaService;
        this.restTemplate = restTemplate;
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

    // ===================== 8) CREAR XML FIRMAR Y ENVIAR =====================
    @GetMapping(path = "/factura_electronica")
    public ResponseEntity<?> crear_firmar_enviarFactura(@RequestParam Long idfactura) {
        try {
            String modo = "XADES_BES";
            Integer ambienteForzado = 1;

            // 1) Intentar obtener factura
            Factura factura = fecFacturaR.findByIdfactura(idfactura);
            if(factura != null && (Objects.equals(factura.getEstado(), "A") || Objects.equals(factura.getEstado(), "O"))){
                return ResponseEntity.status(201).body(Map.of(
                        "error", "No se pudo generar la factura porque ya esta fue enviada",
                        "idfactura", idfactura
                ));
            }
                if (factura == null) {
                Boolean request = facturasService.findByIdfactura(idfactura);
                if (Boolean.TRUE.equals(request)) {
                    String url = eurekaServiceUrl + ":8080/fec_factura/createFacElectro?idfactura=" + idfactura;
                    restTemplate.getForObject(url, Void.class);

                    // 🔹 Reintentos: esperar hasta que ya exista en BD
                    for (int i = 0; i < 5; i++) {
                        Thread.sleep(2000); // espera 2s
                        factura = fecFacturaR.findByIdfactura(idfactura); // 🔹 RECONSULTA aquí
                        if (factura != null) break;
                    }

                    if (factura == null) {
                        return ResponseEntity.status(404).body(Map.of(
                                "error", "No se pudo generar la factura en los reintentos",
                                "idfactura", idfactura
                        ));
                    }

                } else {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Factura no pagada o no encontrada en facturasService",
                            "idfactura", idfactura
                    ));
                }
            }
            String estadoRaw = factura.getEstado();
            String estado = normEstado(estadoRaw);

            if (!"I".equalsIgnoreCase(estado)) {
                return ResponseEntity.status(404).body(Map.of(
                        "error", "No se pudo enviar la factura: el estado debe ser 'I'",
                        "estadoActual", estado,
                        "idfactura", idfactura
                ));
            }

            // 2) Generar XML sin firmar
            String xmlPlano = facturaSRIService.generarXmlFactura(factura);

            // Limpia BOM si existe (importante para parseo/firma)
            if (xmlPlano != null && !xmlPlano.isEmpty() && xmlPlano.charAt(0) == '\uFEFF') {
                xmlPlano = xmlPlano.substring(1);
            }

            // 3) Elegir modo de firma
            ModoFirma mf = "XMLDSIG".equalsIgnoreCase(modo) ? ModoFirma.XMLDSIG : ModoFirma.XADES_BES;

            // 4) Firmar
            String xmlFirmado = firmaService.firmarFactura(xmlPlano, mf);

            // 5) Ambiente
            if (ambienteForzado != null) {
                envioComprobantesWs.setAmbiente(ambienteForzado == 2 ? 2 : 1);
            } else {
                envioComprobantesWs.setAmbienteFromXml(xmlFirmado);
            }

            // 6) Enviar a recepción
            RespuestaSolicitud recepcion = envioComprobantesWs.enviarFacturaFirmadaTxt(xmlFirmado);

            if (!"RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "estado", recepcion.getEstado(),
                        "errores", resumenErroresRecepcion(recepcion)
                ));
            }

            // 7) Polling/consulta de autorización
            RespuestaComprobante rc = envioComprobantesWs.consultarAutorizacionConEspera(
                    xmlFirmado,
                    clave -> {
                        try { return envioComprobantesWs.consultarAutorizacion(clave); }
                        catch (Exception e) { throw new RuntimeException(e); }
                    },
                    10,   // intentos
                    4000  // ms entre intentos
            );

            // 8) Extraer el XML autorizado (si existe)
// 8) Extraer el XML autorizado (si existe)
            String xmlAutorizado = extraerXmlAutorizado(rc);
            if (xmlAutorizado != null) {
                // Generar PDF desde el XML autorizado
                ByteArrayOutputStream pdfStream = xmlToPdfService.generarFacturaPDF(xmlAutorizado);
                if (pdfStream == null || pdfStream.size() == 0) {
                    return ResponseEntity.status(500).body(Map.of(
                            "error", "No se pudo generar el PDF de la autorización"
                    ));
                }

                // Persistir en BD (opcional)
                factura.setXmlautorizado(xmlAutorizado);
                factura.setEstado("O");
                fecFacturaR.save(factura);

                // --- Preparar adjuntos para correo ---
                String nombrePdf  = "factura_" + factura.getIdfactura() + ".pdf";
                String nombreXml  = "factura_" + factura.getIdfactura() + ".xml";
                byte[] pdfBytes   = pdfStream.toByteArray();
                byte[] xmlBytes   = xmlAutorizado.getBytes(java.nio.charset.StandardCharsets.UTF_8);

                // (Opcional) también armo un payload JSON para tu frontend
                String pdfBase64 = java.util.Base64.getEncoder().encodeToString(pdfBytes);
                Map<String, Object> payload = Map.of(
                        "xmlNombre", nombreXml,
                        "xmlContenido", xmlAutorizado,       // XML en texto (no base64)
                        "pdfNombre", nombrePdf,
                        "pdfMimeType", "application/pdf",
                        "pdfBase64", pdfBase64
                );

                // --- Enviar correo con adjuntos (XML+PDF) ---
                // ajusta estos datos o pásalos como parámetros si lo prefieres
                String emisor      = "facturacion@epmapatulcan.gob.ec";
                String password    = "TU_PASSWORD_APP"; // usa password de aplicación
                java.util.List<String> receptores = java.util.List.of(
                        "destino1@correo.com",
                        "destino2@correo.com"
                );
                String asunto      = "Factura electrónica " + factura.getEstablecimiento() + "-" + factura.getPuntoemision() + "-" + factura.getSecuencial();
                String htmlMensaje = """
            <h3>Factura electrónica autorizada</h3>
            <p>Se adjuntan los archivos XML (autorizado) y PDF.</p>
            """;

                // Llama a tu EmailService (ver firma más abajo)
                sendMail(
                        nombreXml, xmlBytes,
                        nombrePdf, pdfBytes
                );

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload);
            }



            // 9) Si no hay autorización aún
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

    // ===================== 9) GENERAR UN PDF DESDE UN STRING XML =====================

    public ResponseEntity<Resource> generarPdfDesdeXml(
            String xmlAutorizado
    ) {
        try {
            if (xmlAutorizado == null || xmlAutorizado.isBlank()) {
                return ResponseEntity.badRequest().build();
            }

            // Limpia BOM si vino
            if (xmlAutorizado.charAt(0) == '\uFEFF') {
                xmlAutorizado = xmlAutorizado.substring(1);
            }

            // 1) Extraer fechaEmision del XML (formato dd/MM/yyyy)
            LocalDate fechaEmision = extraerFechaEmision(xmlAutorizado);
            LocalDate fechaLimite  = LocalDate.of(2025, 5, 6);

            // 2) Elegir plantilla
            ByteArrayOutputStream pdfStream = (fechaEmision != null && fechaEmision.isBefore(fechaLimite))
                    ? xmlToPdfService.generarFacturaPDF_v2(xmlAutorizado)  // antes de 06/05/2025
                    : xmlToPdfService.generarFacturaPDF(xmlAutorizado);     // ese día o después (o si no se pudo leer fecha)

            if (pdfStream == null || pdfStream.size() == 0) {
                throw new IllegalStateException("No se pudo generar el PDF (stream vacío).");
            }

            //String filename = "factura" + (idfactura != null ? "_" + idfactura : "") + ".pdf";
            InputStreamResource resource =
                    new InputStreamResource();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfStream.size())
                    .body((Resource) resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===================== 10) ENVIAR POR CORREO =====================
    @PostMapping("/send-mail")
    public ResponseEntity<Map<String, Object>> sendMail(
            @RequestParam String nombreXml,
            @RequestParam byte[] xmlBytes,
            @RequestParam String nombrePdf,
            @RequestParam byte[] pdfBytes
    ) {
        try {
            String emisor = "facturacion@epmapatulcan.gob.ec";
            String password = "gOqp8L7EYkWa2NveUdKRtN3fl7qxlXPSDa5BKxIvgjEjOYonERiuDscG0WwTXBfZ";
            List<String> receptores = List.of("alexis.ortiz81@outlook.com");
            String asunto = "Factura electrónica EPMAPA-T";
            String mensaje = "<h1>Factura Electrónica</h1><p>Adjunto se encuentra la factura autorizada.</p>";

            // 🔹 Armar el objeto con toda la info
            Map<String, Object> obj = new HashMap<>();
            obj.put("emisor", emisor);
            obj.put("password", password);
            obj.put("receptores", receptores);
            obj.put("asunto", asunto);
            obj.put("html", mensaje);
            obj.put("nombreXml", nombreXml);
            obj.put("xmlBase64", Base64.getEncoder().encodeToString(xmlBytes));
            obj.put("nombrePdf", nombrePdf);
            obj.put("pdfBase64", Base64.getEncoder().encodeToString(pdfBytes));
            Definir d = definirService.findById(1L).orElseThrow();
            System.out.println("CIFRADO: ==> "+d.getClave_email());
            System.out.println("DESCIFRADO: ==> "+AESUtil.descifrar(d.getClave_email()));
            // 🔹 Llamada al servicio que realmente envía
            boolean resultado = sendAttachments(obj);

            // 🔹 Respuesta HTTP
            Map<String, Object> response = new HashMap<>();
            response.put("success", resultado);
            response.put("message", resultado ? "Correo enviado exitosamente con adjuntos" : "Error al enviar el correo");
            response.put("timestamp", new Date());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error en el servidor: " + e.getMessage());
            errorResponse.put("timestamp", new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Envía XML y PDF adjuntos (base64 en body) usando spring.mail.*
    public boolean sendAttachments(Map<String, Object> obj) {
        try {

            String emisor      = (String) obj.get("emisor");
            String password    = (String) obj.get("password");
            @SuppressWarnings("unchecked")
            List<String> receptores = (List<String>) obj.get("receptores");
            String asunto      = (String) obj.get("asunto");
            String htmlMensaje = (String) obj.get("html");

            String nombreXml   = (String) obj.get("nombreXml");
            String xmlBase64   = (String) obj.get("xmlBase64");
            byte[] xmlBytes    = xmlBase64 != null ? Base64.getDecoder().decode(xmlBase64) : null;

            String nombrePdf   = (String) obj.get("nombrePdf");
            String pdfBase64   = (String) obj.get("pdfBase64");
            byte[] pdfBytes    = pdfBase64 != null ? Base64.getDecoder().decode(pdfBase64) : null;

            return true;
        } catch (Exception e) {
            System.out.println("Error en sendAttachments: " + e.getMessage());
            return false;
        }
    }

    private LocalDate extraerFechaEmision(String xml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            // endurecer parser
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            Document doc = dbf.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));
            NodeList list = doc.getElementsByTagName("fechaEmision");
            if (list.getLength() == 0) return null;

            String texto = list.item(0).getTextContent();
            if (texto == null || texto.isBlank()) return null;

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(texto.trim(), fmt);
        } catch (Exception ignore) {
            return null; // si no se puede leer, que el caller use la plantilla por defecto
        }
    }

    // Normaliza estado: null-safe, trim y sin espacios raros
    private static String normEstado(String s) {
        return s == null ? "" : s.trim();
    }

    // Para depurar: imprime códigos Unicode de cada char
    private static String debugCodes(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append(String.format("\\u%04X", (int) s.charAt(i)));
            if (i < s.length() - 1) sb.append(' ');
        }
        return sb.toString();
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


    //=================================================================================================================

    @PostMapping("/send")
    public ResponseEntity<SendMailResponse> send(@Valid @RequestBody SendMailRequest req) {
        try {
            mailService.send(req);
            return ResponseEntity.ok(new SendMailResponse(true, "Enviado", java.time.Instant.now()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new SendMailResponse(false, e.getMessage(), java.time.Instant.now()));
        }
    }

    @PostMapping("/send-template")
    public ResponseEntity<SendMailResponse> sendTemplate(@Valid @RequestBody TemplateMailRequest req) {
        try {
            mailService.sendTemplate(req);
            return ResponseEntity.ok(new SendMailResponse(true, "Enviado", java.time.Instant.now()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new SendMailResponse(false, e.getMessage(), java.time.Instant.now()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String,Object>> health() {
        boolean ok = mailService.smtpHealth();
        return ResponseEntity.ok(Map.of("smtp", ok ? "UP" : "DOWN", "time", java.time.Instant.now()));
    }

}
