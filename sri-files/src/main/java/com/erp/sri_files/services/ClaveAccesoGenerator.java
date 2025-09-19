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

        // 2. Tipo de comprobante (2 dígitos, por ejemplo factura = 01)
        String tipoComprobante = "01";

        // 3. RUC del emisor (13 dígitos)
        String ruc = definir.getRuc();

        // 4. Ambiente (1 dígito: 1=Pruebas, 2=Producción)
        String ambiente = definir.getTipoambiente().toString();

        // 5. Serie: 3 dígitos establecimiento + 3 dígitos punto de emisión
        String serie = String.format("%03d%03d",
                Integer.parseInt(factura.getEstablecimiento()),
                Integer.parseInt(factura.getPuntoemision()));

        // 6. Secuencial (9 dígitos)
        String secuencial = String.format("%09d", Long.parseLong(factura.getSecuencial()));

        // 7. Código numérico (8 dígitos aleatorios)
        String codigoNumerico = generarCodigoNumerico();

        // 8. Tipo de emisión (1 dígito, normal=1)
        String tipoEmision = "1";

        // Concatenar los 43 dígitos
        String claveSinDV = fechaEmision + tipoComprobante + ruc + ambiente +
                serie + secuencial + codigoNumerico + tipoEmision;

        // 9. Calcular dígito verificador módulo 11
        String digitoVerificador = calcularDigitoVerificador(claveSinDV);

        // 10. Retornar clave completa (49 dígitos)
        return claveSinDV + digitoVerificador;
    }


    // Formatear fecha a DDMMYYYY
    public static String formatToDDMMYYYY(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        return dateTime.format(formatter);
    }

    // Generar código numérico aleatorio de 8 dígitos
    private static String generarCodigoNumerico() {
        long randomNum = ThreadLocalRandom.current().nextLong(10000000L, 99999999L);
        return String.valueOf(randomNum);
    }

    // Calcular dígito verificador módulo 11
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
            j = (j + 1) % patrones.length;
        }

        int modulo = suma % 11;
        int digitoVerificador = (modulo == 0 || modulo == 1) ? 0 : 11 - modulo;

        return String.valueOf(digitoVerificador);
    }
}

