package com.erp.sri_files.services;

import com.erp.sri_files.dto.AttachmentDTO;
import com.erp.sri_files.dto.AutorizacionInfo;
import com.erp.sri_files.dto.AutorizacionSriResult;
import com.erp.sri_files.dto.SendMailRequest;
import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.utils.FirmaComprobantesService;
import com.erp.sri_files.utils.SriAutorizacionAdapter;
import ec.gob.sri.ws.recepcion.RespuestaSolicitud;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
@RequiredArgsConstructor
@Service
@Slf4j
public class EnvioSriBatchService {

    private static final int LOTE = 10;

    private final FacturaR facturaR;
    private final org.springframework.transaction.PlatformTransactionManager transactionManager;
    private final FacturaXmlGeneratorService xmlFacturaService;
    private final FirmaComprobantesService firmaService;
    private final SendXmlToSriService sendXmlToSriService;
    private final XmlToPdfService xmlToPdfService;
    private final MailService mailService;
    private final SriAutorizacionRetryPolicy retryPolicy;
    @org.springframework.beans.factory.annotation.Autowired
    private FacturaOutboxService outbox;
    @org.springframework.beans.factory.annotation.Autowired
    private FacturaPreparationService preparation;
    // private final StorageService storageService; // opcional, para guardar XMLs



    // ====== CONFIG desde application.properties ======
    @Value("${sri.firma.modo:XADES_BES}") // XADES_BES o XMLDSIG
    private String modoFirmaCfg;

    @Value("${sri.ambiente:0}") // 0 = deducir del XML, 1 = pruebas, 2 = producción
    private int ambienteForzado;

        @Scheduled(cron = "${sri.scheduler.envio-facturas.cron}") // ej: 0 */2 * * * *
        public void automatizacionEnvioFacturasElectonicas() {
            long started = System.currentTimeMillis();
            String threadName = Thread.currentThread().getName();
            logTaskStart("automatizacionEnvioFacturasElectonicas", threadName);
            log.info("Ejecutando envio de facturas at={}", LocalDateTime.now());
            try {
                List<Factura> facturas = facturaR.findEnviosSriVencidos(LocalDateTime.now(),
                        retryPolicy.getBaseMs(), retryPolicy.getMaxMs(), LOTE);
                int exitosas = 0;
                int fallidas = 0;

                if (facturas.isEmpty()) {
                    log.info("No hay facturas pendientes estado=I");
                    logTaskEnd("automatizacionEnvioFacturasElectonicas", threadName, started, 0, 0, 0);
                    return;
                }

                for (Factura f : facturas) {
                    try {
                        procesarFacturaEnNuevaTx(f.getIdfactura());
                        Factura resultado=facturaR.findById(f.getIdfactura()).orElseThrow();
                        boolean autorizada=resultado.getXmlautorizado()!=null && !resultado.getXmlautorizado().isBlank();
                        if (Boolean.TRUE.equals(autorizada)) exitosas++;
                        else fallidas++;
                    } catch (Exception ex) {
                        fallidas++;
                        log.error("Error procesando factura idfactura={}", f.getIdfactura(), ex);
                    }
                }

                log.info("Lote procesado total={}", facturas.size());
                logTaskEnd("automatizacionEnvioFacturasElectonicas", threadName, started, facturas.size(), exitosas, fallidas);
            } catch (Exception e) {
                logTaskError("automatizacionEnvioFacturasElectonicas", threadName, e);
                log.error("Error en la tarea programada automatizacionEnvioFacturasElectonicas", e);
            }
        }
    // Dominios que NO quieres que reciban correos
    private static final Set<String> DOMINIOS_BLOQUEADOS = Set.of(
            "epmapatulcan.gob.ec",
            "yimail.com",
            "gamil.com",
            "yahoo.com.mx",
            "hotail.com",
            "homail.com",
            "outloock.com",
            "gmaill.com",
            "hotamil.es",
            "hotmail.com.ar",
            "yahoo.com.ar",
            "YAHOO.COM"
            // agrega más: "hotmail.com", "yahoo.com", etc.
    );
//Este servicio sirve para conultar las facturas en estado C y volver a buscar el xml auotizado en el sri
    @Scheduled(cron = "${sri.scheduler.recuperacion-xml.cron}")
    public void automatizacionConsultarXml() {
        long started = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();
        logTaskStart("automatizacionConsultarXml", threadName);
        var candidatas = new ArrayList<>(facturaR.findConsultasSriVencidas(LocalDateTime.now(),
                retryPolicy.getBaseMs(), retryPolicy.getMaxMs(), LOTE));
        int autorizadas = 0;
        int pendientes = 0;
        for (Factura candidata : candidatas) {
            try {
                var tx = new org.springframework.transaction.support.TransactionTemplate(transactionManager);
                tx.setPropagationBehavior(Propagation.REQUIRES_NEW.value());
                Boolean autorizada = tx.execute(status -> consultarPendiente(candidata.getIdfactura()));
                if (Boolean.TRUE.equals(autorizada)) autorizadas++;
                else pendientes++;
            } catch (Exception ex) {
                pendientes++;
                log.error("Error procesando consulta SRI idfactura={}", candidata.getIdfactura(), ex);
            }
        }
        log.info("[SCHEDULER] FIN automatizacionConsultarXml | detectados={} | autorizadas={} | pendientes={} | duracionMs={}",
                candidatas.size(), autorizadas, pendientes, System.currentTimeMillis() - started);
    }

    boolean consultarPendiente(Long idFactura) {
        Factura f = facturaR.findParaProcesar(idFactura).orElseThrow();
        if (!Set.of("C", "O", "A", "P").contains(safeStr(f.getEstado()).toUpperCase(Locale.ROOT))
                || !retryPolicy.corresponde(f, LocalDateTime.now())) return false;
        if (f.getXmlautorizado() != null && !f.getXmlautorizado().isBlank()) {
            f.setEstado(Boolean.TRUE.equals(f.getMail_enviado()) ? "A" : "O");
            facturaR.save(f);
            if (!Boolean.TRUE.equals(f.getMail_enviado())) outbox.registrar(f);
            return true;
        }
        try {
            var resultado = sendXmlToSriService.consultar_Autorizacion(f.getClaveacceso());
            if (resultado != null && resultado.isAutorizado()
                    && resultado.getXmlAutorizado() != null && !resultado.getXmlAutorizado().isBlank()) {
                f.setXmlautorizado(FacturaAutorizacionXml.completo(resultado, f.getClaveacceso()));
                f.setFecha_autorizacion(resultado.getFechaAutorizacion());
                f.setEstado("O");
                f.setMail_enviado(false);
                f.setRecepcionConfirmada(true);
                retryPolicy.autorizada(f);
                facturaR.saveAndFlush(f);
                outbox.registrar(f);
            } else if (resultado != null && "NO AUTORIZADO".equalsIgnoreCase(resultado.getMensaje())) {
                f.setEstado("N");
                f.setErrores("NO AUTORIZADO: revisar respuesta SRI antes de reenviar");
            } else {
                // Solo reenviar el XML firmado original si NO existe confirmacion de recepcion
                // y el SRI respondio sin autorizaciones (un timeout no prueba ausencia).
                if (!Boolean.TRUE.equals(f.getRecepcionConfirmada())
                        && !safeStr(f.getXmlFirmadoEnvio()).isBlank()
                        && f.getIntentos_autorizacion() != null && f.getIntentos_autorizacion() >= 3
                        && resultado != null && safeStr(resultado.getMensaje()).startsWith("Sin autorizaciones")) {
                    var recepcion = sendXmlToSriService.enviarFacturaFirmadaTxt(f.getXmlFirmadoEnvio(),
                            sendXmlToSriService.inferAmbienteFromXml(f.getXmlFirmadoEnvio()));
                    if (recepcion != null && ("RECIBIDA".equalsIgnoreCase(recepcion.getEstado()) || esClaveRegistrada(recepcion))) {
                        f.setRecepcionConfirmada(true);
                    } else if (recepcion != null) {
                        f.setEstado("M"); f.setErrores(trunc(erroresRecepcion(recepcion), 1500));
                        facturaR.save(f); return false;
                    }
                }
                retryPolicy.pendiente(f, resultado == null ? "Sin respuesta de autorizacion" : resultado.getMensaje());
            }
        } catch (org.springframework.dao.DataAccessException | jakarta.persistence.PersistenceException persistence) {
            throw persistence;
        } catch (Exception ex) {
            retryPolicy.pendiente(f, ex.getMessage());
            log.warn("Consulta SRI pendiente idfactura={} intento={} esperaMs={} causa={}",
                    idFactura, f.getIntentos_autorizacion(), retryPolicy.demoraMs(idFactura, f.getIntentos_autorizacion()), ex.toString());
        }
        facturaR.saveAndFlush(f);
        outbox.estadoSri(f, "O".equals(f.getEstado()) ? "AUTORIZADO" : "N".equals(f.getEstado()) ? "RECHAZADO" : "ESPERANDO_AUTORIZACION");
        return f.getXmlautorizado() != null && !f.getXmlautorizado().isBlank();
    }

    private boolean esCorreoPermitido(String email) {
        if (email == null) return false;

        String e = email.trim();
        if (e.isEmpty()) return false;

        // Normalizamos en minúsculas
        e = e.toLowerCase(Locale.ROOT);

        if ("no@gmail.com".equals(e)) return false; // Direccion de relleno usada en facturas sin correo.

        if (e.length() > 254 || !e.matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?(?:\\.[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?)+")) return false;

        int atIndex = e.lastIndexOf('@');
        if (atIndex <= 0 || atIndex > 64 || atIndex == e.length() - 1) {
            // no tiene @ o está mal formado
            return false;
        }

        String dominio = e.substring(atIndex + 1); // todo lo que está después del @

        // Si el dominio está en la lista negra → NO permitir
        if (DOMINIOS_BLOQUEADOS.stream().anyMatch(d -> d.equalsIgnoreCase(dominio))) {
            return false;
        }

        // aquí podrías meter más validaciones de formato si quieres
        return true;
    }





    public void procesarFacturaEnNuevaTx(Long idFactura) {
        if(!preparation.preparar(idFactura))return;
        var tx=new org.springframework.transaction.support.TransactionTemplate(transactionManager);
        tx.setPropagationBehavior(Propagation.REQUIRES_NEW.value());
        // El XML firmado ya esta confirmado en otra transaccion. Persistir la intencion ANTES de usar la red.
        String firmado=tx.execute(status->{
            var f=facturaR.findParaProcesar(idFactura).orElse(null);
            if(f==null || !"G".equals(f.getEstado()))return null;
            f.setEstado("P");f.setFecha_ultimo_intento(LocalDateTime.now());
            facturaR.saveAndFlush(f);outbox.estadoSri(f,"ENVIANDO_RECEPCION");return f.getXmlFirmadoEnvio();
        });
        if(firmado==null)return;
        RespuestaSolicitud recepcion=null;Exception error=null;
        try {
            int ambiente=sendXmlToSriService.inferAmbienteFromXml(firmado);
            if((ambienteForzado==1 || ambienteForzado==2) && ambienteForzado!=ambiente)
                throw new com.erp.sri_files.validation.FacturaPrevalidacionException(List.of("Ambiente configurado no coincide con la factura"));
            recepcion=sendXmlToSriService.enviarFacturaFirmadaTxt(firmado,ambiente);
            if(recepcion==null)throw new IllegalStateException("Recepcion SRI sin respuesta");
        }catch(Exception failure){error=failure;}
        final var respuesta=recepcion;final var fallo=error;
        tx.executeWithoutResult(status->{
            var f=facturaR.findParaProcesar(idFactura).orElseThrow();
            if(f.getXmlautorizado()!=null && !f.getXmlautorizado().isBlank())return;
            if(fallo instanceof com.erp.sri_files.validation.FacturaPrevalidacionException){
                f.setEstado("E");f.setErrores(trunc(fallo.getMessage(),1500));
            }else if(fallo!=null){
                retryPolicy.pendiente(f,fallo.getMessage());
            }else if("RECIBIDA".equalsIgnoreCase(respuesta.getEstado()) || esClaveRegistrada(respuesta)){
                f.setRecepcionConfirmada(true);retryPolicy.pendiente(f,"Recepcion confirmada; pendiente de autorizacion SRI");
            }else{
                f.setEstado("M");f.setErrores(trunc(erroresRecepcion(respuesta),1500));
            }
            facturaR.saveAndFlush(f);
            outbox.estadoSri(f, "C".equals(f.getEstado())?"ESPERANDO_AUTORIZACION":"M".equals(f.getEstado())?"DEVUELTA":"DATOS_INVALIDOS");
        });
    }

    private static boolean esClaveRegistrada(RespuestaSolicitud rs) {
        if (rs.getComprobantes() == null) return false;
        return rs.getComprobantes().getComprobante().stream()
                .filter(c -> c.getMensajes() != null)
                .flatMap(c -> c.getMensajes().getMensaje().stream())
                .anyMatch(m -> "43".equals(m.getIdentificador()) || "70".equals(m.getIdentificador()));
    }

    // helper sencillo, igual al del controller
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean reintentarCorreoPendiente(Long idFactura) {
        Factura f = facturaR.findParaProcesar(idFactura).orElse(null);
        if (f == null || !"O".equalsIgnoreCase(safeStr(f.getEstado()))
                || safeStr(f.getXmlautorizado()).isBlank()) return false;
        reintentarEnvioCorreoFactura(f);
        return f.getEmailMessageId() != null || Boolean.TRUE.equals(f.getMail_enviado());
    }

    private void reintentarEnvioCorreoFactura(Factura f) {
        try {
            if (f.getEmailMessageId() != null) {
                var correo = mailService.consultarEstado(f.getEmailMessageId());
                if (correo != null && "SENT".equals(correo.status())) {
                    f.setMail_enviado(true); f.setEstado("A"); f.setErrores(null);
                } else {
                    f.setMail_enviado(false); f.setEstado("O");
                    f.setErrores(trunc("Correo " + (correo == null ? "sin respuesta" : correo.status())
                            + "; emailId=" + f.getEmailMessageId()
                            + (correo != null && correo.lastError()!=null ? "; " + correo.lastError() : ""), 1500));
                }
                facturaR.save(f); return;
            }
            String xmlAutorizado = safeStr(f.getXmlautorizado());
            if (xmlAutorizado.isBlank()) {
                f.setEstado("C");
                f.setErrores("No existe XML autorizado para reenviar correo");
                facturaR.save(f);
                return;
            }

            String correoComprador = safeStr(f.getEmailcomprador());
            if (!esCorreoPermitido(correoComprador)) {
                f.setEstado("O");
                f.setErrores("Correo comprador invalido o bloqueado; corregir para reenviar");
                facturaR.save(f);
                return;
            }

            boolean metadataCompleta = false;
            try {
                var parsed = FacturaPdfXml.parse(xmlAutorizado);
                metadataCompleta = !parsed.numeroAutorizacion.isBlank() && !parsed.fechaAutorizacion.isBlank()
                        && safeStr(f.getClaveacceso()).equals(FacturaPdfXml.text(parsed.factura, "claveAcceso"));
            } catch (Exception invalid) { /* Intentar recuperar evidencia oficial, conservando el XML actual. */ }
            if (!metadataCompleta) {
                var evidencia = sendXmlToSriService.consultar_Autorizacion(f.getClaveacceso());
                xmlAutorizado = FacturaAutorizacionXml.completo(evidencia, f.getClaveacceso());
                f.setXmlautorizado(xmlAutorizado); f.setFecha_autorizacion(evidencia.getFechaAutorizacion());
                facturaR.saveAndFlush(f);
            }
            byte[] cache = outbox.ride(f.getIdfactura(), xmlAutorizado);
            ByteArrayOutputStream pdfStream;
            if (cache != null && cache.length > 0) {
                pdfStream = new ByteArrayOutputStream(); pdfStream.writeBytes(cache);
            } else pdfStream = xmlToPdfService.generarFacturaPDF_v3(xmlAutorizado);
            if (pdfStream == null || pdfStream.size() == 0) {
                f.setEstado("O");
                f.setErrores("Factura autorizada, pero no se pudo regenerar el PDF para reenviar correo");
                facturaR.save(f);
                return;
            }

            byte[] pdfBytes = pdfStream.toByteArray();
            outbox.guardarRide(f, pdfBytes);
            byte[] xmlBytes = xmlAutorizado.getBytes(StandardCharsets.UTF_8);

            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);
            String xmlBase64 = Base64.getEncoder().encodeToString(xmlBytes);

            String nombrePdf = "factura_" + f.getIdfactura() + ".pdf";
            String nombreXml = "factura_" + f.getIdfactura() + ".xml";

            List<AttachmentDTO> attachments = List.of(
                    new AttachmentDTO(nombrePdf, "application/pdf", pdfBase64),
                    new AttachmentDTO(nombreXml, "application/xml", xmlBase64)
            );

            List<String> to = List.of(correoComprador.trim());
            List<String> cc = Collections.emptyList();
            List<String> bcc = Collections.emptyList();
            String from = null;

            String subject = "Factura electrónica #"
                    + safeStr(f.getEstablecimiento()) + "-"
                    + safeStr(f.getPuntoemision()) + "-"
                    + safeStr(f.getSecuencial());

            String htmlBody = "<p>Estimado/a " + (f.getRazonsocialcomprador() != null ? f.getRazonsocialcomprador() : "cliente") + ",</p>"
                    + "<p>Su factura electrónica fue autorizada por el SRI. Adjuntamos nuevamente el PDF y el XML.</p>"
                    + "<p><strong>Clave de acceso:</strong> " + safeStr(f.getClaveacceso()) + "</p>"
                    + "<p>Este es un mensaje automático de EPMAPA-T.</p>";

            Map<String, String> inlineImages = Collections.emptyMap();
            SendMailRequest mailReq = new SendMailRequest(
                    from,
                    to,
                    cc,
                    bcc,
                    subject,
                    htmlBody,
                    attachments,
                    inlineImages
            );

            f.setEmailMessageId(mailService.send(mailReq, "factura:" + f.getIdfactura() + (f.getNotificacionVersion() > 1 ? ":v" + f.getNotificacionVersion() : "")));
            f.setMail_enviado(false);
            f.setEstado("O");
            f.setErrores("Correo encolado; pendiente confirmar envio SMTP/API");
            facturaR.save(f);
        } catch (Exception ex) {
            f.setEstado("O");
            f.setErrores(trunc("Error reenviando correo: " + ex.getMessage(), 1500));
            facturaR.save(f);
        }
    }

    private String safeStr(Object o) {
        return (o == null) ? "" : o.toString().trim();
    }


    private void logTaskStart(String taskName, String threadName) {
        log.info("[SCHEDULER][{}] INICIO {} @ {}", threadName, taskName, LocalDateTime.now());
    }

    private void logTaskEnd(String taskName, String threadName, long started, int totalDetectado, int exitosas, int fallidas) {
        long durationMs = System.currentTimeMillis() - started;
        log.info("[SCHEDULER][{}] FIN {} | detectados={} | exitosas={} | fallidas={} | duracionMs={}",
                threadName, taskName, totalDetectado, exitosas, fallidas, durationMs);
    }

    private void logTaskError(String taskName, String threadName, Exception e) {
        log.error("[SCHEDULER][{}] ERROR {}", threadName, taskName, e);
    }

    // helpers
        private static void requireNotBlank(String s, String msg) {
            if (s == null || s.isBlank()) throw new IllegalStateException(msg);
        }

        private static String trunc(String s, int max) {
            if (s == null) return null;
            return s.length() <= max ? s : s.substring(0, max);
        }

    // Extrae mensajes legibles de RespuestaSolicitud (recepción SRI)
    private static String erroresRecepcion(RespuestaSolicitud rs) {
        if (rs == null || rs.getComprobantes() == null || rs.getComprobantes().getComprobante() == null) {
            return "Respuesta de recepción sin detalles de comprobantes";
        }
        StringBuilder sb = new StringBuilder();
        for (var comp : rs.getComprobantes().getComprobante()) {
            String clave = nonNull(comp.getClaveAcceso());
            if (sb.length() > 0) sb.append(" || ");
            sb.append("Clave: ").append(clave.isEmpty() ? "-" : clave);

            if (comp.getMensajes() != null && comp.getMensajes().getMensaje() != null) {
                for (var m : comp.getMensajes().getMensaje()) {
                    sb.append(" | [")
                            .append(nonNull(m.getIdentificador()))
                            .append("] ")
                            .append(nonNull(m.getMensaje()));
                    String ia = nonNull(m.getInformacionAdicional());
                    if (!ia.isEmpty()) sb.append(" (").append(ia).append(")");
                    String tipo = nonNull(m.getTipo()); // ERROR/INFO/ADVERTENCIA
                    if (!tipo.isEmpty()) sb.append(" <").append(tipo).append(">");
                }
            }
        }
        if (sb.length() == 0) return "DEVUELTA sin mensajes";
        return sb.toString();
    }

    private static String nonNull(String s) { return s == null ? "" : s.trim(); }

}

