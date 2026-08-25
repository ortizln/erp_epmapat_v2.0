package com.erp.sri_files.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.services.SriGateway;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecuperarDevueltasScheduler {

    private static final Logger log = LoggerFactory.getLogger(RecuperarDevueltasScheduler.class);

    private final FacturaR facturaR;
    private final SriGateway sriGateway;

    private static final int MAX_INTENTOS = 5;
    private static final int LOTE = 20;

    @Scheduled(cron = "${sri.scheduler.recuperar-devueltas:0 */5 * * * *}")
    public void recuperarFacturasDevueltas() {
        MDC.put("scheduler", "recuperar-devueltas");

        long inicio = System.currentTimeMillis();
        int detectadas = 0;
        int recuperadas = 0;
        int pendientes = 0;
        int fallidas = 0;

        try {
            log.info("[SCHEDULER] INICIO recuperación facturas estado M @ {}", LocalDateTime.now());

            List<Factura> devueltas = facturaR.findDevueltas(PageRequest.of(0, LOTE));
            detectadas = devueltas.size();

            for (Factura f : devueltas) {
                try {
                    MDC.put("idFactura", String.valueOf(f.getIdfactura()));
                    MDC.put("claveAcceso", f.getClaveacceso());

                    if (f.getClaveacceso() == null || f.getClaveacceso().isBlank()) {
                        log.warn("Factura {} sin clave de acceso, saltando", f.getIdfactura());
                        f.setEstado("E");
                        f.setErrores("Sin clave de acceso para recuperar del SRI");
                        facturaR.save(f);
                        fallidas++;
                        continue;
                    }

                    var authResult = sriGateway.consultarAutorizacion(f.getClaveacceso());

                    if (authResult.autorizado()) {
                        f.setXmlautorizado(authResult.xmlAutorizado());
                        f.setEstado("A");
                        f.setErrores(null);
                        f.setFecha_autorizacion(LocalDateTime.now());
                        f.setIntentos_autorizacion(f.getIntentos_autorizacion() != null ? f.getIntentos_autorizacion() + 1 : 1);
                        facturaR.save(f);
                        recuperadas++;
                        log.info("Factura {} recuperada del SRI: nro={}", f.getIdfactura(), authResult.numeroAutorizacion());
                    } else {
                        int intentos = f.getIntentos_autorizacion() != null ? f.getIntentos_autorizacion() + 1 : 1;
                        f.setIntentos_autorizacion(intentos);
                        f.setFecha_ultimo_intento(LocalDateTime.now());
                        f.setErrores(authResult.mensaje() != null ? authResult.mensaje() : "No autorizada aún");
                        facturaR.save(f);
                        pendientes++;

                        if (intentos >= MAX_INTENTOS) {
                            log.warn("Factura {} superó máximo de intentos ({})", f.getIdfactura(), MAX_INTENTOS);
                            f.setEstado("E");
                            f.setErrores("Máximo de intentos alcanzado: " + authResult.mensaje());
                            facturaR.save(f);
                        }
                    }

                } catch (Exception e) {
                    log.error("Error recuperando factura {}", f.getIdfactura(), e);
                    fallidas++;
                } finally {
                    MDC.remove("idFactura");
                    MDC.remove("claveAcceso");
                }
            }

        } catch (Exception e) {
            log.error("[SCHEDULER] Error en ciclo de recuperación de devueltas", e);
        } finally {
            long duracion = System.currentTimeMillis() - inicio;
            log.info("[SCHEDULER] FIN recuperación devueltas | detectadas={} | recuperadas={} | pendientes={} | fallidas={} | duracionMs={}",
                detectadas, recuperadas, pendientes, fallidas, duracion);
            MDC.clear();
        }
    }
}
