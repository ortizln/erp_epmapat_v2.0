package com.erp.sri_files.services;

import com.erp.sri_files.models.Factura;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/** Backoff durable usando los campos existentes de intentos y último intento. */
@Component
public class SriAutorizacionRetryPolicy {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SriAutorizacionRetryPolicy.class);
    @Value("${sri.autorizacion.retry.base-ms:60000}")
    private long baseMs = 60000;
    @Value("${sri.autorizacion.retry.max-ms:1800000}")
    private long maxMs = 1800000;

    @PostConstruct
    void validarConfiguracion() {
        if (baseMs < 1000 || maxMs < baseMs || maxMs > 86400000)
            throw new IllegalArgumentException("Backoff SRI: 1000 <= base-ms <= max-ms <= 86400000");
    }
    public long getBaseMs() { return baseMs; }
    public long getMaxMs() { return maxMs; }

    // Jitter reproducible por factura e intento, consistente con la selección SQL.
    public long demoraMs(Long id, Integer intentos) {
        int n = Math.max(0, intentos == null ? 0 : intentos);
        long techo = Math.min(maxMs, baseMs * (1L << Math.min(20, Math.max(0, n - 1))));
        long bucket = Math.floorMod(Math.floorMod(id == null ? 0 : id, 1000) + (long) n * 37, 1000);
        return techo * (1000 + bucket) / 2000;
    }
    public boolean corresponde(Factura f, LocalDateTime ahora) {
        return f.getFecha_ultimo_intento() == null
                || !f.getFecha_ultimo_intento().plusNanos(demoraMs(f.getIdfactura(), f.getIntentos_autorizacion()) * 1000000).isAfter(ahora);
    }
    public void pendiente(Factura f, String mensaje) {
        int intentos = Math.max(0, f.getIntentos_autorizacion() == null ? 0 : f.getIntentos_autorizacion());
        f.setIntentos_autorizacion(intentos == Integer.MAX_VALUE ? intentos : intentos + 1);
        f.setFecha_ultimo_intento(LocalDateTime.now());
        f.setEstado("C");
        f.setErrores(mensaje == null ? "Pendiente de autorización SRI" : mensaje.substring(0, Math.min(1500, mensaje.length())));
        log.info("Consulta SRI programada idfactura={} intento={} esperaMs={}",
                f.getIdfactura(), f.getIntentos_autorizacion(), demoraMs(f.getIdfactura(), f.getIntentos_autorizacion()));
    }
    public void autorizada(Factura f) {
        f.setIntentos_autorizacion(0);
        f.setFecha_ultimo_intento(null);
        f.setErrores(null);
    }
}
