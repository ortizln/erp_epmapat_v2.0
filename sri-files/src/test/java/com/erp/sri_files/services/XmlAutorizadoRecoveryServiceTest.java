package com.erp.sri_files.services;

import com.erp.sri_files.models.DocumentoElectronico;
import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.DocumentoElectronicoR;
import com.erp.sri_files.repositories.FacturaR;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class XmlAutorizadoRecoveryServiceTest {
    private static final String KEY = "0810202601046002881000120010150000291908329164211";
    private static final String XML = "<factura><infoTributaria><claveAcceso>" + KEY
            + "</claveAcceso><razonSocial>A &amp; B</razonSocial></infoTributaria></factura>";
    private final DocumentoElectronicoR documents = mock(DocumentoElectronicoR.class);
    private final FacturaR invoices = mock(FacturaR.class);
    private final SriGateway gateway = mock(SriGateway.class);
    private final XmlAutorizadoRecoveryService service = new XmlAutorizadoRecoveryService(documents, invoices, gateway);

    @BeforeEach void setup() {
        when(documents.findByClaveAcceso(KEY)).thenReturn(Optional.empty());
        when(invoices.findByClaveacceso(KEY)).thenReturn(Optional.empty());
    }
    private SriGateway.SriAutorizacionResult authorized(String xml) {
        return new SriGateway.SriAutorizacionResult(true, xml, KEY, "2026-10-08T19:00:00Z", "AUTORIZADO", null);
    }
    @Test void usesDocumentCacheWithoutConsultingSriAndPreservesXml() {
        var doc = new DocumentoElectronico(); doc.setXmlAutorizado(XML); doc.setEstado("AUTORIZADO");
        when(documents.findByClaveAcceso(KEY)).thenReturn(Optional.of(doc));
        var result = service.recuperar(" " + KEY + " ", false);
        assertEquals(XML, result.xml()); assertEquals("BD_documento_electronico", result.fuente());
        verifyNoInteractions(gateway); verify(invoices, never()).findByClaveacceso(anyString());
    }
    @Test void usesInvoiceCacheEvenWhenItsStateIsM() {
        var invoice = new Factura(); invoice.setXmlautorizado(XML); invoice.setEstado("M");
        when(invoices.findByClaveacceso(KEY)).thenReturn(Optional.of(invoice));
        var result = service.recuperar(KEY, false);
        assertTrue(result.recuperado()); assertEquals("BD_fec_factura", result.fuente());
        verifyNoInteractions(gateway);
    }
    @Test void missingCacheQueriesSriAndUsesRealAuthorizationDate() {
        when(gateway.consultarAutorizacion(KEY)).thenReturn(authorized(XML));
        var result = service.recuperar(KEY, false);
        assertEquals(XML, result.xml()); assertEquals("SRI", result.fuente());
        assertEquals(java.time.LocalDateTime.of(2026, 10, 8, 14, 0), result.fechaAutorizacion());
    }
    @Test void forceSriBypassesBothCaches() {
        when(gateway.consultarAutorizacion(KEY)).thenReturn(authorized(XML));
        assertTrue(service.recuperar(KEY, true).recuperado());
        verifyNoInteractions(documents, invoices);
    }
    @Test void mismatchedCachedXmlFallsBackToSri() {
        var invoice = new Factura(); invoice.setXmlautorizado(XML.replace(KEY, "otra-clave"));
        when(invoices.findByClaveacceso(KEY)).thenReturn(Optional.of(invoice));
        when(gateway.consultarAutorizacion(KEY)).thenReturn(authorized(XML));
        assertEquals("SRI", service.recuperar(KEY, false).fuente());
    }
    @Test void rejectsMismatchedSriXmlAndNonAuthorizedResponse() {
        when(gateway.consultarAutorizacion(KEY)).thenReturn(authorized(XML.replace(KEY, "otra-clave")));
        assertFalse(service.recuperar(KEY, false).recuperado());
        when(gateway.consultarAutorizacion(KEY)).thenReturn(
                new SriGateway.SriAutorizacionResult(false, XML, null, null, "NO AUTORIZADO", null));
        assertFalse(service.recuperar(KEY, false).recuperado());
    }
    @Test void rejectsInvalidKeyBeforeAccessingRepositoriesOrSri() {
        assertThrows(IllegalArgumentException.class, () -> service.recuperar("", false));
        assertThrows(IllegalArgumentException.class, () -> service.recuperar("123", false));
        verifyNoInteractions(documents, invoices, gateway);
    }
    @Test void rejectsXmlExternalEntitiesAndMalformedDocuments() {
        when(gateway.consultarAutorizacion(KEY)).thenReturn(authorized("<!DOCTYPE factura [<!ENTITY x SYSTEM 'file:///invalid'>]><factura>&x;</factura>"));
        assertFalse(service.recuperar(KEY, true).recuperado());
        when(gateway.consultarAutorizacion(KEY)).thenReturn(authorized("<factura>"));
        assertFalse(service.recuperar(KEY, true).recuperado());
    }
}
