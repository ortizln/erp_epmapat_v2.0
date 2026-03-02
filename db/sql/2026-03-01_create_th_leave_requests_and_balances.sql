-- TH Leave Requests + Balances

CREATE TABLE IF NOT EXISTS th_leave_balances (
    idbalance BIGSERIAL PRIMARY KEY,
    idpersonal_personal BIGINT NOT NULL,
    anio INTEGER NOT NULL,
    dias_asignados NUMERIC(6,2) NOT NULL DEFAULT 0,
    dias_usados NUMERIC(6,2) NOT NULL DEFAULT 0,
    dias_disponibles NUMERIC(6,2) NOT NULL DEFAULT 0,
    feccrea DATE NOT NULL DEFAULT CURRENT_DATE,
    usucrea BIGINT,
    fecmodi DATE,
    usumodi BIGINT,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_th_leave_balances_personal
        FOREIGN KEY (idpersonal_personal) REFERENCES personal(idpersonal),
    CONSTRAINT uq_th_leave_balances_personal_anio
        UNIQUE (idpersonal_personal, anio)
);

CREATE INDEX IF NOT EXISTS idx_th_leave_balances_personal
    ON th_leave_balances(idpersonal_personal);

CREATE TABLE IF NOT EXISTS th_leave_requests (
    idrequest BIGSERIAL PRIMARY KEY,
    idpersonal_personal BIGINT NOT NULL,
    tipolicencia VARCHAR(30) NOT NULL,  -- VACACION, PERMISO, LICENCIA
    fechainicio DATE NOT NULL,
    fechafin DATE NOT NULL,
    dias_solicitados NUMERIC(6,2) NOT NULL DEFAULT 0,
    motivo VARCHAR(500),
    estado VARCHAR(20) NOT NULL DEFAULT 'SOLICITADA', -- SOLICITADA/APROBADA/RECHAZADA/ANULADA
    aprobador_id BIGINT,
    fecha_aprobacion DATE,
    observacion_aprobacion VARCHAR(500),
    feccrea DATE NOT NULL DEFAULT CURRENT_DATE,
    usucrea BIGINT,
    fecmodi DATE,
    usumodi BIGINT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_th_leave_requests_personal
        FOREIGN KEY (idpersonal_personal) REFERENCES personal(idpersonal),
    CONSTRAINT ck_th_leave_requests_fechas
        CHECK (fechainicio <= fechafin)
);

CREATE INDEX IF NOT EXISTS idx_th_leave_requests_personal
    ON th_leave_requests(idpersonal_personal);

CREATE INDEX IF NOT EXISTS idx_th_leave_requests_estado
    ON th_leave_requests(estado);
