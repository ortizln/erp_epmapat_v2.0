package com.erp.sri_files.services;

import com.erp.sri_files.dto.AutorizacionInfo;
import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.utils.FirmaComprobantesService;
import com.erp.sri_files.utils.SriAutorizacionAdapter;
import ec.gob.sri.ws.autorizacion.RespuestaComprobante;
import ec.gob.sri.ws.recepcion.RespuestaSolicitud;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class EnvioSriBatchService {

    private static final int LOTE = 5;

    private final FacturaR facturaR;
    private final FacturaXmlGeneratorService xmlFacturaService;
    private final FirmaComprobantesService firmaService;
    private final SendXmlToSriService sendXmlToSriService;
    // private final StorageService storageService; // opcional, para guardar XMLs

    public EnvioSriBatchService(
            FacturaR facturaR,
            FacturaXmlGeneratorService xmlFacturaService,
            FirmaComprobantesService firmaService,
            SendXmlToSriService sendXmlToSriService
    ) {
        this.facturaR = facturaR;
        this.xmlFacturaService = xmlFacturaService;
        this.firmaService = firmaService;
        this.sendXmlToSriService = sendXmlToSriService;
    }

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
                        f.setEstado("A");
                        f.setXmlautorizado(Arrays.toString(info.xmlAutorizado()));
                        facturaR.save(f);
                    } else {
                        f.setEstado("N");
                        f.setErrores(trunc(Arrays.toString(info.xmlAutorizado()), 1500));

                        f.setErrores(trunc(info.mensajesConcatenados(), 1500));
                        facturaR.save(f);
                    }

                } else {
                    // DEVUELTA en recepción
                    f.setEstado("M"); // tu estado para devuelta/observada
                    String errores = erroresRecepcion(recepcion);
                    f.setErrores(trunc(errores, 1500));  // reutiliza tu helper trunc(...)
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
