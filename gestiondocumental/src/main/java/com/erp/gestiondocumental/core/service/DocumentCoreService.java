package com.erp.gestiondocumental.core.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocumentCoreService {

    private final JdbcTemplate jdbc;

    public DocumentCoreService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, Object> list(String entityCode, String estado, String flujo, String q,
                                    String dependencyId, String typeId, String userId,
                                    String seriesId, String subseriesId,
                                    String dateFrom, String dateTo,
                                    int page, int pageSize) {
        page = Math.max(1, page);
        pageSize = Math.max(1, Math.min(200, pageSize));
        int offset = (page - 1) * pageSize;

        StringBuilder where = new StringBuilder(" d.entidad_id = (SELECT id FROM entidades WHERE codigo = ?) ");
        List<Object> params = new java.util.ArrayList<>();
        params.add(entityCode);

        if (estado != null && !estado.isBlank()) { where.append(" AND d.estado = ? "); params.add(estado); }
        if (flujo != null && !flujo.isBlank()) { where.append(" AND d.flujo = ? "); params.add(flujo); }
        if (q != null && !q.isBlank()) { where.append(" AND (d.asunto ILIKE ? OR COALESCE(d.remitente_externo,'') ILIKE ?) "); params.add("%"+q+"%"); params.add("%"+q+"%"); }
        if (dependencyId != null && !dependencyId.isBlank()) { where.append(" AND d.dependencia_emisora_id::text = ? "); params.add(dependencyId); }
        if (typeId != null && !typeId.isBlank()) { where.append(" AND d.tipo_doc_id::text = ? "); params.add(typeId); }
        if (userId != null && !userId.isBlank()) { where.append(" AND d.owner_user_id::text = ? "); params.add(userId); }
        if (seriesId != null && !seriesId.isBlank()) { where.append(" AND d.series_id::text = ? "); params.add(seriesId); }
        if (subseriesId != null && !subseriesId.isBlank()) { where.append(" AND d.subseries_id::text = ? "); params.add(subseriesId); }
        if (dateFrom != null && !dateFrom.isBlank()) { where.append(" AND d.fecha_elaboracion >= ?::date "); params.add(dateFrom); }
        if (dateTo != null && !dateTo.isBlank()) { where.append(" AND d.fecha_elaboracion <= ?::date "); params.add(dateTo); }

        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM documentos d WHERE " + where, Integer.class, params.toArray());

        String sql = """
                SELECT d.id, d.flujo, d.origen, d.estado, d.prioridad, d.confidencialidad,
                       d.tipo_doc_id, td.codigo AS tipo_codigo, td.nombre AS tipo_nombre,
                       d.dependencia_emisora_id, dep.codigo AS dep_codigo, dep.nombre AS dep_nombre,
                       d.series_id, s.code AS series_code, s.name AS series_name,
                       d.subseries_id, ss.code AS subseries_code, ss.name AS subseries_name,
                       d.retention_schedule_id,
                       d.numero_oficial, d.fecha_elaboracion, d.fecha_emision, d.fecha_recepcion,
                       d.requiere_respuesta, d.fecha_plazo, d.estado_respuesta,
                       d.remitente_persona_id, d.remitente_externo,
                       d.asunto, d.referencia,
                       d.creado_en, d.actualizado_en
                FROM documentos d
                JOIN tipos_documento td ON td.id = d.tipo_doc_id
                LEFT JOIN dependencias dep ON dep.id = d.dependencia_emisora_id
                LEFT JOIN document_series s ON s.id = d.series_id
                LEFT JOIN document_subseries ss ON ss.id = d.subseries_id
                WHERE %s
                ORDER BY d.creado_en DESC
                LIMIT ? OFFSET ?
                """.formatted(where);

        List<Object> listParams = new java.util.ArrayList<>(params);
        listParams.add(pageSize);
        listParams.add(offset);
        List<Map<String, Object>> items = jdbc.queryForList(sql, listParams.toArray());

        int pages = (int) Math.ceil((total == null ? 0 : total) / (double) pageSize);
        return Map.of("items", items, "page", page, "page_size", pageSize, "total", total == null ? 0 : total, "pages", pages);
    }

    public Map<String, Object> dashboard(String entityCode, String dateFrom, String dateTo) {
        StringBuilder where = new StringBuilder(" d.entidad_id = (SELECT id FROM entidades WHERE codigo = ?) ");
        List<Object> params = new java.util.ArrayList<>();
        params.add(entityCode);
        if (dateFrom != null && !dateFrom.isBlank()) { where.append(" AND d.fecha_elaboracion >= ?::date "); params.add(dateFrom); }
        if (dateTo != null && !dateTo.isBlank()) { where.append(" AND d.fecha_elaboracion <= ?::date "); params.add(dateTo); }

        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM documentos d WHERE " + where, Integer.class, params.toArray());
        List<Map<String, Object>> byStatus = jdbc.queryForList("SELECT estado, COUNT(*) AS total FROM documentos d WHERE " + where + " GROUP BY estado ORDER BY estado", params.toArray());
        List<Map<String, Object>> byFlow = jdbc.queryForList("SELECT flujo, COUNT(*) AS total FROM documentos d WHERE " + where + " GROUP BY flujo ORDER BY flujo", params.toArray());

        return Map.of(
                "total_documents", total == null ? 0 : total,
                "by_status", byStatus,
                "by_flow", byFlow
        );
    }

    public Map<String, Object> get(String docId) {
        return jdbc.queryForMap("""
                SELECT d.*,
                       td.codigo AS tipo_codigo, td.nombre AS tipo_nombre,
                       dep.codigo AS dep_codigo, dep.nombre AS dep_nombre
                FROM documentos d
                JOIN tipos_documento td ON td.id = d.tipo_doc_id
                LEFT JOIN dependencias dep ON dep.id = d.dependencia_emisora_id
                WHERE d.id::text = ?
                """, docId);
    }

    public String create(Map<String, Object> data) {
        return jdbc.queryForObject("""
                INSERT INTO documentos (
                    entidad_id, flujo, origen, estado, prioridad, confidencialidad,
                    requiere_respuesta, fecha_plazo, estado_respuesta,
                    tipo_doc_id, dependencia_emisora_id,
                    series_id, subseries_id, retention_schedule_id,
                    fecha_elaboracion,
                    remitente_persona_id, remitente_externo,
                    asunto, cuerpo, referencia, observaciones,
                    creado_por, actualizado_por
                ) VALUES (
                    (SELECT id FROM entidades WHERE codigo = ?),
                    ?, ?, COALESCE(?, 'BORRADOR'), COALESCE(?, 'MEDIA'), COALESCE(?, 'INTERNA'),
                    COALESCE(?, false), ?::timestamp, COALESCE(?, 'NO_REQUIERE'),
                    ?::uuid, ?::uuid,
                    ?::uuid, ?::uuid, ?::uuid,
                    COALESCE(?::date, CURRENT_DATE),
                    ?::uuid, ?,
                    ?, ?, ?, ?,
                    ?::uuid, ?::uuid
                ) RETURNING id::text
                """,
                String.class,
                data.get("entity_code"),
                data.get("flujo"), data.get("origen"), data.get("estado"), data.get("prioridad"), data.get("confidencialidad"),
                data.get("requiere_respuesta"), data.get("fecha_plazo"), data.get("estado_respuesta"),
                data.get("tipo_doc_id"), data.get("dependencia_emisora_id"),
                data.get("series_id"), data.get("subseries_id"), data.get("retention_schedule_id"),
                data.get("fecha_elaboracion"), data.get("remitente_persona_id"), data.get("remitente_externo"),
                data.get("asunto"), data.get("cuerpo"), data.get("referencia"), data.get("observaciones"),
                data.get("usuario_id"), data.get("usuario_id")
        );
    }

    public int update(String docId, Map<String, Object> data) {
        return jdbc.update("""
                UPDATE documentos SET
                    flujo = ?,
                    origen = ?,
                    estado = COALESCE(?, 'BORRADOR'),
                    prioridad = COALESCE(?, 'MEDIA'),
                    confidencialidad = COALESCE(?, 'INTERNA'),
                    requiere_respuesta = COALESCE(?, false),
                    fecha_plazo = ?::timestamp,
                    estado_respuesta = COALESCE(?, 'NO_REQUIERE'),
                    tipo_doc_id = ?::uuid,
                    dependencia_emisora_id = ?::uuid,
                    series_id = ?::uuid,
                    subseries_id = ?::uuid,
                    retention_schedule_id = ?::uuid,
                    fecha_elaboracion = COALESCE(?::date, fecha_elaboracion),
                    remitente_persona_id = ?::uuid,
                    remitente_externo = ?,
                    asunto = ?,
                    cuerpo = ?,
                    referencia = ?,
                    observaciones = ?,
                    actualizado_por = ?::uuid,
                    actualizado_en = now()
                WHERE id::text = ?
                """,
                data.get("flujo"), data.get("origen"), data.get("estado"), data.get("prioridad"), data.get("confidencialidad"),
                data.get("requiere_respuesta"), data.get("fecha_plazo"), data.get("estado_respuesta"),
                data.get("tipo_doc_id"), data.get("dependencia_emisora_id"), data.get("series_id"), data.get("subseries_id"), data.get("retention_schedule_id"),
                data.get("fecha_elaboracion"), data.get("remitente_persona_id"), data.get("remitente_externo"),
                data.get("asunto"), data.get("cuerpo"), data.get("referencia"), data.get("observaciones"),
                data.get("usuario_id"), docId
        );
    }

    public int delete(String docId) {
        return jdbc.update("DELETE FROM documentos WHERE id::text = ?", docId);
    }
}
