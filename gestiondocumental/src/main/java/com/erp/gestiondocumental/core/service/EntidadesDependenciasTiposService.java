package com.erp.gestiondocumental.core.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EntidadesDependenciasTiposService {

    private final JdbcTemplate jdbc;

    public EntidadesDependenciasTiposService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> listEntities(String q) {
        if (q == null || q.isBlank()) {
            return jdbc.queryForList("""
                    SELECT id, codigo, nombre, activo
                    FROM entidades
                    ORDER BY codigo
                    """);
        }
        String like = "%" + q + "%";
        return jdbc.queryForList("""
                SELECT id, codigo, nombre, activo
                FROM entidades
                WHERE codigo ILIKE ? OR nombre ILIKE ?
                ORDER BY codigo
                """, like, like);
    }

    public List<Map<String, Object>> listDependencies(String entityCode) {
        return jdbc.queryForList("""
                SELECT d.id, d.entidad_id, d.codigo, d.nombre, d.padre_id, d.activo
                FROM dependencias d
                JOIN entidades e ON e.id = d.entidad_id
                WHERE e.codigo = ?
                ORDER BY d.codigo
                """, entityCode);
    }

    public List<Map<String, Object>> listDocumentTypes(String entityCode) {
        return jdbc.queryForList("""
                SELECT t.id, t.entidad_id, t.codigo, t.nombre, t.flujo, t.activo
                FROM tipos_documento t
                JOIN entidades e ON e.id = t.entidad_id
                WHERE e.codigo = ?
                ORDER BY t.codigo
                """, entityCode);
    }

    public String createEntity(String codigo, String nombre, boolean activo) {
        return jdbc.queryForObject("""
                INSERT INTO entidades (codigo, nombre, activo)
                VALUES (?, ?, ?)
                RETURNING id::text
                """, String.class, codigo, nombre, activo);
    }

    public int updateEntity(String id, String codigo, String nombre) {
        return jdbc.update("""
                UPDATE entidades
                SET codigo = ?, nombre = ?, actualizado_en = now()
                WHERE id::text = ?
                """, codigo, nombre, id);
    }

    public int setEntityStatus(String id, boolean active) {
        return jdbc.update("""
                UPDATE entidades
                SET activo = ?, actualizado_en = now()
                WHERE id::text = ?
                """, active, id);
    }

    public String createDependency(String entityCode, String codigo, String nombre, String padreId) {
        String entidadId = jdbc.queryForObject("SELECT id::text FROM entidades WHERE codigo = ?", String.class, entityCode);
        return jdbc.queryForObject("""
                INSERT INTO dependencias (entidad_id, codigo, nombre, padre_id, activo)
                VALUES (?::uuid, ?, ?, ?::uuid, TRUE)
                RETURNING id::text
                """, String.class, entidadId, codigo, nombre, padreId);
    }

    public int updateDependency(String id, String codigo, String nombre, String padreId) {
        return jdbc.update("""
                UPDATE dependencias
                SET codigo = ?, nombre = ?, padre_id = ?::uuid, actualizado_en = now()
                WHERE id::text = ?
                """, codigo, nombre, padreId, id);
    }

    public int setDependencyStatus(String id, boolean active) {
        return jdbc.update("""
                UPDATE dependencias
                SET activo = ?, actualizado_en = now()
                WHERE id::text = ?
                """, active, id);
    }

    public String createDocumentType(String entityCode, String codigo, String nombre, String flujo, boolean activo) {
        String entidadId = jdbc.queryForObject("SELECT id::text FROM entidades WHERE codigo = ?", String.class, entityCode);
        return jdbc.queryForObject("""
                INSERT INTO tipos_documento (entidad_id, codigo, nombre, flujo, activo)
                VALUES (?::uuid, ?, ?, ?, ?)
                RETURNING id::text
                """, String.class, entidadId, codigo, nombre, flujo, activo);
    }

    public int updateDocumentType(String id, String codigo, String nombre, String flujo) {
        return jdbc.update("""
                UPDATE tipos_documento
                SET codigo = ?, nombre = ?, flujo = ?, actualizado_en = now()
                WHERE id::text = ?
                """, codigo, nombre, flujo, id);
    }

    public int setDocumentTypeStatus(String id, boolean active) {
        return jdbc.update("""
                UPDATE tipos_documento
                SET activo = ?, actualizado_en = now()
                WHERE id::text = ?
                """, active, id);
    }

    public Map<String, Object> lookupUsers(String entityCode, String q, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        String qq = (q == null || q.isBlank()) ? null : q;

        Integer total = jdbc.queryForObject("""
                SELECT COUNT(*)
                FROM usuarios u
                JOIN entidades e ON e.id = u.entidad_id
                LEFT JOIN personas p ON p.id = u.persona_id
                WHERE e.codigo = ?
                  AND (? IS NULL OR u.username ILIKE ('%' || ? || '%') OR p.nombres ILIKE ('%' || ? || '%') OR p.apellidos ILIKE ('%' || ? || '%'))
                """, Integer.class, entityCode, qq, qq, qq, qq);

        List<Map<String, Object>> items = jdbc.queryForList("""
                SELECT u.id, u.username, u.activo,
                       p.id AS persona_id,
                       COALESCE(p.nombres || ' ' || p.apellidos, u.username) AS nombre,
                       p.email,
                       d.id AS dependencia_id,
                       d.codigo AS dependencia_codigo,
                       d.nombre AS dependencia_nombre
                FROM usuarios u
                JOIN entidades e ON e.id = u.entidad_id
                LEFT JOIN personas p ON p.id = u.persona_id
                LEFT JOIN dependencias d ON d.id = p.dependencia_id
                WHERE e.codigo = ?
                  AND (? IS NULL OR u.username ILIKE ('%' || ? || '%') OR p.nombres ILIKE ('%' || ? || '%') OR p.apellidos ILIKE ('%' || ? || '%'))
                ORDER BY u.activo DESC, nombre ASC
                OFFSET ? LIMIT ?
                """, entityCode, qq, qq, qq, qq, offset, pageSize);

        return Map.of("items", items, "total", total == null ? 0 : total, "page", page, "page_size", pageSize);
    }

    public Map<String, Object> lookupPersons(String entityCode, String q, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        String qq = (q == null || q.isBlank()) ? null : q;

        Integer total = jdbc.queryForObject("""
                SELECT COUNT(*)
                FROM personas p
                JOIN entidades e ON e.id = p.entidad_id
                WHERE e.codigo = ?
                  AND (? IS NULL OR p.nombres ILIKE ('%' || ? || '%') OR p.apellidos ILIKE ('%' || ? || '%') OR p.identificacion ILIKE ('%' || ? || '%'))
                """, Integer.class, entityCode, qq, qq, qq, qq);

        List<Map<String, Object>> items = jdbc.queryForList("""
                SELECT p.id, p.identificacion, p.nombres, p.apellidos, p.cargo, p.email, p.activo,
                       d.id AS dependencia_id, d.codigo AS dependencia_codigo, d.nombre AS dependencia_nombre
                FROM personas p
                JOIN entidades e ON e.id = p.entidad_id
                LEFT JOIN dependencias d ON d.id = p.dependencia_id
                WHERE e.codigo = ?
                  AND (? IS NULL OR p.nombres ILIKE ('%' || ? || '%') OR p.apellidos ILIKE ('%' || ? || '%') OR p.identificacion ILIKE ('%' || ? || '%'))
                ORDER BY p.activo DESC, p.apellidos ASC, p.nombres ASC
                OFFSET ? LIMIT ?
                """, entityCode, qq, qq, qq, qq, offset, pageSize);

        return Map.of("items", items, "total", total == null ? 0 : total, "page", page, "page_size", pageSize);
    }
}


