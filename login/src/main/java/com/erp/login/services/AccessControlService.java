package com.erp.login.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccessControlService {
    private final JdbcTemplate jdbc;

    public AccessControlService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> getProfile(Long idusuario, String platform) {
        String pf = (platform == null || platform.isBlank()) ? "WEB" : platform.trim().toUpperCase();

        List<Map<String, Object>> modulos = jdbc.queryForList("""
                SELECT m.iderpmodulo,
                       m.descripcion,
                       m.platform,
                       COALESCE(um.enabled, false) AS enabled
                FROM erpmodulos m
                LEFT JOIN usrxmodulos um
                  ON um.iderpmodulo_erpmodulos = m.iderpmodulo
                 AND um.idusuario_usuarios = ?
                 AND UPPER(COALESCE(um.platform,'BOTH')) IN (UPPER(?), 'BOTH')
                WHERE UPPER(COALESCE(m.platform,'BOTH')) IN (UPPER(?), 'BOTH')
                ORDER BY m.iderpmodulo
                """, idusuario, pf, pf);

        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> m : modulos) {
            Long idModulo = ((Number) m.get("iderpmodulo")).longValue();
            List<Map<String, Object>> secciones = jdbc.queryForList("""
                    SELECT s.iderpseccion,
                           s.iderpmodulo_erpmodulos AS iderpmodulo,
                           s.codigo,
                           s.descripcion,
                           s.ruta,
                           s.orden,
                           s.platform,
                           COALESCE(us.enabled, false) AS enabled
                    FROM erpsecciones s
                    LEFT JOIN usrxsecciones us
                      ON us.iderpseccion_erpsecciones = s.iderpseccion
                     AND us.idusuario_usuarios = ?
                    WHERE s.iderpmodulo_erpmodulos = ?
                      AND UPPER(COALESCE(s.platform,'BOTH')) IN (UPPER(?), 'BOTH')
                    ORDER BY s.orden, s.iderpseccion
                    """, idusuario, idModulo, pf);

            Map<String, Object> row = new HashMap<>();
            row.put("iderpmodulo", idModulo);
            row.put("descripcion", m.get("descripcion"));
            row.put("platform", m.get("platform"));
            row.put("enabled", m.get("enabled"));
            row.put("secciones", secciones);
            out.add(row);
        }

        return out;
    }

    public int saveUserSection(Long idusuario, Long idseccion, Boolean enabled) {
        return jdbc.update("""
                INSERT INTO usrxsecciones(idusuario_usuarios, iderpseccion_erpsecciones, enabled)
                VALUES (?::bigint, ?::bigint, COALESCE(?, false))
                ON CONFLICT (idusuario_usuarios, iderpseccion_erpsecciones)
                DO UPDATE SET enabled = EXCLUDED.enabled
                """, idusuario, idseccion, enabled);
    }
}
