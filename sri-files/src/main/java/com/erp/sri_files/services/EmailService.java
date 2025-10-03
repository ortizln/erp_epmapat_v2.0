package com.erp.sri_files.services;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender; // autoconfigurado por spring.mail.*
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía un correo con XML y PDF adjuntos (cualquiera de los dos puede ser null).
     */
    public boolean enviarXmlYPdf(
            String emisor,
            String password,// opcional: si no seteas, el server pondrá el username
            List<String> receptores,
            String asunto,
            String htmlMensaje,
            String nombreXml, byte[] xmlBytes,
            String nombrePdf, byte[] pdfBytes
    ) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, StandardCharsets.UTF_8.name());

            if (emisor != null && !emisor.isBlank()) {
                helper.setFrom(emisor);
            }
            helper.setTo(receptores.toArray(String[]::new));
            helper.setSubject((asunto == null || asunto.isBlank()) ? "Factura electrónica" : asunto);
            helper.setText((htmlMensaje == null || htmlMensaje.isBlank()) ? "<p>Adjunto comprobante.</p>" : htmlMensaje, true);

            if (xmlBytes != null && xmlBytes.length > 0) {
                helper.addAttachment(
                        (nombreXml == null || nombreXml.isBlank()) ? "comprobante.xml" : nombreXml,
                        new ByteArrayResource(xmlBytes),
                        "application/xml"
                );
            }
            if (pdfBytes != null && pdfBytes.length > 0) {
                helper.addAttachment(
                        (nombrePdf == null || nombrePdf.isBlank()) ? "comprobante.pdf" : nombrePdf,
                        new ByteArrayResource(pdfBytes),
                        "application/pdf"
                );
            }

            mailSender.send(mime);
            return true;
        } catch (Exception ex) {
            System.out.println("Error enviando correo: " + ex.getMessage());
            return false;
        }
    }
}
