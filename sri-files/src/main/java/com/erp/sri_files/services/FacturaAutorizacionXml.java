package com.erp.sri_files.services;

import com.erp.sri_files.dto.AutorizacionSriResult;

/** Solo construye un RIDE con evidencia devuelta por el SRI. */
public final class FacturaAutorizacionXml {
    private FacturaAutorizacionXml() {}
    public static String completo(AutorizacionSriResult result, String clave) throws Exception {
        if (result == null || !result.isAutorizado() || result.getXmlAutorizado() == null)
            throw new IllegalArgumentException("SRI sin XML autorizado");
        String xml = result.getXmlAutorizado();
        var parsed = FacturaPdfXml.parse(xml);
        if (clave == null || !clave.trim().equals(FacturaPdfXml.text(parsed.factura, "claveAcceso")))
            throw new IllegalArgumentException("XML autorizado no corresponde a la clave solicitada");
        if (!parsed.numeroAutorizacion.isBlank() && !parsed.fechaAutorizacion.isBlank()) return xml;
        if (result.getNumeroAutorizacion() == null || result.getNumeroAutorizacion().isBlank()
                || result.getFechaAutorizacion() == null)
            throw new IllegalArgumentException("SRI autorizo sin numero/fecha; pendiente recuperar evidencia completa");
        String envelope = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><autorizacion><estado>AUTORIZADO</estado>"
                + "<numeroAutorizacion>" + escape(result.getNumeroAutorizacion()) + "</numeroAutorizacion>"
                + "<fechaAutorizacion>" + result.getFechaAutorizacion() + "</fechaAutorizacion>"
                + "<comprobante><![CDATA[" + xml.replace("]]>", "]]]]><![CDATA[>") + "]]></comprobante></autorizacion>";
        FacturaPdfXml.parse(envelope);
        return envelope;
    }
    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
