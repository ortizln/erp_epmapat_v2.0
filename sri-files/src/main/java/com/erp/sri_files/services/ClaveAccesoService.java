package com.erp.sri_files.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClaveAccesoService {

    private static final Logger log = LoggerFactory.getLogger(ClaveAccesoService.class);
    
    private static final DateTimeFormatter DDMMYYYY_CLAVE = DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final int[] PESOS_MOD11 = {2, 3, 4, 5, 6, 7};

    public String generarClaveAcceso(
            LocalDateTime fechaEmision,
            String tipoComprobante,
            String ruc,
            int ambiente,
            String establecimiento,
            String puntoEmision,
            String secuencial
    ) {
        String fecha = formatForClave(fechaEmision);
        String tipoComp = leftPadDigits(requireDigits(tipoComprobante, "Tipo comprobante"), 2);
        String rucFmt = leftPadDigits(requireDigits(ruc, "RUC"), 13);
        String amb = requireDigits(String.valueOf(ambiente), "Ambiente");
        if (!amb.equals("1") && !amb.equals("2")) {
            throw new IllegalArgumentException("Ambiente debe ser '1' o '2'");
        }
        String estab = leftPadDigits(requireDigits(establecimiento, "Establecimiento"), 3);
        String ptoEmi = leftPadDigits(requireDigits(puntoEmision, "Punto emisión"), 3);
        String serie = estab + ptoEmi;
        String sec = leftPadDigits(requireDigits(secuencial, "Secuencial"), 9);
        String codigoNumerico = generarCodigoNumerico8();
        String tipoEmision = "1";

        String base48 = fecha + tipoComp + rucFmt + amb + serie + sec + codigoNumerico + tipoEmision;
        if (base48.length() != 48 || !base48.chars().allMatch(Character::isDigit)) {
            throw new IllegalStateException("Base de claveAcceso inválida: longitud=" + base48.length());
        }

        char dv = calcularDigitoVerificadorModulo11(base48);
        String clave = base48 + dv;
        
        log.debug("Clave acceso generada: {} (tipo={}, serie={}, sec={})", 
            clave, tipoComprobante, serie, secuencial);
        
        return clave;
    }

    public boolean validarClaveAcceso(String clave) {
        if (clave == null || !clave.matches("\\d{49}")) {
            return false;
        }
        String base48 = clave.substring(0, 48);
        char dvEsperado = clave.charAt(48);
        char dvCalculado = calcularDigitoVerificadorModulo11(base48);
        return dvEsperado == dvCalculado;
    }

    public char calcularDigitoVerificadorModulo11(String base48) {
        Objects.requireNonNull(base48, "Base no puede ser nula");
        if (base48.length() != 48) {
            throw new IllegalArgumentException("Base debe tener 48 dígitos");
        }
        int suma = 0;
        int idx = 0;
        for (int i = base48.length() - 1; i >= 0; i--) {
            int digito = base48.charAt(i) - '0';
            suma += digito * PESOS_MOD11[idx];
            idx = (idx + 1) % PESOS_MOD11.length;
        }
        int mod = suma % 11;
        int dv = 11 - mod;
        if (dv == 11) dv = 0;
        else if (dv == 10) dv = 1;
        return (char) ('0' + dv);
    }

    public String extraerClaveAcceso(String claveAccesoCompleta) {
        if (claveAccesoCompleta != null && claveAccesoCompleta.matches("\\d{49}")) {
            return claveAccesoCompleta;
        }
        return null;
    }

    private static String formatForClave(LocalDateTime dt) {
        if (dt == null) throw new IllegalArgumentException("La fecha no puede ser nula");
        return dt.format(DDMMYYYY_CLAVE);
    }

    private static String generarCodigoNumerico8() {
        int n = ThreadLocalRandom.current().nextInt(0, 100_000_000);
        return String.format("%08d", n);
    }

    private static String leftPadDigits(String value, int length) {
        if (value == null) throw new IllegalArgumentException("Valor no puede ser nulo");
        if (value.length() >= length) return value.substring(value.length() - length);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length - value.length(); i++) sb.append('0');
        sb.append(value);
        return sb.toString();
    }

    private static String requireDigits(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " no puede ser nulo o vacío");
        }
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " debe contener solo dígitos: " + value);
        }
        return digits;
    }
}
