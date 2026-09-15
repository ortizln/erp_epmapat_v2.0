package com.erp.sri_files.services;

import com.erp.sri_files.models.*;
import com.erp.sri_files.repositories.*;
import com.erp.sri_files.validation.*;
import com.erp.sri_files.utils.FirmaComprobantesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import java.math.BigDecimal;
import static com.erp.sri_files.validation.FacturaPrevalidacionServiceTest.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FacturaFiltroIntegrationTest {
    private FacturaXmlGeneratorService generator;
    private SriFacturaValidationService xml;
    private DefinirR definir;
    @BeforeEach void setup() {
        var pre = new FacturaPrevalidacionService(new ClaveAccesoService());
        xml = new SriFacturaValidationService(pre);
        generator = new FacturaXmlGeneratorService();
        definir = mock(DefinirR.class); when(definir.findById(1L)).thenReturn(Optional.of(emisor()));
        ReflectionTestUtils.setField(generator, "prevalidacion", pre);
        ReflectionTestUtils.setField(generator, "xmlValidation", xml);
        ReflectionTestUtils.setField(generator, "definirR", definir);
        ReflectionTestUtils.setField(generator, "facturaR", mock(FacturaR.class));
        ReflectionTestUtils.setField(generator, "facturasOrigenR", mock(FacturasR.class));
    }
    @Test void reintentarConservaClaveFechaPagosYTotales() throws Exception {
        Factura f = factura(); String clave = f.getClaveacceso();
        String primero = generator.generarXmlFactura(f), segundo = generator.generarXmlFactura(f);
        assertEquals(primero, segundo); assertEquals(clave, f.getClaveacceso());
        assertTrue(primero.contains("<fechaEmision>01/09/2026</fechaEmision>"));
        assertTrue(primero.contains("<formaPago>01</formaPago>"));
        assertTrue(xml.validate(primero).valid(), xml.validate(primero).errors().toString());
    }
    @Test void filtroDetectaAlteracionDeTotalBaseIvaYDetalle() throws Exception {
        String valido = generator.generarXmlFactura(factura());
        assertFalse(xml.validate(valido.replace("<importeTotal>20.70</importeTotal>", "<importeTotal>21.70</importeTotal>")).valid());
        assertFalse(xml.validate(valido.replace("<valor>2.70</valor>", "<valor>0.00</valor>")).valid());
        assertFalse(xml.validate(valido.replace("<precioTotalSinImpuesto>18.00</precioTotalSinImpuesto>", "<precioTotalSinImpuesto>19.00</precioTotalSinImpuesto>")).valid());
    }
    @Test void noConsolidaFuentesSiPerderiaDescuentosOImpuestos() throws Exception {
        Factura f = factura(); f.getDetalles().get(0).setCodigoprincipal("1006");
        String generado = generator.generarXmlFactura(f);
        assertTrue(generado.contains("<codigoPrincipal>1006</codigoPrincipal>"));
        assertTrue(xml.validate(generado).valid());
    }
    @Test void facturaInvalidaNoAccedeAlCertificadoNiALaRed() {
        var firma = new FirmaComprobantesService(definir, null, null);
        ReflectionTestUtils.setField(firma, "facturaValidation", xml);
        clearInvocations(definir);
        assertThrows(FacturaPrevalidacionException.class,
                () -> firma.firmarFactura("<factura/>", FirmaComprobantesService.ModoFirma.XADES_BES));
        verifyNoInteractions(definir);
        var envio = new SendXmlToSriService();
        ReflectionTestUtils.setField(envio, "facturaValidation", xml);
        assertThrows(FacturaPrevalidacionException.class, () -> envio.enviarFacturaFirmada("<factura/>".getBytes(), 2));
    }
    @Test void xmlIncompletoYEntidadesExternasSeRechazan() {
        assertFalse(xml.validate("<factura/>").valid());
        assertThrows(FacturaPrevalidacionException.class,
                () -> xml.exigirSiFactura("<f:factura xmlns:f='urn:invalida'/>"));
        assertFalse(xml.validate("<!DOCTYPE factura [<!ENTITY x SYSTEM 'file:///no-leer'>]><factura>&x;</factura>").valid());
    }
}
