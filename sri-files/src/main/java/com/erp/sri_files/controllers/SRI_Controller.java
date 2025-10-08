package com.erp.sri_files.controllers;

import com.erp.sri_files.config.AESUtil;
import com.erp.sri_files.dto.AttachmentDTO;
import com.erp.sri_files.dto.SendMailRequest;
import com.erp.sri_files.dto.SendMailResponse;
import com.erp.sri_files.dto.TemplateMailRequest;
import com.erp.sri_files.models.Definir;
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

@RestController
@RequestMapping("/api/singsend")
@CrossOrigin(origins = "*")
public class SRI_Controller {

    private final SendXmlToSriService sendXmlToSriService;
    private final FirmaComprobantesService firmaService;
    private final RestTemplate restTemplate;

    @Autowired
    private FacturaR fecFacturaR;
    @Autowired
    private FacturaXmlGeneratorService facturaXmlGeneratorService;
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

    public SRI_Controller(SendXmlToSriService sendXmlToSriService,
                          FirmaComprobantesService firmaService, RestTemplate restTemplate) {
        this.sendXmlToSriService = sendXmlToSriService;
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
                sendXmlToSriService.setAmbiente(ambienteForzado == 2 ? 2 : 1);
            } else {
                sendXmlToSriService.setAmbienteFromXml(xmlFirmado);
            }

            // 3) Recepción
            RespuestaSolicitud respuesta = sendXmlToSriService.enviarFacturaFirmada(xmlBytes);

            // 4) Si RECIBIDA, intenta autorización con polling
            if ("RECIBIDA".equalsIgnoreCase(respuesta.getEstado())) {
                RespuestaComprobante autorizacion = sendXmlToSriService.consultarAutorizacionConEspera(
                        xmlFirmado,
                        clave -> {
                            try { return sendXmlToSriService.consultarAutorizacion(clave); }
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


    // ===================== 2) SOLO FIRMAR → XML =====================
    @PostMapping(
            path = "/factura/firmar/upload.xml"
    )
    public String firmarFacturaUploadXml(
            @RequestPart("xml") MultipartFile xmlFile,
            @RequestParam(value = "modo", required = false, defaultValue = "XADES_BES") String modo,
            @RequestParam(value = "definirId", required = false) Long definirId
    ) throws Exception {

        String xmlPlano = toUtf8String(xmlFile);
        ModoFirma mf = "XMLDSIG".equalsIgnoreCase(modo) ? ModoFirma.XMLDSIG : ModoFirma.XADES_BES;

        return (definirId == null)
                ? firmaService.firmarFactura(xmlPlano, mf)
                : firmaService.firmarFactura(xmlPlano, mf);
    }

    // ===================== 3) FIRMAR Y ENVIAR EN UNO SOLO =====================
    @PostMapping(
            path = "/factura/xml"
    )
    public ResponseEntity<?> firmarYEnviarFacturaUloadXml(
            @RequestPart("xml") MultipartFile xmlFile,
            @RequestParam(value = "modo", required = false, defaultValue = "XADES_BES") String modo,
            @RequestParam(value = "definirId", required = false, defaultValue = "1") Long definirId,
            @RequestParam(value = "ambiente", required = false) Integer ambienteForzado
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
                sendXmlToSriService.setAmbiente(ambienteForzado == 2 ? 2 : 1);
            } else {
                sendXmlToSriService.setAmbienteFromXml(xmlFirmado);
            }

            // 5) Enviar a recepción
            RespuestaSolicitud recepcion = sendXmlToSriService.enviarFacturaFirmadaTxt(xmlFirmado);

            // 6) Si RECIBIDA, aplicar polling hasta autorización
            if ("RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
                RespuestaComprobante autorizacion = sendXmlToSriService.consultarAutorizacionConEspera(
                        xmlFirmado,
                        clave -> {
                            try { return sendXmlToSriService.consultarAutorizacion(clave); }
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

    // ===================== 4) FIRMAR Y ENVIAR RECIBIENDO XML EN STRING =====================
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
                sendXmlToSriService.setAmbiente(ambienteForzado == 2 ? 2 : 1);
            } else {
                sendXmlToSriService.setAmbienteFromXml(xmlFirmado);
            }

            // 4) Enviar a recepción
            RespuestaSolicitud recepcion = sendXmlToSriService.enviarFacturaFirmadaTxt(xmlFirmado);


            // 5) Si RECIBIDA, aplicar polling hasta autorización
            if ("RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
                RespuestaComprobante autorizacion = sendXmlToSriService.consultarAutorizacionConEspera(
                        xmlFirmado,
                        clave -> {
                            try { return sendXmlToSriService.consultarAutorizacion(clave); }
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

    // ===================== 5) FIRMAR Y ENVIAR RETENCIÓN (solo retorna XML autorizado) =====================
    @PostMapping(
            path = "/retencion",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<?> firmarYEnviarRetencion(
            @RequestPart("xml") MultipartFile xmlFile,
            @RequestParam(value = "modo", required = false, defaultValue = "XADES_BES") String modo,
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
                sendXmlToSriService.setAmbiente(ambienteForzado == 2 ? 2 : 1);
            } else {
                sendXmlToSriService.setAmbienteFromXml(xmlFirmado);
            }
            // 5) Enviar a recepción
            RespuestaSolicitud recepcion = sendXmlToSriService.enviarFacturaFirmadaTxt(xmlFirmado);

            // 6) Si recepción NO fue RECIBIDA -> 400 con errores
            if (!"RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "estado", recepcion.getEstado(),
                        "errores", resumenErroresRecepcion(recepcion)
                ));
            }
            // 7) Polling de autorización
            RespuestaComprobante rc = sendXmlToSriService.consultarAutorizacionConEspera(
                    xmlFirmado,
                    clave -> {
                        try { return sendXmlToSriService.consultarAutorizacion(clave); }
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
            sendXmlToSriService.setAmbiente(ambienteForzado == 2 ? 2 : 1);
        } else {
            sendXmlToSriService.setAmbienteFromXml(xmlFirmado);
        }

        // 4) Enviar a recepción
        RespuestaSolicitud recepcion = sendXmlToSriService.enviarFacturaFirmadaTxt(xmlFirmado);

        // Si NO fue recibida, devolvemos error resumido (JSON)
        if (!"RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "estado", recepcion.getEstado(),
                    "errores", resumenErroresRecepcion(recepcion)
            ));
        }

        // 5) Polling/consulta de autorización
        RespuestaComprobante rc = sendXmlToSriService.consultarAutorizacionConEspera(
                xmlFirmado,
                clave -> {
                    try { return sendXmlToSriService.consultarAutorizacion(clave); }
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
        //crear variable de respuesta estado y detalla
        // 1) Intentar obtener factura
        Factura factura = fecFacturaR.findByIdfactura(idfactura);
        try {
            String modo = "XADES_BES";
            Integer ambienteForzado = 1;


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
            String xmlPlano = facturaXmlGeneratorService.generarXmlFactura(factura);

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
                sendXmlToSriService.setAmbiente(ambienteForzado == 2 ? 2 : 1);
            } else {
                sendXmlToSriService.setAmbienteFromXml(xmlFirmado);
            }

            // 6) Enviar a recepción
            RespuestaSolicitud recepcion = sendXmlToSriService.enviarFacturaFirmadaTxt(xmlFirmado);

            if (!"RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "estado", recepcion.getEstado(),
                        "errores", resumenErroresRecepcion(recepcion)
                ));
            }

            // 7) Polling/consulta de autorización
            RespuestaComprobante rc = sendXmlToSriService.consultarAutorizacionConEspera(
                    xmlFirmado,
                    clave -> {
                        try { return sendXmlToSriService.consultarAutorizacion(clave); }
                        catch (Exception e) { throw new RuntimeException(e); }
                    },
                    20,   // intentos
                    4000  // ms entre intentos
            );

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
                // --- Preparar adjuntos (Base64) ---
                String nombrePdf  = "factura_" + factura.getIdfactura() + ".pdf";
                String nombreXml  = "factura_" + factura.getIdfactura() + ".xml";

                byte[] pdfBytes = pdfStream.toByteArray();
                byte[] xmlBytes = xmlAutorizado.getBytes(java.nio.charset.StandardCharsets.UTF_8);

                String pdfBase64 = java.util.Base64.getEncoder().encodeToString(pdfBytes);
                String xmlBase64 = java.util.Base64.getEncoder().encodeToString(xmlBytes);

                List<AttachmentDTO> attachments = java.util.List.of(
                        new AttachmentDTO(nombrePdf, "application/pdf", pdfBase64),
                        new AttachmentDTO(nombreXml, "application/xml", xmlBase64)
                );
// --- Destinatarios ---
// Usa el correo del comprador si lo tienes en la entidad; de lo contrario, uno de prueba
                /*List<String> to = (factura.getEmailcomprador() != null && !factura.getEmailcomprador().isBlank())
                        ? java.util.List.of(factura.getEmailcomprador().trim())
                        : java.util.List.of("destinatario@dominio.com");*/

                List<String> to = (java.util.List.of("ortizln9@gmail.com", "alexis.ortiz81@outlook.com"));

// CC/BCC opcionales
                List<String> cc = java.util.Collections.emptyList();
                List<String> bcc = java.util.Collections.emptyList();

// From: pásalo como null para que el servicio use app.mail.from
                String from = null;

// Asunto y cuerpo
                String subject = "Factura electrónica #" + factura.getEstablecimiento() + "-"
                        + factura.getPuntoemision() + "-" + factura.getSecuencial();

                String htmlBody =
                        "<h1>Factura electrónica autorizada</h1>" +
                                "<p>Estimado/a " + (factura.getRazonsocialcomprador() != null ? factura.getRazonsocialcomprador() : "cliente") + ",</p>" +
                                "<p>Adjuntamos su comprobante electrónico en formato PDF y XML.</p>" +
                                "<p>Saludos,<br>EPMAPA-T</p>";

// Inline images (si no usas, envía map vacío)
                java.util.Map<String, String> inlineImages = java.util.Collections.emptyMap();

// --- Construir request ---
                SendMailRequest mailReq = new SendMailRequest(
                        from,      // deja que MailService tome el 'from' por defecto de configuración
                        to,
                        cc,
                        bcc,
                        subject,
                        htmlBody,
                        attachments,
                        inlineImages
                );
// --- Enviar ---
                boolean mail = mailService.sendMail(mailReq);
                if(mail){
                    factura.setEstado("A");
                }
                else{
                    factura.setEstado("O");
                }
                fecFacturaR.save(factura);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("Enviado con exito");
            }
            factura.setEstado("P");
            factura.setErrores("Pendiente la autorizacion: " + xmlFirmado);
            fecFacturaR.save(factura);
            // 9) Si no hay autorización aún
            return ResponseEntity.status(202).body(Map.of(
                    "estado", "PENDIENTE",
                    "detalle", "La autorización aún no está disponible."
            ));

        } catch (Exception e) {
            e.printStackTrace();
            factura.setEstado("U");
            factura.setErrores(e.getMessage());
            fecFacturaR.save(factura);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error al firmar/enviar comprobante",
                    "detalle", e.getMessage()
            ));
        }
    }


    // ===================== 5) FIRMAR Y ENVIAR RETENCIÓN (recibe XML como String) =====================
    @PostMapping(
            path = "/retencion/string",
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE },
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<?> firmarYEnviarRetencion(
            @RequestBody String xmlPlanoBody,
            @RequestParam(value = "modo", required = false, defaultValue = "XADES_BES") String modo,
            @RequestParam(value = "ambiente", required = false) Integer ambienteForzado,
            @RequestParam(value = "download", required = false, defaultValue = "false") boolean download
    ) {
        try {
            // 1) Validar y limpiar el XML recibido
            if (xmlPlanoBody == null || xmlPlanoBody.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "XML vacío o nulo"
                ));
            }
            String xmlPlano = stripBom(xmlPlanoBody).trim();

            // 2) Modo firma
            ModoFirma mf = "XMLDSIG".equalsIgnoreCase(modo) ? ModoFirma.XMLDSIG : ModoFirma.XADES_BES;

            // 3) Firmar (re-usa tu servicio actual)
            String xmlFirmado = firmaService.firmarFactura(xmlPlano, mf);

            // 4) Ambiente (forzado o leído del XML firmado)
            if (ambienteForzado != null) {
                sendXmlToSriService.setAmbiente(ambienteForzado == 2 ? 2 : 1);
            } else {
                sendXmlToSriService.setAmbienteFromXml(xmlFirmado);
            }

            // 5) Enviar a recepción
            RespuestaSolicitud recepcion = sendXmlToSriService.enviarFacturaFirmadaTxt(xmlFirmado);

            // 6) Si recepción NO fue RECIBIDA -> 400 con errores
            if (!"RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "estado", recepcion.getEstado(),
                        "errores", resumenErroresRecepcion(recepcion)
                ));
            }

            // 7) Polling de autorización
            RespuestaComprobante rc = sendXmlToSriService.consultarAutorizacionConEspera(
                    xmlFirmado,
                    clave -> {
                        try { return sendXmlToSriService.consultarAutorizacion(clave); }
                        catch (Exception e) { throw new RuntimeException(e); }
                    },
                    10,   // intentos
                    4000  // ms entre intentos
            );

            // 8) Extraer XML autorizado
            String xmlAutorizado = extraerXmlAutorizado(rc);
            ByteArrayOutputStream pdfStream = xmlToPdfService.generarFacturaPDF(xmlAutorizado);

            if (xmlAutorizado != null) {
                // --- Preparar adjuntos (Base64) ---
                String nombrePdf  = "retencion_" + rc.getAutorizaciones() + ".pdf";
                String nombreXml  = "retencion_" + rc.getAutorizaciones() + ".xml";

                byte[] pdfBytes = pdfStream.toByteArray();
                byte[] xmlBytes = xmlAutorizado.getBytes(java.nio.charset.StandardCharsets.UTF_8);

                String pdfBase64 = java.util.Base64.getEncoder().encodeToString(pdfBytes);
                String xmlBase64 = java.util.Base64.getEncoder().encodeToString(xmlBytes);

                List<AttachmentDTO> attachments = java.util.List.of(
                        new AttachmentDTO(nombrePdf, "application/pdf", pdfBase64),
                        new AttachmentDTO(nombreXml, "application/xml", xmlBase64)
                );
// --- Destinatarios ---
// Usa el correo del comprador si lo tienes en la entidad; de lo contrario, uno de prueba
                /*List<String> to = (factura.getEmailcomprador() != null && !factura.getEmailcomprador().isBlank())
                        ? java.util.List.of(factura.getEmailcomprador().trim())
                        : java.util.List.of("destinatario@dominio.com");*/

                List<String> to = (java.util.List.of("ortizln9@gmail.com", "alexis.ortiz81@outlook.com"));

// CC/BCC opcionales
                List<String> cc = java.util.Collections.emptyList();
                List<String> bcc = java.util.Collections.emptyList();

// From: pásalo como null para que el servicio use app.mail.from
                String from = null;

// Asunto y cuerpo
                String subject = "Comprobante electrónico #" ;

                String htmlBody =
                        "<h1>Factura electrónica autorizada</h1>" +
                                "<p>Estimado/a " + "cliente" + ",</p>" +
                                "<p>Adjuntamos su comprobante electrónico en formato PDF y XML.</p>" +
                                "<p>Saludos,<br>EPMAPA-T</p>";

// Inline images (si no usas, envía map vacío)
                java.util.Map<String, String> inlineImages = java.util.Collections.emptyMap();

// --- Construir request ---
                SendMailRequest mailReq = new SendMailRequest(
                        from,      // deja que MailService tome el 'from' por defecto de configuración
                        to,
                        cc,
                        bcc,
                        subject,
                        htmlBody,
                        attachments,
                        inlineImages
                );
// --- Enviar ---
                boolean mail = mailService.sendMail(mailReq);
                if (download) {
                    String fileName = "retencion-" + System.currentTimeMillis() + ".xml";
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_XML)
                            .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                            .body(xmlAutorizado);
                }
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

    // ========== 1) Consultar por CLAVE DE ACCESO ==========
    @GetMapping(path = "/autorizacion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> consultarAutorizacion(
            @RequestParam String claveAcceso,
            @RequestParam(defaultValue = "false") boolean wait,     // true = usar polling
            @RequestParam(defaultValue = "20") int attempts,        // # intentos en modo wait
            @RequestParam(defaultValue = "3000") long sleepMillis,  // pausa entre intentos (ms)
            @RequestParam(defaultValue = "false") boolean includeXml // incluir XML autorizado en la respuesta
    ) {
        try {
            RespuestaComprobante rc;
            if (wait) {
                // Polling hasta que SRI devuelva resultado o se agoten intentos
                rc = sendXmlToSriService.consultarAutorizacionConEspera(
                        // no necesitamos xml aquí; pasamos la clave directamente
                        /*xmlFirmado*/ "<na>", // parámetro no usado en tu impl para polling
                        _clave -> {
                            try { return sendXmlToSriService.consultarAutorizacion(claveAcceso); }
                            catch (Exception e) { throw new RuntimeException(e); }
                        },
                        attempts,
                        sleepMillis
                );
            } else {
                // Solo 1 consulta
                rc = sendXmlToSriService.consultarAutorizacion(claveAcceso);
            }

            return ResponseEntity.ok( mapRespuestaAutorizacion(rc, includeXml) );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error consultando autorización en SRI",
                    "detalle", e.getMessage(),
                    "claveAcceso", claveAcceso
            ));
        }
    }

    // ========== 2) Consultar por XML (extrae <claveAcceso>) ==========
    @PostMapping(
            path = "/autorizacion/by-xml",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> consultarAutorizacionDesdeXml(
            @RequestBody String xml,
            @RequestParam(defaultValue = "false") boolean wait,
            @RequestParam(defaultValue = "10") int attempts,
            @RequestParam(defaultValue = "3000") long sleepMillis,
            @RequestParam(defaultValue = "false") boolean includeXml
    ) {
        try {
            if (xml == null || xml.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "XML vacío"
                ));
            }
            String clave = extraerClaveAcceso(xml);
            if (clave == null || clave.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "No se encontró <claveAcceso> en el XML"
                ));
            }

            RespuestaComprobante rc;
            if (wait) {
                rc = sendXmlToSriService.consultarAutorizacionConEspera(
                        xml,
                        k -> {
                            try { return sendXmlToSriService.consultarAutorizacion(clave); }
                            catch (Exception e) { throw new RuntimeException(e); }
                        },
                        attempts,
                        sleepMillis
                );
            } else {
                rc = sendXmlToSriService.consultarAutorizacion(clave);
            }

            return ResponseEntity.ok( mapRespuestaAutorizacion(rc, includeXml) );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error consultando autorización en SRI",
                    "detalle", e.getMessage()
            ));
        }
    }

    // ================== Helpers ==================

    /** Estructura JSON amigable para el frontend */
    private Map<String, Object> mapRespuestaAutorizacion(RespuestaComprobante rc, boolean includeXml) {
        if (rc == null) {
            return Map.of("estado", "SIN_RESPUESTA");
        }

        String numero = (rc.getNumeroComprobantes() == null) ? "0" : rc.getNumeroComprobantes().trim();
        int n;
        try { n = Integer.parseInt(numero); } catch (NumberFormatException e) { n = 0; }

        List<Map<String, Object>> items = new ArrayList<>();
        if (rc.getAutorizaciones() != null && rc.getAutorizaciones().getAutorizacion() != null) {
            for (var a : rc.getAutorizaciones().getAutorizacion()) {
                Map<String, Object> it = new LinkedHashMap<>();
                it.put("estado", safe(a.getEstado()));
                it.put("numeroAutorizacion", safe(a.getNumeroAutorizacion()));
                it.put("fechaAutorizacion", a.getFechaAutorizacion() != null ? a.getFechaAutorizacion().toString() : null);
                it.put("ambiente", safe(a.getAmbiente()));

                // errores/mensajes (si existen)
                if (a.getMensajes() != null && a.getMensajes().getMensaje() != null && !a.getMensajes().getMensaje().isEmpty()) {
                    List<Map<String, String>> msgs = new ArrayList<>();
                    for (var m : a.getMensajes().getMensaje()) {
                        msgs.add(Map.of(
                                "identificador", safe(m.getIdentificador()),
                                "mensaje", safe(m.getMensaje()),
                                "informacionAdicional", safe(m.getInformacionAdicional())
                        ));
                    }
                    it.put("mensajes", msgs);
                }

                if (includeXml) {
                    String x = extraerComprobanteXmlAutorizado(a);
                    it.put("xmlAutorizado", x); // puede ser null si no hay
                }

                items.add(it);
            }
        }

        return Map.of(
                "numeroComprobantes", n,
                "autorizaciones", items
        );
    }

    private static String safe(String s) { return (s == null) ? "" : s; }

    /** Extrae <claveAcceso> de un XML texto */
    private String extraerClaveAcceso(String xml) {
        try {
            if (xml.charAt(0) == '\uFEFF') xml = xml.substring(1); // limpia BOM si vino
            javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            // endurece parser
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            org.w3c.dom.Document doc = dbf.newDocumentBuilder()
                    .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
            org.w3c.dom.NodeList list = doc.getElementsByTagName("claveAcceso");
            if (list.getLength() == 0) return null;
            return list.item(0).getTextContent().replaceAll("\\s+", "");
        } catch (Exception ignore) {
            return null;
        }
    }

    /** Saca el <comprobante> XML dentro de una Autorizacion (si está presente) */
    private String extraerComprobanteXmlAutorizado(ec.gob.sri.ws.autorizacion.Autorizacion a) {
        try {
            if (a == null || a.getComprobante() == null) return null;
            // Suele venir como CDATA con el XML original del comprobante
            String xml = a.getComprobante();
            // normaliza BOM
            if (xml != null && !xml.isBlank() && xml.charAt(0) == '\uFEFF') xml = xml.substring(1);
            return xml;
        } catch (Exception e) {
            return null;
        }
    }





    /** Quita BOM UTF-8 si viene al inicio del String */
    private static String stripBom(String s) {
        if (s != null && !s.isEmpty() && s.charAt(0) == '\uFEFF') {
            return s.substring(1);
        }
        return s;
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


    //======================================OPCIONES DE ENVIO DE CORREO ELECTRÓNICO========================================================================

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
