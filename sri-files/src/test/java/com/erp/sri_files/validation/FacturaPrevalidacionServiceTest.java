package com.erp.sri_files.validation;

import com.erp.sri_files.models.*;
import com.erp.sri_files.services.ClaveAccesoService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FacturaPrevalidacionServiceTest {
    private final FacturaPrevalidacionService service = new FacturaPrevalidacionService(new ClaveAccesoService());
    public static Definir emisor() {
        Definir d = new Definir(); d.setRuc("0460028810001"); d.setRazonsocial("EMPRESA");
        d.setTipoambiente((byte) 2); return d;
    }
    public static Factura factura() {
        Factura f = new Factura(); f.setIdfactura(10L); f.setEstado("I");
        f.setFechaemision(LocalDateTime.of(2026, 9, 1, 9, 0));
        f.setEstablecimiento("001"); f.setPuntoemision("013"); f.setSecuencial("000000010");
        f.setDireccionestablecimiento("Tulcan"); f.setDireccioncomprador("Tulcan");
        f.setTipoidentificacioncomprador("05"); f.setIdentificacioncomprador("0401234567"); f.setRazonsocialcomprador("Cliente");
        f.setClaveacceso(new ClaveAccesoService().generarClaveAcceso(f.getFechaemision(), "01", emisor().getRuc(), 2, "001", "013", f.getSecuencial()));
        FacturaDetalle d = new FacturaDetalle(); d.setCodigoprincipal("1001"); d.setDescripcion("Servicio");
        d.setCantidad(new BigDecimal("2")); d.setPreciounitario(new BigDecimal("10")); d.setDescuento(new BigDecimal("2"));
        FacturaDetalleImpuesto i = new FacturaDetalleImpuesto(); i.setCodigoimpuesto("2"); i.setCodigoporcentaje("4");
        i.setBaseimponible(new BigDecimal("18")); d.setImpuestos(List.of(i)); f.setDetalles(List.of(d));
        FacturaPago p = new FacturaPago(); p.setFormapago("01"); p.setTotal(new BigDecimal("20.70")); f.setPagos(List.of(p));
        return f;
    }
    @Test void aceptaDatosCompletosYCalculaDescuentoEIva() {
        var r = service.validar(factura(), emisor(), null);
        assertTrue(r.valido(), r.errores().toString());
        assertEquals(new BigDecimal("18.00"), r.subtotal());
        assertEquals(new BigDecimal("2.70"), r.iva());
    }
    @Test void rechazaInformacionFaltanteSinInventarCeros() {
        Factura f = factura(); f.setClaveacceso(null); f.setRazonsocialcomprador(null);
        f.getDetalles().get(0).setImpuestos(List.of()); f.setPagos(List.of());
        var r = service.validar(f, emisor(), null);
        assertFalse(r.valido()); assertTrue(r.errores().size() >= 5);
        assertThrows(FacturaPrevalidacionException.class, r::exigirValido);
        assertNull(f.getClaveacceso());
    }
    @Test void rechazaBaseUnitariaEnVezDeCantidadPorPrecioMenosDescuento() {
        Factura f = factura(); f.getDetalles().get(0).getImpuestos().get(0).setBaseimponible(BigDecimal.TEN);
        assertTrue(service.validar(f, emisor(), null).errores().stream().anyMatch(e -> e.contains("baseimponible")));
    }
    @Test void rechazaClaveConModuloCorrectoPeroFechaODatosDistintos() {
        Factura f = factura(); f.setFechaemision(f.getFechaemision().plusDays(1));
        assertTrue(service.validar(f, emisor(), null).errores().stream().anyMatch(e -> e.contains("clave.fecha")));
        f = factura(); f.setSecuencial("000000011");
        assertTrue(service.validar(f, emisor(), null).errores().stream().anyMatch(e -> e.contains("clave.secuencial")));
    }
    @Test void rechazaPagoQueOmiteIvaEInteresCobradoSinDetalle() {
        Factura f = factura(); f.getPagos().get(0).setTotal(new BigDecimal("18"));
        Facturas origen = new Facturas(); origen.setInterescobrado(new BigDecimal("1.25"));
        var r = service.validar(f, emisor(), origen);
        assertTrue(r.errores().stream().anyMatch(e -> e.contains("suma de pagos")));
        assertTrue(r.errores().stream().anyMatch(e -> e.contains("interescobrado")));
    }
    @Test void rechazaTarifaDesconocidaYDescuentoExcesivo() {
        Factura f = factura(); f.getDetalles().get(0).setDescuento(new BigDecimal("25"));
        f.getDetalles().get(0).getImpuestos().get(0).setCodigoporcentaje("999");
        var r = service.validar(f, emisor(), null);
        assertTrue(r.errores().stream().anyMatch(e -> e.contains("descuento supera")));
        assertTrue(r.errores().stream().anyMatch(e -> e.contains("no soportado")));
    }
}
