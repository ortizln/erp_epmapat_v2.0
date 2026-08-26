package com.erp.sri_files.config;

import javax.net.ssl.*;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SriSslConfig {

    public static SSLContext getSslContext() {
        try { return SSLContext.getDefault(); } catch (Exception e) { return null; }
    }

    public static HostnameVerifier getHostnameVerifier() {
        return HttpsURLConnection.getDefaultHostnameVerifier();
    }
}
