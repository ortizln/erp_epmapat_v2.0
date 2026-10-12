package com.erp.sri_files.controllers;

import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.services.SendXmlToSriService;
import com.erp.sri_files.services.XmlAutorizadoRecoveryService;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutorizacionRecoveryControllerTest {
    @Test void manualEndpointUsesSharedRecoveryAndUpdatesOnlyXml() {
        var invoices = mock(FacturaR.class);
        var recovery = mock(XmlAutorizadoRecoveryService.class);
        var controller = new AutorizacionController(mock(SendXmlToSriService.class), invoices, recovery);
        when(recovery.recuperarConEspera(" key ", false)).thenReturn(new XmlAutorizadoRecoveryService.Resultado(
                "<factura/>", "BD_documento_electronico", "M", "key", null, null));
        when(invoices.actualizarXmlAutorizadoPorClave("key", "<factura/>")).thenReturn(1);
        var response = controller.obtenerXmlAutorizado(" key ", false);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, ((Map<?, ?>) response.getBody()).get("actualizadoEnFecFactura"));
        assertEquals("BD_documento_electronico", ((Map<?, ?>) response.getBody()).get("fuente"));
        assertEquals("AUTORIZADO", ((Map<?, ?>) response.getBody()).get("estado"));
        verify(invoices).actualizarXmlAutorizadoPorClave("key", "<factura/>");
        verify(invoices, never()).save(any());
    }
    @Test void missingXmlNeverUpdatesInvoice() {
        var invoices = mock(FacturaR.class);
        var recovery = mock(XmlAutorizadoRecoveryService.class);
        var controller = new AutorizacionController(mock(SendXmlToSriService.class), invoices, recovery);
        when(recovery.recuperarConEspera("key", true)).thenReturn(new XmlAutorizadoRecoveryService.Resultado(
                null, "SRI", null, null, null, "Sin autorizaciones"));
        var response = controller.obtenerXmlAutorizado("key", true);
        assertEquals(202, response.getStatusCode().value());
        assertEquals("5", response.getHeaders().getFirst("Retry-After"));
        assertEquals("PENDIENTE_CONSULTA", ((Map<?, ?>) response.getBody()).get("estado"));
        verifyNoInteractions(invoices);
    }
    @Test void recoveredXmlIsReturnedEvenIfPersistenceFails() {
        var invoices = mock(FacturaR.class);
        var recovery = mock(XmlAutorizadoRecoveryService.class);
        var controller = new AutorizacionController(mock(SendXmlToSriService.class), invoices, recovery);
        when(recovery.recuperarConEspera("key", false)).thenReturn(new XmlAutorizadoRecoveryService.Resultado(
                "<factura/>", "SRI", "AUTORIZADO", "key", null, null));
        when(invoices.actualizarXmlAutorizadoPorClave(anyString(), anyString()))
                .thenThrow(new org.springframework.dao.DataAccessResourceFailureException("offline"));
        var response = controller.obtenerXmlAutorizado("key", false);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("<factura/>", ((Map<?, ?>) response.getBody()).get("xmlAutorizado"));
        assertEquals(false, ((Map<?, ?>) response.getBody()).get("actualizadoEnFecFactura"));
    }
    @Test void invalidKeyHasBadRequestStatus() {
        var invoices = mock(FacturaR.class);
        var recovery = mock(XmlAutorizadoRecoveryService.class);
        var controller = new AutorizacionController(mock(SendXmlToSriService.class), invoices, recovery);
        when(recovery.recuperarConEspera("123", false)).thenThrow(new IllegalArgumentException("Clave invalida"));
        assertEquals(400, controller.obtenerXmlAutorizado("123", false).getStatusCode().value());
        verifyNoInteractions(invoices);
    }
    @Test void historicalDateLimitIsExplainedWithoutAdvisingAnImmediateRetry() {
        var recovery = mock(XmlAutorizadoRecoveryService.class);
        var controller = new AutorizacionController(mock(SendXmlToSriService.class), mock(FacturaR.class), recovery);
        when(recovery.recuperarConEspera("key", false)).thenReturn(new XmlAutorizadoRecoveryService.Resultado(
                null, "SRI", "PENDIENTE_CONSULTA", null, null, "RECHAZADA | [80] fecha fuera del rango permitido"));
        var response = controller.obtenerXmlAutorizado("key", false);
        assertEquals(202, response.getStatusCode().value());
        assertNull(response.getHeaders().getFirst("Retry-After"));
        assertEquals(true, ((Map<?, ?>) response.getBody()).get("requiereRecuperacionHistorica"));
        assertEquals(false, ((Map<?, ?>) response.getBody()).get("reintentable"));
    }
}
