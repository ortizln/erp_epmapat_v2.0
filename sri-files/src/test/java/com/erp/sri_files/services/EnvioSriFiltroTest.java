package com.erp.sri_files.services;

import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.utils.FirmaComprobantesService;
import com.erp.sri_files.validation.FacturaPrevalidacionException;
import ec.gob.sri.ws.recepcion.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import java.net.SocketException;
import java.util.List;
import java.util.Optional;
import static com.erp.sri_files.validation.FacturaPrevalidacionServiceTest.factura;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnvioSriFiltroTest {
    private FacturaR repo;
    private FacturaXmlGeneratorService generator;
    private FirmaComprobantesService firma;
    private SendXmlToSriService sri;
    private EnvioSriBatchService service;
    private Factura f;
    @BeforeEach void setup() {
        repo = mock(FacturaR.class); generator = mock(FacturaXmlGeneratorService.class);
        firma = mock(FirmaComprobantesService.class); sri = mock(SendXmlToSriService.class);
        service = new EnvioSriBatchService(repo, mock(PlatformTransactionManager.class), generator, firma, sri, mock(XmlToPdfService.class), mock(MailService.class), new SriAutorizacionRetryPolicy());
        f = factura(); when(repo.findParaProcesar(10L)).thenReturn(Optional.of(f));
    }
    @Test void incompletaQuedaEYNoSeFirmaNiEnvia() throws Exception {
        when(generator.generarXmlFactura(f)).thenThrow(new FacturaPrevalidacionException(List.of("detalles: faltantes")));
        service.procesarFacturaEnNuevaTx(10L);
        assertEquals("E", f.getEstado()); assertTrue(f.getErrores().contains("VALIDACION_PREVIA"));
        verifyNoInteractions(firma, sri);
    }
    @Test void connectionResetQuedaParaConsultaNoParaReenvio() throws Exception {
        when(generator.generarXmlFactura(f)).thenReturn("<factura/>");
        when(firma.firmarFactura(anyString(), any())).thenReturn("<factura/>");
        when(sri.inferAmbienteFromXml(anyString())).thenReturn(2);
        when(sri.enviarFacturaFirmadaTxt(anyString(), eq(2))).thenThrow(new jakarta.xml.ws.WebServiceException(new SocketException("Connection reset")));
        service.procesarFacturaEnNuevaTx(10L);
        assertEquals("C", f.getEstado());
    }
    @Test void autorizadaNoSeReenviaAunqueEstadoSeaI() {
        f.setXmlautorizado("<autorizacion/>"); service.procesarFacturaEnNuevaTx(10L);
        verifyNoInteractions(generator, firma, sri);
    }

    @Test void codigo43ConsultaMismaClaveSinReenvioNiPolling() throws Exception {
        when(generator.generarXmlFactura(f)).thenReturn("<factura/>");
        when(firma.firmarFactura(anyString(), any())).thenReturn("<factura/>");
        when(sri.inferAmbienteFromXml(anyString())).thenReturn(2);
        RespuestaSolicitud recepcion = mock(RespuestaSolicitud.class, RETURNS_DEEP_STUBS);
        Comprobante comprobante = mock(Comprobante.class, RETURNS_DEEP_STUBS);
        Mensaje mensaje = new Mensaje(); mensaje.setIdentificador("43");
        when(recepcion.getEstado()).thenReturn("DEVUELTA");
        when(recepcion.getComprobantes().getComprobante()).thenReturn(List.of(comprobante));
        when(comprobante.getMensajes().getMensaje()).thenReturn(List.of(mensaje));
        when(sri.enviarFacturaFirmadaTxt(anyString(), eq(2))).thenReturn(recepcion);
        var pendiente = new com.erp.sri_files.dto.AutorizacionSriResult();
        pendiente.setMensaje("Sin autorizaciones");
        when(sri.consultar_Autorizacion(f.getClaveacceso())).thenReturn(pendiente);
        service.procesarFacturaEnNuevaTx(10L);
        assertEquals("C", f.getEstado()); assertEquals(1, f.getIntentos_autorizacion());
        assertNotNull(f.getFecha_ultimo_intento());
        service.procesarFacturaEnNuevaTx(10L);
        verify(sri, times(1)).enviarFacturaFirmadaTxt(anyString(), eq(2));
        verify(sri, times(1)).consultar_Autorizacion(f.getClaveacceso());
    }

    @Test void resetEnAutorizacionProgramaSiguienteConsulta() throws Exception {
        f.setEstado("C");
        when(sri.consultar_Autorizacion(f.getClaveacceso())).thenThrow(new jakarta.xml.ws.WebServiceException(new SocketException("Connection reset")));
        assertFalse(service.consultarPendiente(10L));
        assertEquals("C", f.getEstado()); assertEquals(1, f.getIntentos_autorizacion());
        assertFalse(service.consultarPendiente(10L));
        verify(sri, times(1)).consultar_Autorizacion(f.getClaveacceso());
        verifyNoInteractions(generator, firma);
    }

    @Test void autorizacionPosteriorGuardaXmlYReiniciaBackoff() throws Exception {
        f.setEstado("C"); f.setIntentos_autorizacion(12);
        f.setFecha_ultimo_intento(java.time.LocalDateTime.now().minusHours(1));
        var autorizada = new com.erp.sri_files.dto.AutorizacionSriResult();
        autorizada.setAutorizado(true); autorizada.setXmlAutorizado("<factura/>");
        when(sri.consultar_Autorizacion(f.getClaveacceso())).thenReturn(autorizada);
        assertTrue(service.consultarPendiente(10L));
        assertEquals("A", f.getEstado()); assertEquals("<factura/>", f.getXmlautorizado());
        assertEquals(0, f.getIntentos_autorizacion()); assertNull(f.getFecha_ultimo_intento());
        verifyNoInteractions(generator, firma);
    }

    @Test void noAutorizadoDefinitivoSeDistingueDeFalloDeRed() throws Exception {
        f.setEstado("C");
        var rechazada = new com.erp.sri_files.dto.AutorizacionSriResult();
        rechazada.setMensaje("NO AUTORIZADO");
        when(sri.consultar_Autorizacion(f.getClaveacceso())).thenReturn(rechazada);
        assertFalse(service.consultarPendiente(10L));
        assertEquals("N", f.getEstado());
    }
}
