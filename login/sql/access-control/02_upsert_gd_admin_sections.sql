-- UPSERT de secciones para Gestión Documental + Administración Central

-- Gestión Documental
WITH m AS (
  SELECT iderpmodulo
  FROM erpmodulos
  WHERE UPPER(descripcion) LIKE '%GESTION DOCUMENTAL%'
  ORDER BY iderpmodulo
  LIMIT 1
)
INSERT INTO erpsecciones
(iderpmodulo_erpmodulos, codigo, descripcion, ruta, orden, platform, activo)
SELECT m.iderpmodulo, x.codigo, x.descripcion, x.ruta, x.orden, x.platform, x.activo
FROM m
JOIN (
  VALUES
    ('GD_DASHBOARD',    'Dashboard',                                '/gd/dashboard',               10, 'WEB',  true),
    ('GD_DOCUMENTOS',   'Documentos',                               '/gd/documentos',              20, 'WEB',  true),
    ('GD_INBOX',        'Bandeja',                                  '/gd/inbox',                   30, 'WEB',  true),
    ('GD_ALERTS',       'Alertas',                                  '/gd/alerts',                  40, 'WEB',  true),
    ('GD_EXPEDIENTES',  'Expedientes',                              '/gd/case-files',              50, 'WEB',  true),
    ('GD_CFG',          'Configuración',                            null,                          60, 'WEB',  true),
    ('GD_CFG.SYSTEM',   'Configuración - Sistema',                  '/gd/settings/system',         61, 'WEB',  true),
    ('GD_CFG.CCD',      'Configuración - CCD',                      '/gd/settings/ccd',            62, 'WEB',  true),
    ('GD_CFG.TRD',      'Configuración - TRD',                      '/gd/settings/trd',            63, 'WEB',  true),
    ('GD_CFG.TIPOS',    'Configuración - Tipos de documento',       '/gd/settings/document-types', 64, 'WEB',  true),
    ('GD_PERFIL',       'Mi Perfil GD',                             '/gd/my-profile',              70, 'WEB',  true)
) AS x(codigo, descripcion, ruta, orden, platform, activo) ON TRUE
ON CONFLICT (iderpmodulo_erpmodulos, codigo)
DO UPDATE SET
  descripcion = EXCLUDED.descripcion,
  ruta        = EXCLUDED.ruta,
  orden       = EXCLUDED.orden,
  platform    = EXCLUDED.platform,
  activo      = EXCLUDED.activo;

-- Administración Central
WITH m AS (
  SELECT iderpmodulo
  FROM erpmodulos
  WHERE UPPER(descripcion) LIKE '%ADMINISTRACION CENTRAL%'
  ORDER BY iderpmodulo
  LIMIT 1
)
INSERT INTO erpsecciones
(iderpmodulo_erpmodulos, codigo, descripcion, ruta, orden, platform, activo)
SELECT m.iderpmodulo, x.codigo, x.descripcion, x.ruta, x.orden, x.platform, x.activo
FROM m
JOIN (
  VALUES
    ('AC_UTIL',          'Utilería',                                null,                    10, 'WEB', true),
    ('AC_UTIL_DOCS',     'Utilería - Documentos de respaldo',       '/documentos',           11, 'WEB', true),
    ('AC_UTIL_USUARIOS', 'Utilería - Usuarios',                     '/usuarios',             12, 'WEB', true),
    ('AC_UTIL_REPORTES', 'Utilería - Cargar Reportes',              '/reportesjr',           13, 'WEB', true),
    ('AC_UTIL_ACCESS',   'Utilería - Accesos y permisos',           '/admin/access-control', 14, 'WEB', true),
    ('AC_SRI',           'SRI',                                     null,                    20, 'WEB', true),
    ('AC_SRI_COMPROB',   'SRI - Comprobantes SRI',                  '/tabla4',               21, 'WEB', true),
    ('AC_SRI_FIRMA',     'SRI - Firma Electrónica',                 '/definir',              22, 'WEB', true)
) AS x(codigo, descripcion, ruta, orden, platform, activo) ON TRUE
ON CONFLICT (iderpmodulo_erpmodulos, codigo)
DO UPDATE SET
  descripcion = EXCLUDED.descripcion,
  ruta        = EXCLUDED.ruta,
  orden       = EXCLUDED.orden,
  platform    = EXCLUDED.platform,
  activo      = EXCLUDED.activo;
