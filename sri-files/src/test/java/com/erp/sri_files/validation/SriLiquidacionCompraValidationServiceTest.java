package com.erp.sri_files.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SriLiquidacionCompraValidationServiceTest {

    private final SriLiquidacionCompraValidationService svc = new SriLiquidacionCompraValidationService();

    @Test
    void nullXml_returnsError() {
        var r = svc.validate(null);
        assertFalse(r.valid());
        assertTrue(r.errors().get(0).contains("vacío o nulo"));
    }

    @Test
    void emptyXml_returnsError() {
        var r = svc.validate("  ");
        assertFalse(r.valid());
        assertTrue(r.errors().get(0).contains("vacío o nulo"));
    }

    @Test
    void rootNotLiquidacionCompra_returnsError() {
        String xml = """
            <otro id="comprobante" version="1.0.0">
              <infoTributaria><ambiente>2</ambiente></infoTributaria>
            </otro>
            """;
        var r = svc.validate(xml);
        assertFalse(r.valid());
        assertTrue(r.errors().stream().anyMatch(e -> e.contains("Raíz inválida")));
    }

    @Test
    void missingInfoTributaria_returnsErrors() {
        String xml = """
            <liquidacionCompra id="comprobante" version="1.0.0">
              <infoTributaria></infoTributaria>
            </liquidacionCompra>
            """;
        var r = svc.validate(xml);
        assertFalse(r.valid());
        assertTrue(r.errors().stream().anyMatch(e -> e.contains("claveAcceso")));
    }

    @Test
    void codDoc03_accepted() {
        String xml = """
            <liquidacionCompra id="comprobante" version="1.0.0">
              <infoTributaria>
                <ambiente>2</ambiente>
                <tipoEmision>1</tipoEmision>
                <razonSocial>X</razonSocial>
                <ruc>1792238694001</ruc>
                <claveAcceso>210820260317922386940012001001000000001234567891</claveAcceso>
                <codDoc>03</codDoc>
                <estab>001</estab>
                <ptoEmi>001</ptoEmi>
                <secuencial>000000001</secuencial>
              </infoTributaria>
            </liquidacionCompra>
            """;
        var r = svc.validate(xml);
        assertFalse(r.errors().stream().anyMatch(e -> e.contains("codDoc inválido")), "codDoc 03 es válido para liquidación de compra");
    }

    @Test
    void codDocNot03_returnsError() {
        String xml = """
            <liquidacionCompra id="comprobante" version="1.0.0">
              <infoTributaria>
                <ambiente>2</ambiente>
                <tipoEmision>1</tipoEmision>
                <razonSocial>X</razonSocial>
                <ruc>1792238694001</ruc>
                <claveAcceso>210820260117922386940012001001000000001234567891</claveAcceso>
                <codDoc>01</codDoc>
                <estab>001</estab>
                <ptoEmi>001</ptoEmi>
                <secuencial>000000001</secuencial>
              </infoTributaria>
            </liquidacionCompra>
            """;
        var r = svc.validate(xml);
        assertTrue(r.errors().stream().anyMatch(e -> e.contains("codDoc inválido")));
    }

    @Test
    void missingDetalles_returnsError() {
        String xml = """
            <liquidacionCompra id="comprobante" version="1.0.0">
              <infoTributaria>
                <ambiente>2</ambiente>
                <tipoEmision>1</tipoEmision>
                <razonSocial>X</razonSocial>
                <ruc>1792238694001</ruc>
                <claveAcceso>210820260317922386940012001001000000001234567891</claveAcceso>
                <codDoc>03</codDoc>
                <estab>001</estab>
                <ptoEmi>001</ptoEmi>
                <secuencial>000000001</secuencial>
              </infoTributaria>
              <detalles></detalles>
            </liquidacionCompra>
            """;
        var r = svc.validate(xml);
        assertTrue(r.errors().stream().anyMatch(e -> e.contains("al menos un detalle")));
    }
}
