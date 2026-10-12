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
        when(recovery.recuperar(" key ", false)).thenReturn(new XmlAutorizadoRecoveryService.Resultado(
                "<factura/>", "BD_documento_electronico", "AUTORIZADO", "key", null, null));
        when(invoices.actualizarXmlAutorizadoPorClave("key", "<factura/>")).thenReturn(1);
        var response = controller.obtenerXmlAutorizado(" key ", false);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, ((Map<?, ?>) response.getBody()).get("actualizadoEnFecFactura"));
        assertEquals("BD_documento_electronico", ((Map<?, ?>) response.getBody()).get("fuente"));
        verify(invoices).actualizarXmlAutorizadoPorClave("key", "<factura/>");
        verify(invoices, never()).save(any());
    }
    @Test void missingXmlNeverUpdatesInvoice() {
        var invoices = mock(FacturaR.class);
        var recovery = mock(XmlAutorizadoRecoveryService.class);
        var controller = new AutorizacionController(mock(SendXmlToSriService.class), invoices, recovery);
        when(recovery.recuperar("key", true)).thenReturn(new XmlAutorizadoRecoveryService.Resultado(
                null, "SRI", null, null, null, "Sin autorizaciones"));
        assertEquals(404, controller.obtenerXmlAutorizado("key", true).getStatusCode().value());
        verifyNoInteractions(invoices);
    }
}
