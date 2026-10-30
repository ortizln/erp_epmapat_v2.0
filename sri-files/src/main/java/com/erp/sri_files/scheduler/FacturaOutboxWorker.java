package com.erp.sri_files.scheduler;

import com.erp.sri_files.services.*;
import com.erp.sri_files.repositories.FacturaR;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;

@Component
public class FacturaOutboxWorker {
    private final FacturaOutboxService outbox;
    private final EnvioSriBatchService envio;
    private final FacturaR facturas;
    @Value("${sri.notificacion.batch-size:10}") private int batch = 10;
    public FacturaOutboxWorker(FacturaOutboxService outbox, EnvioSriBatchService envio, FacturaR facturas) {
        this.outbox=outbox;this.envio=envio;this.facturas=facturas;
    }
    @Scheduled(fixedDelayString="${sri.notificacion.delay-ms:15000}")
    public void procesar() {
        for (int n=0;n<Math.max(1,Math.min(batch,100));n++) {
            var task=outbox.reclamar(); if(task==null)return;
            try {
                Long id=((Number)task.get("factura_id")).longValue();
                envio.reintentarCorreoPendiente(id);
                var f=facturas.findById(id).orElseThrow();
                String estado=Boolean.TRUE.equals(f.getMail_enviado())?"COMPLETE":f.getEmailMessageId()!=null?"WAITING_EMAIL":"RETRY";
                if(f.getErrores()!=null && f.getErrores().startsWith("Correo comprador invalido")) estado="BLOCKED";
                if(f.getErrores()!=null && (f.getErrores().startsWith("Correo FAILED") || f.getErrores().startsWith("Correo CANCELLED"))) estado="BLOCKED";
                if(f.getErrores()!=null && f.getErrores().startsWith("Correo UNCERTAIN")) estado="UNCERTAIN";
                // La notificacion conserva el estado del microservicio; no crea nuevos correos al consultar.
                outbox.terminar(task,estado,f.getErrores());
            } catch(Exception error) {
                outbox.terminar(task,"RETRY",error.getClass().getSimpleName());
                org.slf4j.LoggerFactory.getLogger(getClass()).error("Error en outbox de factura {}",task.get("factura_id"),error);
            }
        }
    }
}
