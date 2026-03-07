-- Generador automático de secciones base para todos los módulos
-- Crea ROOT/LIST/NEW/EDIT y evita duplicados

BEGIN;

CREATE UNIQUE INDEX IF NOT EXISTS ux_erpsecciones_modulo_codigo
  ON erpsecciones (iderpmodulo_erpmodulos, codigo);

WITH modulos_objetivo AS (
  SELECT m.iderpmodulo,
         m.descripcion,
         UPPER(COALESCE(m.platform, 'BOTH')) AS platform
  FROM erpmodulos m
  WHERE NOT EXISTS (
    SELECT 1
    FROM erpsecciones s
    WHERE s.iderpmodulo_erpmodulos = m.iderpmodulo
      AND (
        s.codigo LIKE ('MOD_' || m.iderpmodulo || '.LIST')
        OR s.codigo LIKE ('MOD_' || m.iderpmodulo || '.NEW')
        OR s.codigo LIKE ('MOD_' || m.iderpmodulo || '.EDIT')
      )
  )
)
INSERT INTO erpsecciones
(iderpmodulo_erpmodulos, codigo, descripcion, ruta, orden, platform, activo)
SELECT
  mo.iderpmodulo,
  'MOD_' || mo.iderpmodulo,
  mo.descripcion || ' (Raíz)',
  NULL,
  1,
  mo.platform,
  TRUE
FROM modulos_objetivo mo
ON CONFLICT (iderpmodulo_erpmodulos, codigo)
DO UPDATE SET
  descripcion = EXCLUDED.descripcion,
  platform    = EXCLUDED.platform,
  activo      = EXCLUDED.activo;

WITH modulos_objetivo AS (
  SELECT m.iderpmodulo,
         m.descripcion,
         UPPER(COALESCE(m.platform, 'BOTH')) AS platform
  FROM erpmodulos m
)
INSERT INTO erpsecciones
(iderpmodulo_erpmodulos, codigo, descripcion, ruta, orden, platform, activo)
SELECT
  mo.iderpmodulo,
  x.codigo,
  x.descripcion,
  x.ruta,
  x.orden,
  mo.platform,
  TRUE
FROM modulos_objetivo mo
JOIN LATERAL (
  VALUES
    ('MOD_' || mo.iderpmodulo || '.LIST', 'Listado - ' || mo.descripcion, NULL, 10),
    ('MOD_' || mo.iderpmodulo || '.NEW',  'Nuevo - '   || mo.descripcion, NULL, 20),
    ('MOD_' || mo.iderpmodulo || '.EDIT', 'Editar - '  || mo.descripcion, NULL, 30)
) x(codigo, descripcion, ruta, orden) ON TRUE
ON CONFLICT (iderpmodulo_erpmodulos, codigo)
DO UPDATE SET
  descripcion = EXCLUDED.descripcion,
  orden       = EXCLUDED.orden,
  platform    = EXCLUDED.platform,
  activo      = EXCLUDED.activo;

COMMIT;
