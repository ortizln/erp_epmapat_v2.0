-- TH Employee Files (metadata)
CREATE TABLE IF NOT EXISTS th_employee_files (
  idfile BIGSERIAL PRIMARY KEY,
  idpersonal_personal BIGINT NOT NULL,
  tipo_doc VARCHAR(50) NOT NULL,
  nombre_archivo VARCHAR(200) NOT NULL,
  ruta_archivo VARCHAR(500) NOT NULL,
  hash_archivo VARCHAR(128),
  version_doc INTEGER NOT NULL DEFAULT 1,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  feccrea DATE NOT NULL DEFAULT CURRENT_DATE,
  usucrea BIGINT,
  fecmodi DATE,
  usumodi BIGINT,
  CONSTRAINT fk_th_employee_files_personal
    FOREIGN KEY (idpersonal_personal) REFERENCES personal(idpersonal)
);

CREATE INDEX IF NOT EXISTS idx_th_employee_files_personal ON th_employee_files(idpersonal_personal);
CREATE INDEX IF NOT EXISTS idx_th_employee_files_tipo ON th_employee_files(tipo_doc);
