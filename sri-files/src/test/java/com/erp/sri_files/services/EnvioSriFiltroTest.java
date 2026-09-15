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
        service = new EnvioSriBatchService(repo, mock(PlatformTransactionManager.class), generator, firma, sri, mock(XmlToPdfService.class), mock(MailService.class));
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
}
