package com.erp.sri_files.services;

import com.erp.sri_files.dto.AttachmentDTO;
import com.erp.sri_files.dto.SendMailRequest;
import com.erp.sri_files.dto.TemplateMailRequest;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final int MAX_TOTAL_ATTACHMENTS_MB = 20;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String defaultFrom;

    public MailService(JavaMailSender mailSender,
                       TemplateEngine templateEngine,
                       @Value("${app.mail.from}") String defaultFrom) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.defaultFrom = defaultFrom;
    }

    /** Envío HTML + adjuntos + inline images (cid) */
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1500)) // opcional
    public void send(SendMailRequest req) throws Exception {
        validateAttachments(req.attachments());

        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true, java.nio.charset.StandardCharsets.UTF_8.name());

        // From
        String from = (req.from() == null || req.from().isBlank()) ? defaultFrom : req.from();
        helper.setFrom(from);

        // To/CC/BCC
        helper.setTo(req.to().toArray(String[]::new));
        if (req.cc() != null && !req.cc().isEmpty()) helper.setCc(req.cc().toArray(String[]::new));
        if (req.bcc() != null && !req.bcc().isEmpty()) helper.setBcc(req.bcc().toArray(String[]::new));

        // Asunto y cuerpo
        helper.setSubject(req.subject());
        helper.setText(req.htmlBody(), true);

        // Inline images (opcionales, cid: <img src="cid:logo.png">)
        if (req.inlineImages() != null) {
            for (var e : req.inlineImages().entrySet()) {
                byte[] bytes = java.util.Base64.getDecoder().decode(e.getValue());
                helper.addInline(e.getKey(), new org.springframework.core.io.ByteArrayResource(bytes));
            }
        }

        // Adjuntos
        if (req.attachments() != null) {
            for (AttachmentDTO a : req.attachments()) {
                byte[] data = java.util.Base64.getDecoder().decode(a.base64());
                helper.addAttachment(a.filename(),
                        new org.springframework.core.io.ByteArrayResource(data),
                        a.mimeType());
            }
        }

        mailSender.send(mime);
    }

    /** Envío por plantilla Thymeleaf */
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1500)) // opcional
    public void sendTemplate(TemplateMailRequest req) throws Exception {
        Context ctx = new Context(java.util.Locale.getDefault());
        if (req.model() != null) req.model().forEach(ctx::setVariable);
        String html = templateEngine.process(req.template(), ctx);

        SendMailRequest base = new SendMailRequest(
                req.from(), req.to(), req.cc(), req.bcc(),
                req.subject(), html, req.attachments(), null
        );
        send(base);
    }

    /** Helper: valida peso total adjuntos */
    private void validateAttachments(java.util.List<AttachmentDTO> atts) {
        if (atts == null || atts.isEmpty()) return;
        long totalBytes = 0L;
        for (AttachmentDTO a : atts) {
            byte[] data = java.util.Base64.getDecoder().decode(a.base64());
            totalBytes += data.length;
        }
        long totalMB = totalBytes / (1024 * 1024);
        if (totalMB > MAX_TOTAL_ATTACHMENTS_MB) {
            throw new IllegalArgumentException("Adjuntos superan " + MAX_TOTAL_ATTACHMENTS_MB + "MB");
        }
    }

    /** Ping SMTP (healthcheck básico) */
    public boolean smtpHealth() {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            msg.setFrom(new jakarta.mail.internet.InternetAddress(defaultFrom));
            msg.setRecipients(jakarta.mail.Message.RecipientType.TO,
                    jakarta.mail.internet.InternetAddress.parse(defaultFrom));
            msg.setSubject("(healthcheck) " + java.time.Instant.now());
            msg.setText("OK");
            // No lo envíes realmente—solo intentamos crear/validar. Si deseas prueba real, descomenta:
            // mailSender.send(msg);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
