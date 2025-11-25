package com.erp.sri_files.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Properties;

@Configuration
@EnableRetry
public class EmailConfig {

    /*CONFIGURACION PARA EL HOST
    *   mail:
    host: ${MAIL_HOST:smtp.elasticemail.com}
    port: ${MAIL_PORT:465}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
      mail.smtp.ssl.enable: true
      mail.smtp.ssl.checkserveridentity: true
      mail.smtp.ssl.trust: ${MAIL_HOST:smtp.elasticemail.com}
      mail.smtp.connectiontimeout: 8000
      mail.smtp.timeout: 8000
      mail.smtp.writetimeout: 8000
      mail.debug: true
    * */

    /*CONFIGURACION PARA EL GMAIL
    *   mail:
    host: smtp.gmail.com
    port: 587
    username: locote.fumadote81@gmail.com
    password: cson fgjh bzwb pikr
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
      mail.smtp.starttls.required: true
      mail.smtp.ssl.enable: false
      mail.smtp.ssl.checkserveridentity: true
      mail.smtp.ssl.trust: smtp.gmail.com
      mail.smtp.connectiontimeout: 8000
      mail.smtp.timeout: 8000
      mail.smtp.writetimeout: 8000
      mail.debug: true
    * */

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${app.mail.debug:true}")
    private boolean debug;

    @Value("${app.mail.from:${spring.mail.username}}")
    private String from;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);

       Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.ssl.trust", host);
        props.put("mail.smtp.ssl.checkserveridentity", "true");
        props.put("mail.debug", String.valueOf(debug));
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

/*  CONFIGURACION PARA EL GMAIL
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");   // activar STARTTLS
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.enable", "false");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.checkserveridentity", "true");

        props.put("mail.debug", String.valueOf(debug));

        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");*/


        return sender;
    }
}

