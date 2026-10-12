package com.erp.sri_files.scheduler;

import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.services.SriAutorizacionRetryPolicy;
import com.erp.sri_files.services.XmlAutorizadoRecoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RecuperarDevueltasSchedulerTest {
    private static final String KEY = "0810202601046002881000120010150000291908329164211";
    private final FacturaR invoices = mock(FacturaR.class);
    private final XmlAutorizadoRecoveryService recovery = mock(XmlAutorizadoRecoveryService.class);
    private final RecuperarDevueltasScheduler scheduler = new RecuperarDevueltasScheduler(invoices, recovery, new SriAutorizacionRetryPolicy());
    private Factura invoice;
    @BeforeEach void setup() {
        invoice = new Factura(); invoice.setIdfactura(123L); invoice.setEstado("M");
        invoice.setClaveacceso(" " + KEY + " "); invoice.setIntentos_autorizacion(50);
        when(invoices.findDevueltasVencidas(any(), eq(60000L), eq(1800000L), eq(20))).thenReturn(List.of(invoice));
    }
    @Test void persistsRecoveredXmlUsingConditionalUpdateInsteadOfMergingInvoice() {
        var date = LocalDateTime.of(2026, 10, 8, 14, 0);
        when(recovery.recuperar(KEY, false)).thenReturn(new XmlAutorizadoRecoveryService.Resultado(
                "<factura/>", "BD_documento_electronico", "AUTORIZADO", KEY, date, null));
        when(invoices.guardarXmlRecuperadoDevuelta(123L, KEY, "<factura/>", date)).thenReturn(1);
        scheduler.recuperarFacturasDevueltas();
        verify(invoices).guardarXmlRecuperadoDevuelta(123L, KEY, "<factura/>", date);
        verify(invoices, never()).save(any());
        verify(invoices, never()).registrarRecuperacionPendiente(any(), any(), any(), any());
    }
    @Test void noXmlKeepsMDespiteMoreThanFiveAttempts() {
        when(recovery.recuperar(KEY, false)).thenReturn(new XmlAutorizadoRecoveryService.Resultado(
                null, "SRI", null, null, null, "Sin autorizaciones"));
        scheduler.recuperarFacturasDevueltas();
        assertEquals("M", invoice.getEstado());
        verify(invoices).registrarRecuperacionPendiente(eq(123L), eq(KEY), eq("Sin autorizaciones"), any());
        verify(invoices, never()).guardarXmlRecuperadoDevuelta(any(), any(), any(), any());
        verify(invoices, never()).save(any());
    }
    @Test void networkFailureIsPersistedForRetry() {
        when(recovery.recuperar(KEY, false)).thenThrow(new RuntimeException("Connection reset"));
        scheduler.recuperarFacturasDevueltas();
        assertEquals("M", invoice.getEstado());
        verify(invoices).registrarRecuperacionPendiente(eq(123L), eq(KEY), contains("Connection reset"), any());
    }
    @Test void invalidKeyRemainsDiagnosableAndDoesNotDiscardOtherInvoices() {
        invoice.setClaveacceso(null);
        when(recovery.recuperar("", false)).thenThrow(new IllegalArgumentException("Clave invalida"));
        scheduler.recuperarFacturasDevueltas();
        verify(invoices).registrarRecuperacionPendiente(eq(123L), eq(""), contains("Clave invalida"), any());
        assertEquals("M", invoice.getEstado());
    }
    @Test void concurrentCycleCannotEnterAgain() {
        when(recovery.recuperar(KEY, false)).thenAnswer(call -> {
            scheduler.recuperarFacturasDevueltas();
            return new XmlAutorizadoRecoveryService.Resultado(null, "SRI", null, null, null, "Pendiente");
        });
        scheduler.recuperarFacturasDevueltas();
        verify(invoices, times(1)).findDevueltasVencidas(any(), anyLong(), anyLong(), anyInt());
    }
    @Test void changedStateDuringQueryDoesNotCauseAnEntitySave() {
        when(recovery.recuperar(KEY, false)).thenReturn(new XmlAutorizadoRecoveryService.Resultado(
                "<factura/>", "SRI", "AUTORIZADO", KEY, null, null));
        when(invoices.guardarXmlRecuperadoDevuelta(anyLong(), anyString(), anyString(), any())).thenReturn(0);
        scheduler.recuperarFacturasDevueltas();
        verify(invoices, never()).save(any());
        verify(invoices, never()).registrarRecuperacionPendiente(any(), any(), any(), any());
    }
}
