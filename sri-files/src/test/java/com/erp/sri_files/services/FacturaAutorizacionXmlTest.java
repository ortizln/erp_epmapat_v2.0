package com.erp.sri_files.services;
import com.erp.sri_files.dto.AutorizacionSriResult;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
class FacturaAutorizacionXmlTest {
    static final String KEY = "1009202601046002881000120010130004585995621446311";
    private AutorizacionSriResult authorized() {
        var r = new AutorizacionSriResult(); r.setAutorizado(true);
        r.setXmlAutorizado("<factura><infoTributaria><claveAcceso>"+KEY+"</claveAcceso></infoTributaria></factura>");
        r.setNumeroAutorizacion("autorizacion-real");r.setFechaAutorizacion(LocalDateTime.of(2026,9,10,8,15));return r;
    }
    @Test void preservesSriMetadataForLateAuthorizationAndRide() throws Exception {
        var xml = FacturaAutorizacionXml.completo(authorized(),KEY);
        var parsed = FacturaPdfXml.parse(xml);
        assertEquals("autorizacion-real",parsed.numeroAutorizacion);
        assertEquals("2026-09-10T08:15",parsed.fechaAutorizacion);
        assertEquals(KEY,FacturaPdfXml.text(parsed.factura,"claveAcceso"));
    }
    @Test void refusesToInventAuthorizationNumberFromKey() {
        var r=authorized();r.setNumeroAutorizacion(null);
        assertThrows(IllegalArgumentException.class,()->FacturaAutorizacionXml.completo(r,KEY));
    }
    @Test void refusesMalformedOrMismatchedXml() {
        var r=authorized();r.setXmlAutorizado("<factura/>");
        assertThrows(Exception.class,()->FacturaAutorizacionXml.completo(r,KEY));
    }

    @Test void soapMappingKeepsAuthorizationNumberAndConvertsSriTimezone() throws Exception {
        var response=new ec.gob.sri.ws.autorizacion.RespuestaComprobante();
        var list=new ec.gob.sri.ws.autorizacion.RespuestaComprobante.Autorizaciones();
        var auth=new ec.gob.sri.ws.autorizacion.Autorizacion();auth.setEstado("AUTORIZADO");
        auth.setNumeroAutorizacion("real-soap");auth.setComprobante(authorized().getXmlAutorizado());
        auth.setFechaAutorizacion(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("2026-09-10T13:15:00Z"));
        list.getAutorizacion().add(auth);response.setAutorizaciones(list);
        var result=SendXmlToSriService.interpretarAutorizacion(response);
        assertEquals("real-soap",result.getNumeroAutorizacion());
        assertEquals(LocalDateTime.of(2026,9,10,8,15),result.getFechaAutorizacion());
        assertTrue(FacturaAutorizacionXml.completo(result,KEY).contains("real-soap"));
    }
}
