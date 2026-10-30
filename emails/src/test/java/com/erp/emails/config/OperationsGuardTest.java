package com.erp.emails.config;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
class OperationsGuardTest {
    @Test void administrativeOperationsAreClosedWithoutConfigurationOrValidKey(){
        var g=new OperationsGuard();assertThrows(ResponseStatusException.class,()->g.check("key","actor"));
        ReflectionTestUtils.setField(g,"expected","test-key");assertThrows(ResponseStatusException.class,()->g.check("wrong","actor"));
        assertThrows(ResponseStatusException.class,()->g.check("test-key",""));assertEquals("actor",g.check("test-key","actor"));
    }
}
