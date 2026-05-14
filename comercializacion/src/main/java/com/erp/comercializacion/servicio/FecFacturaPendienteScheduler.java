package com.erp.comercializacion.servicio;

import com.erp.comercializacion.modelo.Fec_factura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FecFacturaPendienteScheduler {
    private static final Logger log = LoggerFactory.getLogger(FecFacturaPendienteScheduler.class);
    private static final int LOTE_PENDIENTES = 25;

    private final Fec_facturaService fecFacturaService;
    private final FecFacturaPostAuthorizationService postAuthorizationService;

    public FecFacturaPendienteScheduler(Fec_facturaService fecFacturaService,
                                        FecFacturaPostAuthorizationService postAuthorizationService) {
        this.fecFacturaService = fecFacturaService;
        this.postAuthorizationService = postAuthorizationService;
    }

    @Scheduled(fixedDelayString = "${app.fec-factura.scheduler.pending-delay-ms:300000}")
    public void reprocesarPendientesAutorizacion() {
        List<Fec_factura> pendientes = fecFacturaService.findPendientesAutorizacion(LOTE_PENDIENTES);
        if (pendientes.isEmpty()) {
            return;
        }

        log.info("Reprocesando {} facturas electronicas pendientes de XML autorizado", pendientes.size());
        for (Fec_factura factura : pendientes) {
            fecFacturaService.recuperarXmlAutorizado(factura)
                    .ifPresent(postAuthorizationService::procesarFacturaAutorizada);
        }
    }
}
