package com.erp.sri_files.controllers;

import com.erp.sri_files.exceptions.FacturaElectronicaException;
import com.erp.sri_files.interfaces.fecFacturaDatos;
import com.erp.sri_files.models.Definir;
import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.services.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

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

    private final FacturaSRIService facturaSRIService;
    @Value("${xml.storage.path}")
    private String xmlStoragePath;

    @Autowired
    private AllMicroServices allMicroServices;

    public FacturaSRIController(FacturaSRIService facturaSRIService) {
        this.facturaSRIService = facturaSRIService;
    }

    @GetMapping("/generar-xml")
    public ResponseEntity<String> generarXmlFactura(@RequestParam Long idfactura) throws Exception {
        Definir definir = definirService.findById(1L).orElseThrow(() -> new RuntimeException("Definir no encontrado"));
        try {
            Factura factura = allMicroServices.findById(idfactura);
            if (factura == null) {
                return ResponseEntity.noContent().build();
            } else {
                String xml = facturaSRIService.generarXmlFactura(factura);
                String xmlFirmado = xmlSignerService.signXml(xml, definir.getFirma(), "Junior2012");
                FacturaSRIService.saveXml(xmlFirmado, ".//xmlFiles//Fac_" + factura.getEstablecimiento() + "-"
                        + factura.getPuntoemision() + "-" + factura.getSecuencial() + ".xml");
                return ResponseEntity.ok(xml);
            }
        } catch (
        FacturaElectronicaException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping(value = "/enviar", consumes = { "multipart/form-data" })
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

    /*
     * @GetMapping("/firmar-xml")
     * public ResponseEntity<Object> firmarDoc(File xmlFile, File p12File, String
     * p12Password, String alias)
     * throws Exception {
     * Document doc = XmlSignerService.signXml(xmlFile, p12File, p12Password,
     * alias);
     * return ResponseEntity.ok(doc);
     * }
     */

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
                pdfStream = xmlToPdfService.generarFacturaPDF(xmlAutorizado);

            } else if (fehchaemision.isBefore(fechaLimite)) {
                pdfStream = xmlToPdfService.generarFacturaPDF_v2(xmlAutorizado);

            } else if (fehchaemision.isEqual(fechaLimite)) {
                pdfStream = xmlToPdfService.generarFacturaPDF(xmlAutorizado);
            } else {
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