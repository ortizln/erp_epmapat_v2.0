-- TH Audit Log
CREATE TABLE IF NOT EXISTS th_audit_log (
  idaudit BIGSERIAL PRIMARY KEY,
  entidad VARCHAR(80) NOT NULL,
  idregistro BIGINT,
  accion VARCHAR(30) NOT NULL,
  detalle VARCHAR(1000),
  usuario BIGINT,
  fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_th_audit_entidad ON th_audit_log(entidad);
CREATE INDEX IF NOT EXISTS idx_th_audit_fecha ON th_audit_log(fecha);
