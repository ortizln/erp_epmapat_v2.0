-- Control de acceso por secciones (msvc-login)

CREATE TABLE IF NOT EXISTS erpsecciones (
  iderpseccion BIGSERIAL PRIMARY KEY,
  iderpmodulo_erpmodulos BIGINT NOT NULL REFERENCES erpmodulos(iderpmodulo) ON DELETE CASCADE,
  codigo VARCHAR(80) NOT NULL,
  descripcion VARCHAR(200) NOT NULL,
  ruta VARCHAR(250),
  orden INT DEFAULT 0,
  platform VARCHAR(20) DEFAULT 'WEB',
  activo BOOLEAN DEFAULT TRUE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_erpsecciones_modulo_codigo
  ON erpsecciones (iderpmodulo_erpmodulos, codigo);

CREATE TABLE IF NOT EXISTS usrxsecciones (
  idusrxseccion BIGSERIAL PRIMARY KEY,
  idusuario_usuarios BIGINT NOT NULL REFERENCES usuarios(idusuario) ON DELETE CASCADE,
  iderpseccion_erpsecciones BIGINT NOT NULL REFERENCES erpsecciones(iderpseccion) ON DELETE CASCADE,
  enabled BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_usrxsecciones_user_section
  ON usrxsecciones (idusuario_usuarios, iderpseccion_erpsecciones);
