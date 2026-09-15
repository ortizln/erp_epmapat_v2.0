package com.erp.sri_files.services;

import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.validation.FacturaPrevalidacionException;
import com.erp.sri_files.validation.FacturaPrevalidacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FacturaRevisionService {
    private final FacturaR facturas;
    private final FacturaXmlGeneratorService generator;
    private final ClaveAccesoService claves;
    private final SendXmlToSriService sri;
    private final FacturaHistorialService historial;

    public record Diagnostico(Long idfactura, String estado, LocalDateTime fechaemision,
                              String claveacceso, String errorAnterior, boolean tieneXmlAutorizado,
                              FacturaPrevalidacionService.Resultado validacion, List<String> erroresXml,
                              boolean datosCompletos) {}
    public record Recuperacion(Long idfactura, String estado, String resultado, List<String> errores) {}

    @Transactional(readOnly = true)
    public Page<Diagnostico> diagnosticar(LocalDate desde, LocalDate hasta, String estado, int pagina, int limite) {
        if (!desde.isBefore(hasta) || pagina < 0 || limite < 1 || limite > 200)
            throw new IllegalArgumentException("Rango [desde,hasta) válido, página >= 0 y límite entre 1 y 200 requeridos");
        return facturas.findParaDiagnostico(desde.atStartOfDay(), hasta.atStartOfDay(), estado,
                PageRequest.of(pagina, limite)).map(this::diagnostico);
    }

    @Transactional(readOnly = true)
    public Diagnostico diagnosticar(Long id) {
        return diagnostico(facturas.findById(id).orElseThrow(() -> new IllegalArgumentException("Factura no encontrada: " + id)));
    }

    private Diagnostico diagnostico(Factura f) {
        var validacion = generator.validarFactura(f);
        List<String> erroresXml = new ArrayList<>();
        if (validacion.valido()) {
            try { generator.generarXmlFactura(f); }
            catch (FacturaPrevalidacionException ex) { erroresXml.addAll(ex.getErrores()); }
            catch (Exception ex) { erroresXml.add("Generación XML: " + ex.getMessage()); }
        }
        return new Diagnostico(f.getIdfactura(), f.getEstado(), f.getFechaemision(), f.getClaveacceso(),
                f.getErrores(), tieneXml(f), validacion, List.copyOf(erroresXml), validacion.valido() && erroresXml.isEmpty());
    }

    // Cada petición revisa una factura con bloqueo. No cambia importes, clave ni fecha.
    @Transactional
    public Recuperacion recuperar(Long id, boolean claveOriginalVerificada) {
        Factura f = facturas.findParaProcesar(id).orElseThrow(() -> new IllegalArgumentException("Factura no encontrada: " + id));
        String estado = Objects.toString(f.getEstado(), "").trim().toUpperCase(Locale.ROOT);
        if (tieneXml(f)) return resultado(f, "YA_TIENE_XML_AUTORIZADO", List.of());
        if (!Set.of("E", "M", "N").contains(estado))
            return resultado(f, "ESTADO_NO_RECUPERABLE", List.of("Se permite recuperar estados E, M o N; estado actual: " + estado));
        String clave = f.getClaveacceso();
        if (!claves.validarClaveAcceso(clave) || !clave.substring(23, 24).matches("[12]"))
            return resultado(f, "REQUIERE_CORRECCION", List.of("Clave inválida o faltante: recuperar la clave original; no generar otra sin conciliación"));
        try {
            var respuesta = sri.consultarAutorizacion(clave, Integer.parseInt(clave.substring(23, 24)));
            if (respuesta == null) return resultado(f, "CONSULTA_INCONCLUSA", List.of("SRI no devolvió respuesta; no se habilita envío"));
            var autorizaciones = respuesta.getAutorizaciones() == null ? List.<ec.gob.sri.ws.autorizacion.Autorizacion>of()
                    : respuesta.getAutorizaciones().getAutorizacion();
            String xml = sri.extraerXmlAutorizado(respuesta);
            if (autorizaciones.stream().anyMatch(a -> "AUTORIZADO".equalsIgnoreCase(a.getEstado()))) {
                if (xml == null || xml.isBlank()) return resultado(f, "CONSULTA_INCONCLUSA", List.of("Autorizada sin XML recuperable"));
                f.setXmlautorizado(xml);
                cambiar(f, "A", "XML autorizado recuperado por revisión");
                return resultado(f, "AUTORIZADA_RECUPERADA", List.of());
            }
            if (!autorizaciones.isEmpty() || !"0".equals(respuesta.getNumeroComprobantes()))
                return resultado(f, "REQUIERE_REVISION_SRI", List.of("SRI tiene respuesta para la clave; revisar sus mensajes de autorización antes de reenviar"));
            // Registro previo con código 43/70 no equivale a ausencia por una consulta vacía.
            String error = Objects.toString(f.getErrores(), "").toUpperCase(Locale.ROOT);
            if (error.contains("CLAVE ACCESO REGISTRADA") || error.contains("[43]") || error.contains("[70]"))
                return resultado(f, "CONSULTA_INCONCLUSA", List.of("Hay evidencia de recepción previa; mantener consulta de autorización, sin reenviar"));
            var diagnostico = diagnostico(f);
            if (!diagnostico.datosCompletos()) {
                List<String> errores = new ArrayList<>(diagnostico.validacion().errores());
                errores.addAll(diagnostico.erroresXml());
                return resultado(f, "REQUIERE_CORRECCION", errores);
            }
            if (!claveOriginalVerificada)
                return resultado(f, "REQUIERE_CONCILIACION_CLAVE", List.of(
                        "Datos completos, pero una consulta vacía no descarta envíos antiguos con otra clave. "
                        + "Contrastar la clave con XML/RIDE e historial original antes de habilitar el envío"));
            cambiar(f, "I", "Revisión completa y consulta SRI sin comprobantes; habilitada para el scheduler");
            return resultado(f, "PENDIENTE_ENVIO", List.of());
        } catch (Exception ex) {
            // Un fallo de consulta nunca significa que la factura no existe en el SRI.
            return resultado(f, "CONSULTA_INCONCLUSA", List.of(Objects.toString(ex.getMessage(), "Error consultando SRI")));
        }
    }
    private void cambiar(Factura f, String estado, String mensaje) {
        historial.registrarCambioEstado(f, f.getEstado(), estado, mensaje);
        f.setEstado(estado); f.setErrores(null);
        facturas.save(f);
    }
    private static boolean tieneXml(Factura f) { return f.getXmlautorizado() != null && !f.getXmlautorizado().isBlank(); }
    private static Recuperacion resultado(Factura f, String resultado, List<String> errores) {
        return new Recuperacion(f.getIdfactura(), f.getEstado(), resultado, errores);
    }
}
