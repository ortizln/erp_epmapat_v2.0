-- Normaliza columna clientes.activo para evitar errores de Hibernate
-- Error observado: "Can not set boolean field ... Clientes.activo to null value"

BEGIN;

-- 1) Limpieza de datos existentes
UPDATE clientes
SET activo = false
WHERE activo IS NULL;

-- 2) Default para nuevos registros
ALTER TABLE clientes
  ALTER COLUMN activo SET DEFAULT false;

-- 3) Restricción para evitar nulos en adelante
ALTER TABLE clientes
  ALTER COLUMN activo SET NOT NULL;

COMMIT;
