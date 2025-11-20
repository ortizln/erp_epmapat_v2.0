package com.erp.sri_files.services;

import com.erp.sri_files.dto.AttachmentDTO;
import com.erp.sri_files.dto.AutorizacionInfo;
import com.erp.sri_files.dto.AutorizacionSriResult;
import com.erp.sri_files.dto.SendMailRequest;
import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.utils.FirmaComprobantesService;
import com.erp.sri_files.utils.SriAutorizacionAdapter;
import ec.gob.sri.ws.autorizacion.RespuestaComprobante;
import ec.gob.sri.ws.recepcion.RespuestaSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
public class EnvioSriBatchService {

    private static final int LOTE = 10;

    private final FacturaR facturaR;
    private final FacturaXmlGeneratorService xmlFacturaService;
    private final FirmaComprobantesService firmaService;
    private final SendXmlToSriService sendXmlToSriService;
    private final XmlToPdfService xmlToPdfService;
    private final MailService mailService;
    // private final StorageService storageService; // opcional, para guardar XMLs



    // ====== CONFIG desde application.properties ======
    @Value("${sri.firma.modo:XADES_BES}") // XADES_BES o XMLDSIG
    private String modoFirmaCfg;

    @Value("${sri.ambiente:0}") // 0 = deducir del XML, 1 = pruebas, 2 = producción
    private int ambienteForzado;

    @Value("${sri.poll.intentos:10}")
    private int pollIntentos;

    @Value("${sri.poll.delay-ms:4000}")
    private long pollDelayMs;

        @Transactional
        @Scheduled(cron = "${interes.tarea.cron}") // ej: 0 */5 * * * *
        public void automatizacionEnvioFacturasElectonicas() {
            System.out.println("⏰ Ejecutando envío de facturas : " + LocalDateTime.now());
            try {
                var limit = PageRequest.of(0, LOTE);
                var page = facturaR.findByEstado("I", limit);
                List<Factura> facturas = page.getContent();

                if (facturas.isEmpty()) {
                    System.out.println("🟢 No hay facturas pendientes (I).");
                    return;
                }

                for (Factura f : facturas) {
                    try {
                        procesarFacturaEnNuevaTx(f.getIdfactura());
                    } catch (Exception ex) {
                        System.err.println("❌ Error procesando factura " + f.getIdfactura() + ": " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }

                System.out.println("✅ Lote procesado: " + facturas.size());
            } catch (Exception e) {
                System.err.println("❌ Error en la tarea programada: " + e.getMessage());
                e.printStackTrace();
            }
        }
//Este servicio sirve para conultar las facturas en estado C y volver a buscar el xml auotizado en el sri
    @Transactional
    @Scheduled(cron = "${interes.tarea.cron}")
    public void automatizacionConsultarXml() {
        System.out.println("Voy a consultar cada xml que aun no esta");
        try {
            var limit = PageRequest.of(0, LOTE);
            var page = facturaR.findByEstado("C", limit);
            List<Factura> facturas = page.getContent();

            if (facturas.isEmpty()) {
                System.out.println("🟢 No hay facturas pendientes (I).");
                return;
            }

            for (Factura f : facturas) {
                try {
                    String claveAcceso = f.getClaveacceso();
                    if(f.getXmlautorizado() != null || f.getXmlautorizado() != "")
                    {
                        f.setEstado("A");
                        f.setErrores(null);
                        facturaR.save(f);
                        break;
                    }
                    AutorizacionSriResult resultado =
                            sendXmlToSriService.consultar_Autorizacion(claveAcceso);

                    if (resultado.isAutorizado()
                            && resultado.getXmlAutorizado() != null) {
                        // 👉 Guardas el XML autorizado
                        f.setXmlautorizado(resultado.getXmlAutorizado());

                        // 👉 Campo autorizado (ajusta según tu tipo de dato)
                        // Si es Boolean:
                       // f.set(true);
                        // Si es String tipo 'S'/'N': f.setAutorizado("S");

                        // 👉 Si manejas estado de factura
                        f.setEstado("A"); // Autorizada

                        // 👉 Si tienes fecha de autorización en la entidad
                        if (resultado.getFechaAutorizacion() != null) {
                            f.setErrores(String.valueOf(resultado.getFechaAutorizacion()));
                        }

                        // Limpia errores si los hubiera
                        f.setErrores(null);

                        System.out.println("✅ Factura " + f.getIdfactura()
                                + " autorizada y XML guardado.");
                    } else {
                        // No autorizado, en proceso o error
                        String msg = resultado.getMensaje() != null
                                ? resultado.getMensaje()
                                : "No autorizado / sin XML";

                        f.setEstado("C"); // o "N"
                        f.setErrores(msg);

                        System.out.println("⚠️ Factura " + f.getIdfactura()
                                + " no autorizada: " + msg);
                    }

                    facturaR.save(f);

                } catch (Exception ex) {
                    System.err.println("❌ Error procesando factura "
                            + f.getIdfactura() + ": " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void procesar_FacturaEnNuevaTx(Long idFactura) {
        // 1) Releer y bloquear lógicamente
        Factura f = facturaR.findById(idFactura).orElseThrow();
        if (!"I".equals(f.getEstado())) {
            System.out.println("ℹ️ Factura " + idFactura + " ya no está en estado I, actual: " + f.getEstado());
            return;
        }
        f.setEstado("P");
        facturaR.saveAndFlush(f);

        try {
            // 2) Generar XML desde la entidad
            String xmlPlano = xmlFacturaService.generarXmlFactura(f);
            requireNotBlank(xmlPlano, "XML generado vacío");

            // 3) Modo de firma
            var mf = "XMLDSIG".equalsIgnoreCase(modoFirmaCfg)
                    ? FirmaComprobantesService.ModoFirma.XMLDSIG
                    : FirmaComprobantesService.ModoFirma.XADES_BES;

            // 4) Firmar
            String xmlFirmado = firmaService.firmarFactura(xmlPlano, mf);
            requireNotBlank(xmlFirmado, "XML firmado vacío");

            // 5) Ambiente
            if (ambienteForzado == 1 || ambienteForzado == 2) {
                sendXmlToSriService.setAmbiente(ambienteForzado);
            } else {
                sendXmlToSriService.setAmbienteFromXml(xmlFirmado);
            }

            // 6) Enviar a recepción
            RespuestaSolicitud recepcion = sendXmlToSriService.enviarFacturaFirmadaTxt(xmlFirmado);
            if (recepcion == null) throw new IllegalStateException("Respuesta de recepción nula");

            if ("RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
                // 7) Polling de autorización
                var rc = sendXmlToSriService.consultarAutorizacionConEspera(
                        xmlFirmado,
                        clave -> {
                            try {
                                return sendXmlToSriService.consultarAutorizacion(clave);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        },
                        pollIntentos,
                        pollDelayMs
                );

                var info = SriAutorizacionAdapter.fromRespuesta(rc)
                        .orElse(new AutorizacionInfo(false, null, null, null, "Sin autorizaciones"));

                if (info.autorizado()) {
                    // ============================
                    // AUTORIZADA: armar XML completo + guardar fecha/hora
                    // ============================

                    // XML del comprobante (factura) que devuelve el adapter
                    String xmlFactura = new String(info.xmlAutorizado(), StandardCharsets.UTF_8);

                    // Datos de autorización
                    String numeroAutorizacion = info.numeroAutorizacion() != null
                            ? info.numeroAutorizacion().trim()
                            : "";

                    // Dependiendo de cómo manejes la fecha en AutorizacionInfo:
                    // Suponiendo que es XMLGregorianCalendar:
                    LocalDateTime fa = info.fechaAutorizacion();
                    String fechaAutStr = (fa != null ? fa.toString() : "");

                    // Ambiente real → tomado de tu clase
                    String ambienteStr = (ambienteForzado == 2 ? "PRODUCCIÓN" : "PRUEBAS");

                    // Armar XML COMPLETO de autorización
                    String xmlAutorizacionCompleta =
                            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                    "<autorizacion>\n" +
                                    "  <estado>AUTORIZADO</estado>\n" +
                                    "  <numeroAutorizacion>" + numeroAutorizacion + "</numeroAutorizacion>\n" +
                                    "  <fechaAutorizacion>" + fechaAutStr + "</fechaAutorizacion>\n" +
                                    "  <ambiente>" + ambienteStr + "</ambiente>\n" +
                                    "  <comprobante><![CDATA[" + xmlFactura + "]]></comprobante>\n" +
                                    "</autorizacion>";

                    f.setEstado("A");
                    // Guardas el XML COMPLETO
                    f.setXmlautorizado(xmlAutorizacionCompleta);

                    // Si tu entidad tiene estos campos, guárdalos también:
                    // (cambia los nombres de métodos según tu entidad)
                  /*  try {
                        f.setNumeroautorizacion(numeroAutorizacion); // o setNumautorizacion(...)
                    } catch (Exception ignore) {}

                    try {
                        if (fa != null) {
                            // convertir a java.util.Date o LocalDateTime según tu campo
                            java.util.Date fechaJava = fa.toGregorianCalendar().getTime();
                            f.setFechaautorizacion(fechaJava); // ajusta el nombre
                        }
                    } catch (Exception ignore) {}*/

                    facturaR.save(f);

                } else {
                    // ============================
                    // NO AUTORIZADA
                    // ============================
                    f.setEstado("U");

                    // Si quieres ver el XML que devolvió SRI:
                    String xml = new String(info.xmlAutorizado(), StandardCharsets.UTF_8);

                    // Mensajes del SRI
                    String mensajes = info.mensajesConcatenados();

                    // Guarda solo mensajes (lo más útil) y evita sobrescribir dos veces
                    String errores = (mensajes != null && !mensajes.isBlank())
                            ? mensajes
                            : xml;

                    f.setErrores(trunc(errores, 1500));
                    facturaR.save(f);
                }

            } else {
                // ============================
                // DEVUELTA en recepción
                // ============================
                f.setEstado("C"); // tu estado para devuelta/observada
                String errores = erroresRecepcion(recepcion);
                f.setErrores(trunc(errores, 1500));
                facturaR.save(f);
                System.out.println("🟠 DEVUELTA (REC): " + f.getIdfactura() + " -> " + errores);
            }

        } catch (Exception ex) {
            // Error inesperado → devolver a I y aumentar reintentos
            System.err.println("❌ Error inesperado en factura " + idFactura + ": " + ex.getMessage());
            ex.printStackTrace();

            f.setEstado("I");
            facturaR.save(f);
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void procesarFacturaEnNuevaTx(Long idFactura) {
        // 1) Releer y bloquear lógicamente
        Factura f = facturaR.findById(idFactura).orElseThrow();
        if (!"I".equals(f.getEstado())) {
            System.out.println("ℹ️ Factura " + idFactura + " ya no está en estado I, actual: " + f.getEstado());
            return;
        }
        f.setEstado("P");
        facturaR.saveAndFlush(f);

        try {
            // 2) Generar XML desde la entidad
            String xmlPlano = xmlFacturaService.generarXmlFactura(f);
            requireNotBlank(xmlPlano, "XML generado vacío");

            // 3) Modo de firma
            var mf = "XMLDSIG".equalsIgnoreCase(modoFirmaCfg)
                    ? FirmaComprobantesService.ModoFirma.XMLDSIG
                    : FirmaComprobantesService.ModoFirma.XADES_BES;

            // 4) Firmar
            String xmlFirmado = firmaService.firmarFactura(xmlPlano, mf);
            requireNotBlank(xmlFirmado, "XML firmado vacío");

            // 5) Ambiente
            if (ambienteForzado == 1 || ambienteForzado == 2) {
                sendXmlToSriService.setAmbiente(ambienteForzado);
            } else {
                sendXmlToSriService.setAmbienteFromXml(xmlFirmado);
            }

            // 6) Enviar a recepción
            RespuestaSolicitud recepcion = sendXmlToSriService.enviarFacturaFirmadaTxt(xmlFirmado);
            if (recepcion == null) throw new IllegalStateException("Respuesta de recepción nula");

            if ("RECIBIDA".equalsIgnoreCase(recepcion.getEstado())) {
                // 7) Polling de autorización
                var rc = sendXmlToSriService.consultarAutorizacionConEspera(
                        xmlFirmado,
                        clave -> {
                            try {
                                return sendXmlToSriService.consultarAutorizacion(clave);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        },
                        pollIntentos,
                        pollDelayMs
                );

                var info = SriAutorizacionAdapter.fromRespuesta(rc)
                        .orElse(new AutorizacionInfo(false, null, null, null, "Sin autorizaciones"));

                if (info.autorizado()) {
                    // ==========================
                    // ✅ AUTORIZADO
                    // ==========================
                    f.setEstado("A");

                    String xmlAutorizado = new String(info.xmlAutorizado(), StandardCharsets.UTF_8);
                    f.setXmlautorizado(xmlAutorizado);   // guardar XML legible

                    // ---------- 1) Generar PDF ----------
                    ByteArrayOutputStream pdfStream = xmlToPdfService.generarFacturaPDF_v3(xmlAutorizado);
                    if (pdfStream == null || pdfStream.size() == 0) {
                        // si falla PDF, deja al menos la factura autorizada guardada
                        f.setErrores("Factura autorizada, pero error generando PDF");
                        f.setEstado("O");
                        facturaR.save(f);
                        return;
                    }

                    byte[] pdfBytes = pdfStream.toByteArray();
                    byte[] xmlBytes = xmlAutorizado.getBytes(StandardCharsets.UTF_8);

                    String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);
                    String xmlBase64 = Base64.getEncoder().encodeToString(xmlBytes);

                    String nombrePdf = "factura_" + f.getIdfactura() + ".pdf";
                    String nombreXml = "factura_" + f.getIdfactura() + ".xml";

                    List<AttachmentDTO> attachments = List.of(
                            new AttachmentDTO(nombrePdf, "application/pdf", pdfBase64),
                            new AttachmentDTO(nombreXml, "application/xml", xmlBase64)
                    );

                    // ---------- 2) Destinatarios ----------
                    List<String> to;
                    /*if (f.getEmailcomprador() != null && !f.getEmailcomprador().isBlank()) {
                        to = List.of(f.getEmailcomprador().trim());
                    } else {
                        // fallback (puedes cambiarlo por un correo de pruebas o soporte)
                        to = List.of("alexis.ortiz81@outlook.com");
                    }*/
                    to = List.of("alexis.ortiz81@outlook.com");
                    List<String> cc  = Collections.emptyList();
                    List<String> bcc = Collections.emptyList();

                    String from = null; // que use el app.mail.from de tu configuración

                    // ---------- 3) Asunto y cuerpo ----------
                    String subject = "Factura electrónica #"
                            + safeStr(f.getEstablecimiento()) + "-"
                            + safeStr(f.getPuntoemision()) + "-"
                            + safeStr(f.getSecuencial());

                    String htmlBody =
                            "<div style='font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px;'>"

                                    // Logo
                                    + "<div style='text-align: center; margin-bottom: 20px;'>"
                                    + "    <img src='https://epmapatulcan.gob.ec/wp/wp-content/uploads/2021/05/LOGO-HORIZONTAL.png' alt='EPMAPA-T' style='max-width: 180px;'/>"
                                    + "</div>"

                                    // Título
                                    + "<h2 style='color: #0b5394; text-align: center;'>Factura Electrónica Autorizada</h2>"

                                    // Saludo
                                    + "<p>Estimado/a <strong>"
                                    + (f.getRazonsocialcomprador() != null ? f.getRazonsocialcomprador() : "cliente")
                                    + "</strong>,</p>"

                                    + "<p>Adjuntamos su comprobante electrónico en formato PDF y XML correspondiente a su compra realizada.</p>"

                                    // Estado SRI
                                    + "<p><strong>Estado SRI:</strong> <span style='color: green;'>AUTORIZADO</span></p>"

                                    // Separador
                                    + "<hr style='border: none; border-top: 1px solid #ccc; margin: 25px 0;'>"

                                    // Información de la empresa
                                    + "<h3 style='color: #0b5394;'>Información de la Empresa</h3>"
                                    + "<p>"
                                    + "EPMAPA-T<br>"
                                    + "Empresa Pública Municipal de Agua Potable y Alcantarillado de Tulcán<br>"
                                    + "Dirección: Ca. Juan Ramón Arellano y Bolívar, Tulcán – Ecuador<br>"
                                    + "Horario de atención: Lunes a Viernes 07h30 - 16h30<br>"
                                    + "Teléfono: (06) 298 0440<br>"
                                    + "Correo: <a href='mailto:info@epmapatulcan.gob.ec'>info@epmapatulcan.gob.ec</a>"
                                    + "</p>"

                                    // Redes sociales
                                    + "<h4 style='margin-top: 20px;'>Síguenos en nuestras redes sociales:</h4>"
                                    + "<p>"
                                    + "Facebook: <a href='https://www.facebook.com/epmapat2023' target='_blank'>facebook.com/epmapat2023</a><br>"
                                    + "Instagram: <a href='https://www.instagram.com/epmapat_/' target='_blank'>@epmapat_</a><br>"
                                    + "</p>"

                                    // Despedida
                                    + "<p style='margin-top: 30px;'>Gracias por confiar en nosotros.<br>Atentamente,<br><strong>EPMAPA-T</strong></p>"

                                    + "</div>";


                    Map<String,String> inlineImages = Collections.emptyMap();

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

                    try {
                        // si tu MailService tiene send(...) como en el controller:
                        mailService.send(mailReq);
                    } catch (Exception mailEx) {
                        // si falla el correo, marca como "O" (opcional)
                        f.setEstado("O");
                        String err = "Error enviando correo: " + mailEx.getMessage();
                        f.setErrores((f.getErrores() == null) ? err : (f.getErrores() + " | " + err));
                    }

                    facturaR.save(f);

                } else {
                    // ==========================
                    // ❌ NO AUTORIZADO
                    // ==========================
                    f.setEstado("N");
                    String xml = new String(info.xmlAutorizado(), StandardCharsets.UTF_8);
                    // Puedes guardar XML de error o solo mensajes:
                    f.setErrores(trunc(info.mensajesConcatenados(), 1500));
                    facturaR.save(f);
                }

            } else {
                // DEVUELTA en recepción
                f.setEstado("M"); // tu estado para devuelta/observada
                String errores = erroresRecepcion(recepcion);
                f.setErrores(trunc(errores, 1500));
                facturaR.save(f);
                System.out.println("🟠 DEVUELTA (REC): " + f.getIdfactura() + " -> " + errores);
            }

        } catch (Exception ex) {
            // Error inesperado → devolver a I y aumentar reintentos
            System.err.println("❌ Error inesperado en factura " + idFactura + ": " + ex.getMessage());
            ex.printStackTrace();

            f.setEstado("I");
            f.setErrores(ex.getMessage());
            facturaR.save(f);
        }
    }

    // helper sencillo, igual al del controller
    private String safeStr(Object o) {
        return (o == null) ? "" : o.toString().trim();
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
