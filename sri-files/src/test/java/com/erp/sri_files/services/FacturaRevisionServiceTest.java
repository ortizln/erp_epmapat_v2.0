package com.erp.sri_files.services;

import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.validation.FacturaPrevalidacionService;
import ec.gob.sri.ws.autorizacion.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static com.erp.sri_files.validation.FacturaPrevalidacionServiceTest.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FacturaRevisionServiceTest {
    private FacturaR repo;
    private SendXmlToSriService sri;
    private FacturaXmlGeneratorService generator;
    private FacturaRevisionService service;
    private Factura factura;
    @BeforeEach void setup() {
        repo = mock(FacturaR.class); sri = mock(SendXmlToSriService.class);
        generator = mock(FacturaXmlGeneratorService.class);
        service = new FacturaRevisionService(repo, generator, new ClaveAccesoService(), sri, mock(FacturaHistorialService.class));
        factura = factura(); factura.setEstado("E");
        when(repo.findParaProcesar(10L)).thenReturn(Optional.of(factura));
    }
    private RespuestaComprobante sinRegistro() {
        RespuestaComprobante r = new RespuestaComprobante(); r.setNumeroComprobantes("0"); return r;
    }
    private void datosCompletos() {
        when(generator.validarFactura(factura)).thenReturn(new FacturaPrevalidacionService(new ClaveAccesoService()).validar(factura, emisor(), null));
    }
    @Test void consultaFallidaNoHabilitaEnvio() throws Exception {
        when(sri.consultarAutorizacion(factura.getClaveacceso(), 2)).thenThrow(new java.net.SocketException("Connection reset"));
        assertEquals("CONSULTA_INCONCLUSA", service.recuperar(10L, true).resultado());
        assertEquals("E", factura.getEstado()); verify(repo, never()).save(any());
    }
    @Test void recuperaAutorizadaSinGenerarNiReenviar() throws Exception {
        RespuestaComprobante r = new RespuestaComprobante(); r.setNumeroComprobantes("1");
        var lista = new RespuestaComprobante.Autorizaciones(); var a = new Autorizacion(); a.setEstado("AUTORIZADO");
        lista.getAutorizacion().add(a); r.setAutorizaciones(lista);
        when(sri.consultarAutorizacion(factura.getClaveacceso(), 2)).thenReturn(r);
        when(sri.extraerXmlAutorizado(r)).thenReturn("<factura>autorizada</factura>");
        assertEquals("AUTORIZADA_RECUPERADA", service.recuperar(10L, false).resultado());
        assertEquals("A", factura.getEstado()); verifyNoInteractions(generator);
        verify(sri, never()).enviarFacturaFirmadaTxt(anyString());
    }
    @Test void datosCompletosSinRegistroExigenConciliarClaveHistorica() throws Exception {
        datosCompletos(); when(sri.consultarAutorizacion(factura.getClaveacceso(), 2)).thenReturn(sinRegistro());
        assertEquals("REQUIERE_CONCILIACION_CLAVE", service.recuperar(10L, false).resultado());
        assertEquals("E", factura.getEstado());
        assertEquals("PENDIENTE_ENVIO", service.recuperar(10L, true).resultado());
        assertEquals("I", factura.getEstado());
    }
    @Test void incompletaPermaneceBloqueadaAunqueClaveFueVerificada() throws Exception {
        factura.setPagos(List.of()); datosCompletos();
        when(sri.consultarAutorizacion(factura.getClaveacceso(), 2)).thenReturn(sinRegistro());
        assertEquals("REQUIERE_CORRECCION", service.recuperar(10L, true).resultado());
        assertEquals("E", factura.getEstado());
    }
    @Test void codigo43ConConsultaVaciaNoAutorizaReenvio() throws Exception {
        factura.setErrores("[43] CLAVE ACCESO REGISTRADA");
        when(sri.consultarAutorizacion(factura.getClaveacceso(), 2)).thenReturn(sinRegistro());
        assertEquals("CONSULTA_INCONCLUSA", service.recuperar(10L, true).resultado());
        verifyNoInteractions(generator);
    }
    @Test void xmlYaPresenteNoConsultaNiReenvia() {
        factura.setXmlautorizado("<factura/>");
        assertEquals("YA_TIENE_XML_AUTORIZADO", service.recuperar(10L, true).resultado());
        verifyNoInteractions(sri, generator);
    }
    @Test void claveInvalidaNoLlamaSri() {
        factura.setClaveacceso(null);
        assertEquals("REQUIERE_CORRECCION", service.recuperar(10L, true).resultado());
        verifyNoInteractions(sri);
    }
}
