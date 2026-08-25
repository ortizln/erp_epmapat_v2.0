package com.erp.sri_files.monitoring;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final AtomicLong totalDocumentosRecibidos = new AtomicLong(0);
    private final AtomicLong totalDocumentosAutorizados = new AtomicLong(0);
    private final AtomicLong totalDocumentosFallidos = new AtomicLong(0);
    private final AtomicLong totalEnviosSri = new AtomicLong(0);
    private final AtomicLong totalConsultasSri = new AtomicLong(0);
    private final AtomicLong totalCorreosEnviados = new AtomicLong(0);
    private final AtomicLong totalBatchJobs = new AtomicLong(0);

    private final Map<String, AtomicLong> documentosPorTipo = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> erroresPorTipo = new ConcurrentHashMap<>();

    private final LocalDateTime fechaInicio = LocalDateTime.now();

    public void incrementarRecibidos(String tipo) {
        totalDocumentosRecibidos.incrementAndGet();
        documentosPorTipo.computeIfAbsent(tipo, k -> new AtomicLong(0)).incrementAndGet();
    }

    public void incrementarAutorizados(String tipo) {
        totalDocumentosAutorizados.incrementAndGet();
    }

    public void incrementarFallidos(String tipo) {
        totalDocumentosFallidos.incrementAndGet();
        erroresPorTipo.computeIfAbsent(tipo, k -> new AtomicLong(0)).incrementAndGet();
    }

    public void incrementarEnviosSri() {
        totalEnviosSri.incrementAndGet();
    }

    public void incrementarConsultasSri() {
        totalConsultasSri.incrementAndGet();
    }

    public void incrementarCorreosEnviados() {
        totalCorreosEnviados.incrementAndGet();
    }

    public void incrementarBatchJobs() {
        totalBatchJobs.incrementAndGet();
    }

    public Map<String, Object> getMetricas() {
        Map<String, Object> metricas = new ConcurrentHashMap<>();
        metricas.put("fechaInicio", fechaInicio.toString());
        metricas.put("uptime", java.time.Duration.between(fechaInicio, LocalDateTime.now()).toMinutes() + " minutos");
        metricas.put("totalRecibidos", totalDocumentosRecibidos.get());
        metricas.put("totalAutorizados", totalDocumentosAutorizados.get());
        metricas.put("totalFallidos", totalDocumentosFallidos.get());
        metricas.put("totalEnviosSri", totalEnviosSri.get());
        metricas.put("totalConsultasSri", totalConsultasSri.get());
        metricas.put("totalCorreosEnviados", totalCorreosEnviados.get());
        metricas.put("totalBatchJobs", totalBatchJobs.get());
        metricas.put("documentosPorTipo", new ConcurrentHashMap<>(documentosPorTipo.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().get()
            ))));
        metricas.put("erroresPorTipo", new ConcurrentHashMap<>(erroresPorTipo.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().get()
            ))));
        return metricas;
    }
}
