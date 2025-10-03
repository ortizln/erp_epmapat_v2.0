package com.erp.sri_files.services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

@Service
public class EmailServicePerCall {

    /**
     * Crea un JavaMailSender con props a medida y envía correo con adjuntos.
     */
    public boolean enviarConProps(
            String emisor,
            String password,
            List<String> receptores,
            String asunto,
            String htmlMensaje,
            String nombreXml, byte[] xmlBytes,
            String nombrePdf, byte[] pdfBytes,

            // Props por llamada:
            String host,
            int port,
            boolean sslEnable,            // 465 => true
            boolean starttlsEnable,       // 587 => true
            boolean checkServerIdentity,  // true para validar CN/SAN
            String sslTrustHost,          // host del cert (evita usar "*")
            int connectTimeoutMs,
            int readTimeoutMs,
            int writeTimeoutMs,
            boolean debug
    ) {
        try {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(host);
            sender.setPort(port);
            sender.setUsername(emisor);
            sender.setPassword(password);

            Properties p = sender.getJavaMailProperties();
            p.put("mail.transport.protocol", "smtp");
            p.put("mail.smtp.auth", "true");
            p.put("mail.smtp.starttls.enable", String.valueOf(starttlsEnable));
            p.put("mail.smtp.ssl.enable", String.valueOf(sslEnable));
            p.put("mail.smtp.ssl.checkserveridentity", String.valueOf(checkServerIdentity));
            if (sslTrustHost != null && !sslTrustHost.isBlank()) {
                p.put("mail.smtp.ssl.trust", sslTrustHost);
            }
            p.put("mail.smtp.connectiontimeout", String.valueOf(connectTimeoutMs));
            p.put("mail.smtp.timeout",           String.valueOf(readTimeoutMs));
            p.put("mail.smtp.writetimeout",      String.valueOf(writeTimeoutMs));
            p.put("mail.debug", String.valueOf(debug));

            MimeMessage mime = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, StandardCharsets.UTF_8.name());

            helper.setFrom(emisor);
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

            sender.send(mime);
            return true;
        } catch (Exception ex) {
            System.out.println("Error enviando correo (props): " + ex.getMessage());
            return false;
        }
    }
}