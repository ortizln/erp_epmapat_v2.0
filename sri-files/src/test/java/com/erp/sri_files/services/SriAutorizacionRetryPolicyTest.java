package com.erp.sri_files.services;

import com.erp.sri_files.models.Factura;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class SriAutorizacionRetryPolicyTest {
    private final SriAutorizacionRetryPolicy policy = new SriAutorizacionRetryPolicy();
    @Test void esperaExponencialConJitterAcotadoYPersistente() {
        for (int intento = 1; intento < 50; intento++) {
            long techo = (long) Math.min(1800000, 60000 * Math.pow(2, Math.min(20, intento - 1)));
            long delay = policy.demoraMs(3040984L, intento);
            assertTrue(delay >= techo / 2 && delay <= techo);
            assertEquals(delay, new SriAutorizacionRetryPolicy().demoraMs(3040984L, intento));
        }
        assertNotEquals(policy.demoraMs(3040984L, 2), policy.demoraMs(3040128L, 2));
        assertTrue(policy.demoraMs(3040984L, Integer.MAX_VALUE) <= 1800000);
    }
    @Test void respetaLimiteTemporalSinDormirYNoDescartaPorNumeroDeIntentos() {
        Factura f = new Factura(); f.setIdfactura(3040984L); f.setIntentos_autorizacion(500);
        policy.pendiente(f, "Connection reset");
        assertEquals("C", f.getEstado()); assertEquals(501, f.getIntentos_autorizacion());
        assertFalse(policy.corresponde(f, f.getFecha_ultimo_intento()));
        var vence = f.getFecha_ultimo_intento().plusNanos(policy.demoraMs(f.getIdfactura(), 501) * 1000000);
        assertFalse(policy.corresponde(f, vence.minusNanos(1)));
        assertTrue(policy.corresponde(f, vence));
        policy.autorizada(f);
        assertEquals(0, f.getIntentos_autorizacion()); assertNull(f.getFecha_ultimo_intento());
    }
    @Test void primeraConsultaEsInmediata() {
        assertTrue(policy.corresponde(new Factura(), LocalDateTime.now()));
    }
}
