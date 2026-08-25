package com.erp.sri_files.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ec.gob.sri.ws.autorizacion.Autorizacion;
import ec.gob.sri.ws.autorizacion.RespuestaComprobante;
import ec.gob.sri.ws.recepcion.RespuestaSolicitud;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;

public final class SriControllerHelper {

    private SriControllerHelper() {}

    public static String stripBom(String s) {
        if (s != null && !s.isEmpty() && s.charAt(0) == '\uFEFF') {
            return s.substring(1);
        }
        return s;
    }

    public static String safeStr(Object o) {
        return (o == null) ? "" : o.toString().trim();
    }

    public static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }

    public static Map<String, Object> resumenErroresRecepcion(RespuestaSolicitud recepcion) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("estado", recepcion.getEstado());
        if (recepcion.getComprobantes() != null && recepcion.getComprobantes().getComprobante() != null) {
            List<Map<String, String>> errores = new ArrayList<>();
            for (var c : recepcion.getComprobantes().getComprobante()) {
                Map<String, String> e = new LinkedHashMap<>();
                e.put("claveAcceso", safeStr(c.getClaveAcceso()));
                e.put("mensajes", safeStr(c.getMensajes()));
                errores.add(e);
            }
            out.put("errores", errores);
        }
        return out;
    }

    public static Autorizacion primeraAutorizacionAutorizada(List<Autorizacion> lista) {
        if (lista == null) return null;
        for (Autorizacion a : lista) {
            if ("AUTORIZADO".equalsIgnoreCase(a.getEstado())) return a;
        }
        return lista.isEmpty() ? null : lista.get(0);
    }

    public static String cleanComprobanteXml(String comprobanteBase64) {
        if (comprobanteBase64 == null || comprobanteBase64.isBlank()) return "";
        try {
            byte[] decoded = java.util.Base64.getDecoder().decode(comprobanteBase64.trim());
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return comprobanteBase64;
        }
    }

    public static String extraerXmlAutorizado(RespuestaComprobante rc) {
        if (rc == null || rc.getAutorizaciones() == null) return null;
        var lista = rc.getAutorizaciones().getAutorizacion();
        if (lista == null || lista.isEmpty()) return null;
        var autorizada = primeraAutorizacionAutorizada(lista);
        if (autorizada == null) return null;
        String xml = cleanComprobanteXml(autorizada.getComprobante());
        return xml.isBlank() ? null : xml;
    }

    public static String construirXmlAutorizacionCompleta(Autorizacion autorizada, String xmlComprobante) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<autorizacion>"
                + "<estado>" + safeStr(autorizada.getEstado()) + "</estado>"
                + "<numeroAutorizacion>" + safeStr(autorizada.getNumeroAutorizacion()) + "</numeroAutorizacion>"
                + "<fechaAutorizacion>" + (autorizada.getFechaAutorizacion() != null ? autorizada.getFechaAutorizacion().toXMLFormat() : "") + "</fechaAutorizacion>"
                + "<ambiente>" + safeStr(autorizada.getAmbiente()) + "</ambiente>"
                + "<comprobante>" + xmlComprobante + "</comprobante>"
                + "</autorizacion>";
    }

    public static byte[] buildZip(String baseName, String xmlAutorizacionCompleta, byte[] pdfBytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry(baseName + ".xml"));
            zos.write(xmlAutorizacionCompleta.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
            zos.putNextEntry(new ZipEntry(baseName + ".pdf"));
            zos.write(pdfBytes);
            zos.closeEntry();
        }
        return baos.toByteArray();
    }

    public static String extraerClaveAcceso(String xml) throws Exception {
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        var doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        var list = doc.getElementsByTagName("claveAcceso");
        if (list.getLength() == 0) return null;
        return list.item(0).getTextContent().replaceAll("\\s+", "");
    }

    public static Map<String, Object> mapRespuestaAutorizacion(RespuestaComprobante rc, boolean includeXml) {
        Map<String, Object> out = new LinkedHashMap<>();
        if (rc == null || rc.getAutorizaciones() == null || rc.getAutorizaciones().getAutorizacion() == null) {
            out.put("estado", "SIN_RESULTADO");
            return out;
        }
        for (Autorizacion a : rc.getAutorizaciones().getAutorizacion()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("estado", safeStr(a.getEstado()));
            item.put("numeroAutorizacion", safeStr(a.getNumeroAutorizacion()));
            item.put("fechaAutorizacion", a.getFechaAutorizacion() != null ? a.getFechaAutorizacion().toString() : "");
            item.put("ambiente", safeStr(a.getAmbiente()));
            if (includeXml) {
                item.put("comprobante", cleanComprobanteXml(a.getComprobante()));
            }
            out.put("autorizacion", item);
            break;
        }
        return out;
    }

    public static String getChildText(org.w3c.dom.Element element, String tagName) {
        NodeList list = element.getElementsByTagName(tagName);
        return list.getLength() == 0 ? "" : safe(list.item(0).getTextContent());
    }

    public static String extraerTexto(Document doc, String tagName) {
        NodeList list = doc.getElementsByTagName(tagName);
        return list.getLength() == 0 ? "" : safe(list.item(0).getTextContent());
    }
}
