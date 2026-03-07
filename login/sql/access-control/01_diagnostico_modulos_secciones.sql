-- Diagnóstico de módulos/secciones para control de acceso

-- 1) Catálogo de módulos
SELECT iderpmodulo, descripcion, COALESCE(platform,'BOTH') AS platform
FROM erpmodulos
ORDER BY iderpmodulo;

-- 2) Módulos sin secciones
SELECT m.iderpmodulo, m.descripcion, COALESCE(m.platform,'BOTH') AS platform
FROM erpmodulos m
LEFT JOIN (
  SELECT DISTINCT iderpmodulo_erpmodulos
  FROM erpsecciones
) s ON s.iderpmodulo_erpmodulos = m.iderpmodulo
WHERE s.iderpmodulo_erpmodulos IS NULL
ORDER BY m.iderpmodulo;

-- 3) Matriz módulo -> secciones
SELECT m.iderpmodulo, m.descripcion AS modulo, m.platform AS modulo_platform,
       s.iderpseccion, s.codigo, s.descripcion AS seccion, s.ruta, s.orden,
       s.platform AS seccion_platform, s.activo
FROM erpmodulos m
LEFT JOIN erpsecciones s ON s.iderpmodulo_erpmodulos = m.iderpmodulo
ORDER BY m.iderpmodulo, s.orden, s.codigo;
