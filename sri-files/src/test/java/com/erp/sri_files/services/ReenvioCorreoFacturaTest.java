package com.erp.sri_files.services;

import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.utils.FirmaComprobantesService;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReenvioCorreoFacturaTest {
    private final FacturaR repo = mock(FacturaR.class);
    private final XmlToPdfService pdf = mock(XmlToPdfService.class);
    private final MailService mail = mock(MailService.class);
    private final SendXmlToSriService sri = mock(SendXmlToSriService.class);
    private final EnvioSriBatchService service = new EnvioSriBatchService(repo,
            mock(PlatformTransactionManager.class), mock(FacturaXmlGeneratorService.class),
            mock(FirmaComprobantesService.class), sri, pdf, mail, new SriAutorizacionRetryPolicy());

    private Factura pendiente(String correo) {
        var f = new Factura(); f.setIdfactura(1L); f.setEstado("O");
        f.setEmailcomprador(correo); f.setXmlautorizado("<autorizacion/>");
        when(repo.findParaProcesar(1L)).thenReturn(Optional.of(f));
        var bytes = new ByteArrayOutputStream(); bytes.writeBytes(new byte[]{1, 2, 3});
        when(pdf.generarFacturaPDF_v3(anyString())).thenReturn(bytes);
        return f;
    }
    @Test void acceptsValidRecipientAndAttachesPdfAndXml() throws Exception {
        var f = pendiente(" cliente@gmail.com ");
        assertTrue(service.reintentarCorreoPendiente(1L));
        var request = org.mockito.ArgumentCaptor.forClass(com.erp.sri_files.dto.SendMailRequest.class);
        verify(mail).send(request.capture());
        assertEquals(java.util.List.of("cliente@gmail.com"), request.getValue().to());
        assertEquals(2, request.getValue().attachments().size());
        assertEquals("A", f.getEstado()); assertNull(f.getErrores());
        verifyNoInteractions(sri);
    }
    @Test void invalidOrBlockedRecipientsRemainPendingAndCanBeCorrected() throws Exception {
        for (String email : new String[]{"a@@gmail.com", "a b@gmail.com", "a@localhost", "a@gamil.com", "a@YAHOO.COM", "no@gmail.com", ""}) {
            var f = pendiente(email);
            assertFalse(service.reintentarCorreoPendiente(1L));
            assertEquals("O", f.getEstado());
            assertTrue(f.getErrores().contains("Correo comprador"));
        }
        verifyNoInteractions(mail);
        var corrected = pendiente("cliente@hotmail.com");
        assertTrue(service.reintentarCorreoPendiente(1L));
        assertEquals("A", corrected.getEstado());
    }
    @Test void mailFailureRemainsPending() throws Exception {
        var f = pendiente("cliente@gmail.com");
        when(mail.send(any())).thenThrow(new IllegalStateException("cola no disponible"));
        assertFalse(service.reintentarCorreoPendiente(1L));
        assertEquals("O", f.getEstado()); assertTrue(f.getErrores().contains("cola no disponible"));
    }
    @Test void alreadyAcceptedOrMissingXmlIsNeverSent() {
        var f = pendiente("cliente@gmail.com"); f.setEstado("A");
        assertFalse(service.reintentarCorreoPendiente(1L));
        f.setEstado("O"); f.setXmlautorizado("");
        assertFalse(service.reintentarCorreoPendiente(1L));
        verifyNoInteractions(mail);
    }
}
