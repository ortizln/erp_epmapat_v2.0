package com.erp.sri_files.controllers;

import com.erp.sri_files.utils.FirmaComprobantesService;
import com.erp.sri_files.utils.FirmaComprobantesService.ModoFirma;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/firmas")
public class FirmaController {

    private final FirmaComprobantesService firmaService;

    public FirmaController(FirmaComprobantesService firmaService) {
        this.firmaService = firmaService;
    }

    public record FirmarResp(String xmlFirmado) {}

    /** Sube el XML como archivo y devuelve JSON con el xmlFirmado. */
    @PostMapping(
            path = "/factura/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public FirmarResp firmarFacturaUploadJson(
            @RequestPart("xml") MultipartFile xmlFile,     // el archivo XML
            @RequestParam("password") String password,     // pass del .p12
            @RequestParam(value = "modo", required = false, defaultValue = "XADES_BES") String modo,
            @RequestParam(value = "definirId", required = false, defaultValue = "1L") Long definirId
    ) throws Exception {

        String xmlPlano = toUtf8String(xmlFile);
        ModoFirma mf = "XMLDSIG".equalsIgnoreCase(modo) ? ModoFirma.XMLDSIG : ModoFirma.XADES_BES;

        String firmado = (definirId == null)
                ? firmaService.firmarFactura(xmlPlano, password, mf)
                : firmaService.firmarFactura(xmlPlano, definirId, password, mf);

        return new FirmarResp(firmado);
    }

    /** Sube el XML como archivo y devuelve directamente application/xml. */
    @PostMapping(
            path = "/factura/upload.xml",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public String firmarFacturaUploadXml(
            @RequestPart("xml") MultipartFile xmlFile,
            @RequestParam("password") String password,
            @RequestParam(value = "modo", required = false, defaultValue = "XADES_BES") String modo,
            @RequestParam(value = "definirId", required = false) Long definirId
    ) throws Exception {

        String xmlPlano = toUtf8String(xmlFile);
        ModoFirma mf = "XMLDSIG".equalsIgnoreCase(modo) ? ModoFirma.XMLDSIG : ModoFirma.XADES_BES;

        return (definirId == null)
                ? firmaService.firmarFactura(xmlPlano, password, mf)
                : firmaService.firmarFactura(xmlPlano, definirId, password, mf);
    }

    // --- Utilidad: lee el archivo como UTF-8 y limpia BOM si existe ---
    private static String toUtf8String(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Archivo XML vacío o no enviado (parte 'xml').");
        byte[] bytes = file.getBytes();
        String s = new String(bytes, StandardCharsets.UTF_8);
        // quita BOM si vino (evita romper la firma)
        if (!s.isEmpty() && s.charAt(0) == '\uFEFF') s = s.substring(1);
        return s;
    }

    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public org.springframework.http.ResponseEntity<String> handleBadRequest(Exception ex) {
        return org.springframework.http.ResponseEntity.badRequest().body(ex.getMessage());
    }
}