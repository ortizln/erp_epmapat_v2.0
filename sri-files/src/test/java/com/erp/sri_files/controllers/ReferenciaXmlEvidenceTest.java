package com.erp.sri_files.controllers;

import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.DocumentoElectronicoR;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.services.SendXmlToSriService;
import com.erp.sri_files.services.SriGateway;
import com.erp.sri_files.services.XmlAutorizadoRecoveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.gob.sri.ws.autorizacion.RespuestaComprobante;
import jakarta.xml.bind.JAXBContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Reproduce el contrato con evidencia descargada, sin conexiones ni escrituras en produccion. */
@EnabledIfSystemProperty(named = "sri.fixture-dir", matches = ".+")
class ReferenciaXmlEvidenceTest {
    @Test void reproducesAll32KeysAndRecoversAll10StoredXmls() throws Exception {
        var directory = Path.of(System.getProperty("sri.fixture-dir"));
        var mapper = new ObjectMapper();
        var invoices = mock(FacturaR.class);
        var documents = mock(DocumentoElectronicoR.class);
        var soap = mock(SendXmlToSriService.class);
        var recovery = new XmlAutorizadoRecoveryService(documents, invoices, new SriGateway(soap));
        ReflectionTestUtils.setField(recovery, "retryDelayMs", 0L);
        var rows = mapper.readTree(directory.resolve("12260_facturas.json").toFile());
        var unmarshaller = JAXBContext.newInstance(RespuestaComprobante.class).createUnmarshaller();
        var factory = DocumentBuilderFactory.newInstance(); factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        for (var row : rows) {
            long id = row.path("idfactura").asLong(); String key = row.path("claveacceso").asText();
            var invoice = new Factura(); invoice.setIdfactura(id); invoice.setClaveacceso(key);
            invoice.setEstado(row.path("estado").asText());
            var cache = directory.resolve("12260_cache/" + id + ".xml");
            if (Files.exists(cache)) invoice.setXmlautorizado(Files.readString(cache));
            when(invoices.findByClaveacceso(key)).thenReturn(Optional.of(invoice));
            var response = factory.newDocumentBuilder().parse(directory.resolve("12260_directo/" + id + ".xml").toFile());
            var element = response.getElementsByTagNameNS("*", "RespuestaAutorizacionComprobante").item(0);
            var typed = unmarshaller.unmarshal(new DOMSource(element), RespuestaComprobante.class).getValue();
            when(soap.consultarAutorizacion(eq(key), eq(2), any(Integer.class))).thenReturn(typed);
        }
        when(invoices.actualizarXmlAutorizadoPorClave(anyString(), anyString())).thenReturn(1);
        var mvc = MockMvcBuilders.standaloneSetup(new AutorizacionController(soap, invoices, recovery)).build();
        var report = new ArrayList<Map<String, Object>>(); int recovered = 0;
        for (var row : rows) {
            long id = row.path("idfactura").asLong(); String key = row.path("claveacceso").asText();
            var response = mvc.perform(get("/api/v1/autorizacion/" + key + "/xml")).andReturn().getResponse();
            var body = mapper.readTree(response.getContentAsString());
            boolean hasXml = !body.path("xmlAutorizado").asText().isBlank();
            if (hasXml) recovered++;
            assertEquals(row.path("tieneXml").asBoolean(), hasXml, "idfactura=" + id);
            assertEquals(hasXml ? 200 : 202, response.getStatus());
            if (hasXml) assertEquals(Files.readString(directory.resolve("12260_cache/" + id + ".xml")), body.path("xmlAutorizado").asText());
            report.add(Map.of("idfactura", id, "http", response.getStatus(), "xmlObtenido", hasXml,
                    "estado", body.path("estado").asText(), "fuente", body.path("fuente").asText(),
                    "detalle", body.path("detalleUltimaConsulta").asText()));
        }
        assertEquals(32, report.size()); assertEquals(10, recovered);
        mapper.writerWithDefaultPrettyPrinter().writeValue(directory.resolve("12260_api_corregido_local.json").toFile(), report);
    }
}
