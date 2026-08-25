package com.erp.sri_files.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SriControllerHelperTest {

    @Nested
    @DisplayName("stripBom")
    class StripBom {

        @Test
        @DisplayName("Debe eliminar BOM si está presente")
        void stripBom_conBom() {
            String conBom = "\uFEFF<?xml version=\"1.0\"?>";
            String resultado = SriControllerHelper.stripBom(conBom);
            assertEquals("<?xml version=\"1.0\"?>", resultado);
        }

        @Test
        @DisplayName("Debe devolver string igual si no tiene BOM")
        void stripBom_sinBom() {
            String sinBom = "<?xml version=\"1.0\"?>";
            String resultado = SriControllerHelper.stripBom(sinBom);
            assertEquals(sinBom, resultado);
        }

        @Test
        @DisplayName("Debe manejar null")
        void stripBom_null() {
            assertNull(SriControllerHelper.stripBom(null));
        }

        @Test
        @DisplayName("Debe manejar string vacío")
        void stripBom_vacio() {
            assertEquals("", SriControllerHelper.stripBom(""));
        }
    }

    @Nested
    @DisplayName("safeStr")
    class SafeStr {

        @Test
        @DisplayName("Debe devolver string vacío para null")
        void safeStr_null() {
            assertEquals("", SriControllerHelper.safeStr(null));
        }

        @Test
        @DisplayName("Debe trimpear el resultado")
        void safeStr_conEspacios() {
            assertEquals("hola", SriControllerHelper.safeStr("  hola  "));
        }

        @Test
        @DisplayName("Debe convertir objeto a string")
        void safeStr_objeto() {
            assertEquals("123", SriControllerHelper.safeStr(Integer.valueOf(123)));
        }
    }

    @Nested
    @DisplayName("resumenErroresRecepcion")
    class ResumenErroresRecepcion {

        @Test
        @DisplayName("Debe retornar mapa con estado")
        void resumenErroresRecepcion_soloEstado() {
            var recepcion = new ec.gob.sri.ws.recepcion.RespuestaSolicitud();
            recepcion.setEstado("DEVUELTA");

            var resultado = SriControllerHelper.resumenErroresRecepcion(recepcion);

            assertNotNull(resultado);
            assertEquals("DEVUELTA", resultado.get("estado"));
        }
    }

    @Nested
    @DisplayName("cleanComprobanteXml")
    class CleanComprobanteXml {

        @Test
        @DisplayName("Debe decodificar Base64 válido")
        void cleanComprobanteXml_base64Valido() {
            String xml = "<?xml version=\"1.0\"?><comprobante></comprobante>";
            String base64 = java.util.Base64.getEncoder().encodeToString(xml.getBytes());

            String resultado = SriControllerHelper.cleanComprobanteXml(base64);

            assertEquals(xml, resultado);
        }

        @Test
        @DisplayName("Debe manejar string vacío")
        void cleanComprobanteXml_vacio() {
            assertEquals("", SriControllerHelper.cleanComprobanteXml(""));
        }

        @Test
        @DisplayName("Debe manejar null")
        void cleanComprobanteXml_null() {
            assertEquals("", SriControllerHelper.cleanComprobanteXml(null));
        }

        @Test
        @DisplayName("Debe devolver el string si no es Base64 válido")
        void cleanComprobanteXml_noBase64() {
            String noBase64 = "esto no es base64";
            String resultado = SriControllerHelper.cleanComprobanteXml(noBase64);
            assertEquals(noBase64, resultado);
        }
    }

    @Nested
    @DisplayName("extraerClaveAcceso")
    class ExtraerClaveAcceso {

        @Test
        @DisplayName("Debe extraer clave de acceso de XML válido")
        void extraerClaveAcceso_xmlValido() throws Exception {
            String xml = "<?xml version=\"1.0\"?>"
                + "<factura>"
                + "<infoTributaria>"
                + "<claveAcceso>2108202601179223869400120010010000000010000000013</claveAcceso>"
                + "</infoTributaria>"
                + "</factura>";

            String clave = SriControllerHelper.extraerClaveAcceso(xml);

            assertEquals("2108202601179223869400120010010000000010000000013", clave);
        }

        @Test
        @DisplayName("Debe devolver null si no hay claveAcceso")
        void extraerClaveAcceso_sinClave() throws Exception {
            String xml = "<?xml version=\"1.0\"?>"
                + "<factura>"
                + "<infoTributaria>"
                + "<ambiente>2</ambiente>"
                + "</infoTributaria>"
                + "</factura>";

            String clave = SriControllerHelper.extraerClaveAcceso(xml);

            assertNull(clave);
        }
    }
}
