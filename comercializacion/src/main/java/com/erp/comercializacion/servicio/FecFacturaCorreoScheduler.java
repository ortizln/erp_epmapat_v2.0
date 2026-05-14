package com.erp.comercializacion.servicio;

import com.erp.comercializacion.modelo.Fec_factura;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FecFacturaCorreoScheduler {
    private static final int LOTE_CORREOS = 25;

    private final Fec_facturaService fecFacturaService;
    private final FecFacturaPostAuthorizationService postAuthorizationService;

    public FecFacturaCorreoScheduler(Fec_facturaService fecFacturaService,
                                     FecFacturaPostAuthorizationService postAuthorizationService) {
        this.fecFacturaService = fecFacturaService;
        this.postAuthorizationService = postAuthorizationService;
    }

    @Scheduled(fixedDelayString = "${app.fec-factura.scheduler.mail-delay-ms:300000}")
    public void enviarFacturasAutorizadasPendientes() {
        List<Fec_factura> pendientes = fecFacturaService.findListasParaCorreo(LOTE_CORREOS);
        for (Fec_factura factura : pendientes) {
            postAuthorizationService.procesarFacturaAutorizada(factura);
        }
    }
}
