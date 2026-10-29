-- Solo lectura. Ejecutar despues de migrar. Configuracion actual: SRI y emails comparten BD.
-- Ajustar el rango de fechas segun el lote que se desea conciliar.
BEGIN TRANSACTION READ ONLY;
SELECT upper(trim(estado)) AS estado, count(*) AS total,
       count(*) FILTER (WHERE nullif(trim(xmlautorizado),'') IS NOT NULL) AS xml_almacenado,
       count(*) FILTER (WHERE email_message_id IS NOT NULL) AS con_id_correo,
       count(*) FILTER (WHERE mail_enviado IS TRUE) AS envio_confirmado,
       count(*) FILTER (WHERE mail_enviado IS NULL) AS sin_confirmacion_historica
FROM public.fec_factura
WHERE fechaemision >= DATE '2026-10-01' AND fechaemision < DATE '2026-11-01'
GROUP BY upper(trim(estado)) ORDER BY estado;

SELECT f.idfactura, f.claveacceso, f.estado, f.recepcion_confirmada,
       (nullif(trim(f.xmlautorizado),'') IS NOT NULL) AS xml_almacenado,
       f.email_message_id, f.mail_enviado, m.status AS estado_correo,
       f.intentos_autorizacion, f.fecha_ultimo_intento,
       f.errores AS error_factura, m.last_error AS error_correo
FROM public.fec_factura f
LEFT JOIN public.email_message m ON m.id = f.email_message_id
WHERE f.fechaemision >= DATE '2026-10-01' AND f.fechaemision < DATE '2026-11-01'
  AND (nullif(trim(f.xmlautorizado),'') IS NULL OR f.mail_enviado IS DISTINCT FROM TRUE)
ORDER BY f.idfactura;
-- xml_almacenado no certifica validez XML: el flujo nuevo verifica clave y metadatos antes de almacenarlo.
COMMIT;
