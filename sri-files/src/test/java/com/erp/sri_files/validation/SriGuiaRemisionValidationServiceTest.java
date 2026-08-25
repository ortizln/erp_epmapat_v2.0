package com.erp.sri_files.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SriGuiaRemisionValidationServiceTest {

    private final SriGuiaRemisionValidationService svc = new SriGuiaRemisionValidationService();

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
    void rootNotGuiaRemision_returnsError() {
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
    void codDoc06_accepted() {
        String xml = """
            <guiaRemision id="comprobante" version="1.0.0">
              <infoTributaria>
                <ambiente>2</ambiente>
                <tipoEmision>1</tipoEmision>
                <razonSocial>X</razonSocial>
                <ruc>1792238694001</ruc>
                <claveAcceso>210820260617922386940012001001000000001234567891</claveAcceso>
                <codDoc>06</codDoc>
                <estab>001</estab>
                <ptoEmi>001</ptoEmi>
                <secuencial>000000001</secuencial>
              </infoTributaria>
            </guiaRemision>
            """;
        var r = svc.validate(xml);
        assertFalse(r.errors().stream().anyMatch(e -> e.contains("codDoc inválido")), "codDoc 06 es válido para guía de remisión");
    }

    @Test
    void codDocNot06_returnsError() {
        String xml = """
            <guiaRemision id="comprobante" version="1.0.0">
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
            </guiaRemision>
            """;
        var r = svc.validate(xml);
        assertTrue(r.errors().stream().anyMatch(e -> e.contains("codDoc inválido")));
    }

    @Test
    void missingDestinatarios_returnsError() {
        String xml = """
            <guiaRemision id="comprobante" version="1.0.0">
              <infoTributaria>
                <ambiente>2</ambiente>
                <tipoEmision>1</tipoEmision>
                <razonSocial>X</razonSocial>
                <ruc>1792238694001</ruc>
                <claveAcceso>210820260617922386940012001001000000001234567891</claveAcceso>
                <codDoc>06</codDoc>
                <estab>001</estab>
                <ptoEmi>001</ptoEmi>
                <secuencial>000000001</secuencial>
              </infoTributaria>
              <destinatarios></destinatarios>
            </guiaRemision>
            """;
        var r = svc.validate(xml);
        assertTrue(r.errors().stream().anyMatch(e -> e.contains("al menos un destinatario")));
    }

    @Test
    void missingDetalles_returnsError() {
        String xml = """
            <guiaRemision id="comprobante" version="1.0.0">
              <infoTributaria>
                <ambiente>2</ambiente>
                <tipoEmision>1</tipoEmision>
                <razonSocial>X</razonSocial>
                <ruc>1792238694001</ruc>
                <claveAcceso>210820260617922386940012001001000000001234567891</claveAcceso>
                <codDoc>06</codDoc>
                <estab>001</estab>
                <ptoEmi>001</ptoEmi>
                <secuencial>000000001</secuencial>
              </infoTributaria>
              <detalles></detalles>
            </guiaRemision>
            """;
        var r = svc.validate(xml);
        assertTrue(r.errors().stream().anyMatch(e -> e.contains("al menos un detalle")));
    }

    @Test
    void missingTransportista_returnsError() {
        String xml = """
            <guiaRemision id="comprobante" version="1.0.0">
              <infoTributaria>
                <ambiente>2</ambiente>
                <tipoEmision>1</tipoEmision>
                <razonSocial>X</razonSocial>
                <ruc>1792238694001</ruc>
                <claveAcceso>210820260617922386940012001001000000001234567891</claveAcceso>
                <codDoc>06</codDoc>
                <estab>001</estab>
                <ptoEmi>001</ptoEmi>
                <secuencial>000000001</secuencial>
              </infoTributaria>
              <infoGuiaRemision>
                <dirEstablecimiento>Tulcan</dirEstablecimiento>
              </infoGuiaRemision>
            </guiaRemision>
            """;
        var r = svc.validate(xml);
        assertTrue(r.errors().stream().anyMatch(e -> e.contains("razonSocialTransportista")));
    }
}
