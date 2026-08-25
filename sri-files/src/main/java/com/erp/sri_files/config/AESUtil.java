package com.erp.sri_files.config;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    
    private static final String FALLBACK_CLAVE = "1234567890123456"; // Solo para backward-compatible con datos legacy
    private static final String ENV_CLAVE = "SRI_AES_SECRET_KEY";
    
    private static String obtenerClave() {
        String clave = System.getenv(ENV_CLAVE);
        if (clave != null && clave.length() == 16) {
            return clave;
        }
        // Fallback para datos cifrados legacy existentes
        // TODO: migrar todos los datos cifrados y eliminar fallback
        return FALLBACK_CLAVE;
    }
    
    public static String cifrar(String datos) throws Exception {
        SecretKeySpec key = new SecretKeySpec(obtenerClave().getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cifrado = cipher.doFinal(datos.getBytes());
        return Base64.getEncoder().encodeToString(cifrado);
    }

    public static String descifrar(String datosCifrados) throws Exception {
        SecretKeySpec key = new SecretKeySpec(obtenerClave().getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] descifrado = cipher.doFinal(Base64.getDecoder().decode(datosCifrados));
        return new String(descifrado);
    }
}