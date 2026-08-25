package com.erp.sri_files.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BatchJobScheduler {

    private static final Logger log = LoggerFactory.getLogger(BatchJobScheduler.class);

    private final BatchProcessorService batchProcessorService;
    private final BatchJobR batchJobR;

    @Scheduled(cron = "${sri.batch.enviar-sri:0 */2 * * * *}")
    public void enviarPendientesASri() {
        ejecutarSiNoHayActivo(BatchProcessorService.TipoProceso.ENVIAR_SRI, "scheduler-enviar-sri");
    }

    @Scheduled(cron = "${sri.batch.consultar-autorizacion:0 */3 * * * *}")
    public void consultarAutorizacionesPendientes() {
        ejecutarSiNoHayActivo(BatchProcessorService.TipoProceso.CONSULTAR_AUTORIZACION, "scheduler-consultar-auth");
    }

    @Scheduled(cron = "${sri.batch.reintentar-fallidos:0 */5 * * * *}")
    public void reintentarFallidos() {
        ejecutarSiNoHayActivo(BatchProcessorService.TipoProceso.REINTENTAR_FALLIDOS, "scheduler-reintentar");
    }

    @Scheduled(cron = "${sri.batch.enviar-correo:0 */4 * * * *}")
    public void enviarCorreosPendientes() {
        ejecutarSiNoHayActivo(BatchProcessorService.TipoProceso.ENVIAR_CORREO, "scheduler-correo");
    }

    private void ejecutarSiNoHayActivo(BatchProcessorService.TipoProceso tipo, String triggeredBy) {
        List<BatchJob> activos = batchJobR.findActivos();
        
        boolean yaActivo = activos.stream()
            .anyMatch(j -> j.getTipoProceso().equals(tipo.name()));
        
        if (yaActivo) {
            log.debug("Ya hay un job activo de tipo {}, saltando", tipo);
            return;
        }

        try {
            var job = batchProcessorService.crearJob(tipo, triggeredBy);
            batchProcessorService.ejecutarJob(job);
            log.info("Batch automático ejecutado: tipo={} uuid={}", tipo, job.getUuid());
        } catch (Exception e) {
            log.error("Error ejecutando batch automático tipo={}", tipo, e);
        }
    }
}
