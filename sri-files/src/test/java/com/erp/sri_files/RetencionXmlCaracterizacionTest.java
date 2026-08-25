package com.erp.sri_files;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Tests de caracterización para validar la estructura XML de retenciones.
 */
class RetencionXmlCaracterizacionTest {

    @Test
    @DisplayName("Golden XML retención debe tener estructura válida")
    void goldenXmlRetencion_estructuraValida() throws Exception {
        String xml = loadGoldenFile("golden/retencion_baseline.xml");
        
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder()
            .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        
        assertEquals("comprobanteRetencion", doc.getDocumentElement().getTagName());
        assertEquals("1.0.0", doc.getDocumentElement().getAttribute("version"));
        assertEquals("comprobante", doc.getDocumentElement().getAttribute("id"));
        
        NodeList codDocNodes = doc.getElementsByTagName("codDoc");
        assertTrue(codDocNodes.getLength() > 0);
        assertEquals("07", codDocNodes.item(0).getTextContent().trim());
        
        NodeList claveAccesoNodes = doc.getElementsByTagName("claveAcceso");
        assertTrue(claveAccesoNodes.getLength() > 0);
        String claveAcceso = claveAccesoNodes.item(0).getTextContent().trim();
        assertEquals(49, claveAcceso.length());
        assertTrue(claveAcceso.matches("\\d{49}"));
        
        NodeList rucNodes = doc.getElementsByTagName("ruc");
        assertTrue(rucNodes.getLength() > 0);
        String ruc = rucNodes.item(0).getTextContent().trim();
        assertEquals(13, ruc.length());
        
        NodeList docSustentoNodes = doc.getElementsByTagName("docSustento");
        assertTrue(docSustentoNodes.getLength() > 0);
        
        NodeList impuestoDocSustentoNodes = doc.getElementsByTagName("impuestoDocSustento");
        assertTrue(impuestoDocSustentoNodes.getLength() > 0);
    }

    @Test
    @DisplayName("Golden XML retención debe tener codDoc = 07")
    void goldenXmlRetencion_codDocRetencion() throws Exception {
        String xml = loadGoldenFile("golden/retencion_baseline.xml");
        
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder()
            .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        
        NodeList codDocNodes = doc.getElementsByTagName("codDoc");
        assertEquals("07", codDocNodes.item(0).getTextContent().trim());
    }

    @Test
    @DisplayName("Clave de acceso retención debe tener tipo comprobante 07")
    void claveAccesoRetencion_tipoComprobante() throws Exception {
        String xml = loadGoldenFile("golden/retencion_baseline.xml");
        
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder()
            .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        
        String claveAcceso = doc.getElementsByTagName("claveAcceso")
            .item(0).getTextContent().trim();
        
        assertEquals(49, claveAcceso.length());
        
        String tipoComprobante = claveAcceso.substring(8, 10);
        assertEquals("07", tipoComprobante);
    }

    private String loadGoldenFile(String path) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            fail("No se encontró archivo golden: " + path);
        }
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
}
