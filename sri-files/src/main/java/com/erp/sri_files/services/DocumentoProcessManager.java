package com.erp.sri_files.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.sri_files.models.DocumentoElectronico;
import com.erp.sri_files.repositories.DocumentoElectronicoR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentoProcessManager {

    private static final Logger log = LoggerFactory.getLogger(DocumentoProcessManager.class);

    private final DocumentoElectronicoR documentoR;
    private final ClaveAccesoService claveAccesoService;
    private final SignService signService;
    private final SriGateway sriGateway;

    public enum EtapaProceso {
        RECEPCION_JSON,
        VALIDACION,
        GENERACION_XML,
        FIRMA,
        RECEPCION_SRI,
        AUTORIZACION_SRI,
        GENERACION_RIDE,
        ENVIO_CORREO,
        FINALIZADO
    }

    public record ProcessResult(
        boolean exitoso,
        String estado,
        String mensaje,
        DocumentoElectronico documento
    ) {}

    @Transactional
    public ProcessResult recibirDocumento(DocumentoElectronico doc) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("tipoDocumento", doc.getTipoDocumento());
        
        try {
            log.info("Recibiendo documento {} externalId={} [requestId={}]", 
                doc.getTipoDocumento(), doc.getExternalId(), requestId);
            
            if (doc.getUuid() == null) {
                doc.setUuid(UUID.randomUUID().toString());
            }
            doc.setEstado("RECIBIDO");
            doc.setFechaRecepcionJson(LocalDateTime.now());
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            
            documentoR.save(doc);
            
            log.info("Documento guardado: id={} uuid={} [requestId={}]", 
                doc.getId(), doc.getUuid(), requestId);
            
            return new ProcessResult(true, "RECIBIDO", "Documento recibido correctamente", doc);
            
        } catch (Exception e) {
            log.error("Error recibiendo documento [requestId={}]", requestId, e);
            return new ProcessResult(false, "ERROR", "Error guardando documento: " + e.getMessage(), doc);
        } finally {
            MDC.remove("requestId");
        }
    }

    @Transactional
    public ProcessResult validar(DocumentoElectronico doc) {
        try {
            doc.setEstado("VALIDANDO");
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            documentoR.save(doc);
            
            if (doc.getClaveAcceso() != null && !doc.getClaveAcceso().isBlank()) {
                if (!claveAccesoService.validarClaveAcceso(doc.getClaveAcceso())) {
                    doc.setEstado("REQUIERE_REVISION");
                    doc.setSubestado("CLAVE_ACCESO_INVALIDA");
                    doc.setFechaUltimaActualizacion(LocalDateTime.now());
                    documentoR.save(doc);
                    return new ProcessResult(false, "REQUIERE_REVISION", 
                        "Clave de acceso inválida: " + doc.getClaveAcceso(), doc);
                }
            }
            
            doc.setEstado("VALIDADO");
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            documentoR.save(doc);
            
            return new ProcessResult(true, "VALIDADO", "Validación exitosa", doc);
            
        } catch (Exception e) {
            log.error("Error validando documento id={}", doc.getId(), e);
            doc.setEstado("ERROR");
            doc.setSubestado("VALIDACION_ERROR");
            doc.setMensajeSri(e.getMessage());
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            documentoR.save(doc);
            return new ProcessResult(false, "ERROR", e.getMessage(), doc);
        }
    }

    @Transactional
    public ProcessResult generarXml(DocumentoElectronico doc, String xmlGenerado) {
        try {
            doc.setEstado("GENERANDO_XML");
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            documentoR.save(doc);
            
            doc.setXmlGenerado(xmlGenerado);
            doc.setFechaGeneracionXml(LocalDateTime.now());
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            doc.setEstado("XML_GENERADO");
            documentoR.save(doc);
            
            return new ProcessResult(true, "XML_GENERADO", "XML generado correctamente", doc);
            
        } catch (Exception e) {
            log.error("Error guardando XML id={}", doc.getId(), e);
            doc.setEstado("ERROR");
            doc.setSubestado("XML_ERROR");
            doc.setMensajeSri(e.getMessage());
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            documentoR.save(doc);
            return new ProcessResult(false, "ERROR", e.getMessage(), doc);
        }
    }

    @Transactional
    public ProcessResult firmar(DocumentoElectronico doc, String xmlPlano) {
        try {
            doc.setEstado("FIRMANDO");
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            documentoR.save(doc);
            
            var firmaResult = signService.firmar(xmlPlano);
            
            if (!firmaResult.exitoso()) {
                doc.setEstado("ERROR");
                doc.setSubestado("FIRMA_ERROR");
                doc.setMensajeSri(firmaResult.mensaje());
                doc.setFechaUltimaActualizacion(LocalDateTime.now());
                documentoR.save(doc);
                return new ProcessResult(false, "ERROR", firmaResult.mensaje(), doc);
            }
            
            doc.setXmlFirmado(firmaResult.xmlFirmado());
            doc.setFechaFirma(LocalDateTime.now());
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            doc.setEstado("FIRMADO");
            documentoR.save(doc);
            
            return new ProcessResult(true, "FIRMADO", "Firma exitosa", doc);
            
        } catch (Exception e) {
            log.error("Error firmando documento id={}", doc.getId(), e);
            doc.setEstado("ERROR");
            doc.setSubestado("FIRMA_ERROR");
            doc.setMensajeSri(e.getMessage());
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            documentoR.save(doc);
            return new ProcessResult(false, "ERROR", e.getMessage(), doc);
        }
    }

    @Transactional
    public ProcessResult enviarASri(DocumentoElectronico doc) {
        try {
            doc.setEstado("ENVIANDO_SRI");
            doc.setIntentosEnvio(doc.getIntentosEnvio() + 1);
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            documentoR.save(doc);
            
            var recepcionResult = sriGateway.enviarRecepcion(doc.getXmlFirmado());
            
            doc.setFechaEnvioSri(LocalDateTime.now());
            
            if (recepcionResult.resultado() == SriGateway.ResultadoRecepcion.RECIBIDA) {
                doc.setEstado("RECIBIDO_SRI");
                doc.setFechaUltimaActualizacion(LocalDateTime.now());
                documentoR.save(doc);
                return new ProcessResult(true, "RECIBIDO_SRI", "Recibido por SRI", doc);
            } else {
                doc.setEstado("ERROR");
                doc.setSubestado("RECEPCION_SRI_ERROR");
                doc.setMensajeSri(recepcionResult.mensaje());
                doc.setFechaUltimaActualizacion(LocalDateTime.now());
                documentoR.save(doc);
                return new ProcessResult(false, "ERROR", recepcionResult.mensaje(), doc);
            }
            
        } catch (Exception e) {
            log.error("Error enviando a SRI id={}", doc.getId(), e);
            doc.setEstado("ERROR");
            doc.setSubestado("RECEPCION_SRI_ERROR");
            doc.setMensajeSri(e.getMessage());
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            documentoR.save(doc);
            return new ProcessResult(false, "ERROR", e.getMessage(), doc);
        }
    }

    @Transactional
    public ProcessResult consultarAutorizacion(DocumentoElectronico doc) {
        try {
            doc.setEstado("PENDIENTE_AUTORIZACION");
            doc.setIntentosAutorizacion(doc.getIntentosAutorizacion() + 1);
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            documentoR.save(doc);
            
            var authResult = sriGateway.consultarAutorizacionConPolling(
                doc.getXmlFirmado(), 10, 4000);
            
            if (authResult.autorizado()) {
                doc.setXmlAutorizado(authResult.xmlAutorizado());
                doc.setNumeroAutorizacion(authResult.numeroAutorizacion());
                doc.setFechaAutorizacionSri(LocalDateTime.now());
                doc.setEstado("AUTORIZADO");
                doc.setFechaUltimaActualizacion(LocalDateTime.now());
                documentoR.save(doc);
                return new ProcessResult(true, "AUTORIZADO", "Autorizado por SRI", doc);
            } else {
                doc.setEstado("NO_AUTORIZADO");
                doc.setMensajeSri(authResult.mensaje());
                doc.setFechaUltimaActualizacion(LocalDateTime.now());
                documentoR.save(doc);
                return new ProcessResult(false, "NO_AUTORIZADO", authResult.mensaje(), doc);
            }
            
        } catch (Exception e) {
            log.error("Error consultando autorización id={}", doc.getId(), e);
            doc.setEstado("ERROR");
            doc.setSubestado("AUTORIZACION_ERROR");
            doc.setMensajeSri(e.getMessage());
            doc.setFechaUltimaActualizacion(LocalDateTime.now());
            documentoR.save(doc);
            return new ProcessResult(false, "ERROR", e.getMessage(), doc);
        }
    }

    @Transactional
    public ProcessResult finalizar(DocumentoElectronico doc) {
        doc.setEstado("FINALIZADO");
        doc.setFechaUltimaActualizacion(LocalDateTime.now());
        documentoR.save(doc);
        return new ProcessResult(true, "FINALIZADO", "Proceso completado", doc);
    }

    public ProcessResult procesarFlujoCompleto(DocumentoElectronico doc, String xmlOriginal) {
        String prevRequestId = MDC.get("requestId");
        MDC.put("requestId", doc.getUuid());
        
        try {
            var result = recibirDocumento(doc);
            if (!result.exitoso()) return result;
            
            result = validar(doc);
            if (!result.exitoso()) return result;
            
            result = generarXml(doc, xmlOriginal);
            if (!result.exitoso()) return result;
            
            result = firmar(doc, xmlOriginal);
            if (!result.exitoso()) return result;
            
            result = enviarASri(doc);
            if (!result.exitoso()) return result;
            
            result = consultarAutorizacion(doc);
            if (!result.exitoso()) return result;
            
            result = finalizar(doc);
            return result;
            
        } finally {
            if (prevRequestId != null) {
                MDC.put("requestId", prevRequestId);
            } else {
                MDC.remove("requestId");
            }
        }
    }
}
