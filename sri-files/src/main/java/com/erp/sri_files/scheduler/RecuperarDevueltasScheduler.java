package com.erp.sri_files.scheduler;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.services.XmlAutorizadoRecoveryService;
import com.erp.sri_files.services.SriAutorizacionRetryPolicy;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecuperarDevueltasScheduler {
    private static final Logger log = LoggerFactory.getLogger(RecuperarDevueltasScheduler.class);
    private final FacturaR facturaR;
    private final XmlAutorizadoRecoveryService recovery;
    private final SriAutorizacionRetryPolicy retry;
    private final AtomicBoolean ejecutando = new AtomicBoolean();
    @Value("${sri.scheduler.recuperar-devueltas.lote:20}")
    private int lote = 20;

    @Scheduled(cron = "${sri.scheduler.recuperar-devueltas.cron:0 */5 * * * *}")
    public void recuperarFacturasDevueltas() {
        if (!ejecutando.compareAndSet(false, true)) return;
        var contextoAnterior = MDC.getCopyOfContextMap();
        MDC.put("scheduler", "recuperar-devueltas");
        long inicio = System.currentTimeMillis();
        int detectadas = 0, recuperadas = 0, pendientes = 0, fallidas = 0;
        try {
            var devueltas = facturaR.findDevueltasVencidas(LocalDateTime.now(),
                    retry.getBaseMs(), retry.getMaxMs(), Math.max(1, Math.min(lote, 200)));
            detectadas = devueltas.size();
            for (Factura factura : devueltas) {
                String clave = factura.getClaveacceso() == null ? "" : factura.getClaveacceso().trim();
                MDC.put("idFactura", String.valueOf(factura.getIdfactura()));
                try {
                    var resultado = recovery.recuperar(clave, false);
                    if (resultado.recuperado()) {
                        int actualizadas = facturaR.guardarXmlRecuperadoDevuelta(factura.getIdfactura(), clave,
                                resultado.xml(), resultado.fechaAutorizacion() != null
                                    ? resultado.fechaAutorizacion() : LocalDateTime.now());
                        recuperadas += actualizadas;
                        log.info("Recuperacion XML idfactura={} fuente={} filasActualizadas={}",
                                factura.getIdfactura(), resultado.fuente(), actualizadas);
                    } else {
                        registrarPendiente(factura, clave, resultado.mensaje());
                        pendientes++;
                    }
                } catch (Exception error) {
                    fallidas++;
                    log.warn("Consulta XML pendiente idfactura={}: {}", factura.getIdfactura(), error.getMessage());
                    try { registrarPendiente(factura, clave, "Error recuperando XML: " + error.getMessage()); }
                    catch (Exception persistencia) {
                        log.error("No se pudo registrar el intento idfactura={}", factura.getIdfactura(), persistencia);
                    }
                } finally { MDC.remove("idFactura"); }
            }
        } catch (Exception error) {
            log.error("Error en ciclo de recuperacion de devueltas", error);
        } finally {
            log.info("Recuperacion devueltas detectadas={} recuperadas={} pendientes={} fallidas={} duracionMs={}",
                    detectadas, recuperadas, pendientes, fallidas, System.currentTimeMillis() - inicio);
            if (contextoAnterior == null) MDC.clear(); else MDC.setContextMap(contextoAnterior);
            ejecutando.set(false);
        }
    }

    private void registrarPendiente(Factura factura, String clave, String detalle) {
        String mensaje = detalle == null || detalle.isBlank() ? "SRI sin XML autorizado disponible" : detalle;
        facturaR.registrarRecuperacionPendiente(factura.getIdfactura(), clave,
                mensaje.substring(0, Math.min(1500, mensaje.length())), LocalDateTime.now());
        // Conservar M: la ausencia temporal de XML no equivale a un rechazo definitivo.
    }
}
