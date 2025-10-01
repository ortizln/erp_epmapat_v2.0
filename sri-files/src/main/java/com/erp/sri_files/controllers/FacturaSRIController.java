package com.erp.sri_files.controllers;

import com.erp.sri_files.config.AESUtil;
import com.erp.sri_files.interfaces.fecFacturaDatos;
import com.erp.sri_files.models.Definir;
import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.services.*;
import com.erp.sri_files.utils.FirmaComprobantesService;
import ec.gob.sri.ws.autorizacion.RespuestaComprobante;
import ec.gob.sri.ws.recepcion.RespuestaSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sri")
@CrossOrigin("*")
public class FacturaSRIController {
    @Autowired
    private FacturaR dao;
    @Autowired
    private XmlSignerService xmlSignerService;
    @Autowired
    private DefinirService definirService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private XmlToPdfService xmlToPdfService;
    @Autowired
    private FirmaComprobantesService firmaComprobantesService;

    private final FacturaSRIService facturaSRIService;
    @Value("${xml.storage.path}")
    private String xmlStoragePath;

    @Value("${sri.xml.output-dir}")
    private String xmlOutputDir;

    @Autowired
    private AllMicroServices allMicroServices;
    @Autowired
    private EnvioComprobantesWs envioComprobantesWs;
    @Autowired
    private KeystoreProbe keystoreProbe;
    public FacturaSRIController(FacturaSRIService facturaSRIService) {
        this.facturaSRIService = facturaSRIService;
    }

    @GetMapping("/generar-xml")
    public ResponseEntity<String> generarXmlFactura(@RequestParam Long idfactura) throws Exception {
        Definir definir = definirService.findById(1L)
                .orElseThrow(() -> new RuntimeException("Definir no encontrado"));

        Factura factura = allMicroServices.findById(idfactura);

        if (factura == null) {
            return ResponseEntity.noContent().build();
        }

        // Generar XML sin firmar
        String xml = facturaSRIService.generarXmlFactura(factura);

        // Firmar XML usando la firma almacenada en BD
        String xmlFirmado = xmlSignerService.signXml(xml, definir.getFirma(), "Junior2012");

        // Guardar XML firmado en disco
        FacturaSRIService.saveXml(
                xmlFirmado,
                "Fac_" + factura.getEstablecimiento() + "-" +
                        factura.getPuntoemision() + "-" + factura.getSecuencial() + ".xml"
        );

        return ResponseEntity.ok(xmlFirmado);
    }


    // ===========================
    // 1) Firmar subiendo el archivo
    // ===========================
    @PostMapping(
            value = "/firmar-xml",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<String> firmarXmlFile(
            @RequestParam("xml") MultipartFile xmlFile,
            @RequestParam(name = "idDefinir", defaultValue = "1") Long idDefinir,
            @RequestParam(name = "password", required = false) String password
    ) {
        try {
            Definir definir = definirService.findById(idDefinir)
                    .orElseThrow(() -> new RuntimeException("Definir no encontrado"));

            // xml del archivo
            String xml = new String(xmlFile.getBytes(), java.nio.charset.StandardCharsets.UTF_8);

            // password: param -> BD -> fallback
            String pass = (password != null && !password.isBlank())
                    ? password
                    : (definir.getClave_firma() != null && !definir.getClave_firma().isBlank()
                    ? definir.getClave_firma()
                    : "Junior2012");

            String xmlFirmado = xmlSignerService.signXml(xml, definir.getFirma(), pass);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xmlFirmado);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error firmando XML: " + e.getMessage());
        }
    }

    // ===========================
    // 2) Firmar enviando el XML como texto
    // ===========================
    @PostMapping(
            value = "/firmar-xml-text",
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE, MediaType.TEXT_PLAIN_VALUE },
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<String> firmarXmlText(
            @RequestBody String xml,
            @RequestParam(name = "idDefinir", defaultValue = "1") Long idDefinir,
            @RequestParam(name = "password", required = false) String password
    ) {
        try {
            Definir definir = definirService.findById(idDefinir)
                    .orElseThrow(() -> new RuntimeException("Definir no encontrado"));

            String pass = (password != null && !password.isBlank())
                    ? password
                    : (definir.getFirma() != null && !definir.getClave_firma().isBlank()
                    ? definir.getClave_firma()
                    : "Junior2012");

            String xmlFirmado = xmlSignerService.signXml(xml, definir.getFirma(), pass);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xmlFirmado);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error firmando XML: " + e.getMessage());
        }
    }

    @PostMapping(
            value = "/firmar-xml-upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> firmarXmlUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "idDefinir", defaultValue = "1") Long idDefinir,
            @RequestParam(name = "password", required = false) String password,
            @RequestParam(name = "save", defaultValue = "true") boolean save,
            @RequestParam(name = "download", defaultValue = "false") boolean download,
            @RequestParam(name = "filename", required = false) String filename
    ) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Sube un archivo XML en el parámetro 'file'.");
            }

            Definir definir = definirService.findById(idDefinir)
                    .orElseThrow(() -> new RuntimeException("Definir no encontrado"));
            if (definir.getFirma() == null /* && def.getFirmaBase64()==null si aplica */) {
                throw new IllegalStateException("No hay archivo de firma en Definir");
            }
            System.out.println(definir.getClave_firma());
            System.out.println(AESUtil.descifrar(definir.getClave_firma()));
            // --- PROBAMOS CARGA ---
            String contr = AESUtil.descifrar(definir.getClave_firma());

            KeystoreProbe.Result info = KeystoreProbe.probePkcs12(definir.getFirma(), contr);
            // Si llegó aquí, la contraseña es válida (ya sea para el keystore o para la key)
            System.out.println("PKCS12 OK. Alias: {}" + info.alias());
            // Password: parámetro -> BD -> fallback
            String pass = (password != null && !password.isBlank())
                    ? password
                    : (contr != null && !contr.isBlank()
                    ? contr
                    : "Junior2012");
            System.out.println(pass);
            // Leer XML del archivo (UTF-8)
            String xml = new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);

            // Firmar
            String xmlFirmado = xmlSignerService.signXml(xml, definir.getFirma(), pass);

            // Guardar en disco si se pide
            if (save) {
                // Si no te pasan filename, usa el nombre original con sufijo _firmado.xml
                String baseName = (filename != null && !filename.isBlank())
                        ? filename
                        : (file.getOriginalFilename() != null ? file.getOriginalFilename() : "factura.xml");
                if (!baseName.toLowerCase().endsWith(".xml")) baseName += ".xml";

                // Añade sufijo para distinguir firmado
                String nameWithSuffix = baseName.replace(".xml", "_firmado.xml");
                FacturaSRIService.saveXml(xmlFirmado, nameWithSuffix);
            }

            // ¿Devolver como descarga?
            if (download) {
                String dlName = (filename != null && !filename.isBlank())
                        ? filename
                        : (file.getOriginalFilename() != null ? file.getOriginalFilename() : "factura.xml");
                if (!dlName.toLowerCase().endsWith(".xml")) dlName += ".xml";
                dlName = dlName.replace(".xml", "_firmado.xml");

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_XML)
                        .header("Content-Disposition", "attachment; filename=\"" + dlName + "\"")
                        .body(xmlFirmado);
            }

            // Si no es descarga, lo devuelvo directamente (texto XML)
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xmlFirmado);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error firmando XML: " + e.getMessage());
        }
    }

    @PostMapping("/enviar")
    public ResponseEntity<?> enviarComprobante(@RequestParam("file") MultipartFile file) {
        try {
            // 1) Leer el XML firmado en bytes
            byte[] xmlBytes = file.getBytes();

            // 2) Llamar al servicio de recepción
            RespuestaSolicitud respuesta = envioComprobantesWs.enviarFacturaFirmada(xmlBytes);

            // 3) Procesar respuesta
            if ("RECIBIDA".equalsIgnoreCase(respuesta.getEstado())) {
                // Extraer la clave de acceso del XML firmado
                String xmlString = new String(xmlBytes, StandardCharsets.UTF_8);
                String claveAcceso = extraerClaveAcceso(xmlString);

                // Consultar autorización
                RespuestaComprobante autorizacion = envioComprobantesWs.consultarAutorizacion(claveAcceso);

                return ResponseEntity.ok(autorizacion);
            } else {
                // Si no fue recibida, retornar detalle de errores
                return ResponseEntity.badRequest().body(respuesta);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al enviar comprobante: " + e.getMessage());
        }
    }
    @RestController
    @RequestMapping("/api/comprobantes")
    public class ComprobantesController {

        private final EnvioComprobantesWs envioComprobantesWs; // tu cliente SOAP/REST al SRI

        public ComprobantesController(EnvioComprobantesWs envioComprobantesWs) {
            this.envioComprobantesWs = envioComprobantesWs;
        }

        @PostMapping("/enviar")
        public ResponseEntity<?> enviarComprobante(@RequestParam("file") MultipartFile file) {
            try {
                // 1) Leer el XML firmado en bytes
                byte[] xmlBytes = file.getBytes();

                // 2) Llamar al servicio de recepción del SRI
                RespuestaSolicitud respuesta = envioComprobantesWs.enviarFacturaFirmada(xmlBytes);

                // 3) Procesar respuesta
                if ("RECIBIDA".equalsIgnoreCase(respuesta.getEstado())) {
                    // 3a) Extraer claveAcceso del XML firmado
                    String xmlString = new String(xmlBytes, StandardCharsets.UTF_8);
                    String claveAcceso = extraerClaveAcceso(xmlString);

                    // 3b) Consultar autorización en el SRI
                    RespuestaComprobante autorizacion = envioComprobantesWs.consultarAutorizacion(claveAcceso);

                    return ResponseEntity.ok(autorizacion);
                } else {
                    // Si no fue recibida, devolver detalle de errores
                    return ResponseEntity.badRequest().body(respuesta);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Error al enviar comprobante: " + e.getMessage());
            }
        }

        /**
         * Extrae el valor de <claveAcceso> del XML.
         */
        private String extraerClaveAcceso(String xml) throws Exception {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            Document doc = dbf.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));

            NodeList list = doc.getElementsByTagName("claveAcceso");
            if (list.getLength() > 0) {
                return list.item(0).getTextContent().trim();
            }
            throw new IllegalStateException("No se encontró <claveAcceso> en el XML");
        }
    }




    /**
     * Método auxiliar para extraer la clave de acceso del XML firmado
     */
    private String extraerClaveAcceso(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));

        NodeList nodes = doc.getElementsByTagName("claveAcceso");
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        throw new Exception("No se encontró la clave de acceso en el XML");
    }


    @PostMapping(value = "/enviarcv", consumes = { "multipart/form-data" })
    public ResponseEntity<String> enviarFactura(
            @RequestParam String toEmail,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestParam MultipartFile xmlFile) {
        try {
            facturaSRIService.processAndSendInvoice(toEmail, subject, body, xmlFile);
            return ResponseEntity.ok("Factura convertida a PDF y enviada por email exitosamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al procesar la factura: " + e.getMessage());
        }
    }


    @GetMapping("/generar-pdf")
    public ResponseEntity<Resource> generarPdf(@RequestParam Long idfactura) {
        fecFacturaDatos fecFactura = allMicroServices.getNroFactura(idfactura);
        if (fecFactura == null || fecFactura.getXmlautorizado() == null) {
            return ResponseEntity.noContent().build();
        }
        String xmlAutorizado = fecFactura.getXmlautorizado();
        LocalDate fehchaemision = fecFactura.getFechaemision();
        LocalDate fechaLimite = LocalDate.of(2025, 5, 6);

        try {
            // Generar el PDF como ByteArrayOutputStream
            ByteArrayOutputStream pdfStream;
            // Comparar fechas
            if (fehchaemision.isAfter(fechaLimite)) {
                System.out.println("===> 1 <===");
                pdfStream = xmlToPdfService.generarFacturaPDF(xmlAutorizado);

            } else if (fehchaemision.isBefore(fechaLimite)) {
                System.out.println("===> 2 <===");
                pdfStream = xmlToPdfService.generarFacturaPDF_v2(xmlAutorizado);

            } else if (fehchaemision.isEqual(fechaLimite)) {
                System.out.println("===> 3 <===");
                pdfStream = xmlToPdfService.generarFacturaPDF(xmlAutorizado);
            } else {
                System.out.println("===> 4 <===");
                pdfStream = xmlToPdfService.generarFacturaPDF(xmlAutorizado);
            }

            if (pdfStream == null || pdfStream.size() == 0) {
                throw new RuntimeException("No se pudo generar el PDF.");
            }

            // Convertir el stream en un InputStreamResource
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfStream.toByteArray()));

            // Retornar el PDF como un archivo descargable
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfStream.size())
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMail(@RequestParam String emisor, @RequestParam String password,
            @RequestParam List<String> receptores, @RequestParam String asunto, @RequestParam String mensaje) {
        try {
            // Configuración del correo
            emisor = "facturacion@epmapatulcan.gob.ec";
            password = "79DB6F2BFA7FFED2E17F16CABA197D2063EB";
            receptores = List.of("ortizln9@gmail.com", "alexis.ortiz81@outlook.com",
                    "saulruales@gmail.com", "ortizln9@gmail.com");
            asunto = "Prueba mail facturas";
            mensaje = "<h1>ANUNCIO EPMAPA-T</h1><p>Este es un correo de prueba enviado desde el sistema.</p>";
            // Envío del correo
            boolean resultado = emailService.envioEmail(emisor, password, receptores, asunto, mensaje);

            // Respuesta estructurada
            Map<String, Object> response = new HashMap<>();
            response.put("success", resultado);
            response.put("message", resultado ? "Correo enviado exitosamente" : "Error al enviar el correo");
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
}