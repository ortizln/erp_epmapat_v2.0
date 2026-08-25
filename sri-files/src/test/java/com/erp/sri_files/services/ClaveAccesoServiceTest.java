package com.erp.sri_files.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ClaveAccesoServiceTest {

    private ClaveAccesoService service;

    @BeforeEach
    void setUp() {
        service = new ClaveAccesoService();
    }

    @Nested
    @DisplayName("generarClaveAcceso")
    class GenerarClaveAcceso {

        @Test
        @DisplayName("Debe generar clave de 49 dígitos")
        void generarClaveAcceso_longitudCorrecta() {
            String clave = service.generarClaveAcceso(
                LocalDateTime.of(2026, 8, 21, 10, 30),
                "01",
                "1792238694001",
                2,
                "001",
                "001",
                "000000001"
            );

            assertNotNull(clave);
            assertEquals(49, clave.length());
            assertTrue(clave.matches("\\d{49}"));
        }

        @Test
        @DisplayName("Debe generar clave con ambiente 1 (pruebas)")
        void generarClaveAcceso_ambientePruebas() {
            String clave = service.generarClaveAcceso(
                LocalDateTime.of(2026, 1, 1, 0, 0),
                "01",
                "1792238694001",
                1,
                "001",
                "001",
                "000000001"
            );

            assertNotNull(clave);
            assertEquals(49, clave.length());
        }

        @Test
        @DisplayName("Debe generar claves diferentes con diferentes secuenciales")
        void generarClaveAcceso_diferentesSecuenciales() {
            String clave1 = service.generarClaveAcceso(
                LocalDateTime.of(2026, 8, 21, 0, 0), "01", "1792238694001", 2, "001", "001", "000000001"
            );
            String clave2 = service.generarClaveAcceso(
                LocalDateTime.of(2026, 8, 21, 0, 0), "01", "1792238694001", 2, "001", "001", "000000002"
            );

            assertNotEquals(clave1, clave2);
        }

        @Test
        @DisplayName("Debe lanzar excepción con RUC nulo")
        void generarClaveAcceso_rucNulo() {
            assertThrows(IllegalArgumentException.class, () ->
                service.generarClaveAcceso(
                    LocalDateTime.of(2026, 8, 21, 0, 0), "01", null, 2, "001", "001", "000000001"
                )
            );
        }

        @Test
        @DisplayName("Debe lanzar excepción con ambiente inválido")
        void generarClaveAcceso_ambienteInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                service.generarClaveAcceso(
                    LocalDateTime.of(2026, 8, 21, 0, 0), "01", "1792238694001", 3, "001", "001", "000000001"
                )
            );
        }
    }

    @Nested
    @DisplayName("validarClaveAcceso")
    class ValidarClaveAcceso {

        @Test
        @DisplayName("Debe validar una clave generada por el servicio")
        void validarClaveAcceso_claveGenerada() {
            String clave = service.generarClaveAcceso(
                LocalDateTime.of(2026, 8, 21, 10, 30),
                "01",
                "1792238694001",
                2,
                "001",
                "001",
                "000000001"
            );

            assertTrue(service.validarClaveAcceso(clave));
        }

        @Test
        @DisplayName("Debe rechazar clave con DV incorrecto")
        void validarClaveAcceso_dvIncorrecto() {
            String clave = service.generarClaveAcceso(
                LocalDateTime.of(2026, 8, 21, 10, 30),
                "01",
                "1792238694001",
                2,
                "001",
                "001",
                "000000001"
            );
            // Cambiar el último dígito
            char ultimaPosicion = clave.charAt(48);
            char nuevoDv = ultimaPosicion == '9' ? '0' : (char)(ultimaPosicion + 1);
            String claveInvalida = clave.substring(0, 48) + nuevoDv;

            assertFalse(service.validarClaveAcceso(claveInvalida));
        }

        @Test
        @DisplayName("Debe rechazar clave con longitud incorrecta")
        void validarClaveAcceso_longitudIncorrecta() {
            assertFalse(service.validarClaveAcceso("12345678901234567890"));
        }

        @Test
        @DisplayName("Debe rechazar clave nula")
        void validarClaveAcceso_claveNula() {
            assertFalse(service.validarClaveAcceso(null));
        }

        @Test
        @DisplayName("Debe rechazar clave con caracteres no numéricos")
        void validarClaveAcceso_caracteresNoNumericos() {
            String clave = "123456789012345678901234567890123456789012345678A";
            assertFalse(service.validarClaveAcceso(clave));
        }
    }

    @Nested
    @DisplayName("calcularDigitoVerificadorModulo11")
    class CalcularDigitoVerificadorModulo11 {

        @Test
        @DisplayName("Debe calcular DV para base de 48 dígitos")
        void calcularDV_baseValida() {
            String base48 = "210820260117922386940012001001000000001000000001";
            char dv = service.calcularDigitoVerificadorModulo11(base48);
            assertTrue(dv >= '0' && dv <= '9');
        }

        @Test
        @DisplayName("Debe lanzar excepción con base nula")
        void calcularDV_baseNula() {
            assertThrows(NullPointerException.class, () ->
                service.calcularDigitoVerificadorModulo11(null)
            );
        }

        @Test
        @DisplayName("Debe lanzar excepción con base de longitud incorrecta")
        void calcularDV_longitudIncorrecta() {
            assertThrows(IllegalArgumentException.class, () ->
                service.calcularDigitoVerificadorModulo11("12345")
            );
        }
    }
}
