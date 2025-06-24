package com.erp.sri_files.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import com.erp.sri_files.models.Definir;
import org.springframework.stereotype.Service;

import com.erp.sri_files.models.Factura;

@Service
public class ClaveAccesoGenerator {

    public String generarClaveAcceso(Factura factura, Definir definir) {

        // 1. Fecha de emisión (DDMMYYYY)
        String fechaEmision = formatToDDMMYYYY(factura.getFechaemision());
        System.out.println("fecha emision" + fechaEmision);

        // 2. Tipo de comprobante (2 dígitos)
        String tipoComprobante = String.format("%02d", 1);
        System.out.println("tipocomprobante: " + tipoComprobante);

        // 3. RUC del emisor (13 dígitos)
        String ruc = definir.getRuc();
        System.out.println("ruc " + ruc);

        // 4. Ambiente (1 dígito: 1=Pruebas, 2=Producción)
        Byte ambiente = definir.getTipoambiente(); // "1" o "2"
        System.out.println("ambiente " + ambiente);

        // 5. Serie (4 dígitos establecimiento + 3 dígitos punto emisión)
        String serie = factura.getEstablecimiento() + factura.getPuntoemision();
        System.out.println("serie " + serie);

        // 6. Secuencial (9 dígitos)
        String secuencial = String.format("%09d", Long.parseLong(factura.getSecuencial()));
        System.out.println("secuancial " + secuencial);

        // 7. Código numérico (8 dígitos aleatorios)
        String codigoNumerico = generarCodigoNumerico();
        System.out.println("codigoNumerico: " + codigoNumerico);

        // 8. Tipo de emisión (1 dígito, normal=1)
        byte tipoEmision = definir.getTipoambiente(); // Normal
        System.out.println("tipoEmision: " + tipoEmision);

        // Concatenar todos los componentes
        String claveAcceso = fechaEmision + tipoComprobante + ruc + ambiente +
                serie + secuencial + codigoNumerico + tipoEmision;
        System.out.println("claveAcceso: " + claveAcceso);

        // 9. Calcular dígito verificador
        /*
         * String digitoVerificador = calcularDigitoVerificador(claveAcceso);
         * System.out.println("digitoVerificador: "+ digitoVerificador);
         */

        // Retornar clave de acceso completa (49 dígitos)
        return claveAcceso;
        /* return claveAcceso + digitoVerificador; */
    }

    public static String formatToDDMMYYYY(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        return dateTime.format(formatter);
    }

    private static String generarCodigoNumerico() {
        // Generar 8 dígitos aleatorios
        long randomNum = ThreadLocalRandom.current().nextLong(10000000L, 99999999L);
        return String.valueOf(randomNum);
    }

    private static String calcularDigitoVerificador(String claveAcceso43) {
        if (claveAcceso43 == null || claveAcceso43.length() != 43) {
            throw new IllegalArgumentException("La clave de acceso debe tener exactamente 43 dígitos");
        }

        int[] patrones = { 2, 3, 4, 5, 6, 7 };
        int suma = 0;
        int j = 0;

        // Recorrer desde el final hacia el inicio
        for (int i = claveAcceso43.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(claveAcceso43.charAt(i));
            suma += digito * patrones[j];
            j = (j + 1) % patrones.length; // repetir patrón
        }

        int modulo = suma % 11;
        int digitoVerificador = (modulo == 0 || modulo == 1) ? 0 : 11 - modulo;

        return String.valueOf(digitoVerificador);
    }

}
