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
        f.setEmailcomprador(correo);
        f.setClaveacceso("1009202601046002881000120010130004585995621446311");
        f.setXmlautorizado("<autorizacion><numeroAutorizacion>real</numeroAutorizacion><fechaAutorizacion>2026-09-10T08:15:00</fechaAutorizacion>"
            + "<comprobante><![CDATA[<factura><claveAcceso>" + f.getClaveacceso() + "</claveAcceso></factura>]]></comprobante></autorizacion>");
        when(repo.findParaProcesar(1L)).thenReturn(Optional.of(f));
        try { when(mail.send(any(), anyString())).thenReturn(java.util.UUID.randomUUID()); }
        catch (Exception impossible) { throw new RuntimeException(impossible); }
        var bytes = new ByteArrayOutputStream(); bytes.writeBytes(new byte[]{1, 2, 3});
        when(pdf.generarFacturaPDF_v3(anyString())).thenReturn(bytes);
        return f;
    }
    @Test void acceptsValidRecipientAndAttachesPdfAndXml() throws Exception {
        var f = pendiente(" cliente@gmail.com ");
        assertTrue(service.reintentarCorreoPendiente(1L));
        var request = org.mockito.ArgumentCaptor.forClass(com.erp.sri_files.dto.SendMailRequest.class);
        verify(mail).send(request.capture(), eq("factura:1"));
        assertEquals(java.util.List.of("cliente@gmail.com"), request.getValue().to());
        assertEquals(2, request.getValue().attachments().size());
        assertEquals("O", f.getEstado()); assertFalse(f.getMail_enviado());
        assertNotNull(f.getEmailMessageId());
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
        assertEquals("O", corrected.getEstado()); assertNotNull(corrected.getEmailMessageId());
    }
    @Test void mailFailureRemainsPending() throws Exception {
        var f = pendiente("cliente@gmail.com");
        when(mail.send(any(), eq("factura:1"))).thenThrow(new IllegalStateException("cola no disponible"));
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

    @Test void queuedIsNotSentAndIsNeverEnqueuedAgain() throws Exception {
        var f = pendiente("cliente@gmail.com"); var id = java.util.UUID.randomUUID();
        f.setEmailMessageId(id);
        when(mail.consultarEstado(id)).thenReturn(new com.erp.sri_files.dto.EmailMsStatusResponse(id,"PENDING",null));
        assertTrue(service.reintentarCorreoPendiente(1L));
        assertEquals("O",f.getEstado()); assertFalse(f.getMail_enviado());
        verify(mail, never()).send(any(),anyString());
        when(mail.consultarEstado(id)).thenReturn(new com.erp.sri_files.dto.EmailMsStatusResponse(id,"SENT",null));
        assertTrue(service.reintentarCorreoPendiente(1L));
        assertEquals("A",f.getEstado()); assertTrue(f.getMail_enviado());
        verify(mail, never()).send(any(),anyString());
    }

    @Test void emailStatusOutageKeepsInvoicePendingWithoutAnotherEmail() throws Exception {
        var f = pendiente("cliente@gmail.com"); var id = java.util.UUID.randomUUID(); f.setEmailMessageId(id);
        when(mail.consultarEstado(id)).thenThrow(new IllegalStateException("timeout"));
        service.reintentarCorreoPendiente(1L);
        assertEquals("O",f.getEstado()); assertEquals(id,f.getEmailMessageId());
        verify(mail,never()).send(any(),anyString());
    }
}
