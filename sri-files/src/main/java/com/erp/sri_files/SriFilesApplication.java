package com.erp.sri_files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
@EnableRetry
public class SriFilesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SriFilesApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    static {
        org.apache.xml.security.Init.init();
        System.setProperty("org.apache.xml.security.ignoreLineBreaks", "true");

        // === SSL BYPASS PARA SRI ===
        // Debe ejecutarse ANTES de que cualquier clase cree conexiones HTTPS
        try {
            TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] c, String a) {}
                    public void checkServerTrusted(X509Certificate[] c, String a) {}
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
            };

            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(null, trustAll, new SecureRandom());

            // Estos 3 juntos cubren todos los caminos posibles
            SSLContext.setDefault(ctx);
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((h, s) -> true);

            // Forzar properties JVM
            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
            System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");

            System.out.println("[SSL-SRI] TLSv1.2 trust-all + hostname-bypass configurado correctamente");
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            System.err.println("[SSL-SRI] ERROR configurando SSL: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }
}
