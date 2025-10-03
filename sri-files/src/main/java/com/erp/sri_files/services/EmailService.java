package com.erp.sri_files.services;

import com.erp.sri_files.models.EmailAttachment;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import java.io.File;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public boolean envioEmail(final String emisor, final String password, List<String> receptores,
            String asunto, String mensajeHtml) {
        boolean envioExitoso = true;
        String domiCorreo ="smtp.cmaginet.net";
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", domiCorreo );
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.user", emisor);
        props.setProperty("mail.smtp.password", password);
        props.put("mail.smtp.ssl.trust", domiCorreo);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");  // <- Esta línea es clave

        // Configuración adicional para mejor rendimiento
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emisor, password);
                }
            });

            MimeMessage message = new MimeMessage(session);

            // Configurar remitente
            message.setFrom(new InternetAddress(emisor));
            InternetAddress[] replyTo = { new InternetAddress(emisor) };
            message.setReplyTo(replyTo);

            // Configurar destinatarios
            InternetAddress[] destinos = new InternetAddress[receptores.size()];
            for (int i = 0; i < receptores.size(); i++) {
                destinos[i] = new InternetAddress(receptores.get(i));
            }
            message.addRecipients(Message.RecipientType.TO, destinos);

            // Asunto del correo
            message.setSubject(asunto, "UTF-8");

            // Contenido del mensaje en HTML
            MimeBodyPart contenidoHtml = new MimeBodyPart();
            contenidoHtml.setContent(mensajeHtml, "text/html; charset=utf-8");

            // Crear multipart para el mensaje
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(contenidoHtml);

            // Asignar contenido al mensaje
            message.setContent(multipart);

            // Enviar el mensaje con un tiempo de espera
            Transport.send(message, emisor, password);

        } catch (Exception e) {
            envioExitoso = false;

            // Manejo específico de excepciones comunes
            if (e instanceof AuthenticationFailedException) {
                System.err.println("Error de autenticación con el servidor SMTP");
            } else if (e instanceof SendFailedException) {
                System.err.println("Error al enviar a uno o más destinatarios");
            }
        }

        return envioExitoso;
    }

    public boolean enviarXmlYPdf(
            String emisor,
            String password, // <- si ya está en Spring, ignóralo
            List<String> receptores,
            String asunto,
            String htmlMensaje,
            String nombreXml, byte[] xmlBytes,
            String nombrePdf, byte[] pdfBytes
    ) {
        try {
            MimeMessage mime = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, StandardCharsets.UTF_8.name());

            helper.setFrom(emisor);
            helper.setTo(receptores.toArray(String[]::new));
            helper.setSubject(asunto);
            helper.setText(htmlMensaje, true);

            helper.addAttachment(nombreXml, new ByteArrayResource(xmlBytes), "application/xml");
            helper.addAttachment(nombrePdf, new ByteArrayResource(pdfBytes), "application/pdf");

            javaMailSender.send(mime);
            return true;
        } catch (Exception ex) {
            // log.error("Error enviando correo", ex);
            return false;
        }
    }
}

