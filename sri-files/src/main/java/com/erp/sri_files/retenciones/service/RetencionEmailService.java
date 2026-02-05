package com.erp.sri_files.retenciones.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class RetencionEmailService {

    private final JavaMailSender mailSender;
    private final RetencionPdfService pdfService;

    @Value("${app.mail.from}")
    private String from;

    public RetencionEmailService(JavaMailSender mailSender, RetencionPdfService pdfService) {
        this.mailSender = mailSender;
        this.pdfService = pdfService;
    }

    public void enviarRetencion(
            String correoDestino,
            String subject,
            String body,
            String baseName,
            String xmlAutorizacionCompleta,
            byte[] pdfBytes
    ) throws Exception {
        byte[] xmlBytes = xmlAutorizacionCompleta.getBytes(StandardCharsets.UTF_8);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(from); // ver punto 2
        helper.setTo(correoDestino);
        helper.setSubject(subject);
        helper.setText(body, false);

        helper.addAttachment(baseName + ".pdf", new ByteArrayResource(pdfBytes));
        helper.addAttachment(baseName + ".xml", new ByteArrayResource(xmlBytes));

        mailSender.send(message);
    }

}