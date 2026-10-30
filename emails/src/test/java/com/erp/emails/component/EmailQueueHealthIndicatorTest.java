package com.erp.emails.component;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.actuate.health.Status;
import java.util.Map;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class EmailQueueHealthIndicatorTest {
    @Test void reportsActualQueueConfigurationFailuresInsteadOfTestingAnUnrelatedGlobalAccount(){
        var jdbc=mock(JdbcTemplate.class);var health=new EmailQueueHealthIndicator(jdbc);
        when(jdbc.queryForMap(anyString())).thenReturn(Map.of("pending",0L,"uncertain",0L,"blocked_configuration",0L,"blocked_recipient_or_document",0L));
        assertEquals(Status.UP,health.health().getStatus());
        when(jdbc.queryForMap(anyString())).thenReturn(Map.of("pending",1L,"uncertain",0L,"blocked_configuration",1L,"blocked_recipient_or_document",0L));
        assertEquals(Status.DOWN,health.health().getStatus());
    }
}
