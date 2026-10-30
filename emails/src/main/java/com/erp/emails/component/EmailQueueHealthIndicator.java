package com.erp.emails.component;
import org.springframework.stereotype.Component;
import org.springframework.boot.actuate.health.*;
import org.springframework.jdbc.core.JdbcTemplate;
@Component("emailQueue")
public class EmailQueueHealthIndicator implements HealthIndicator {
    private final JdbcTemplate jdbc;
    public EmailQueueHealthIndicator(JdbcTemplate jdbc){this.jdbc=jdbc;}
    @Override public Health health(){
        try {
            var counts=jdbc.queryForMap("""
              SELECT count(*) FILTER(WHERE status='PENDING') AS pending,
              count(*) FILTER(WHERE status='UNCERTAIN') AS uncertain,
              count(*) FILTER(WHERE status='FAILED' AND failure_kind='CONFIGURATION') AS blocked_configuration,
              count(*) FILTER(WHERE status='FAILED' AND failure_kind='PERMANENT') AS blocked_recipient_or_document
              FROM public.email_message
              """);
            boolean needsAttention=((Number)counts.get("uncertain")).longValue()>0
                    || ((Number)counts.get("blocked_configuration")).longValue()>0;
            return (needsAttention?Health.down():Health.up()).withDetails(counts)
                    .withDetail("smtpCheck","Usar prueba de conexion de la cuenta almacenada").build();
        }catch(org.springframework.dao.DataAccessException failure){return Health.down().withDetail("database","unavailable").build();}
    }
}
