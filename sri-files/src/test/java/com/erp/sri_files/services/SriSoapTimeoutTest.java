package com.erp.sri_files.services;

import jakarta.xml.ws.BindingProvider;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SriSoapTimeoutTest {
    @Test void aplicaTimeoutsConfiguradosEnElPuertoSoap() {
        var service = new SendXmlToSriService();
        ReflectionTestUtils.setField(service, "connectTimeoutMs", 10000);
        ReflectionTestUtils.setField(service, "readTimeoutMs", 60000);
        var port = mock(BindingProvider.class);
        var context = new HashMap<String, Object>();
        when(port.getRequestContext()).thenReturn(context);
        ReflectionTestUtils.invokeMethod(service, "applyTimeouts", port);
        assertEquals(10000, context.get("com.sun.xml.ws.connect.timeout"));
        assertEquals(60000, context.get("com.sun.xml.ws.request.timeout"));
        assertEquals("60000", context.get("javax.xml.ws.client.receiveTimeout"));
    }
    @Test void rechazaEsperaInfinitaAccidental() {
        var service = new SendXmlToSriService();
        ReflectionTestUtils.setField(service, "readTimeoutMs", 0);
        assertThrows(IllegalArgumentException.class, service::init);
    }
}
