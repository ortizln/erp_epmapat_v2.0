-- Seed inicial de secciones por módulo
-- Requiere: ACCESS_CONTROL_SECCIONES.sql aplicado previamente

-- =========================
-- Gestión Documental
-- =========================
WITH m AS (
  SELECT iderpmodulo
  FROM erpmodulos
  WHERE UPPER(descripcion) LIKE '%GESTION DOCUMENTAL%'
  ORDER BY iderpmodulo
  LIMIT 1
)
INSERT INTO erpsecciones (iderpmodulo_erpmodulos, codigo, descripcion, ruta, orden, platform, activo)
SELECT m.iderpmodulo, s.codigo, s.descripcion, s.ruta, s.orden, 'WEB', TRUE
FROM m
CROSS JOIN (
  VALUES
    ('GD_DASHBOARD', 'Dashboard', '/gd/dashboard', 10),
    ('GD_DOCUMENTOS', 'Documentos', '/gd/documentos', 20),
    ('GD_INBOX', 'Bandeja', '/gd/inbox', 30),
    ('GD_ALERTS', 'Alertas', '/gd/alerts', 40),
    ('GD_EXPEDIENTES', 'Expedientes', '/gd/case-files', 50),
    ('GD_CFG_SYSTEM', 'Configuración - Sistema', '/gd/settings/system', 60),
    ('GD_CFG_CCD', 'Configuración - CCD', '/gd/settings/ccd', 70),
    ('GD_CFG_TRD', 'Configuración - TRD', '/gd/settings/trd', 80),
    ('GD_CFG_TIPOS', 'Configuración - Tipos de documento', '/gd/settings/document-types', 90),
    ('GD_PERFIL', 'Mi Perfil GD', '/gd/my-profile', 100)
) AS s(codigo, descripcion, ruta, orden)
WHERE NOT EXISTS (
  SELECT 1
  FROM erpsecciones e
  WHERE e.iderpmodulo_erpmodulos = m.iderpmodulo
    AND e.codigo = s.codigo
);

-- =========================
-- Administración Central
-- =========================
WITH m AS (
  SELECT iderpmodulo
  FROM erpmodulos
  WHERE UPPER(descripcion) LIKE '%ADMINISTRACION CENTRAL%'
  ORDER BY iderpmodulo
  LIMIT 1
)
INSERT INTO erpsecciones (iderpmodulo_erpmodulos, codigo, descripcion, ruta, orden, platform, activo)
SELECT m.iderpmodulo, s.codigo, s.descripcion, s.ruta, s.orden, 'WEB', TRUE
FROM m
CROSS JOIN (
  VALUES
    ('AC_UTIL_DOCS', 'Utilería - Documentos de respaldo', '/documentos', 10),
    ('AC_UTIL_USUARIOS', 'Utilería - Usuarios', '/usuarios', 20),
    ('AC_UTIL_REPORTES', 'Utilería - Cargar Reportes', '/reportesjr', 30),
    ('AC_UTIL_ACCESS', 'Utilería - Accesos y permisos', '/admin/access-control', 35),
    ('AC_SRI_COMPROB', 'SRI - Comprobantes SRI', '/tabla4', 40),
    ('AC_SRI_FIRMA', 'SRI - Firma Electrónica', '/definir', 50)
) AS s(codigo, descripcion, ruta, orden)
WHERE NOT EXISTS (
  SELECT 1
  FROM erpsecciones e
  WHERE e.iderpmodulo_erpmodulos = m.iderpmodulo
    AND e.codigo = s.codigo
);

-- =========================
-- Opcional: habilitar secciones para usuario admin (id=1)
-- =========================
INSERT INTO usrxsecciones (idusuario_usuarios, iderpseccion_erpsecciones, enabled)
SELECT 1, e.iderpseccion, TRUE
FROM erpsecciones e
WHERE NOT EXISTS (
  SELECT 1 FROM usrxsecciones u
  WHERE u.idusuario_usuarios = 1
    AND u.iderpseccion_erpsecciones = e.iderpseccion
);
