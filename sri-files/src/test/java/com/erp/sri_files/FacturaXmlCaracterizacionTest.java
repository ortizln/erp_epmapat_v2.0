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
 * Tests de caracterización para validar que la estructura XML de facturas
 * se mantiene consistente. Estos tests usan archivos "golden" que representan
 * el comportamiento actual esperado.
 */
class FacturaXmlCaracterizacionTest {

    @Test
    @DisplayName("Golden XML factura debe tener estructura válida")
    void goldenXmlFactura_estructuraValida() throws Exception {
        String xml = loadGoldenFile("golden/factura_baseline.xml");
        
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder()
            .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        
        // Validar raíz
        assertEquals("factura", doc.getDocumentElement().getTagName());
        assertEquals("1.1.0", doc.getDocumentElement().getAttribute("version"));
        
        // Validar infoTributaria
        NodeList ambienteNodes = doc.getElementsByTagName("ambiente");
        assertTrue(ambienteNodes.getLength() > 0);
        assertEquals("2", ambienteNodes.item(0).getTextContent());
        
        NodeList claveAccesoNodes = doc.getElementsByTagName("claveAcceso");
        assertTrue(claveAccesoNodes.getLength() > 0);
        String claveAcceso = claveAccesoNodes.item(0).getTextContent().trim();
        assertEquals(49, claveAcceso.length());
        assertTrue(claveAcceso.matches("\\d{49}"));
        
        // Validar infoFactura
        NodeList fechaEmisionNodes = doc.getElementsByTagName("fechaEmision");
        assertTrue(fechaEmisionNodes.getLength() > 0);
        
        NodeList importeTotalNodes = doc.getElementsByTagName("importeTotal");
        assertTrue(importeTotalNodes.getLength() > 0);
        
        // Validar detalles
        NodeList detalleNodes = doc.getElementsByTagName("detalle");
        assertTrue(detalleNodes.getLength() > 0);
        
        // Validar impuestos en detalle
        NodeList impuestoNodes = doc.getElementsByTagName("impuesto");
        assertTrue(impuestoNodes.getLength() > 0);
        
        // Validar pagos
        NodeList pagoNodes = doc.getElementsByTagName("pago");
        assertTrue(pagoNodes.getLength() > 0);
    }

    @Test
    @DisplayName("Golden XML factura debe tener codDoc = 01")
    void goldenXmlFactura_codDocFactura() throws Exception {
        String xml = loadGoldenFile("golden/factura_baseline.xml");
        
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder()
            .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        
        NodeList codDocNodes = doc.getElementsByTagName("codDoc");
        assertTrue(codDocNodes.getLength() > 0);
        assertEquals("01", codDocNodes.item(0).getTextContent().trim());
    }

    @Test
    @DisplayName("Golden XML factura debe tener totalConImpuestos")
    void goldenXmlFactura_totalConImpuestos() throws Exception {
        String xml = loadGoldenFile("golden/factura_baseline.xml");
        
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder()
            .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        
        NodeList totalImpuestoNodes = doc.getElementsByTagName("totalImpuesto");
        assertTrue(totalImpuestoNodes.getLength() > 0);
        
        // Validar que cada totalImpuesto tiene código y valor
        for (int i = 0; i < totalImpuestoNodes.getLength(); i++) {
            var elemento = (org.w3c.dom.Element) totalImpuestoNodes.item(i);
            assertNotNull(elemento.getElementsByTagName("codigo").item(0));
            assertNotNull(elemento.getElementsByTagName("baseImponible").item(0));
            assertNotNull(elemento.getElementsByTagName("valor").item(0));
        }
    }

    @Test
    @DisplayName("Clave de acceso debe tener formato válido")
    void claveAcceso_formatoValido() throws Exception {
        String xml = loadGoldenFile("golden/factura_baseline.xml");
        
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder()
            .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
        
        String claveAcceso = doc.getElementsByTagName("claveAcceso")
            .item(0).getTextContent().trim();
        
        // 49 dígitos
        assertEquals(49, claveAcceso.length());
        assertTrue(claveAcceso.matches("\\d{49}"));
        
        // Primeros 8 dígitos = fecha (ddMMyyyy)
        String fechaParte = claveAcceso.substring(0, 8);
        assertNotNull(fechaParte);
        
        // Dígito 9-10 = tipo comprobante (01 = factura)
        String tipoComprobante = claveAcceso.substring(8, 10);
        assertEquals("01", tipoComprobante);
        
        // Dígitos 11-23 = RUC (13 dígitos)
        String ruc = claveAcceso.substring(10, 23);
        assertEquals(13, ruc.length());
        
        // Dígito 24 = ambiente (1 o 2)
        char ambiente = claveAcceso.charAt(23);
        assertTrue(ambiente == '1' || ambiente == '2');
    }

    private String loadGoldenFile(String path) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            fail("No se encontró archivo golden: " + path);
        }
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
}
