package com.erp.sri_files;

import static org.junit.jupiter.api.Assertions.*;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import com.erp.sri_files.validation.SriGuiaRemisionValidationService;

class GuiaRemisionXmlCaracterizacionTest {

    private static final String GOLDEN_DIR = "/golden/";
    private static final String GOLDEN_FILE = "guia_remision_baseline.xml";

    private static String goldenXml;

    private final SriGuiaRemisionValidationService validationService = new SriGuiaRemisionValidationService();

    @BeforeAll
    static void loadGoldenFile() throws Exception {
        try (InputStream is = GuiaRemisionXmlCaracterizacionTest.class.getResourceAsStream(GOLDEN_DIR + GOLDEN_FILE)) {
            assertNotNull(is, "No se encontró " + GOLDEN_DIR + GOLDEN_FILE + " en resources/test");
            goldenXml = new String(is.readAllBytes());
        }
    }

    @Test
    void goldenXml_matchesStructurally() {
        assertNotNull(goldenXml, "goldenXml no debe ser null");
        assertTrue(goldenXml.contains("<guiaRemision"), "Debe contener <guiaRemision>");
        assertTrue(goldenXml.contains("id=\"comprobante\""), "Debe tener id=comprobante");
        assertTrue(goldenXml.contains("version=\"1.0.0\""), "Debe tener version 1.0.0");
        assertTrue(goldenXml.contains("claveAcceso"), "Debe tener claveAcceso");
    }

    @Test
    void goldenXml_validates_successfully() {
        var result = validationService.validate(goldenXml);
        assertTrue(result.valid(), "Golden XML debe pasar validación: " + result.errors());
    }

    @Test
    void parseXML_deterministic() {
        var r1 = validationService.validate(goldenXml);
        var r2 = validationService.validate(goldenXml);
        assertEquals(r1.valid(), r2.valid(), "Validación debe ser determinista");
    }
}
