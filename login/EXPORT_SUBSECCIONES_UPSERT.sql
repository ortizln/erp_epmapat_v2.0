-- =============================================
-- EXPORT + UPSERT de secciones/subsecciones (erpsecciones)
-- =============================================

-- 1) GENERAR INSERTS desde lo existente (copiar resultado y guardar como backup)
SELECT
  'INSERT INTO erpsecciones (iderpmodulo_erpmodulos, codigo, descripcion, ruta, orden, platform, activo) VALUES (' ||
  e.iderpmodulo_erpmodulos || ', ' ||
  quote_literal(e.codigo) || ', ' ||
  quote_literal(e.descripcion) || ', ' ||
  COALESCE(quote_literal(e.ruta), 'NULL') || ', ' ||
  COALESCE(e.orden::text, '0') || ', ' ||
  quote_literal(COALESCE(e.platform,'WEB')) || ', ' ||
  COALESCE(e.activo::text, 'true') ||
  ');'
AS sql_insert
FROM erpsecciones e
ORDER BY e.iderpmodulo_erpmodulos, e.orden, e.iderpseccion;


-- 2) UPSERT recomendado (actualiza si ya existe por modulo+codigo)
-- Requiere índice único (ya creado en ACCESS_CONTROL_SECCIONES.sql):
--   ux_erpsecciones_modulo_codigo (iderpmodulo_erpmodulos, codigo)

-- Ejemplo plantilla:
-- INSERT INTO erpsecciones (iderpmodulo_erpmodulos, codigo, descripcion, ruta, orden, platform, activo)
-- VALUES
--   (12, 'GD_CFG.SYSTEM', 'Configuración sistema', '/gd/settings/system', 10, 'WEB', true),
--   (12, 'GD_CFG.CCD', 'Configuración CCD', '/gd/settings/ccd', 20, 'WEB', true)
-- ON CONFLICT (iderpmodulo_erpmodulos, codigo)
-- DO UPDATE SET
--   descripcion = EXCLUDED.descripcion,
--   ruta        = EXCLUDED.ruta,
--   orden       = EXCLUDED.orden,
--   platform    = EXCLUDED.platform,
--   activo      = EXCLUDED.activo;


-- 3) Query útil para revisar subsecciones jerárquicas por prefijo (ej: GD_CFG.)
-- SELECT *
-- FROM erpsecciones
-- WHERE codigo LIKE 'GD_CFG.%'
-- ORDER BY orden, codigo;
