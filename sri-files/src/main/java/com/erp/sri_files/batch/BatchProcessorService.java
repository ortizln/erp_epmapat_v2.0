package com.erp.sri_files.batch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.sri_files.models.DocumentoElectronico;
import com.erp.sri_files.repositories.DocumentoElectronicoR;
import com.erp.sri_files.services.ClaveAccesoService;
import com.erp.sri_files.services.SignService;
import com.erp.sri_files.services.SriGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchProcessorService {

    private static final Logger log = LoggerFactory.getLogger(BatchProcessorService.class);

    private final BatchJobR batchJobR;
    private final DocumentoElectronicoR documentoR;
    private final ClaveAccesoService claveAccesoService;
    private final SignService signService;
    private final SriGateway sriGateway;

    public enum TipoProceso {
        ENVIAR_SRI,
        CONSULTAR_AUTORIZACION,
        REINTENTAR_FALLIDOS,
        ENVIAR_CORREO
    }

    @Transactional
    public BatchJob crearJob(TipoProceso tipo, String triggeredBy) {
        BatchJob job = new BatchJob();
        job.setUuid(UUID.randomUUID().toString());
        job.setTipoProceso(tipo.name());
        job.setEstado("PENDIENTE");
        job.setTriggeredBy(triggeredBy);
        job.setFechaInicio(LocalDateTime.now());
        return batchJobR.save(job);
    }

    @Async
    @Transactional
    public void ejecutarJob(BatchJob job) {
        String prevRequestId = MDC.get("requestId");
        MDC.put("requestId", job.getUuid());
        MDC.put("batchJob", job.getUuid());

        try {
            job.setEstado("EN_PROCESO");
            batchJobR.save(job);

            log.info("Iniciando batch job {} tipo={} [batchJob={}]", 
                job.getUuid(), job.getTipoProceso(), job.getUuid());

            List<DocumentoElectronico> documentos = obtenerDocumentosPendientes(job.getTipoProceso());
            job.setTotalDocumentos(documentos.size());
            batchJobR.save(job);

            log.info("Documentos encontrados: {} [batchJob={}]", documentos.size(), job.getUuid());

            for (DocumentoElectronico doc : documentos) {
                try {
                    boolean exitoso = procesarDocumento(doc, job.getTipoProceso());
                    job.incrementarProcesados(exitoso);
                    batchJobR.save(job);
                } catch (Exception e) {
                    log.error("Error procesando doc id={} [batchJob={}]", doc.getId(), job.getUuid(), e);
                    job.incrementarProcesados(false);
                    batchJobR.save(job);
                }
            }

            job.setEstado("COMPLETADO");
            job.setFechaFin(LocalDateTime.now());
            job.setMensaje(String.format("Procesados: %d, Exitosos: %d, Fallidos: %d", 
                job.getProcesados(), job.getExitosos(), job.getFallidos()));
            batchJobR.save(job);

            log.info("Batch job {} completado: {} [batchJob={}]", 
                job.getUuid(), job.getMensaje(), job.getUuid());

        } catch (Exception e) {
            log.error("Error fatal en batch job {} [batchJob={}]", job.getUuid(), job.getUuid(), e);
            job.setEstado("ERROR");
            job.setFechaFin(LocalDateTime.now());
            job.setMensaje("Error: " + e.getMessage());
            batchJobR.save(job);
        } finally {
            if (prevRequestId != null) {
                MDC.put("requestId", prevRequestId);
            } else {
                MDC.remove("requestId");
            }
            MDC.remove("batchJob");
        }
    }

    private List<DocumentoElectronico> obtenerDocumentosPendientes(String tipoProceso) {
        return switch (tipoProceso) {
            case "ENVIAR_SRI" -> documentoR.findByEstadoIn(List.of("FIRMADO", "VALIDADO"));
            case "CONSULTAR_AUTORIZACION" -> documentoR.findByEstadoIn(List.of("RECIBIDO_SRI", "PENDIENTE_AUTORIZACION"));
            case "REINTENTAR_FALLIDOS" -> documentoR.findByEstadoIn(List.of("ERROR", "NO_AUTORIZADO"));
            case "ENVIAR_CORREO" -> documentoR.findByEstadoAndMailEnviadoFalse("AUTORIZADO");
            default -> List.of();
        };
    }

    private boolean procesarDocumento(DocumentoElectronico doc, String tipoProceso) {
        return switch (tipoProceso) {
            case "ENVIAR_SRI" -> enviarASri(doc);
            case "CONSULTAR_AUTORIZACION" -> consultarAutorizacion(doc);
            case "REINTENTAR_FALLIDOS" -> reintentar(doc);
            case "ENVIAR_CORREO" -> enviarCorreo(doc);
            default -> false;
        };
    }

    private boolean enviarASri(DocumentoElectronico doc) {
        try {
            if (doc.getXmlFirmado() == null) {
                log.warn("Doc id={} no tiene XML firmado, saltando", doc.getId());
                return false;
            }

            var result = sriGateway.enviarRecepcion(doc.getXmlFirmado());
            doc.setFechaEnvioSri(LocalDateTime.now());
            doc.setIntentosEnvio(doc.getIntentosEnvio() + 1);

            if (result.resultado() == SriGateway.ResultadoRecepcion.RECIBIDA) {
                doc.setEstado("RECIBIDO_SRI");
                documentoR.save(doc);
                return true;
            } else {
                doc.setEstado("ERROR");
                doc.setSubestado("RECEPCION_SRI_ERROR");
                doc.setMensajeSri(result.mensaje());
                documentoR.save(doc);
                return false;
            }
        } catch (Exception e) {
            doc.setEstado("ERROR");
            doc.setSubestado("BATCH_ENVIO_ERROR");
            doc.setMensajeSri(e.getMessage());
            documentoR.save(doc);
            return false;
        }
    }

    private boolean consultarAutorizacion(DocumentoElectronico doc) {
        try {
            if (doc.getXmlFirmado() == null) {
                log.warn("Doc id={} no tiene XML firmado para consultar autorización", doc.getId());
                return false;
            }

            var result = sriGateway.consultarAutorizacionConPolling(doc.getXmlFirmado(), 5, 3000);

            if (result.autorizado()) {
                doc.setXmlAutorizado(result.xmlAutorizado());
                doc.setNumeroAutorizacion(result.numeroAutorizacion());
                doc.setFechaAutorizacionSri(LocalDateTime.now());
                doc.setEstado("AUTORIZADO");
                doc.setMensajeSri(null);
                documentoR.save(doc);
                return true;
            } else {
                doc.setIntentosAutorizacion(doc.getIntentosAutorizacion() + 1);
                doc.setMensajeSri(result.mensaje());
                documentoR.save(doc);
                return false;
            }
        } catch (Exception e) {
            doc.setIntentosAutorizacion(doc.getIntentosAutorizacion() + 1);
            doc.setMensajeSri(e.getMessage());
            documentoR.save(doc);
            return false;
        }
    }

    private boolean reintentar(DocumentoElectronico doc) {
        try {
            doc.setEstado("PENDIENTE_REENVIO");
            doc.setSubestado(null);
            doc.setMensajeSri(null);
            doc.setIntentosEnvio(0);
            doc.setIntentosAutorizacion(0);
            documentoR.save(doc);

            boolean enviado = enviarASri(doc);
            if (!enviado) return false;

            return consultarAutorizacion(doc);
        } catch (Exception e) {
            doc.setEstado("ERROR");
            doc.setSubestado("BATCH_REINTENTO_ERROR");
            doc.setMensajeSri(e.getMessage());
            documentoR.save(doc);
            return false;
        }
    }

    private boolean enviarCorreo(DocumentoElectronico doc) {
        try {
            if (doc.getXmlAutorizado() == null) {
                log.warn("Doc id={} no tiene XML autorizado para enviar correo", doc.getId());
                return false;
            }

            doc.setMailEnviado(true);
            doc.setFechaEnvioCorreo(LocalDateTime.now());
            documentoR.save(doc);

            log.info("Correo enviado para doc id={} clave={}", doc.getId(), doc.getClaveAcceso());
            return true;
        } catch (Exception e) {
            doc.setMailEnviado(false);
            doc.setIntentosCorreo(doc.getIntentosCorreo() + 1);
            documentoR.save(doc);
            return false;
        }
    }
}
