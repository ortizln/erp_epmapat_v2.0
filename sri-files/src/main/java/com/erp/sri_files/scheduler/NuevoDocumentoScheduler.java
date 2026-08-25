package com.erp.sri_files.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.erp.sri_files.models.DocumentoElectronico;
import com.erp.sri_files.repositories.DocumentoElectronicoR;
import com.erp.sri_files.services.SriGateway;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NuevoDocumentoScheduler {

    private static final Logger log = LoggerFactory.getLogger(NuevoDocumentoScheduler.class);

    private final DocumentoElectronicoR documentoR;
    private final SriGateway sriGateway;

    private static final int MAX_INTENTOS_AUTORIZACION = 5;
    private static final int MAX_INTENTOS_CORREO = 3;
    private static final int LOCK_EXPIRATION_MINUTES = 30;

    @Scheduled(cron = "${sri.scheduler.v1.procesar-pendientes:0 */1 * * * *}")
    public void procesarPendientesEnvio() {
        String threadName = Thread.currentThread().getName();
        MDC.put("scheduler", "v1-envio");
        
        long inicio = System.currentTimeMillis();
        int detectados = 0;
        int exitosos = 0;
        int fallidos = 0;

        try {
            log.info("[SCHEDULER][V1] INICIO envío pendientes @ {}", LocalDateTime.now());

            List<DocumentoElectronico> pendientes = documentoR.findPendientesProcesamiento(
                PageRequest.of(0, 10));
            detectados = pendientes.size();

            for (DocumentoElectronico doc : pendientes) {
                if (doc.getProcessingLock() != null && doc.getProcessingLock()) {
                    log.debug("Documento {} con lock activo, saltando", doc.getId());
                    continue;
                }

                try {
                    if (!doc.intentarLock("scheduler-v1")) {
                        log.debug("No se pudo adquirir lock para documento {}", doc.getId());
                        continue;
                    }
                    documentoR.save(doc);

                    boolean procesado = procesarDocumento(doc);
                    if (procesado) {
                        exitosos++;
                    } else {
                        fallidos++;
                    }

                } catch (Exception e) {
                    log.error("Error procesando documento {}", doc.getId(), e);
                    fallidos++;
                } finally {
                    doc.liberarLock();
                    documentoR.save(doc);
                    MDC.remove("documentoId");
                }
            }

        } catch (Exception e) {
            log.error("[SCHEDULER][V1] Error en ciclo de envío", e);
        } finally {
            long duracion = System.currentTimeMillis() - inicio;
            log.info("[SCHEDULER][V1] FIN envío | detectados={} | exitosos={} | fallidos={} | duracionMs={}",
                detectados, exitosos, fallidos, duracion);
            MDC.clear();
        }
    }

    @Scheduled(cron = "${sri.scheduler.v1.recuperar-autorizaciones:30 */3 * * * *}")
    public void recuperarAutorizacionesPendientes() {
        MDC.put("scheduler", "v1-autorizacion");
        
        long inicio = System.currentTimeMillis();
        int detectados = 0;
        int autorizados = 0;
        int pendientes = 0;

        try {
            log.info("[SCHEDULER][V1] INICIO recuperación autorizaciones @ {}", LocalDateTime.now());

            List<DocumentoElectronico> pendientesAuth = documentoR.findPendientesAutorizacion(
                PageRequest.of(0, 10));
            detectados = pendientesAuth.size();

            for (DocumentoElectronico doc : pendientesAuth) {
                try {
                    MDC.put("documentoId", String.valueOf(doc.getId()));
                    MDC.put("claveAcceso", doc.getClaveAcceso());

                    var authResult = sriGateway.consultarAutorizacion(doc.getClaveAcceso());

                    if (authResult.autorizado()) {
                        doc.setXmlAutorizado(authResult.xmlAutorizado());
                        doc.setNumeroAutorizacion(authResult.numeroAutorizacion());
                        doc.setFechaAutorizacionSri(LocalDateTime.now());
                        doc.setEstado("AUTORIZADO");
                        doc.setFechaUltimaActualizacion(LocalDateTime.now());
                        documentoR.save(doc);
                        autorizados++;
                        log.info("Documento {} autorizado: nro={}", doc.getId(), authResult.numeroAutorizacion());
                    } else {
                        doc.setIntentosAutorizacion(doc.getIntentosAutorizacion() + 1);
                        doc.setFechaUltimaActualizacion(LocalDateTime.now());
                        documentoR.save(doc);
                        pendientes++;
                        
                        if (doc.getIntentosAutorizacion() >= MAX_INTENTOS_AUTORIZACION) {
                            log.warn("Documento {} superó máximo de intentos de autorización", doc.getId());
                        }
                    }

                } catch (Exception e) {
                    log.error("Error consultando autorización para documento {}", doc.getId(), e);
                } finally {
                    MDC.remove("documentoId");
                    MDC.remove("claveAcceso");
                }
            }

        } catch (Exception e) {
            log.error("[SCHEDULER][V1] Error en recuperación de autorizaciones", e);
        } finally {
            long duracion = System.currentTimeMillis() - inicio;
            log.info("[SCHEDULER][V1] FIN recuperación | detectados={} | autorizados={} | pendientes={} | duracionMs={}",
                detectados, autorizados, pendientes, duracion);
            MDC.clear();
        }
    }

    @Scheduled(cron = "${sri.scheduler.v1.limpiar-locks:0 */5 * * * *}")
    public void limpiarLocksExpirados() {
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusMinutes(LOCK_EXPIRATION_MINUTES);
            List<DocumentoElectronico> locksExpirados = documentoR.findLocksExpirados(cutoff);
            
            for (DocumentoElectronico doc : locksExpirados) {
                log.warn("Liberando lock expirado para documento {}", doc.getId());
                doc.liberarLock();
                documentoR.save(doc);
            }
            
            if (!locksExpirados.isEmpty()) {
                log.info("Liberados {} locks expirados", locksExpirados.size());
            }
        } catch (Exception e) {
            log.error("Error limpiando locks expirados", e);
        }
    }

    private boolean procesarDocumento(DocumentoElectronico doc) {
        try {
            if (doc.getXmlFirmado() == null) {
                log.debug("Documento {} sin XML firmado, requiere procesamiento previo", doc.getId());
                return false;
            }

            var recepcionResult = sriGateway.enviarRecepcion(doc.getXmlFirmado());
            doc.setFechaEnvioSri(LocalDateTime.now());
            doc.setIntentosEnvio(doc.getIntentosEnvio() + 1);

            if (recepcionResult.resultado() == SriGateway.ResultadoRecepcion.RECIBIDA) {
                doc.setEstado("RECIBIDO_SRI");
                doc.setFechaUltimaActualizacion(LocalDateTime.now());
                documentoR.save(doc);
                return true;
            } else {
                doc.setEstado("ERROR");
                doc.setSubestado("RECEPCION_SRI_ERROR");
                doc.setMensajeSri(recepcionResult.mensaje());
                doc.setFechaUltimaActualizacion(LocalDateTime.now());
                documentoR.save(doc);
                return false;
            }
        } catch (Exception e) {
            log.error("Error en procesamiento de documento {}", doc.getId(), e);
            return false;
        }
    }
}
