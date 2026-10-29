# Ciclo de facturas SRI / XML / correo

Cambios del 29 de octubre de 2026. Alcance: facturas ERP en fec_factura y su cola
msvc-emails. No se modifican importes, recaudacion, claves ni secuenciales historicos.
No se redisenan los flujos de otros tipos de comprobante en documento_electronico.

## Fallos corregidos

- La autorizacion recuperada posteriormente ahora guarda un sobre XML con numero,
  fecha y comprobante del SRI y pasa a O (correo pendiente), en lugar de terminar en A
  sin correo. Lo mismo ocurre al recuperar devueltas M o mediante revision manual.
- Se utiliza el numero real de autorizacion; no se inventa a partir de la clave.
  El XML debe ser parseable y corresponder a la misma clave. Metadatos incompletos
  mantienen la factura pendiente. Se conserva la fecha SRI en America/Guayaquil.
- Se almacena el XML firmado enviado y si hubo confirmacion de recepcion. Tras un
  corte incierto se consulta primero autorizacion. Solo despues de tres consultas
  inconclusas previas, con respuesta efectiva sin autorizaciones, puede reenviarse
  el mismo XML firmado. Nunca se genera otra clave/secuencial. RECIBIDA, 43 y 70
  confirman recepcion y deshabilitan ese reenvio. Un timeout de consulta no lo habilita.
- Errores inesperados previos a recepcion vuelven a I con backoff. Datos invalidos
  quedan E, devoluciones M y no autorizaciones definitivas N para revision.
  Los I se seleccionan por vencimiento para que los antiguos no bloqueen el lote.
  P interrumpidas entran en recuperacion de autorizacion.
- Si el PDF falla, se conserva XML y estado O. Si el XML historico pendiente carece
  de metadatos de autorizacion, se consulta el SRI antes de reconstruir el RIDE.
- Se guarda email_message_id. Encolado no equivale a enviado: permanece O y
  mail_enviado=false hasta que GET /api/v1/emails/{id}/status devuelve SENT.
  Solo entonces pasa a A y mail_enviado=true. Si emails no responde no se encola otro.
- La correlacion factura:<id> se reutiliza al encolar. emails obtiene un bloqueo
  transaccional PostgreSQL por correlacion y devuelve el ID ya existente, incluso
  SENT. Esto evita duplicar el encolamiento si se pierde la respuesta HTTP.
  Una correlacion que ya tenia varios correos historicos requiere revision.
- DOC_ELECTRONICO continua reintentando fallos de envio con backoff hasta 1 hora;
  no se descarta al quinto intento. El resto conserva max-attempts. Adjuntos faltantes
  o lista negra quedan FAILED visibles para reparacion. Los archivos no se borran en fallos.
- Las llamadas HTTP entre servicios y al proveedor de correo tienen timeouts.
  Las alertas Telegram de emails siguen activables con la configuracion local.

## Aplicacion

Los cambios estan en codigo; no se han ejecutado migraciones ni reenvios en produccion.

1. Respaldar la BD y aplicar db/2026-10-29_factura_ciclo_confiable.sql y
   emails/src/main/resources/sql/2026-10-29_email_retry.sql antes de arrancar los JAR.
   Hibernate esta configurado con ddl-auto=none. Conservar las migraciones anteriores
   de intentos_autorizacion, fecha_ultimo_intento y respaldo content de adjuntos.
2. Corregir la cuenta SMTP usada en email_account. El log muestra 535 Access denied;
   los reintentos no sustituyen credenciales validas. MAIL_HOST no reemplaza esa cuenta.
3. Desde la raiz:

   ```powershell
   docker compose --env-file .env --env-file .env.telegram.local up -d --build msvc-emails msvc-sri
   ```

   .env.telegram.local esta excluido de Git y contiene la configuracion local del bot.
   En el servidor crear el equivalente y proteger sus permisos.
4. Ejecutar db/2026-10-29_auditoria_ciclo_facturas.sql ajustando el rango de fechas.
   Revisar E/M/N, falta de XML, correos FAILED y estados historicos sin trazabilidad.
5. Para correos FAILED antiguos por agotamiento, corregir la causa y reintentar por
   POST /api/v1/emails/{id}/retry. Verificar primero si el proveedor ya acepto el envio.
   Para adjuntos perdidos comprobar content en email_attachment o regenerarlos desde XML.
   No ejecutar un reenvio masivo de A historicos sin conciliar: antes no se registraba
   correctamente mail_enviado y podrian haberse enviado.
6. En una factura nueva verificar recepcion SRI, XML completo, email_message_id y
   finalmente A + mail_enviado=true + email_message.status=SENT. Simular una caida de
   emails y comprobar O sin duplicados, luego restaurar y verificar la confirmacion.

## Validacion y limites

Pruebas Maven de ambos modulos sin arrancar aplicaciones ni conectarse a produccion:

```powershell
mvn -pl emails,sri-files -am test '-Dtest=*Test,!*ApplicationTests' '-Dsurefire.failIfNoSpecifiedTests=false'
```

Incluyen fallos de red, autorizacion tardia, XML de otra clave, metadatos faltantes,
reintento de recepcion incierta, recepcion confirmada sin reenvio, recuperacion manual,
correo encolado sin confirmacion, fallo de consulta de estado e idempotencia de encolamiento.
No se ejecutaron pruebas contra un PostgreSQL aislado: el motor Docker local no esta
iniciado. Las pruebas de bloqueo/idempotencia usan dobles; verificar concurrencia con
PostgreSQL en preproduccion antes de escalar replicas.

El objetivo operativo es que ninguna factura quede olvidada y cada etapa tenga evidencia.
No se garantiza 100% de autorizaciones si SRI rechaza datos, faltan certificados, existe
una caida permanente o el XML historico ya no esta disponible. Estos casos requieren
intervencion y deben aparecer en auditoria. Las P/C historicas sin XML firmado conservado
se consultan, pero no se reconstruyen y reenvian automaticamente sin conciliacion.
SENT confirma aceptacion SMTP/API; la entrega en bandeja requiere eventos de rebote/
entrega del proveedor, aun no implementados. Una caida entre aceptacion SMTP y persistir
SENT puede producir un envio duplicado al recuperar la reserva; SMTP no garantiza
exactamente una vez. Las reservas de correo duran 15 minutos y la recuperacion opera
por lotes, con las frecuencias configuradas. Las alertas Telegram no son una cola durable.

Referencia SRI: https://www.sri.gob.ec/nl-BE/facturacion-electronica
