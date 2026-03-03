package com.erp.gestiondocumental.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WorkflowDocumentosService {

    private final JdbcTemplate jdbc;

    public WorkflowDocumentosService(JdbcTemplate jdbc) {
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
        List<String> ids = new ArrayList<>();
        if (targets != null) {
            for (Map<String, Object> t : targets) {
                if (t.get("to_user_id") == null && t.get("to_dependency_id") == null) continue;
                String id = derive(docId, Map.of(
                        "from_user_id", body.get("from_user_id"),
                        "from_dependency_id", body.get("from_dependency_id"),
                        "to_user_id", t.get("to_user_id"),
                        "to_dependency_id", t.get("to_dependency_id"),
                        "note", body.get("note"),
                        "requires_response", body.get("requires_response"),
                        "due_at", body.get("due_at")
                ));
                ids.add(id);
            }
        }
        return Map.of("created", ids.size(), "derivation_ids", ids);
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
        ArrayList<Object> params = new ArrayList<>();
        if (toUserId != null && !toUserId.isBlank()) { where.append(" AND para_user_id::text = ? "); params.add(toUserId); }
        if (toDependencyId != null && !toDependencyId.isBlank()) { where.append(" AND para_dependencia_id::text = ? "); params.add(toDependencyId); }

        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM documento_derivaciones d WHERE " + where, Integer.class, params.toArray());
        ArrayList<Object> lp = new ArrayList<>(params);
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

    public Map<String, Object> createNestedResponse(String docId, Map<String, Object> body) {
        String childId = jdbc.queryForObject("""
                INSERT INTO documentos(
                  entidad_id, flujo, origen, estado, prioridad, confidencialidad,
                  requiere_respuesta, estado_respuesta,
                  tipo_doc_id, dependencia_emisora_id,
                  fecha_elaboracion, asunto, cuerpo,
                  creado_por, actualizado_por
                )
                SELECT
                  d.entidad_id, 'SALIDA', 'INTERNO', 'BORRADOR', 'MEDIA', 'INTERNA',
                  false, 'NO_REQUIERE', d.tipo_doc_id, d.dependencia_emisora_id,
                  CURRENT_DATE, ?, ?, ?::uuid, ?::uuid
                FROM documentos d WHERE d.id::text = ?
                RETURNING id::text
                """, String.class,
                body.getOrDefault("nested_subject", body.get("subject")),
                body.getOrDefault("nested_body", body.get("body")),
                body.get("responded_by_user_id"), body.get("responded_by_user_id"), docId);

        String relationId = jdbc.queryForObject("""
                INSERT INTO documento_relaciones(documento_padre_id, documento_hijo_id, tipo_relacion, creado_por_user_id, detalle)
                VALUES (?::uuid, ?::uuid, 'RESPUESTA_A', ?::uuid, jsonb_build_object('derivation_id', ?))
                RETURNING id::text
                """, String.class,
                docId, childId, body.get("responded_by_user_id"), body.get("derivation_id"));

        return Map.of("relation_id", relationId, "child_document_id", childId);
    }

    public List<Map<String, Object>> listRelations(String docId, String relationType) {
        return jdbc.queryForList("""
                SELECT r.id, r.tipo_relacion, r.creado_en, r.creado_por_user_id,
                       r.documento_padre_id, r.documento_hijo_id,
                       h.numero_oficial AS hijo_numero_oficial,
                       h.asunto AS hijo_asunto,
                       h.estado AS hijo_estado,
                       h.fecha_emision AS hijo_fecha_emision
                FROM documento_relaciones r
                JOIN documentos h ON h.id = r.documento_hijo_id
                WHERE r.documento_padre_id = ?::uuid
                  AND (? IS NULL OR r.tipo_relacion = ?)
                ORDER BY r.creado_en DESC
                """, docId, relationType, relationType);
    }

    public String addFileRecord(String docId, Map<String, Object> body) {
        int version = jdbc.queryForObject("SELECT COALESCE(MAX(version),0)+1 FROM documento_archivos WHERE documento_id = ?::uuid", Integer.class, docId);
        return jdbc.queryForObject("""
                INSERT INTO documento_archivos(
                    documento_id, version, tipo,
                    nombre_original, nombre_storage, extension,
                    mime_type, size_bytes, sha256,
                    storage_path, subido_por_user_id
                ) VALUES (?::uuid, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::uuid)
                RETURNING id::text
                """, String.class,
                docId, version,
                body.getOrDefault("file_kind", "ANEXO"),
                body.get("nombre_original"), body.get("nombre_storage"), body.get("extension"),
                body.getOrDefault("mime_type", "application/octet-stream"), body.getOrDefault("size_bytes", 0), body.get("sha256"),
                body.get("storage_path"), body.get("uploaded_by_user_id"));
    }

    public List<Map<String, Object>> listFiles(String docId) {
        return jdbc.queryForList("""
                SELECT id, documento_id, version, tipo, nombre_original, nombre_storage,
                       extension, mime_type, size_bytes, sha256, storage_path, subido_por_user_id, subido_en
                FROM documento_archivos
                WHERE documento_id = ?::uuid AND activo = true
                ORDER BY version DESC, subido_en DESC
                """, docId);
    }

    public Map<String, Object> getFile(String docId, String fileId) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT id, documento_id, version, tipo, nombre_original, nombre_storage,
                       extension, mime_type, size_bytes, sha256, storage_path, subido_por_user_id, subido_en
                FROM documento_archivos
                WHERE documento_id = ?::uuid AND id = ?::uuid AND activo = true
                """, docId, fileId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public Map<String, Object> generateAlerts(String entityCode) {
        Integer t24 = jdbc.queryForObject("""
                WITH ins AS (
                INSERT INTO documento_alertas(documento_id, user_id, tipo, scheduled_at, estado, payload)
                SELECT d.id, d.owner_user_id, 'T_24H', now(), 'PENDIENTE',
                       jsonb_build_object('asunto', d.asunto, 'fecha_plazo', d.fecha_plazo)
                FROM documentos d
                JOIN entidades e ON e.id = d.entidad_id
                WHERE e.codigo = ?
                  AND d.requiere_respuesta = true
                  AND d.fecha_plazo IS NOT NULL
                  AND d.fecha_plazo BETWEEN now() AND now() + interval '24 hour'
                  AND d.estado_respuesta <> 'RESPONDIDO'
                RETURNING id)
                SELECT COUNT(*) FROM ins
                """, Integer.class, entityCode);

        Integer venc = jdbc.queryForObject("""
                WITH ins AS (
                INSERT INTO documento_alertas(documento_id, user_id, tipo, scheduled_at, estado, payload)
                SELECT d.id, d.owner_user_id, 'VENCIDO', now(), 'PENDIENTE',
                       jsonb_build_object('asunto', d.asunto, 'fecha_plazo', d.fecha_plazo)
                FROM documentos d
                JOIN entidades e ON e.id = d.entidad_id
                WHERE e.codigo = ?
                  AND d.requiere_respuesta = true
                  AND d.fecha_plazo IS NOT NULL
                  AND d.fecha_plazo < now()
                  AND d.estado_respuesta <> 'RESPONDIDO'
                RETURNING id)
                SELECT COUNT(*) FROM ins
                """, Integer.class, entityCode);

        return Map.of("t24_created", t24 == null ? 0 : t24, "overdue_created", venc == null ? 0 : venc);
    }

    public Map<String, Object> listAlerts(String entityCode, String state, int page, int pageSize) {
        page = Math.max(1, page);
        pageSize = Math.max(1, Math.min(200, pageSize));
        int offset = (page - 1) * pageSize;

        Integer total = jdbc.queryForObject("""
                SELECT COUNT(*)
                FROM documento_alertas a
                JOIN documentos d ON d.id = a.documento_id
                JOIN entidades e ON e.id = d.entidad_id
                WHERE e.codigo = ?
                  AND (? IS NULL OR a.estado = ?)
                """, Integer.class, entityCode, state, state);

        List<Map<String, Object>> items = jdbc.queryForList("""
                SELECT a.id, a.documento_id, a.user_id, a.tipo, a.scheduled_at, a.sent_at, a.estado, a.payload,
                       d.asunto, d.fecha_plazo
                FROM documento_alertas a
                JOIN documentos d ON d.id = a.documento_id
                JOIN entidades e ON e.id = d.entidad_id
                WHERE e.codigo = ?
                  AND (? IS NULL OR a.estado = ?)
                ORDER BY a.scheduled_at DESC
                LIMIT ? OFFSET ?
                """, entityCode, state, state, pageSize, offset);

        int pages = (int) Math.ceil((total == null ? 0 : total) / (double) pageSize);
        return Map.of("items", items, "page", page, "page_size", pageSize, "total", total == null ? 0 : total, "pages", pages);
    }

    public Map<String, Object> dispatchAlerts(String entityCode, int limit) {
        Integer updated = jdbc.queryForObject("""
                WITH cte AS (
                  SELECT a.id
                  FROM documento_alertas a
                  JOIN documentos d ON d.id = a.documento_id
                  JOIN entidades e ON e.id = d.entidad_id
                  WHERE e.codigo = ? AND a.estado = 'PENDIENTE'
                  ORDER BY a.scheduled_at ASC
                  LIMIT ?
                ), upd AS (
                  UPDATE documento_alertas a
                  SET estado = 'ENVIADA', sent_at = now()
                  FROM cte
                  WHERE a.id = cte.id
                  RETURNING a.id
                )
                SELECT COUNT(*) FROM upd
                """, Integer.class, entityCode, limit);
        return Map.of("channel", "INTERNAL", "sent", updated == null ? 0 : updated);
    }

    public Map<String, Object> issue(String docId, String userId) {
        Map<String, Object> doc = jdbc.queryForMap("""
                SELECT d.id, d.entidad_id, d.tipo_doc_id, d.dependencia_emisora_id, d.estado, d.numero_oficial,
                       COALESCE(d.fecha_elaboracion, CURRENT_DATE) AS fecha_elaboracion,
                       dep.codigo AS dep_codigo, td.codigo AS tipo_codigo
                FROM documentos d
                JOIN tipos_documento td ON td.id = d.tipo_doc_id
                LEFT JOIN dependencias dep ON dep.id = d.dependencia_emisora_id
                WHERE d.id::text = ?
                """, docId);

        if (doc.get("numero_oficial") != null) {
            throw new RuntimeException("El documento ya está emitido");
        }
        String estado = String.valueOf(doc.get("estado"));
        if (!("BORRADOR".equals(estado) || "EN_REVISION".equals(estado))) {
            throw new RuntimeException("No se puede emitir en estado: " + estado);
        }

        int anio = Integer.parseInt(String.valueOf(doc.get("fecha_elaboracion")).substring(0, 4));
        String area = (doc.get("dep_codigo") == null ? "GEN" : String.valueOf(doc.get("dep_codigo"))).trim().toUpperCase();
        String tipo = (doc.get("tipo_codigo") == null ? "DOC" : String.valueOf(doc.get("tipo_codigo"))).trim().toUpperCase();

        List<Map<String, Object>> series = jdbc.queryForList("""
                SELECT id, prefijo, longitud_seq, siguiente_seq
                FROM series_numeracion
                WHERE entidad_id = ?::uuid
                  AND tipo_doc_id = ?::uuid
                  AND anio = ?
                  AND activo = TRUE
                  AND (dependencia_id = ?::uuid OR dependencia_id IS NULL)
                ORDER BY (dependencia_id IS NULL) ASC
                """, doc.get("entidad_id"), doc.get("tipo_doc_id"), anio, doc.get("dependencia_emisora_id"));
        if (series.isEmpty()) throw new RuntimeException("No hay serie configurada para emisión");

        Map<String, Object> serie = series.get(0);
        String prefijo = (serie.get("prefijo") == null ? tipo : String.valueOf(serie.get("prefijo"))).trim().toUpperCase();
        int seq = ((Number) serie.get("siguiente_seq")).intValue();
        int length = ((Number) serie.get("longitud_seq")).intValue();
        String numero = prefijo + "-" + area + "-" + anio + "-" + String.format("%0" + length + "d", seq);

        jdbc.update("UPDATE series_numeracion SET siguiente_seq = siguiente_seq + 1 WHERE id = ?::uuid", serie.get("id"));
        int upd = jdbc.update("""
                UPDATE documentos
                SET numero_oficial = ?, serie_id = ?::uuid, estado = 'EMITIDO', fecha_emision = now(),
                    actualizado_por = ?::uuid, actualizado_en = now()
                WHERE id::text = ? AND numero_oficial IS NULL
                """, numero, serie.get("id"), userId, docId);
        if (upd == 0) throw new RuntimeException("No se pudo emitir (concurrencia)");

        List<Map<String, Object>> dests = jdbc.queryForList("SELECT persona_id, dependencia_id, externo_nombre FROM documento_destinatarios WHERE documento_id = ?::uuid", docId);
        int creadas = 0;
        for (Map<String, Object> d : dests) {
            if (d.get("externo_nombre") != null) continue;
            if (d.get("persona_id") != null) {
                creadas += jdbc.update("INSERT INTO documento_recepciones (documento_id, receptor_id, estado) VALUES (?::uuid, ?::uuid, 'PENDIENTE') ON CONFLICT DO NOTHING", docId, d.get("persona_id"));
            } else if (d.get("dependencia_id") != null) {
                creadas += jdbc.update("INSERT INTO documento_recepciones (documento_id, receptor_id, dependencia_id, estado) VALUES (?::uuid, NULL, ?::uuid, 'PENDIENTE') ON CONFLICT DO NOTHING", docId, d.get("dependencia_id"));
            }
        }
        return Map.of("id", docId, "numero_oficial", numero, "anio", anio, "prefijo", prefijo, "area", area, "pendientes", creadas);
    }

    public Map<String, Object> receive(String docId, String receptorId, String dependenciaId, String userId, String comentario) {
        if ((receptorId == null && dependenciaId == null) || (receptorId != null && dependenciaId != null)) {
            throw new RuntimeException("Debe enviar receptor_id o dependencia_id, pero no ambos");
        }

        List<Map<String, Object>> docs = jdbc.queryForList("SELECT id, estado, numero_oficial FROM documentos WHERE id::text = ?", docId);
        if (docs.isEmpty()) throw new RuntimeException("Documento no encontrado");
        String estado = String.valueOf(docs.get(0).get("estado"));
        if (!("EMITIDO".equals(estado) || "DERIVADO".equals(estado) || "RECIBIDO".equals(estado))) {
            throw new RuntimeException("No se puede recibir en estado: " + estado);
        }

        String recId;
        if (receptorId != null) {
            recId = jdbc.queryForObject("""
                    INSERT INTO documento_recepciones(documento_id, receptor_id, dependencia_id, recibido_en, confirmado_por, estado, comentario)
                    VALUES (?::uuid, ?::uuid, NULL, now(), ?::uuid, 'RECIBIDO', ?)
                    ON CONFLICT (documento_id, receptor_id)
                    DO UPDATE SET recibido_en = COALESCE(documento_recepciones.recibido_en, now()), confirmado_por = EXCLUDED.confirmado_por,
                                  estado = 'RECIBIDO', comentario = COALESCE(EXCLUDED.comentario, documento_recepciones.comentario)
                    RETURNING id::text
                    """, String.class, docId, receptorId, userId, comentario);
        } else {
            List<Map<String, Object>> ex = jdbc.queryForList("SELECT id FROM documento_recepciones WHERE documento_id = ?::uuid AND receptor_id IS NULL AND dependencia_id::text = ?", docId, dependenciaId);
            if (ex.isEmpty()) {
                recId = jdbc.queryForObject("INSERT INTO documento_recepciones(documento_id, receptor_id, dependencia_id, recibido_en, confirmado_por, estado, comentario) VALUES (?::uuid, NULL, ?::uuid, now(), ?::uuid, 'RECIBIDO', ?) RETURNING id::text", String.class, docId, dependenciaId, userId, comentario);
            } else {
                recId = String.valueOf(ex.get(0).get("id"));
                jdbc.update("UPDATE documento_recepciones SET recibido_en = COALESCE(recibido_en, now()), confirmado_por = ?::uuid, estado='RECIBIDO', comentario=COALESCE(?,comentario) WHERE id::text = ?", userId, comentario, recId);
            }
        }

        jdbc.update("UPDATE documentos SET estado='RECIBIDO', fecha_recepcion=COALESCE(fecha_recepcion, now()), actualizado_por=?::uuid, actualizado_en=now() WHERE id::text = ?", userId, docId);
        return Map.of("documento_id", docId, "recepcion_id", recId, "estado_documento", "RECIBIDO");
    }

    public List<Map<String, Object>> pendingReceptions(String entityCode, String receptorId) {
        StringBuilder where = new StringBuilder(" d.entidad_id = (SELECT id FROM entidades WHERE codigo = ?) ");
        List<Object> params = new ArrayList<>();
        params.add(entityCode);
        if (receptorId != null && !receptorId.isBlank()) { where.append(" AND r.receptor_id::text = ? "); params.add(receptorId); }
        return jdbc.queryForList("""
                SELECT r.id AS recepcion_id, r.estado AS estado_recepcion, r.receptor_id, r.recibido_en,
                       d.id AS documento_id, d.numero_oficial, d.estado AS estado_documento, d.asunto, d.fecha_elaboracion, d.fecha_emision
                FROM documento_recepciones r
                JOIN documentos d ON d.id = r.documento_id
                WHERE %s AND r.estado = 'PENDIENTE'
                ORDER BY d.fecha_emision DESC NULLS LAST, d.creado_en DESC
                """.formatted(where), params.toArray());
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

