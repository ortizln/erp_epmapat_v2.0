package com.erp.comercializacion.servicio;

import com.erp.comercializacion.modelo.Fec_factura;
import com.erp.comercializacion.servicio.administracion.DefinirServicio;
import com.erp.config.AESUtil;
import com.erp.interfaces.DefinirProjection;
import com.erp.sri.services.EmailService;
import com.erp.sri.services.XmlToPdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FecFacturaPostAuthorizationService {
    private static final Logger log = LoggerFactory.getLogger(FecFacturaPostAuthorizationService.class);
    private static final LocalDate FECHA_LIMITE_PDF = LocalDate.of(2025, 5, 6);

    private final Fec_facturaService fecFacturaService;
    private final XmlToPdfService xmlToPdfService;
    private final EmailService emailService;
    private final DefinirServicio definirServicio;
    private final FecFacturaLogService logService;

    public FecFacturaPostAuthorizationService(
            Fec_facturaService fecFacturaService,
            XmlToPdfService xmlToPdfService,
            EmailService emailService,
            DefinirServicio definirServicio,
            FecFacturaLogService logService) {
        this.fecFacturaService = fecFacturaService;
        this.xmlToPdfService = xmlToPdfService;
        this.emailService = emailService;
        this.definirServicio = definirServicio;
        this.logService = logService;
    }

    public void procesarFacturaAutorizada(Fec_factura factura) {
        if (factura == null || factura.getIdfactura() == null) {
            return;
        }
        if (Boolean.TRUE.equals(factura.getMailEnviado()) || !"X".equals(factura.getEstado())) {
            return;
        }

        try {
            validarFacturaListaParaCorreo(factura);
            byte[] pdf = generarPdf(factura);
            MultipartFile adjunto = construirAdjuntoPdf(factura, pdf);
            DefinirProjection definir = definirServicio.findDefinirWithoutFirma(1L);
            String password = AESUtil.descifrar(definir.getClave_email());
            List<String> destinatarios = List.of(factura.getEmailcomprador().trim());
            String asunto = construirAsunto(definir, factura);
            String mensaje = construirMensaje(definir, factura);

            boolean enviado = emailService.envioEmail(definir.getEmail(), password, destinatarios, asunto, mensaje, adjunto);
            if (!enviado) {
                throw new IllegalStateException("El servicio SMTP no confirmo el envio del correo");
            }

            fecFacturaService.marcarCorreoEnviado(factura);
            logService.registrar(factura.getIdfactura(), "C", "Correo enviado con PDF adjunto a " + factura.getEmailcomprador());
        } catch (Exception e) {
            fecFacturaService.marcarErrorPostAutorizacion(factura, "Error en post-autorizacion: " + e.getMessage());
            logService.registrar(factura.getIdfactura(), factura.getEstado(), "Post-autorizacion fallida: " + e.getMessage());
            log.warn("No se pudo completar el post-proceso de la factura {}", factura.getIdfactura(), e);
        }
    }

    public void procesarFacturaAutorizada(Long idfactura) {
        Optional<Fec_factura> factura = fecFacturaService.findById(idfactura);
        factura.ifPresent(this::procesarFacturaAutorizada);
    }

    private void validarFacturaListaParaCorreo(Fec_factura factura) {
        if (factura.getXmlautorizado() == null || factura.getXmlautorizado().isBlank()) {
            throw new IllegalArgumentException("La factura no tiene XML autorizado");
        }
        if (factura.getEmailcomprador() == null || factura.getEmailcomprador().isBlank()) {
            throw new IllegalArgumentException("La factura no tiene email del comprador");
        }
    }

    private byte[] generarPdf(Fec_factura factura) {
        ByteArrayOutputStream pdfStream;
        LocalDate fechaEmision = factura.getFechaemision() == null ? LocalDate.now() : factura.getFechaemision().toLocalDate();
        if (fechaEmision.isBefore(FECHA_LIMITE_PDF)) {
            pdfStream = xmlToPdfService.generarFacturaPDF_v2(factura.getXmlautorizado());
        } else {
            pdfStream = xmlToPdfService.generarFacturaPDF(factura.getXmlautorizado());
        }
        byte[] pdf = pdfStream.toByteArray();
        if (pdf.length == 0) {
            throw new IllegalStateException("El PDF generado esta vacio");
        }
        logService.registrar(factura.getIdfactura(), "X", "PDF generado correctamente");
        return pdf;
    }

    private MultipartFile construirAdjuntoPdf(Fec_factura factura, byte[] pdf) {
        String nombre = "factura-" + factura.getSecuencial() + ".pdf";
        return new ByteArrayMultipartFile("file", nombre, "application/pdf", pdf);
    }

    private String construirAsunto(DefinirProjection definir, Fec_factura factura) {
        String asunto = definir.getAsunto();
        if (asunto == null || asunto.isBlank()) {
            asunto = "Factura electronica " + factura.getEstablecimiento() + "-" + factura.getPuntoemision() + "-" + factura.getSecuencial();
        }
        return reemplazarVariables(asunto, factura);
    }

    private String construirMensaje(DefinirProjection definir, Fec_factura factura) {
        String plantilla = definir.getTextomail();
        if (plantilla == null || plantilla.isBlank()) {
            plantilla = "<p>Estimado cliente, adjuntamos su factura electronica {numero}.</p>";
        }
        return reemplazarVariables(plantilla, factura);
    }

    private String reemplazarVariables(String texto, Fec_factura factura) {
        String numero = factura.getEstablecimiento() + "-" + factura.getPuntoemision() + "-" + factura.getSecuencial();
        return texto
                .replace("{numero}", numero)
                .replace("{cliente}", Optional.ofNullable(factura.getRazonsocialcomprador()).orElse(""))
                .replace("{claveAcceso}", Optional.ofNullable(factura.getClaveacceso()).orElse(""));
    }
}
