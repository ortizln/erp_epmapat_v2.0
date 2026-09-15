package com.erp.sri_files.validation;

import com.erp.sri_files.models.*;
import com.erp.sri_files.services.ClaveAccesoService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FacturaPrevalidacionService {
    // Catálogo soportado por este emisor. Nunca inferir tarifas desconocidas.
    private static final Map<String, BigDecimal> IVA = Map.of(
            "0", BigDecimal.ZERO, "2", new BigDecimal("12"), "3", new BigDecimal("14"),
            "4", new BigDecimal("15"), "5", new BigDecimal("5"),
            "6", BigDecimal.ZERO, "7", BigDecimal.ZERO);
    public static final Set<String> FORMAS_PAGO = Set.of("01", "15", "16", "17", "18", "19", "20", "21");
    private final ClaveAccesoService claves;
    public FacturaPrevalidacionService(ClaveAccesoService claves) { this.claves = claves; }

    public record Resultado(boolean valido, List<String> errores, List<String> advertencias,
                            BigDecimal subtotal, BigDecimal descuento, BigDecimal iva,
                            BigDecimal total, BigDecimal pagos, BigDecimal intereses) {
        public void exigirValido() {
            if (!valido) throw new FacturaPrevalidacionException(errores);
        }
    }

    public Resultado validar(Factura f, Definir emisor, Facturas origen) {
        List<String> errores = new ArrayList<>();
        List<String> advertencias = new ArrayList<>();
        requerido(errores, f.getRazonsocialcomprador(), "razonsocialcomprador");
        requerido(errores, f.getDireccioncomprador(), "direccioncomprador");
        requerido(errores, f.getDireccionestablecimiento(), "direccionestablecimiento");
        validarIdentificacion(errores, f.getTipoidentificacioncomprador(), f.getIdentificacioncomprador());
        formato(errores, f.getEstablecimiento(), "[0-9]{3}", "establecimiento");
        formato(errores, f.getPuntoemision(), "[0-9]{3}", "puntoemision");
        formato(errores, f.getSecuencial(), "[0-9]{9}", "secuencial");
        if ("000000000".equals(f.getSecuencial())) errores.add("secuencial: no puede ser cero");
        if (f.getFechaemision() == null) errores.add("fechaemision: obligatoria");
        else if (f.getFechaemision().toLocalDate().isAfter(LocalDate.now())) errores.add("fechaemision: fecha futura");
        if (emisor == null) errores.add("definir: falta configuración del emisor");
        else {
            requerido(errores, emisor.getRazonsocial(), "emisor.razonsocial");
            formato(errores, emisor.getRuc(), "[0-9]{13}", "emisor.ruc");
            formato(errores, Objects.toString(emisor.getTipoambiente(), ""), "[12]", "emisor.ambiente");
        }
        String clave = f.getClaveacceso();
        if (!claves.validarClaveAcceso(clave)) errores.add("claveacceso: requiere 49 dígitos y módulo 11 válido; no se regenera automáticamente");
        else {
            if (f.getFechaemision() != null) coincide(errores, "clave.fecha", clave.substring(0, 8), f.getFechaemision().format(DateTimeFormatter.ofPattern("ddMMyyyy")));
            coincide(errores, "clave.codDoc", clave.substring(8, 10), "01");
            if (emisor != null) {
                coincide(errores, "clave.ruc", clave.substring(10, 23), emisor.getRuc());
                coincide(errores, "clave.ambiente", clave.substring(23, 24), Objects.toString(emisor.getTipoambiente(), ""));
            }
            coincide(errores, "clave.establecimiento", clave.substring(24, 27), f.getEstablecimiento());
            coincide(errores, "clave.puntoemision", clave.substring(27, 30), f.getPuntoemision());
            coincide(errores, "clave.secuencial", clave.substring(30, 39), f.getSecuencial());
            coincide(errores, "clave.tipoEmision", clave.substring(47, 48), "1");
        }
        BigDecimal subtotal = BigDecimal.ZERO, descuento = BigDecimal.ZERO, iva = BigDecimal.ZERO, intereses = BigDecimal.ZERO;
        List<FacturaDetalle> detalles = f.getDetalles() == null ? List.of() : f.getDetalles();
        if (detalles.isEmpty()) errores.add("detalles: se requiere al menos un detalle");
        int n = 0;
        for (FacturaDetalle d : detalles) {
            String campo = "detalle[" + (++n) + "]";
            if (d == null) { errores.add(campo + ": nulo"); continue; }
            requerido(errores, d.getCodigoprincipal(), campo + ".codigoprincipal");
            requerido(errores, d.getDescripcion(), campo + ".descripcion");
            BigDecimal cantidad = numero(errores, d.getCantidad(), campo + ".cantidad");
            if (cantidad.signum() == 0) errores.add(campo + ".cantidad: debe ser mayor a cero");
            BigDecimal precio = numero(errores, d.getPreciounitario(), campo + ".preciounitario");
            BigDecimal desc = numero(errores, d.getDescuento(), campo + ".descuento");
            BigDecimal base = dinero(cantidad.multiply(precio).subtract(desc));
            if (base.signum() < 0) errores.add(campo + ": descuento supera el valor del detalle");
            subtotal = subtotal.add(base);
            descuento = descuento.add(desc);
            // Rubro 5 = intereses en comercialización. Comparar el cobrado, no recalcular a hoy.
            if ("5".equals(d.getCodigoprincipal())) intereses = intereses.add(base);
            List<FacturaDetalleImpuesto> impuestos = d.getImpuestos() == null ? List.of() : d.getImpuestos();
            if (impuestos.isEmpty()) errores.add(campo + ".impuestos: faltantes; declarar explícitamente incluso IVA cero");
            Set<String> grupos = new HashSet<>();
            for (FacturaDetalleImpuesto i : impuestos) {
                if (i == null) { errores.add(campo + ".impuestos: registro nulo"); continue; }
                BigDecimal tarifa = tarifa(i.getCodigoimpuesto(), i.getCodigoporcentaje());
                if (tarifa == null) errores.add(campo + ": impuesto/tarifa no soportado " + i.getCodigoimpuesto() + "/" + i.getCodigoporcentaje());
                if (!grupos.add(i.getCodigoimpuesto())) errores.add(campo + ": impuesto duplicado " + i.getCodigoimpuesto());
                BigDecimal baseImpuesto = numero(errores, i.getBaseimponible(), campo + ".baseimponible");
                igual(errores, campo + ".baseimponible", baseImpuesto, base);
                if (tarifa != null) iva = iva.add(impuesto(baseImpuesto, tarifa));
            }
        }
        BigDecimal total = dinero(subtotal.add(iva)), pagos = BigDecimal.ZERO;
        List<FacturaPago> listaPagos = f.getPagos() == null ? List.of() : f.getPagos();
        if (listaPagos.isEmpty()) errores.add("pagos: faltantes; no se crea un pago ficticio");
        for (FacturaPago p : listaPagos) {
            if (p == null) { errores.add("pagos: registro nulo"); continue; }
            if (!FORMAS_PAGO.contains(Objects.toString(p.getFormapago(), ""))) errores.add("pagos.formapago: código no soportado " + p.getFormapago());
            pagos = pagos.add(numero(errores, p.getTotal(), "pagos.total"));
            if (p.getPlazo() != null && p.getPlazo() < 0) errores.add("pagos.plazo: negativo");
            if (p.getPlazo() != null && p.getPlazo() > 0) requerido(errores, p.getUnidadtiempo(), "pagos.unidadtiempo");
        }
        igual(errores, "suma de pagos / total (incluye intereses e IVA)", pagos, total);
        if (origen != null) {
            if (origen.getInterescobrado() != null) igual(errores, "intereses rubro 5 / facturas.interescobrado", intereses, origen.getInterescobrado());
            else advertencias.add("facturas.interescobrado es nulo: no se pudo contrastar intereses con el cobro");
        } else advertencias.add("Sin factura de origen: intereses incluidos en detalles, sin contraste con el cobro");
        return new Resultado(errores.isEmpty(), List.copyOf(errores), List.copyOf(advertencias), dinero(subtotal), dinero(descuento), dinero(iva), total, dinero(pagos), dinero(intereses));
    }

    public static BigDecimal tarifa(String codigo, String porcentaje) { return "2".equals(codigo) ? IVA.get(porcentaje == null ? "" : porcentaje) : null; }
    public static BigDecimal dinero(BigDecimal n) { return n.setScale(2, RoundingMode.HALF_UP); }
    public static BigDecimal impuesto(BigDecimal base, BigDecimal tarifa) { return base.multiply(tarifa).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP); }
    public static void requerido(List<String> e, String v, String campo) { if (v == null || v.isBlank()) e.add(campo + ": obligatorio"); }
    public static void formato(List<String> e, String v, String regex, String campo) { if (v == null || !v.matches(regex)) e.add(campo + ": formato inválido"); }
    public static BigDecimal numero(List<String> e, BigDecimal v, String campo) {
        if (v == null) { e.add(campo + ": obligatorio"); return BigDecimal.ZERO; }
        if (v.signum() < 0) e.add(campo + ": no puede ser negativo");
        return v;
    }
    public static void igual(List<String> e, String campo, BigDecimal actual, BigDecimal esperado) {
        if (dinero(actual).compareTo(dinero(esperado)) != 0) e.add(campo + ": valor=" + actual + ", esperado=" + dinero(esperado));
    }
    public static void coincide(List<String> e, String campo, String actual, String esperado) {
        if (!Objects.equals(actual, esperado)) e.add(campo + ": no coincide con el comprobante");
    }
    public static void validarIdentificacion(List<String> e, String tipo, String id) {
        requerido(e, id, "identificacionComprador");
        switch (Objects.toString(tipo, "")) {
            case "04" -> formato(e, id, "[0-9]{13}", "identificacionComprador.RUC");
            case "05" -> formato(e, id, "[0-9]{10}", "identificacionComprador.cedula");
            case "07" -> coincide(e, "identificacionComprador.consumidorFinal", id, "9999999999999");
            case "06", "08" -> { if (id != null && id.length() > 20) e.add("identificacionComprador: máximo 20 caracteres"); }
            default -> e.add("tipoIdentificacionComprador: inválido");
        }
    }
}
