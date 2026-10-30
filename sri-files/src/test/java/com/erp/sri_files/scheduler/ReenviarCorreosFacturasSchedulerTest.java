package com.erp.sri_files.scheduler;

import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.services.EnvioSriBatchService;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.mockito.Mockito.*;

class ReenviarCorreosFacturasSchedulerTest {
    private final FacturaR repo = mock(FacturaR.class);
    private final com.erp.sri_files.services.FacturaOutboxService outbox=mock(com.erp.sri_files.services.FacturaOutboxService.class);
    @org.junit.jupiter.api.BeforeEach void setup(){org.springframework.test.util.ReflectionTestUtils.setField(scheduler,"outbox",outbox);}
    private final EnvioSriBatchService envio = mock(EnvioSriBatchService.class);
    private final ReenviarCorreosFacturasScheduler scheduler = new ReenviarCorreosFacturasScheduler(repo, envio);
    @Test void advancesPastFailuresAndWrapsToRetryEarlierInvoices() {
        var f = new Factura(); f.setIdfactura(1L);
        when(repo.findCorreosPendientesDesde(eq(0L), any())).thenReturn(List.of(f));
        when(repo.findCorreosPendientesDesde(eq(1L), any())).thenReturn(List.of());
        scheduler.reenviarCorreosPendientes(); scheduler.reenviarCorreosPendientes();
        verify(outbox, times(2)).registrar(f);
        verify(repo).findCorreosPendientesDesde(eq(1L), any());
    }
    @Test void avoidsOverlappingCyclesAndContinuesAfterOneFailure() {
        var f = new Factura(); f.setIdfactura(1L);
        var g = new Factura(); g.setIdfactura(2L);
        when(repo.findCorreosPendientesDesde(eq(0L), any())).thenReturn(List.of(f, g));
        doAnswer(call -> {scheduler.reenviarCorreosPendientes();throw new IllegalStateException("fallo");}).when(outbox).registrar(f);
        scheduler.reenviarCorreosPendientes();
        verify(repo, times(1)).findCorreosPendientesDesde(anyLong(), any());
        verify(outbox).registrar(g);
    }
}
