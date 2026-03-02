-- TH Actions (Talento Humano)
CREATE TABLE IF NOT EXISTS th_actions (
    idaction BIGSERIAL PRIMARY KEY,
    idpersonal_personal BIGINT NOT NULL,
    tipoaccion VARCHAR(40) NOT NULL,
    motivo VARCHAR(300),
    observacion VARCHAR(600),
    fecvigencia DATE NOT NULL,
    feccrea DATE NOT NULL DEFAULT CURRENT_DATE,
    usucrea BIGINT,
    fecmodi DATE,
    usumodi BIGINT,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_th_actions_personal
        FOREIGN KEY (idpersonal_personal) REFERENCES personal(idpersonal)
);

CREATE INDEX IF NOT EXISTS idx_th_actions_personal ON th_actions(idpersonal_personal);
CREATE INDEX IF NOT EXISTS idx_th_actions_tipo ON th_actions(tipoaccion);
