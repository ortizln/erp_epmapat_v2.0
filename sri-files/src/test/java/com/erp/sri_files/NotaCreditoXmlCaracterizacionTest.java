package com.erp.sri_files;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

class NotaCreditoXmlCaracterizacionTest {

    @Test
    @DisplayName("Golden XML nota de crédito debe tener estructura válida")
    void goldenXmlNotaCredito_estructuraValida() throws Exception {
        String xml = loadGoldenFile("golden/nota_credito_baseline.xml");
        
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder()
            .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        
        assertEquals("notaCredito", doc.getDocumentElement().getTagName());
        assertEquals("1.0.0", doc.getDocumentElement().getAttribute("version"));
        assertEquals("comprobante", doc.getDocumentElement().getAttribute("id"));
        
        NodeList codDocNodes = doc.getElementsByTagName("codDoc");
        assertTrue(codDocNodes.getLength() > 0);
        assertEquals("04", codDocNodes.item(0).getTextContent().trim());
        
        NodeList claveAccesoNodes = doc.getElementsByTagName("claveAcceso");
        assertTrue(claveAccesoNodes.getLength() > 0);
        String claveAcceso = claveAccesoNodes.item(0).getTextContent().trim();
        assertEquals(49, claveAcceso.length());
        assertTrue(claveAcceso.matches("\\d{49}"));
        
        NodeList codDocModNodes = doc.getElementsByTagName("codDocModificado");
        assertTrue(codDocModNodes.getLength() > 0);
        assertEquals("01", codDocModNodes.item(0).getTextContent().trim());
        
        NodeList numDocModNodes = doc.getElementsByTagName("numDocModificado");
        assertTrue(numDocModNodes.getLength() > 0);
        
        NodeList motivoNodes = doc.getElementsByTagName("motivo");
        assertTrue(motivoNodes.getLength() > 0);
    }

    @Test
    @DisplayName("Clave de acceso nota crédito debe tener tipo comprobante 04")
    void claveAccesoNotaCredito_tipoComprobante() throws Exception {
        String xml = loadGoldenFile("golden/nota_credito_baseline.xml");
        
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder()
            .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        
        String claveAcceso = doc.getElementsByTagName("claveAcceso")
            .item(0).getTextContent().trim();
        
        String tipoComprobante = claveAcceso.substring(8, 10);
        assertEquals("04", tipoComprobante);
    }

    @Test
    @DisplayName("Nota de crédito debe tener detalles con impuestos")
    void notaCredito_detallesConImpuestos() throws Exception {
        String xml = loadGoldenFile("golden/nota_credito_baseline.xml");
        
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder()
            .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        
        NodeList detalleNodes = doc.getElementsByTagName("detalle");
        assertTrue(detalleNodes.getLength() > 0);
        
        NodeList impuestoNodes = doc.getElementsByTagName("impuesto");
        assertTrue(impuestoNodes.getLength() > 0);
    }

    private String loadGoldenFile(String path) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            fail("No se encontró archivo golden: " + path);
        }
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
}
