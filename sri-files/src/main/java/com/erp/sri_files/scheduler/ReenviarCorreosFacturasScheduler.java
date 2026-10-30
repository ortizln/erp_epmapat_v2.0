package com.erp.sri_files.scheduler;

import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.services.EnvioSriBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReenviarCorreosFacturasScheduler {
    private final FacturaR facturas;
    private final EnvioSriBatchService envio;
    private final AtomicBoolean ejecutando = new AtomicBoolean();
    @org.springframework.beans.factory.annotation.Autowired
    private com.erp.sri_files.services.FacturaOutboxService outbox;
    private long ultimoId;
    @Value("${sri.scheduler.reenvio-correos.lote:20}")
    private int lote = 20;

    @Scheduled(cron = "${sri.scheduler.reenvio-correos.cron:0 */5 * * * *}")
    public void reenviarCorreosPendientes() {
        if (!ejecutando.compareAndSet(false, true)) return;
        int aceptadas = 0, pendientes = 0;
        try {
            var pagina = PageRequest.of(0, Math.max(1, Math.min(lote, 200)));
            var candidatas = facturas.findCorreosPendientesDesde(ultimoId, pagina);
            if (candidatas.isEmpty() && ultimoId != 0) {
                ultimoId = 0;
                candidatas = facturas.findCorreosPendientesDesde(ultimoId, pagina);
            }
            for (var factura : candidatas) {
                try {
                    outbox.registrar(factura);
                    aceptadas++;
                } catch (Exception error) {
                    pendientes++;
                    log.warn("Reenvio correo pendiente idfactura={}", factura.getIdfactura(), error);
                } finally { ultimoId = factura.getIdfactura(); }
            }
            log.info("Reenvio correos detectadas={} aceptadas={} pendientes={}", candidatas.size(), aceptadas, pendientes);
        } catch (Exception error) {
            log.error("Error en scheduler de reenvio de correos", error);
        } finally { ejecutando.set(false); }
    }
}
