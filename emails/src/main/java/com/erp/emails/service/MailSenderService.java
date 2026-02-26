package com.erp.emails.service;
import com.erp.emails.model.EmailAttachment;
import com.erp.emails.model.EmailMessage;
import com.erp.emails.repository.EmailAttachmentR;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class MailSenderService {

    private final JavaMailSender mailSender;
    private final EmailAttachmentR attachRepo;

    @Value("${mailms.from}")
    private String from;

    public MailSenderService(JavaMailSender mailSender, EmailAttachmentR attachRepo) {
        this.mailSender = mailSender;
        this.attachRepo = attachRepo;
    }

    public void sendNow(EmailMessage msg) {
        List<EmailAttachment> attachments = attachRepo.findByEmailId(msg.getId());

        MimeMessage mime = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(splitCsv(msg.getToRecipients()));

            if (msg.getCcRecipients() != null) helper.setCc(splitCsv(msg.getCcRecipients()));
            if (msg.getBccRecipients() != null) helper.setBcc(splitCsv(msg.getBccRecipients()));

            helper.setSubject(msg.getSubject());

            if (msg.getBodyHtml() != null && !msg.getBodyHtml().isBlank()) {
                helper.setText(msg.getBodyText(), msg.getBodyHtml());
            } else {
                helper.setText(msg.getBodyText() != null ? msg.getBodyText() : "");
            }

            for (EmailAttachment att : attachments) {
                File f = new File(att.getStorageRef());
                helper.addAttachment(att.getFilename(), new FileSystemResource(f), att.getContentType());
            }

            mailSender.send(mime);
        } catch (Exception e) {
            throw new RuntimeException("Falló envío SMTP: " + e.getMessage(), e);
        }
    }

    private String[] splitCsv(String csv) {
        return csv.split("\\s*,\\s*");
    }
}