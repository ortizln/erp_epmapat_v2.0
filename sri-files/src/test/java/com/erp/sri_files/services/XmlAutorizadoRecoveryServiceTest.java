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
        org.springframework.test.util.ReflectionTestUtils.setField(service, "retryDelayMs", 0L);
        when(documents.findByClaveAcceso(KEY)).thenReturn(Optional.empty());
        when(invoices.findByClaveacceso(KEY)).thenReturn(Optional.empty());
    }
    private SriGateway.SriAutorizacionResult authorized(String xml) {
        return new SriGateway.SriAutorizacionResult(true, xml, KEY, "2026-10-08T19:00:00Z", "AUTORIZADO", null);
    }
    @Test void rideRecoveryRefreshesIncompleteInvoiceCacheAndStoresAuthorizationEnvelope() throws Exception {
        var invoice = new Factura(); invoice.setEstado("M"); invoice.setXmlautorizado(XML);
        when(invoices.findByClaveacceso(KEY)).thenReturn(Optional.of(invoice));
        when(gateway.consultarAutorizacion(KEY)).thenReturn(authorized(XML));
        var result = service.recuperarFacturaParaRide(KEY);
        var parsed = FacturaPdfXml.parse(result.xml());
        assertEquals(KEY, parsed.numeroAutorizacion);
        assertEquals("2026-10-08T14:00", parsed.fechaAutorizacion);
        assertEquals(KEY, FacturaPdfXml.text(parsed.factura, "claveAcceso"));
        assertEquals("A & B", FacturaPdfXml.text(parsed.factura, "razonSocial"));
        assertTrue(result.xml().contains(XML));
        verify(gateway).consultarAutorizacion(KEY);
    }
    @Test void rideRecoveryReusesCompleteEnvelopeAndReadsItsDate() {
        String xml = "<autorizacion><estado>AUTORIZADO</estado><numeroAutorizacion>" + KEY
                + "</numeroAutorizacion><fechaAutorizacion>2026-10-08T19:00:00Z</fechaAutorizacion>"
                + "<comprobante><![CDATA[" + XML + "]]></comprobante></autorizacion>";
        var invoice = new Factura(); invoice.setXmlautorizado(xml);
        when(invoices.findByClaveacceso(KEY)).thenReturn(Optional.of(invoice));
        var result = service.recuperarFacturaParaRide(KEY);
        assertEquals(xml, result.xml());
        assertEquals(java.time.LocalDateTime.of(2026, 10, 8, 14, 0), result.fechaAutorizacion());
        verifyNoInteractions(gateway);
    }
    @Test void rideRecoveryDoesNotAcceptMissingAuthorizationDateOrInventOne() {
        when(gateway.consultarAutorizacion(KEY)).thenReturn(
                new SriGateway.SriAutorizacionResult(true, XML, KEY, null, "AUTORIZADO", null));
        var result = service.recuperarFacturaParaRide(KEY);
        assertFalse(result.recuperado()); assertNull(result.fechaAutorizacion());
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
        assertEquals("AUTORIZADO", result.estado());
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
    private SriGateway.SriAutorizacionResult pending(String message) {
        return new SriGateway.SriAutorizacionResult(false, null, null, null, message, null);
    }
    private String soap(String authorizations) {
        return "<s:Envelope xmlns:s='http://schemas.xmlsoap.org/soap/envelope/'><s:Body>"
                + "<RespuestaAutorizacionComprobante><autorizaciones>" + authorizations
                + "</autorizaciones></RespuestaAutorizacionComprobante></s:Body></s:Envelope>";
    }
    private String auth(String state, String xml) {
        return "<autorizacion><estado>" + state + "</estado><comprobante>" + xml
                + "</comprobante></autorizacion>";
    }
    @Test void acceptsStoredAuthorizedSoapWithoutChangingItOrConsultingSri() {
        var invoice = new Factura(); invoice.setEstado("M");
        String envelope = soap(auth("AUTORIZADO", XML)); invoice.setXmlautorizado(envelope);
        when(invoices.findByClaveacceso(KEY)).thenReturn(Optional.of(invoice));
        var result = service.recuperarConEspera(KEY, false);
        assertEquals(envelope, result.xml()); assertEquals("AUTORIZADO", result.estado());
        verifyNoInteractions(gateway);
    }
    @Test void acceptsSoapCdataAndBase64() {
        var invoice = new Factura(); invoice.setXmlautorizado(soap(auth("AUTORIZADO", "<![CDATA[" + XML + "]]>")));
        when(invoices.findByClaveacceso(KEY)).thenReturn(Optional.of(invoice));
        assertTrue(service.recuperarConEspera(KEY, false).recuperado());
        invoice.setXmlautorizado(soap(auth("AUTORIZADO", java.util.Base64.getEncoder()
                .encodeToString(XML.getBytes(java.nio.charset.StandardCharsets.UTF_8)))));
        assertTrue(service.recuperarConEspera(KEY, false).recuperado());
    }
    @Test void rejectedAuthorizationWithMatchingKeyCannotBorrowAnotherAuthorizationState() {
        var invoice = new Factura(); invoice.setXmlautorizado(soap(auth("RECHAZADA", XML)
                + auth("AUTORIZADO", XML.replace(KEY, "otra-clave"))));
        when(invoices.findByClaveacceso(KEY)).thenReturn(Optional.of(invoice));
        when(gateway.consultarAutorizacionAcotada(eq(KEY), anyInt())).thenReturn(pending("Sin XML"));
        assertFalse(service.recuperarConEspera(KEY, false).recuperado());
    }
    @Test void pollingContinuesAfterNegativeAndEmptyResultsUntilXmlArrives() {
        when(gateway.consultarAutorizacionAcotada(eq(KEY), anyInt()))
                .thenReturn(pending("NO AUTORIZADO"), pending("Sin autorizaciones"), authorized(XML));
        var result = service.recuperarConEspera(KEY, false);
        assertEquals(XML, result.xml()); assertEquals("AUTORIZADO", result.estado());
        verify(gateway, times(3)).consultarAutorizacionAcotada(eq(KEY), anyInt());
    }
    @Test void historicalDateLimitDoesNotWasteAdditionalRetries() {
        when(gateway.consultarAutorizacionAcotada(eq(KEY), anyInt())).thenReturn(pending(
                "RECHAZADA | [80] ERROR EN LA ESTRUCTURA DE LA CLAVE DE ACCESO: fecha de emision fuera del rango permitido"));
        var result = service.recuperarConEspera(KEY, true);
        assertTrue(result.requiereRecuperacionHistorica()); assertFalse(result.recuperado());
        verify(gateway, times(1)).consultarAutorizacionAcotada(eq(KEY), anyInt());
    }
    @Test void exhaustedPollingIsInconclusiveAndDoesNotLoopForever() {
        when(gateway.consultarAutorizacionAcotada(eq(KEY), anyInt())).thenReturn(pending("Connect timed out"));
        var result = service.recuperarConEspera(KEY, false);
        assertFalse(result.recuperado()); assertEquals("PENDIENTE_CONSULTA", result.estado());
        verify(gateway, times(3)).consultarAutorizacionAcotada(eq(KEY), intThat(value -> value > 0 && value <= 3500));
    }
    @Test void forceSriFailurePreservesKnownAuthorizedXml() {
        var invoice = new Factura(); invoice.setXmlautorizado(XML); invoice.setEstado("C");
        when(invoices.findByClaveacceso(KEY)).thenReturn(Optional.of(invoice));
        when(gateway.consultarAutorizacionAcotada(eq(KEY), anyInt())).thenReturn(pending("Connect timed out"));
        var result = service.recuperarConEspera(KEY, true);
        assertEquals(XML, result.xml()); assertEquals("AUTORIZADO", result.estado());
        assertEquals("BD_fec_factura", result.fuente()); assertNotNull(result.mensaje());
    }
    @Test void cacheDatabaseFailureDoesNotHideAnXmlReturnedBySri() {
        when(documents.findByClaveAcceso(KEY)).thenThrow(new org.springframework.dao.DataAccessResourceFailureException("offline"));
        when(invoices.findByClaveacceso(KEY)).thenThrow(new org.springframework.dao.DataAccessResourceFailureException("offline"));
        when(gateway.consultarAutorizacionAcotada(eq(KEY), anyInt())).thenReturn(authorized(XML));
        assertEquals(XML, service.recuperarConEspera(KEY, false).xml());
    }
    @Test void successfulFirstAttemptDoesNotPollAgain() {
        when(gateway.consultarAutorizacionAcotada(eq(KEY), anyInt())).thenReturn(authorized(XML));
        assertTrue(service.recuperarConEspera(KEY, true).recuperado());
        verify(gateway, times(1)).consultarAutorizacionAcotada(eq(KEY), anyInt());
    }
    @Test void simultaneousRequestsShareTheSameSriQuery() throws Exception {
        var entered = new java.util.concurrent.CountDownLatch(1);
        var release = new java.util.concurrent.CountDownLatch(1);
        when(gateway.consultarAutorizacionAcotada(eq(KEY), anyInt())).thenAnswer(call -> {
            entered.countDown();
            assertTrue(release.await(3, java.util.concurrent.TimeUnit.SECONDS));
            return authorized(XML);
        });
        var executor = java.util.concurrent.Executors.newFixedThreadPool(2);
        try {
            var leader = executor.submit(() -> service.recuperarConEspera(KEY, true));
            assertTrue(entered.await(2, java.util.concurrent.TimeUnit.SECONDS));
            var follower = executor.submit(() -> service.recuperarConEspera(KEY, true));
            @SuppressWarnings("unchecked")
            var running = (java.util.Map<String, java.util.concurrent.CompletableFuture<?>>)
                    org.springframework.test.util.ReflectionTestUtils.getField(service, "enCurso");
            long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(2);
            while (running.get(KEY).getNumberOfDependents() == 0 && System.nanoTime() < deadline) Thread.sleep(1);
            assertTrue(running.get(KEY).getNumberOfDependents() > 0);
            release.countDown();
            assertEquals(XML, leader.get(2, java.util.concurrent.TimeUnit.SECONDS).xml());
            assertEquals(XML, follower.get(2, java.util.concurrent.TimeUnit.SECONDS).xml());
            verify(gateway, times(1)).consultarAutorizacionAcotada(eq(KEY), anyInt());
        } finally { release.countDown(); executor.shutdownNow(); }
    }
}
