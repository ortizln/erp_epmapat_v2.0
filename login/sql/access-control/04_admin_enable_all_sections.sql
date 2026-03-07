-- Habilitar todas las secciones al admin (idusuario=1)

INSERT INTO usrxsecciones (idusuario_usuarios, iderpseccion_erpsecciones, enabled)
SELECT 1, s.iderpseccion, TRUE
FROM erpsecciones s
WHERE NOT EXISTS (
  SELECT 1
  FROM usrxsecciones u
  WHERE u.idusuario_usuarios = 1
    AND u.iderpseccion_erpsecciones = s.iderpseccion
);
