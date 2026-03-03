package com.erp.gestiondocumental.core.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WorkflowCoreService {

    private final JdbcTemplate jdbc;

    public WorkflowCoreService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String assign(String docId, Map<String, Object> body) {
        return jdbc.queryForObject("""
                INSERT INTO documento_asignaciones(
                    documento_id, asignado_a_user_id, asignado_por_user_id,
                    dependencia_id, rol_responsable, principal, estado, observacion
                ) VALUES (?::uuid, ?::uuid, ?::uuid, ?::uuid, ?, COALESCE(?, true), 'ACTIVA', ?)
                RETURNING id::text
                """, String.class,
                docId,
                body.get("assigned_to_user_id"),
                body.get("assigned_by_user_id"),
                body.get("dependency_id"),
                body.getOrDefault("role", "RESPONSABLE"),
                body.get("principal"),
                body.get("note")
        );
    }

    public String derive(String docId, Map<String, Object> body) {
        return jdbc.queryForObject("""
                INSERT INTO documento_derivaciones(
                    documento_id, de_user_id, de_dependencia_id,
                    para_user_id, para_dependencia_id,
                    sumilla, requiere_respuesta, fecha_plazo, estado
                ) VALUES (?::uuid, ?::uuid, ?::uuid, ?::uuid, ?::uuid, ?, COALESCE(?, false), ?::timestamp, 'PENDIENTE')
                RETURNING id::text
                """, String.class,
                docId,
                body.get("from_user_id"), body.get("from_dependency_id"),
                body.get("to_user_id"), body.get("to_dependency_id"),
                body.get("note"), body.get("requires_response"), body.get("due_at")
        );
    }

    public Map<String, Object> deriveBulk(String docId, Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> targets = (List<Map<String, Object>>) body.get("targets");
        int created = 0;
        if (targets != null) {
            for (Map<String, Object> t : targets) {
                if (t.get("to_user_id") == null && t.get("to_dependency_id") == null) continue;
                derive(docId, Map.of(
                        "from_user_id", body.get("from_user_id"),
                        "from_dependency_id", body.get("from_dependency_id"),
                        "to_user_id", t.get("to_user_id"),
                        "to_dependency_id", t.get("to_dependency_id"),
                        "note", body.get("note"),
                        "requires_response", body.get("requires_response"),
                        "due_at", body.get("due_at")
                ));
                created++;
            }
        }
        return Map.of("created", created);
    }

    public List<Map<String, Object>> listDerivations(String docId) {
        return jdbc.queryForList("""
                SELECT id, documento_id, de_user_id, de_dependencia_id, para_user_id, para_dependencia_id,
                       sumilla, requiere_respuesta, fecha_plazo, estado, leido_en, respondido_en, cerrado_en, creado_en
                FROM documento_derivaciones
                WHERE documento_id = ?::uuid
                ORDER BY creado_en DESC
                """, docId);
    }

    public Map<String, Object> pendingDerivations(String toUserId, String toDependencyId, int page, int pageSize) {
        page = Math.max(1, page);
        pageSize = Math.max(1, Math.min(200, pageSize));
        int offset = (page - 1) * pageSize;

        StringBuilder where = new StringBuilder(" d.estado IN ('PENDIENTE','LEIDO','EN_GESTION') ");
        java.util.List<Object> params = new java.util.ArrayList<>();
        if (toUserId != null && !toUserId.isBlank()) { where.append(" AND para_user_id::text = ? "); params.add(toUserId); }
        if (toDependencyId != null && !toDependencyId.isBlank()) { where.append(" AND para_dependencia_id::text = ? "); params.add(toDependencyId); }

        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM documento_derivaciones d WHERE " + where, Integer.class, params.toArray());
        java.util.List<Object> lp = new java.util.ArrayList<>(params);
        lp.add(pageSize); lp.add(offset);
        List<Map<String, Object>> items = jdbc.queryForList("""
                SELECT d.id, d.documento_id, d.para_user_id, d.para_dependencia_id,
                       d.sumilla, d.requiere_respuesta, d.fecha_plazo, d.estado, d.creado_en
                FROM documento_derivaciones d
                WHERE %s
                ORDER BY d.fecha_plazo NULLS LAST, d.creado_en DESC
                LIMIT ? OFFSET ?
                """.formatted(where), lp.toArray());

        int pages = (int) Math.ceil((total == null ? 0 : total) / (double) pageSize);
        return Map.of("items", items, "page", page, "page_size", pageSize, "total", total == null ? 0 : total, "pages", pages);
    }

    public int markRead(String derivationId, String readerUserId) {
        return jdbc.update("""
                UPDATE documento_derivaciones
                SET estado = CASE WHEN estado = 'PENDIENTE' THEN 'LEIDO' ELSE estado END,
                    leido_en = COALESCE(leido_en, now())
                WHERE id::text = ?
                """, derivationId);
    }

    public String respond(String docId, Map<String, Object> body) {
        return jdbc.queryForObject("""
                INSERT INTO documento_respuestas(documento_id, derivacion_id, respondido_por_user_id, asunto, cuerpo)
                VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?)
                RETURNING id::text
                """, String.class,
                docId, body.get("derivation_id"), body.get("responded_by_user_id"), body.get("subject"), body.get("body"));
    }

    public List<Map<String, Object>> timeline(String docId) {
        return jdbc.queryForList("""
                SELECT id, actor_user_id, actor_rol, evento, detalle, created_at
                FROM documento_eventos
                WHERE documento_id = ?::uuid
                ORDER BY created_at ASC, id ASC
                """, docId);
    }

    public List<Map<String, Object>> overdue(String entityCode) {
        return jdbc.queryForList("""
                SELECT d.id, d.asunto, d.fecha_plazo, d.estado_respuesta
                FROM documentos d
                JOIN entidades e ON e.id = d.entidad_id
                WHERE e.codigo = ?
                  AND d.requiere_respuesta = true
                  AND d.fecha_plazo IS NOT NULL
                  AND d.fecha_plazo < now()
                  AND d.estado_respuesta <> 'RESPONDIDO'
                ORDER BY d.fecha_plazo ASC
                """, entityCode);
    }

    public List<Map<String, Object>> dueSoon(String entityCode, int hours) {
        return jdbc.queryForList("""
                SELECT d.id, d.asunto, d.fecha_plazo, d.estado_respuesta
                FROM documentos d
                JOIN entidades e ON e.id = d.entidad_id
                WHERE e.codigo = ?
                  AND d.requiere_respuesta = true
                  AND d.fecha_plazo IS NOT NULL
                  AND d.fecha_plazo >= now()
                  AND d.fecha_plazo <= now() + (? || ' hour')::interval
                  AND d.estado_respuesta <> 'RESPONDIDO'
                ORDER BY d.fecha_plazo ASC
                """, entityCode, hours);
    }
}
