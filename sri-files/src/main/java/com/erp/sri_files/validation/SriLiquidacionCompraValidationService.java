package com.erp.sri_files.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;

@Service
public class SriLiquidacionCompraValidationService {

    public LiquidacionCompraValidationResult validate(String xml) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (xml == null || xml.isBlank()) {
            errors.add("XML vacío o nulo");
            return new LiquidacionCompraValidationResult(false, errors, warnings, null, null, null, null);
        }

        try {
            Document doc = parse(xml);
            Element root = doc.getDocumentElement();
            String rootName = root.getLocalName() == null ? root.getNodeName() : root.getLocalName();

            if (!"liquidacionCompra".equals(rootName)) {
                errors.add("Raíz inválida. Se esperaba <liquidacionCompra> y se recibió <" + rootName + ">");
            }

            String id = root.getAttribute("id");
            if (!"comprobante".equals(id)) {
                errors.add("El atributo id del comprobante debe ser id=\"comprobante\"");
            }

            String version = root.getAttribute("version");
            if (version == null || version.isBlank()) {
                errors.add("El atributo version es obligatorio en <liquidacionCompra>");
            } else if (!version.matches("1\\.0\\.0")) {
                warnings.add("Versión de liquidación de compra no reconocida: " + version);
            }

            String claveAcceso = text(doc, "claveAcceso");
            String ambiente = text(doc, "ambiente");
            String tipoEmision = text(doc, "tipoEmision");
            String codDoc = text(doc, "codDoc");
            String ruc = text(doc, "ruc");

            require(errors, claveAcceso, "infoTributaria.claveAcceso");
            require(errors, ambiente, "infoTributaria.ambiente");
            require(errors, tipoEmision, "infoTributaria.tipoEmision");
            require(errors, codDoc, "infoTributaria.codDoc");
            require(errors, ruc, "infoTributaria.ruc");
            require(errors, text(doc, "estab"), "infoTributaria.estab");
            require(errors, text(doc, "ptoEmi"), "infoTributaria.ptoEmi");
            require(errors, text(doc, "secuencial"), "infoTributaria.secuencial");

            if (!"03".equals(codDoc)) {
                errors.add("codDoc inválido para liquidación de compra. Se esperaba 03 y se recibió " + empty(codDoc));
            }
            if (!ambiente.matches("[12]")) {
                errors.add("ambiente inválido. Valores permitidos: 1 o 2");
            }
            if (!ruc.matches("\\d{13}")) {
                errors.add("RUC inválido. Debe tener 13 dígitos");
            }

            require(errors, text(doc, "fechaEmision"), "infoLiquidacionCompra.fechaEmision");
            require(errors, text(doc, "dirEstablecimiento"), "infoLiquidacionCompra.dirEstablecimiento");
            require(errors, text(doc, "contribuyenteEspecial"), "infoLiquidacionCompra.contribuyenteEspecial");
            require(errors, text(doc, "obligadoContabilidad"), "infoLiquidacionCompra.obligadoContabilidad");
            require(errors, text(doc, "tipoIdentificacionProveedor"), "infoLiquidacionCompra.tipoIdentificacionProveedor");
            require(errors, text(doc, "identificacionProveedor"), "infoLiquidacionCompra.identificacionProveedor");
            require(errors, text(doc, "razonSocialProveedor"), "infoLiquidacionCompra.razonSocialProveedor");

            require(errors, text(doc, "totalSinImpuestos"), "infoLiquidacionCompra.totalSinImpuestos");
            require(errors, text(doc, "totalImpuestos"), "infoLiquidacionCompra.totalImpuestos");
            require(errors, text(doc, "importeTotal"), "infoLiquidacionCompra.importeTotal");

            NodeList detalleNodes = doc.getElementsByTagName("detalle");
            if (detalleNodes.getLength() == 0) {
                errors.add("Debe tener al menos un detalle en liquidacionCompra.detalles");
            }

            validateClaveAcceso(errors, warnings, claveAcceso, codDoc, ruc, ambiente);

            return new LiquidacionCompraValidationResult(
                errors.isEmpty(), errors, warnings, claveAcceso, ambiente, codDoc, version
            );
        } catch (Exception e) {
            errors.add("XML corrupto o no parseable: " + e.getMessage());
            return new LiquidacionCompraValidationResult(false, errors, warnings, null, null, null, null);
        }
    }

    private static Document parse(String xml) throws Exception {
        String clean = stripBom(xml).trim();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        return dbf.newDocumentBuilder().parse(new InputSource(new java.io.StringReader(clean)));
    }

    private static String stripBom(String s) {
        if (s != null && !s.isEmpty() && s.charAt(0) == '\uFEFF') return s.substring(1);
        return s;
    }

    private static String text(Document doc, String tag) {
        NodeList list = doc.getElementsByTagName(tag);
        if (list.getLength() == 0) return null;
        String val = list.item(0).getTextContent();
        return (val == null || val.isBlank()) ? null : val.trim();
    }

    private static void require(List<String> errors, String value, String field) {
        if (value == null || value.isBlank()) {
            errors.add("Campo obligatorio faltante: " + field);
        }
    }

    private static String empty(String s) {
        return (s == null) ? "<vacío>" : s;
    }

    private void validateClaveAcceso(List<String> errors, List<String> warnings,
                                      String claveAcceso, String codDoc, String ruc, String ambiente) {
        if (claveAcceso == null) return;

        if (!claveAcceso.matches("\\d{49}")) {
            errors.add("Clave de acceso debe tener 49 dígitos. Longitud actual: " + claveAcceso.length());
            return;
        }

        String tipoComp = claveAcceso.substring(8, 10);
        if (!codDoc.equals(tipoComp)) {
            errors.add("Tipo comprobante en clave (" + tipoComp + ") no coincide con codDoc (" + codDoc + ")");
        }

        String rucClave = claveAcceso.substring(10, 23);
        if (!ruc.equals(rucClave)) {
            errors.add("RUC en clave (" + rucClave + ") no coincide con RUC del comprobante (" + ruc + ")");
        }

        String ambClave = String.valueOf(claveAcceso.charAt(23));
        if (!ambiente.equals(ambClave)) {
            warnings.add("Ambiente en clave (" + ambClave + ") no coincide con ambiente del comprobante (" + ambiente + ")");
        }

        String base48 = claveAcceso.substring(0, 48);
        char dvEsperado = claveAcceso.charAt(48);
        char dvCalculado = calcularDVMod11(base48);
        if (dvEsperado != dvCalculado) {
            errors.add("Dígito verificador inválido. Esperado: " + dvEsperado + ", calculado: " + dvCalculado);
        }
    }

    private static char calcularDVMod11(String base48) {
        final int[] pesos = {2, 3, 4, 5, 6, 7};
        int suma = 0, idx = 0;
        for (int i = base48.length() - 1; i >= 0; i--) {
            int digito = base48.charAt(i) - '0';
            suma += digito * pesos[idx];
            idx = (idx + 1) % pesos.length;
        }
        int mod = suma % 11;
        int dv = 11 - mod;
        if (dv == 11) dv = 0;
        else if (dv == 10) dv = 1;
        return (char) ('0' + dv);
    }

    public record LiquidacionCompraValidationResult(
        boolean valid,
        List<String> errors,
        List<String> warnings,
        String claveAcceso,
        String ambiente,
        String codDoc,
        String version
    ) {}
}
