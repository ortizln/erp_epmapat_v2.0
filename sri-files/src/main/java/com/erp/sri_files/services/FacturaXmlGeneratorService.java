package com.erp.sri_files.services;

import com.erp.sri_files.exceptions.FacturaElectronicaException;
import com.erp.sri_files.interfaces.TotalSinImpuestos;
import com.erp.sri_files.models.*;
import com.erp.sri_files.models.TotalConImpuestos.TotalImpuesto;
import com.erp.sri_files.repositories.DefinirR;
import com.erp.sri_files.repositories.FacturaDetalleR;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.repositories.FacturasR;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;

@Service
public class FacturaXmlGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(FacturaXmlGeneratorService.class);
    private static String nvlStr(String v, String def) { return (v == null || v.isBlank()) ? def : v; }
    private static String nullIfBlank(String v) { return (v == null || v.isBlank()) ? null : v; }

    private static final String VERSION = "1.1.0";
    private static final String TIPO_COMPROBANTE_FACTURA = "01";
    private static final DateTimeFormatter DDMMYYYY_CLAVE = DateTimeFormatter.ofPattern("ddMMyyyy"); // para clave
    private static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");  // para XML
    @Autowired private DefinirR definirR;
    @Autowired private FacturaDetalleR fDetalleR;
    @Autowired private FacturaR facturaR;
    @Autowired private ClaveAccesoService claveAccesoService;
    @Autowired private com.erp.sri_files.validation.FacturaPrevalidacionService prevalidacion;
    @Autowired private com.erp.sri_files.validation.SriFacturaValidationService xmlValidation;
    @Autowired private FacturasR facturasOrigenR;

    public com.erp.sri_files.validation.FacturaPrevalidacionService.Resultado validarFactura(Factura factura) {
        if (factura.getDetalles() == null || factura.getDetalles().isEmpty()) {
            factura.setDetalles(fDetalleR.findByFacturaIdWithImpuestos(factura.getIdfactura()));
        }
        var resultado = prevalidacion.validar(factura, definirR.findById(1L).orElse(null),
                facturasOrigenR.findByIdfactura(factura.getIdfactura()));
        if (factura.getClaveacceso() != null && facturaR.existsByClaveaccesoAndIdfacturaNot(
                factura.getClaveacceso(), factura.getIdfactura())) {
            var errores = new ArrayList<>(resultado.errores());
            errores.add("claveacceso: ya pertenece a otra factura local");
            return new com.erp.sri_files.validation.FacturaPrevalidacionService.Resultado(false, errores,
                    resultado.advertencias(), resultado.subtotal(), resultado.descuento(), resultado.iva(),
                    resultado.total(), resultado.pagos(), resultado.intereses());
        }
        return resultado;
    }

    //@Transactional
    //@Scheduled(cron = "${interes.tarea.cron}") // ej: 0 */5 * * * *
    public void automatizacionEnvioFacturasElectonicas() {
        System.out.println("⏰ Ejecutando envío de facturas : " + LocalDateTime.now());
        try {
            PageRequest limit = PageRequest.of(0, 5); // primera "página" de 5
            Page<Factura> page = facturaR.findByEstadoNormalizado("I", limit);
            List<Factura> facturas = page.getContent();

            for (Factura f : facturas) {
                System.out.println("Procesando factura ID: " + f.getIdfactura());
                System.out.println("Clave acceso: " + f.getClaveacceso());
                // Generar el xml, firmar y enviar al sri.
                String xmlString = generarXmlFactura(f);
            }
            System.out.println("✅ Intereses actualizados correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error en la tarea programada: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* ==========================================================
     * API PRINCIPAL
     * ========================================================== */
    public String generarXmlFactura(Factura factura) throws FacturaElectronicaException {
        validarFactura(factura).exigirValido();
        try {

            // 1) Raíz
            Comprobante comp = new Comprobante();
            comp.setVersion(VERSION);
            comp.setId("comprobante");
            // 2) infoTributaria (incluye claveAcceso 49 dígitos)
            comp.setInfoTributaria(crearInfoTributaria(factura));

            // 3) infoFactura (totales, impuestos, importeTotal)
            comp.setInfoFactura(crearInfoFactura(factura));

            // 4) detalles (cada línea con sus impuestos)
            comp.setDetalles(mapearDetalles(factura.getDetalles()));


            // 👉 Construir/actualizar infoAdicional
            Map<String,String> extras = new LinkedHashMap<>();
            // Ejemplos (opcional): vienen de UI o reglas de negocio
            // extras.put("Observación", "Pago en ventanilla");
            // extras.put("Cajero", "001-A");

            upsertInfoAdicional(comp, factura, extras);

            // 5) Marshal a XML
            String xml = convertirObjetoAXml(comp);
            xmlValidation.validate(xml).exigirValido();
            return xml;

        } catch (com.erp.sri_files.validation.FacturaPrevalidacionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generando XML de factura", e);
            throw new FacturaElectronicaException("Error al generar XML para el SRI: " + e.getMessage(), e);
        }
    }
    /* Carga de detalles: usa los que vengan en la entidad; si están vacíos, intenta repo */
    private List<FacturaDetalle> obtenerDetallesFactura(Factura factura) {
        List<FacturaDetalle> detalles = factura.getDetalles();
        if (detalles == null || detalles.isEmpty()) {
            try {
                detalles = fDetalleR.findByFacturaIdWithImpuestos(factura.getIdfactura());
            } catch (Exception ignore) {
                detalles = List.of();
            }
        }
        return detalles == null ? List.of() : detalles;
    }

    /* ==========================================================
     * BLOQUE: infoTributaria (incluye clave de acceso)
     * ========================================================== */
    private InfoTributaria crearInfoTributaria(Factura factura) {
        Definir def = getDefinir();

        String claveAcceso = (factura.getClaveacceso() == null || factura.getClaveacceso().isBlank())
                ? null
                : factura.getClaveacceso();

        InfoTributaria it = new InfoTributaria();
        it.setAmbiente(String.valueOf(def.getTipoambiente()));       // 1=pruebas, 2=producción
        it.setTipoEmision(String.valueOf((byte) 1));                 // normal
        it.setRazonSocial(def.getRazonsocial());
        it.setNombreComercial(def.getNombrecomercial());
        it.setRuc(def.getRuc());
        it.setClaveAcceso(claveAcceso);
        it.setCodDoc(TIPO_COMPROBANTE_FACTURA);
        it.setEstab(factura.getEstablecimiento());
        it.setPtoEmi(factura.getPuntoemision());
        it.setSecuencial(factura.getSecuencial());
        it.setDirMatriz(factura.getDireccionestablecimiento()); // ajusta si quieres usar la matriz del emisor
        return it;
    }

    /* ==========================================================
     * BLOQUE: infoFactura + totales
     * ========================================================== */
    private InfoFactura crearInfoFactura(Factura factura) {
        var validacion = validarFactura(factura);
        validacion.exigirValido();
        BigDecimal descuento = validacion.descuento();
        BigDecimal totalSinImp = validacion.subtotal();

        InfoFactura info = new InfoFactura();
        info.setFechaEmision(formatForXml(factura.getFechaemision()));
        info.setObligadoContabilidad("SI");
        info.setTipoIdentificacionComprador(factura.getTipoidentificacioncomprador());
        info.setRazonSocialComprador(factura.getRazonsocialcomprador());
        info.setIdentificacionComprador(factura.getIdentificacioncomprador());
        info.setDireccionComprador(factura.getDireccioncomprador());

        info.setTotalSinImpuestos(totalSinImp);
        info.setTotalDescuento(descuento);

        // totalConImpuestos (nunca vacío; fallback IVA 0%)
        TotalConImpuestos tci = crearTotalConImpuestos(factura, totalSinImp, descuento);
        info.setTotalConImpuestos(tci);

        BigDecimal sumaImpuestos = tci.getTotalImpuestos() == null ? BigDecimal.ZERO
                : tci.getTotalImpuestos().stream()
                .map(TotalImpuesto::getValor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        info.setPropina(BigDecimal.ZERO);

        // importeTotal = totalSinImpuestos + Σimpuestos + propina
        BigDecimal importeTotal = totalSinImp
                .add(sumaImpuestos)
                .setScale(2, RoundingMode.HALF_UP);

        info.setImporteTotal(importeTotal);
        info.setMoneda("DOLAR");

        info.setPagos(factura.getPagos().stream().map(origen -> {
            InfoFactura.Pago pago = new InfoFactura.Pago();
            pago.setFormaPago(origen.getFormapago());
            pago.setTotal(origen.getTotal());
            if (origen.getPlazo() != null) {
                pago.setPlazo(origen.getPlazo().toString());
                pago.setUnidadTiempo(origen.getUnidadtiempo());
            }
            return pago;
        }).toList());

        return info;
    }


    private static String formatForXml(LocalDateTime dt) {
        if (dt == null) throw new IllegalArgumentException("La fecha no puede ser nula");
        return dt.format(DD_MM_YYYY); // dd/MM/yyyy
    }

    /* TotalConImpuestos agregando por (codigo, codigoPorcentaje).
       Si no hay impuestos en las líneas -> agrega un bloque IVA 0% con base neta. */
    private TotalConImpuestos crearTotalConImpuestos(Factura factura,
                                                     BigDecimal totalSinImp,
                                                     BigDecimal descuento) {

        TotalConImpuestos tci = new TotalConImpuestos();

        List<FacturaDetalle> detallesFactura = obtenerDetallesFactura(factura);

        Map<String, TotalesAcumulados> agrupado =
                detallesFactura == null ? Map.of() :
                        detallesFactura.stream()
                                .filter(Objects::nonNull)
                                .map(FacturaDetalle::getImpuestos)
                                .filter(Objects::nonNull)
                                .flatMap(List::stream)
                                .collect(Collectors.groupingBy(
                                        i -> i.getCodigoimpuesto() + "|" + obtenerCodigoPorcentajeSeguro(i),
                                        Collectors.reducing(new TotalesAcumulados(),
                                                i -> {
                                                    BigDecimal base   = nvl(i.getBaseimponible());
                                                    BigDecimal tarifa = obtenerTarifaPorcentaje(i.getCodigoimpuesto(),
                                                            obtenerCodigoPorcentajeSeguro(i)); // % (0,12,15,...)
                                                    BigDecimal valor  = base.multiply(tarifa)
                                                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                                                    return new TotalesAcumulados(base, valor);
                                                },
                                                TotalesAcumulados::sumar)));

        List<TotalImpuesto> totales = agrupado.entrySet().stream().map(e -> {
            String[] key = e.getKey().split("\\|");
            String codigo = key[0];
            String codigoPorcentaje = key[1];
            TotalesAcumulados t = e.getValue();

            TotalImpuesto ti = new TotalImpuesto();
            ti.setCodigo(codigo);
            ti.setCodigoPorcentaje(codigoPorcentaje);
            ti.setBaseImponible(t.base.setScale(2, RoundingMode.HALF_UP));
            ti.setValor(t.valor.setScale(2, RoundingMode.HALF_UP));
            return ti;
        }).collect(Collectors.toList());

        // Fallback: al menos un totalImpuesto IVA 0%
        if (totales.isEmpty()) {
            BigDecimal baseNeta = totalSinImp;
            if (baseNeta.compareTo(BigDecimal.ZERO) < 0) baseNeta = BigDecimal.ZERO;

            TotalImpuesto ti0 = new TotalImpuesto();
            ti0.setCodigo("2");               // IVA
            ti0.setCodigoPorcentaje("0");     // 0%
            ti0.setBaseImponible(baseNeta.setScale(2, RoundingMode.HALF_UP));
            ti0.setValor(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            totales.add(ti0);
        }

        tci.setTotalImpuestos(totales);
        return tci;
    }

    private static class TotalesAcumulados {
        BigDecimal base = BigDecimal.ZERO;
        BigDecimal valor = BigDecimal.ZERO;
        TotalesAcumulados() {}
        TotalesAcumulados(BigDecimal b, BigDecimal v) { base = nvl(b); valor = nvl(v); }
        TotalesAcumulados sumar(TotalesAcumulados o) {
            return new TotalesAcumulados(base.add(o.base), valor.add(o.valor));
        }
    }

    private static String obtenerCodigoPorcentajeSeguro(FacturaDetalleImpuesto i) {
        String cp = i.getCodigoporcentaje();
        return (cp == null || cp.isBlank()) ? "0" : cp;
    }

    /* Devuelve % como número (0, 5, 12, 14, 15, ...) según codigo/codigoPorcentaje que manejes. */
    private static BigDecimal obtenerTarifaPorcentaje(String codigo, String codigoPorcentaje) {
        if ("2".equals(codigo)) { // IVA
            switch (codigoPorcentaje) {
                case "0": return BigDecimal.ZERO;
                case "2": return new BigDecimal("12"); // histórico 12%
                case "3": return new BigDecimal("14"); // histórico 14%
                case "4": return new BigDecimal("15"); // IVA vigente 15%
                case "5": return new BigDecimal("5");  // tarifa especial 5%
                case "6": return BigDecimal.ZERO;      // no objeto
                case "7": return BigDecimal.ZERO;      // exento
                default:  return BigDecimal.ZERO;
            }
        }
        // Otros impuestos si aplican
        return BigDecimal.ZERO;
    }

    /* ==========================================================
     * BLOQUE: detalles con impuestos
     * ========================================================== */
    private List<Detalle> _mapearDetalles(List<FacturaDetalle> detallesFactura) {
        if (detallesFactura == null || detallesFactura.isEmpty()) return List.of();

        List<Detalle> lista = new ArrayList<>();
        for (FacturaDetalle d : detallesFactura) {
            if (d == null) continue;

            Detalle det = new Detalle();
            det.setCodigoPrincipal(nvlStr(d.getCodigoprincipal(), "SIN-CODIGO"));
            det.setCodigoAuxiliar(nullIfBlank(d.getCodigoprincipal()));
            det.setDescripcion(nvlStr(d.getDescripcion(), "SIN DESCRIPCION"));

            BigDecimal cantidad = nvl(d.getCantidad());
            BigDecimal pUnit    = nvl(d.getPreciounitario());
            BigDecimal desc     = nvl(d.getDescuento());

            det.setCantidad(cantidad);
            det.setPrecioUnitario(pUnit);
            det.setDescuento(desc);

            BigDecimal baseLinea = cantidad.multiply(pUnit).subtract(desc);
            if (baseLinea.compareTo(BigDecimal.ZERO) < 0) baseLinea = BigDecimal.ZERO;
            baseLinea = baseLinea.setScale(2, RoundingMode.HALF_UP);
            det.setPrecioTotalSinImpuesto(baseLinea);

            // ===== Impuestos por detalle =====
            List<Detalle.Impuesto> imps = new ArrayList<>();
            List<FacturaDetalleImpuesto> fuente = d.getImpuestos() == null ? List.of() : d.getImpuestos();
            for (FacturaDetalleImpuesto i : fuente) {
                if (i == null) continue;

                String cod = nvlStr(i.getCodigoimpuesto(), "2"); // IVA
                String cp  = nvlStr(obtenerCodigoPorcentajeSeguro(i), "0");

                BigDecimal base   = nvl(i.getBaseimponible());
                if (base.compareTo(BigDecimal.ZERO) == 0) base = baseLinea;
                BigDecimal tarifa = obtenerTarifaPorcentaje(cod, cp);
                BigDecimal valor  = base.multiply(tarifa).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                Detalle.Impuesto imp = new Detalle.Impuesto();
                imp.setCodigo(cod);
                imp.setCodigoPorcentaje(cp);
                imp.setTarifa(tarifa); // opcional
                imp.setBaseImponible(base.setScale(2, RoundingMode.HALF_UP));
                imp.setValor(valor.setScale(2, RoundingMode.HALF_UP));
                imps.add(imp);
            }
            if (imps.isEmpty()) {
                Detalle.Impuesto imp0 = new Detalle.Impuesto();
                imp0.setCodigo("2");
                imp0.setCodigoPorcentaje("0");
                imp0.setTarifa(BigDecimal.ZERO);
                imp0.setBaseImponible(baseLinea);
                imp0.setValor(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                imps.add(imp0);
            }
            det.setImpuestos(imps);

            // ===== Detalles adicionales =====
            List<Detalle.DetalleAdicional> ads = new ArrayList<>();

            // Ejemplos si tu entidad tiene campos opcionales:
            // if (d.getSerie() != null && !d.getSerie().isBlank())
            //     ads.add(new Detalle.DetAdicional("Serie", d.getSerie()));
            // if (d.getMedidor() != null && !d.getMedidor().isBlank())
            //     ads.add(new Detalle.DetAdicional("Medidor", d.getMedidor()));

            // Solo asigna si hay al menos un detAdicional; si no, deja null para NO emitir el wrapper
            det.setDetallesAdicionales(ads.isEmpty() ? null : ads);

            lista.add(det);
        }
        return lista;
    }

    private List<Detalle> mapearDetalles(List<FacturaDetalle> detallesFactura) {
        if (detallesFactura == null || detallesFactura.isEmpty()) return List.of();

        List<Detalle> lista = new ArrayList<>();

        // ===== acumulador para 1006 + 1007 =====
        BigDecimal baseConsFuentes = BigDecimal.ZERO;
        boolean huboConsFuentes = false;

        for (FacturaDetalle d : detallesFactura) {
            if (d == null) continue;

            String codPrin = nvlStr(d.getCodigoprincipal(), "");

            BigDecimal cantidad = nvl(d.getCantidad());
            BigDecimal pUnit    = nvl(d.getPreciounitario());
            BigDecimal desc     = nvl(d.getDescuento());

            BigDecimal baseLinea = cantidad.multiply(pUnit).subtract(desc);
            if (baseLinea.compareTo(BigDecimal.ZERO) < 0) baseLinea = BigDecimal.ZERO;
            baseLinea = baseLinea.setScale(2, RoundingMode.HALF_UP);

            // ✅ Si es 1006 o 1007, SOLO acumula y NO lo agregues a la lista
            if (("1006".equals(codPrin) || "1007".equals(codPrin))
                    && desc.signum() == 0
                    && d.getImpuestos() != null && d.getImpuestos().size() == 1
                    && "2".equals(d.getImpuestos().get(0).getCodigoimpuesto())
                    && "0".equals(d.getImpuestos().get(0).getCodigoporcentaje())) {
                baseConsFuentes = baseConsFuentes.add(baseLinea);
                huboConsFuentes = true;
                continue;
            }

            // ===== detalle normal =====
            Detalle det = new Detalle();
            det.setCodigoPrincipal(nvlStr(d.getCodigoprincipal(), "SIN-CODIGO"));
            det.setCodigoAuxiliar(nullIfBlank(d.getCodigoprincipal()));
            det.setDescripcion(nvlStr(d.getDescripcion(), "SIN DESCRIPCION"));

            det.setCantidad(cantidad);
            det.setPrecioUnitario(pUnit);
            det.setDescuento(desc);
            det.setPrecioTotalSinImpuesto(baseLinea);

            // ===== Impuestos por detalle =====
            List<Detalle.Impuesto> imps = new ArrayList<>();
            List<FacturaDetalleImpuesto> fuente = d.getImpuestos() == null ? List.of() : d.getImpuestos();

            for (FacturaDetalleImpuesto i : fuente) {
                if (i == null) continue;

                String cod = nvlStr(i.getCodigoimpuesto(), "2");
                String cp  = nvlStr(obtenerCodigoPorcentajeSeguro(i), "0");

                BigDecimal base   = nvl(i.getBaseimponible());
                if (base.compareTo(BigDecimal.ZERO) == 0) base = baseLinea;

                BigDecimal tarifa = obtenerTarifaPorcentaje(cod, cp);
                BigDecimal valor  = base.multiply(tarifa)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                Detalle.Impuesto imp = new Detalle.Impuesto();
                imp.setCodigo(cod);
                imp.setCodigoPorcentaje(cp);
                imp.setTarifa(tarifa);
                imp.setBaseImponible(base.setScale(2, RoundingMode.HALF_UP));
                imp.setValor(valor.setScale(2, RoundingMode.HALF_UP));
                imps.add(imp);
            }

            if (imps.isEmpty()) {
                Detalle.Impuesto imp0 = new Detalle.Impuesto();
                imp0.setCodigo("2");
                imp0.setCodigoPorcentaje("0");
                imp0.setTarifa(BigDecimal.ZERO);
                imp0.setBaseImponible(baseLinea);
                imp0.setValor(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                imps.add(imp0);
            }

            det.setImpuestos(imps);

            // ===== Detalles adicionales (igual que tu código) =====
            List<Detalle.DetalleAdicional> ads = new ArrayList<>();
            det.setDetallesAdicionales(ads.isEmpty() ? null : ads);

            lista.add(det);
        }

        // ✅ Al final, si hubo 1006/1007, crea 1 solo detalle consolidado (1004)
        if (huboConsFuentes && baseConsFuentes.compareTo(BigDecimal.ZERO) > 0) {
            baseConsFuentes = baseConsFuentes.setScale(2, RoundingMode.HALF_UP);

            Detalle det = new Detalle();
            det.setCodigoPrincipal("1004");
            det.setCodigoAuxiliar(null); // o "1004" si lo necesitas
            det.setDescripcion("Conservación de fuentes");

            det.setCantidad(BigDecimal.ONE);                 // ✅ cantidad 1
            det.setPrecioUnitario(baseConsFuentes);          // ✅ unitario = suma
            det.setDescuento(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            det.setPrecioTotalSinImpuesto(baseConsFuentes);  // ✅ base total

            // ✅ 1 solo impuesto
            Detalle.Impuesto imp0 = new Detalle.Impuesto();
            imp0.setCodigo("2");
            imp0.setCodigoPorcentaje("0");
            imp0.setTarifa(BigDecimal.ZERO);
            imp0.setBaseImponible(baseConsFuentes);
            imp0.setValor(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));

            det.setImpuestos(List.of(imp0));

            lista.add(det);
        }

        return lista;
    }

    /* ==========================================================
     * BLOQUE: info adicional
     * ========================================================== */
    private void upsertInfoAdicional(Comprobante comp,
                                     Factura factura,
                                     Map<String, String> extras) {
        if (comp.getInfoAdicional() == null) {
            comp.setInfoAdicional(new ArrayList<>());
        }

        // Construir candidatos (desde Factura + extras)
        List<Comprobante.CampoAdicional> candidatos = new ArrayList<>();

        // ===== desde Factura (ajusta a tus campos reales) =====
        //putCampo(candidatos, "Dirección",     safe(factura.getDireccioncomprador(), 300));
        // Si tienes email y teléfono en tu entidad:
         putCampo(candidatos, "e-mail",        safe(factura.getEmailcomprador(), 300));
        putCampo(candidatos, "Teléfono",      safe(factura.getTelefonocomprador(), 300));
        putCampo(candidatos, "Fecha de cobro", safe(formatForXml(factura.getFechaemision()), 300));
        putCampo(candidatos, "Recaudador", safe(factura.getRecaudador(), 300));
        putCampo(candidatos, "Cuenta", safe(factura.getReferencia(), 300));
        putCampo(candidatos, "Concepto", safe(factura.getConcepto(), 300));

        // ===== extras externos =====
        if (extras != null) {
            extras.forEach((k, v) -> putCampo(candidatos, safeName(k, 30), safe(v, 300)));
        }

        // Deduplicar por nombre: lo último gana
        Map<String, Comprobante.CampoAdicional> porNombre = new LinkedHashMap<>();
        // primero lo que ya tenía el comprobante
        for (Comprobante.CampoAdicional c : comp.getInfoAdicional()) {
            if (isOk(c.getNombre(), c.getValor())) porNombre.put(c.getNombre(), c);
        }
        // luego candidatos (pisan)
        for (Comprobante.CampoAdicional c : candidatos) {
            if (isOk(c.getNombre(), c.getValor())) porNombre.put(c.getNombre(), c);
        }

        comp.setInfoAdicional(new ArrayList<>(porNombre.values()));
    }

    private boolean isOk(String nombre, String valor) {
        return nombre != null && !nombre.isBlank() && valor != null && !valor.isBlank();
    }

    private void putCampo(List<Comprobante.CampoAdicional> lista, String nombre, String valor) {
        if (!isOk(nombre, valor)) return;
        Comprobante.CampoAdicional c = new Comprobante.CampoAdicional();
        c.setNombre(nombre);
        c.setValor(valor);
        lista.add(c);
    }

    // Recorta y limpia nombre (ajusta límite si tu XSD lo exige distinto)
    private String safeName(String s, int max) {
        if (s == null) return null;
        s = s.trim();
        if (s.length() > max) s = s.substring(0, max);
        return s;
    }

    // Recorta y limpia valor
    private String safe(String s, int max) {
        if (s == null) return null;
        s = s.trim();
        if (s.length() > max) s = s.substring(0, max);
        return s;
    }


    /* ==========================================================
     * UTILIDADES (marshal, repos, guardado, etc.)
     * ========================================================== */
    private String convertirObjetoAXml(Comprobante comprobante) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(Comprobante.class);
        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter writer = new StringWriter();
        m.marshal(comprobante, writer);
        return writer.toString();
    }

    private Definir getDefinir() {
        Long id = 1L; // ajusta si lo necesitas parametrizable
        return definirR.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Registro no encontrado con ID: " + id));
    }

    public static void saveXml(String xmlContent, String fileName) throws Exception {
        System.out.println("GUARDANDO ....") ;

        Path dir = Paths.get(".\\xmlFiles");
        Files.createDirectories(dir);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String newFileName = fileName.replace(".xml", "_" + timestamp + ".xml");
        Path path = dir.resolve(newFileName);
        Files.write(path, xmlContent.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        System.out.println("✅ XML guardado en: " + path.toAbsolutePath());
    }

    public void processAndSendInvoice(String toEmail, String subject, String body, MultipartFile xmlFile) throws Exception {
        byte[] xmlData = xmlFile.getBytes();
        // byte[] pdfData = PdfGenerationService.generatePdfFromXml(xmlData);
        String attachmentName = "factura_" + System.currentTimeMillis() + ".pdf";
        // emailService.se(toEmail, subject, body, pdfData, attachmentName);
    }

    /* Helpers numéricos/texto */
    private static BigDecimal nvl(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }

    private static String requireDigits(String value, String label) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(label + " no puede ser nulo o vacío");
        String digits = value.replaceAll("\\D", "");
        if (digits.isEmpty())
            throw new IllegalArgumentException(label + " debe contener dígitos");
        return digits;
    }

    private static String leftPadDigits(String digits, int length) {
        if (!digits.chars().allMatch(Character::isDigit))
            throw new IllegalArgumentException("Valor no numérico: " + digits);
        if (digits.length() > length)
            throw new IllegalArgumentException("Longitud mayor a " + length + ": " + digits);
        return String.format("%0" + length + "d", Long.parseLong(digits));
    }

    // En FacturaSRIService (o un Utils):
    private static void setPagoContado(InfoFactura info, BigDecimal total) {
        InfoFactura.Pago p = new InfoFactura.Pago();
        p.setFormaPago("20");      // Efectivo/otros (ajusta según catálogo SRI)
        p.setTotal(total);
        info.getPagos().clear();
        info.getPagos().add(p);
    }

    private static void setPagoCredito(InfoFactura info, BigDecimal total, int plazoDias) {
        InfoFactura.Pago p = new InfoFactura.Pago();
        p.setFormaPago("20");      // o "01" / "19" / etc., según tu negocio
        p.setTotal(total);
        p.setPlazo(String.valueOf(BigDecimal.valueOf(plazoDias)));
        p.setUnidadTiempo("dias");
        info.getPagos().clear();
        info.getPagos().add(p);
    }

}
