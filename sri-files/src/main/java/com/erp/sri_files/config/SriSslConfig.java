package com.erp.sri_files.config;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SriSslConfig {

    private static final Logger log = LoggerFactory.getLogger(SriSslConfig.class);

    private static SSLContext sharedSslContext;
    private static HostnameVerifier sharedHostnameVerifier;

    @PostConstruct
    public void configureSsl() {
        try {
            // TrustManager que acepta todos los certificados (solo para SRI GOB)
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        log.debug("SSL: Accepting SRI certificate, chain length={}", chain.length);
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
            };

            // HostnameVerifier que acepta IPs del SRI
            sharedHostnameVerifier = (hostname, session) -> {
                if (hostname != null && (
                    hostname.contains("sri.gob.ec") ||
                    hostname.equals("181.113.227.222") ||
                    hostname.equals("181.113.227.223")
                )) {
                    log.debug("SSL: Accepting SRI host/IP: {}", hostname);
                    return true;
                }
                return HttpsURLConnection.getDefaultHostnameVerifier().verify(hostname, session);
            };

            // SSLContext con TLS 1.2
            sharedSslContext = SSLContext.getInstance("TLSv1.2");
            sharedSslContext.init(null, trustAllCerts, new SecureRandom());

            // Aplicar globalmente para HttpsURLConnection
            HttpsURLConnection.setDefaultSSLSocketFactory(sharedSslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(sharedHostnameVerifier);

            log.info("SSL configurado para SRI: TLSv1.2 + hostname bypass para IPs SRI");

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("Error configurando SSL para SRI: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo configurar SSL para SRI", e);
        }
    }

    public static SSLContext getSslContext() {
        return sharedSslContext;
    }

    public static HostnameVerifier getHostnameVerifier() {
        return sharedHostnameVerifier;
    }
}
