package com.erp.sri_files.services;

import ec.gob.sri.ws.autorizacion.Autorizacion;
import ec.gob.sri.ws.autorizacion.RespuestaComprobante;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SriGatewayRecoveryTest {
    private static final String KEY = "1009202601046002881000120010130004585995621446311";
    private static final String XML = "<factura><claveAcceso>" + KEY + "</claveAcceso></factura>";
    private final SendXmlToSriService soap = mock(SendXmlToSriService.class);
    private final SriGateway gateway = new SriGateway(soap);

    private RespuestaComprobante response(String... xmls) {
        var response = new RespuestaComprobante();
        var authorizations = new RespuestaComprobante.Autorizaciones();
        for (String xml : xmls) {
            var auth = new Autorizacion(); auth.setEstado("AUTORIZADO"); auth.setComprobante(xml);
            authorizations.getAutorizacion().add(auth);
        }
        response.setAutorizaciones(authorizations);
        return response;
    }
    @Test void skipsAnAuthorizedEntryWithoutXmlWhenALaterEntryContainsIt() throws Exception {
        when(soap.consultarAutorizacion(KEY, 2, 3500)).thenReturn(response("", XML));
        var result = gateway.consultarAutorizacionAcotada(KEY, 3500);
        assertTrue(result.autorizado()); assertEquals(XML, result.xmlAutorizado());
    }
    @Test void handlesCdataAndBase64WithoutChangingXml() throws Exception {
        when(soap.consultarAutorizacion(KEY, 2, 3500)).thenReturn(response("<![CDATA[" + XML + "]]>"),
                response(Base64.getEncoder().encodeToString(XML.getBytes(StandardCharsets.UTF_8))));
        assertEquals(XML, gateway.consultarAutorizacionAcotada(KEY, 3500).xmlAutorizado());
        assertEquals(XML, gateway.consultarAutorizacionAcotada(KEY, 3500).xmlAutorizado());
    }
    @Test void connectionFailureIsInconclusiveInsteadOfAnAuthorizationDecision() throws Exception {
        when(soap.consultarAutorizacion(KEY, 2, 3500)).thenThrow(new java.net.SocketTimeoutException("Connect timed out"));
        var result = gateway.consultarAutorizacionAcotada(KEY, 3500);
        assertFalse(result.autorizado()); assertNull(result.respuestaCompleta()); assertNull(result.xmlAutorizado());
        assertEquals("Connect timed out", result.mensaje());
    }
    @Test void preservesSriIdentifierAndDateRangeExplanation() throws Exception {
        var response = response("");
        var auth = response.getAutorizaciones().getAutorizacion().get(0); auth.setEstado("RECHAZADA");
        var messages = new Autorizacion.Mensajes();
        var message = new ec.gob.sri.ws.autorizacion.Mensaje(); message.setIdentificador("80");
        message.setMensaje("ERROR EN LA ESTRUCTURA DE LA CLAVE DE ACCESO");
        message.setInformacionAdicional("La fecha de emision esta fuera del rango permitido");
        messages.getMensaje().add(message); auth.setMensajes(messages);
        when(soap.consultarAutorizacion(KEY, 2, 3500)).thenReturn(response);
        var result = gateway.consultarAutorizacionAcotada(KEY, 3500);
        assertFalse(result.autorizado()); assertTrue(result.mensaje().contains("[80]"));
        assertTrue(result.mensaje().contains("fuera del rango permitido"));
    }
}
