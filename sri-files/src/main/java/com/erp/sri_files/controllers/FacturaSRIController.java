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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sri")
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
    @Value("${eureka.service-url}")
    private String eurekaServiceUrl;

    public FacturaSRIController(FacturaSRIService facturaSRIService) {
        this.facturaSRIService = facturaSRIService;
    }

    @GetMapping("/generar-xml")
    public ResponseEntity<String> generarXmlFactura(@RequestParam Long idfactura) throws Exception {
        System.out.println("VAMOS A GENERAR EL XML");

        Factura factura = dao.findByIdfactura(idfactura);
        if (factura == null) {
            System.out.println("FACTURA NULL");
            // Construimos la URL del microservicio
            String url = eurekaServiceUrl + ":8080/fec_factura/createFacElectro?idfactura=" + idfactura;
            return ResponseEntity.noContent().build();
        }
        // Generar XML sin firmar
        String xml = facturaSRIService.generarXmlFactura(factura);
        // Guardar XML firmado en disco
        FacturaSRIService.saveXml(
                xml,
                "Fac_" + factura.getEstablecimiento() + "-" +
                        factura.getPuntoemision() + "-" + factura.getSecuencial() + ".xml"
        );
        return ResponseEntity.ok(xml);
    }
    @GetMapping("/generar-pdf")
    public ResponseEntity<Resource> generarPdf(@RequestParam Long idfactura) {
        Factura factura = dao.findByIdfactura(idfactura);
        if (factura == null || factura.getXmlautorizado() == null) {
            return ResponseEntity.noContent().build();
        }

        String xmlAutorizado = factura.getXmlautorizado();
        LocalDateTime fechaEmision = factura.getFechaemision();
        if (fechaEmision == null) {
            return ResponseEntity.badRequest().build();
        }

        // Compararemos como LocalDate para evitar problemas de hora
        LocalDate emisionDate = fechaEmision.toLocalDate();
        LocalDate fechaLimite = LocalDate.of(2025, 5, 6);

        try {
            ByteArrayOutputStream pdfStream;

            // Si fechaEmision >= fechaLimite -> generarFacturaPDF
            // Si fechaEmision <  fechaLimite -> generarFacturaPDF_v2
            if (!emisionDate.isBefore(fechaLimite)) {
                // emisionDate es igual o posterior a fechaLimite
                pdfStream = xmlToPdfService.generarFacturaPDF(xmlAutorizado);
            } else {
                // emisionDate es anterior a fechaLimite
                pdfStream = xmlToPdfService.generarFacturaPDF_v2(xmlAutorizado);
            }

            if (pdfStream == null || pdfStream.size() == 0) {
                throw new RuntimeException("No se pudo generar el PDF.");
            }

            InputStreamResource resource =
                    new InputStreamResource(new ByteArrayInputStream(pdfStream.toByteArray()));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfStream.size())
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


/*
    @GetMapping("/generar-pdf")
    public ResponseEntity<Resource> generarPdf(@RequestParam Long idfactura) {

        Factura factura = dao.findByIdfactura(idfactura);
        if (factura == null || factura.getXmlautorizado() == null) {
            return ResponseEntity.noContent().build();
        }
        String xmlAutorizado = factura.getXmlautorizado();
        LocalDateTime fehchaemision = factura.getFechaemision();
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
    }*/

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