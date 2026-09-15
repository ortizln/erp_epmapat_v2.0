package com.erp.sri_files.validation;

import com.erp.sri_files.models.*;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;
import static com.erp.sri_files.validation.FacturaPrevalidacionService.*;

@Service
public class SriFacturaValidationService {
    private final FacturaPrevalidacionService prevalidacion;
    public SriFacturaValidationService(FacturaPrevalidacionService prevalidacion) { this.prevalidacion = prevalidacion; }
    public record Resultado(boolean valid, List<String> errors, String claveAcceso, String ambiente, String codDoc) {
        public void exigirValido() { if (!valid) throw new FacturaPrevalidacionException(errors); }
    }
    public void exigirSiFactura(String xml) {
        try {
            Element root = parse(xml);
            if ("factura".equals(root.getLocalName())) validate(xml).exigirValido();
        } catch (FacturaPrevalidacionException e) { throw e; }
        catch (Exception e) { throw new FacturaPrevalidacionException(List.of("XML no parseable: " + e.getMessage())); }
    }
    public Resultado validate(String xml) {
        List<String> e = new ArrayList<>();
        String clave = "", ambiente = "", codDoc = "";
        try {
            Element root = parse(xml);
            coincide(e, "raíz XML", root.getTagName(), "factura");
            coincide(e, "id XML", root.getAttribute("id"), "comprobante");
            if (!Set.of("1.0.0", "1.1.0").contains(root.getAttribute("version"))) e.add("Versión factura no soportada por este filtro");
            Element it = one(root, "infoTributaria"), info = one(root, "infoFactura");
            clave = text(it, "claveAcceso"); ambiente = text(it, "ambiente"); codDoc = text(it, "codDoc");
            coincide(e, "codDoc", codDoc, "01");
            coincide(e, "tipoEmision", text(it, "tipoEmision"), "1");
            Definir def = new Definir();
            def.setRuc(text(it, "ruc")); def.setRazonsocial(text(it, "razonSocial"));
            def.setTipoambiente(ambiente.matches("[12]") ? Byte.valueOf(ambiente) : null);
            Factura f = new Factura();
            f.setClaveacceso(clave); f.setEstablecimiento(text(it, "estab")); f.setPuntoemision(text(it, "ptoEmi"));
            f.setSecuencial(text(it, "secuencial")); f.setDireccionestablecimiento(text(it, "dirMatriz"));
            try { f.setFechaemision(LocalDate.parse(text(info, "fechaEmision"), DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)).atStartOfDay()); }
            catch (Exception ex) { e.add("fechaEmision: fecha inválida, formato dd/MM/yyyy"); }
            f.setTipoidentificacioncomprador(text(info, "tipoIdentificacionComprador"));
            f.setIdentificacioncomprador(text(info, "identificacionComprador"));
            f.setRazonsocialcomprador(text(info, "razonSocialComprador"));
            f.setDireccioncomprador(text(info, "direccionComprador"));
            Map<String, BigDecimal[]> grupos = new HashMap<>();
            int n = 0;
            for (Element d : children(one(root, "detalles"), "detalle")) {
                String campo = "detalle[" + (++n) + "]";
                FacturaDetalle fd = new FacturaDetalle();
                fd.setCodigoprincipal(text(d, "codigoPrincipal")); fd.setDescripcion(text(d, "descripcion"));
                fd.setCantidad(decimal(d, "cantidad", e)); fd.setPreciounitario(decimal(d, "precioUnitario", e));
                fd.setDescuento(decimal(d, "descuento", e));
                BigDecimal base = dinero(fd.getCantidad().multiply(fd.getPreciounitario()).subtract(fd.getDescuento()));
                igual(e, campo + ".precioTotalSinImpuesto", decimal(d, "precioTotalSinImpuesto", e), base);
                for (Element imp : children(one(d, "impuestos"), "impuesto")) {
                    FacturaDetalleImpuesto fi = new FacturaDetalleImpuesto();
                    fi.setCodigoimpuesto(text(imp, "codigo")); fi.setCodigoporcentaje(text(imp, "codigoPorcentaje"));
                    fi.setBaseimponible(decimal(imp, "baseImponible", e)); fd.getImpuestos().add(fi);
                    BigDecimal tarifa = tarifa(fi.getCodigoimpuesto(), fi.getCodigoporcentaje());
                    BigDecimal valor = decimal(imp, "valor", e);
                    BigDecimal tarifaXml = decimal(imp, "tarifa", e);
                    if (tarifa != null) {
                        igual(e, campo + ".tarifa", tarifaXml, tarifa);
                        igual(e, campo + ".valorImpuesto", valor, impuesto(fi.getBaseimponible(), tarifa));
                    }
                    String key = fi.getCodigoimpuesto() + "/" + fi.getCodigoporcentaje();
                    BigDecimal[] sum = grupos.computeIfAbsent(key, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
                    sum[0] = sum[0].add(fi.getBaseimponible()); sum[1] = sum[1].add(valor);
                }
                f.getDetalles().add(fd);
            }
            for (Element p : children(one(info, "pagos"), "pago")) {
                FacturaPago fp = new FacturaPago();
                fp.setFormapago(text(p, "formaPago")); fp.setTotal(decimal(p, "total", e));
                if (!text(p, "plazo").isBlank()) {
                    try { fp.setPlazo(Integer.valueOf(text(p, "plazo"))); }
                    catch (NumberFormatException ex) { e.add("pagos.plazo: entero inválido"); }
                    fp.setUnidadtiempo(text(p, "unidadTiempo"));
                }
                f.getPagos().add(fp);
            }
            var r = prevalidacion.validar(f, def, null);
            e.addAll(r.errores());
            igual(e, "totalSinImpuestos", decimal(info, "totalSinImpuestos", e), r.subtotal());
            igual(e, "totalDescuento", decimal(info, "totalDescuento", e), r.descuento());
            BigDecimal propina = decimal(info, "propina", e);
            if (propina.signum() != 0) e.add("propina: no soportada por este emisor");
            igual(e, "importeTotal", decimal(info, "importeTotal", e), r.total());
            requerido(e, text(info, "moneda"), "moneda");
            Set<String> vistos = new HashSet<>();
            for (Element t : children(one(info, "totalConImpuestos"), "totalImpuesto")) {
                String key = text(t, "codigo") + "/" + text(t, "codigoPorcentaje");
                if (!vistos.add(key)) e.add("totalImpuesto duplicado: " + key);
                BigDecimal base = decimal(t, "baseImponible", e), valor = decimal(t, "valor", e);
                if (!grupos.containsKey(key)) e.add("totalImpuesto sin detalles: " + key);
                else {
                    igual(e, "totalImpuesto[" + key + "].baseImponible", base, grupos.get(key)[0]);
                    igual(e, "totalImpuesto[" + key + "].valor", valor, grupos.get(key)[1]);
                }
            }
            if (!vistos.equals(grupos.keySet())) e.add("totalConImpuestos: grupos no coinciden con los detalles");
        } catch (Exception ex) { e.add("XML incompleto o inválido: " + ex.getMessage()); }
        return new Resultado(e.isEmpty(), List.copyOf(e), clave, ambiente, codDoc);
    }
    private static Element parse(String xml) throws Exception {
        if (xml == null || xml.isBlank()) throw new IllegalArgumentException("XML vacío");
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        dbf.setXIncludeAware(false); dbf.setExpandEntityReferences(false);
        return dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml.replaceFirst("^\uFEFF", "").trim()))).getDocumentElement();
    }
    private static List<Element> children(Element parent, String name) {
        List<Element> result = new ArrayList<>();
        for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling())
            if (n instanceof Element el && name.equals(el.getTagName())) result.add(el);
        return result;
    }
    private static Element one(Element parent, String name) {
        var list = children(parent, name);
        if (list.size() != 1) throw new IllegalArgumentException(parent.getTagName() + "." + name + ": requiere un único elemento");
        return list.get(0);
    }
    private static String text(Element parent, String name) {
        var list = children(parent, name);
        if (list.size() > 1) throw new IllegalArgumentException(name + ": duplicado");
        return list.isEmpty() ? "" : list.get(0).getTextContent().trim();
    }
    private static BigDecimal decimal(Element parent, String name, List<String> e) {
        String value = text(parent, name);
        if (!value.matches("[0-9]+(\\.[0-9]+)?")) { e.add(parent.getTagName() + "." + name + ": decimal obligatorio no negativo"); return BigDecimal.ZERO; }
        return new BigDecimal(value);
    }
}
