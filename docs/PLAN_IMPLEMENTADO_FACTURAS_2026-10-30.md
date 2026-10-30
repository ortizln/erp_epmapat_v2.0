# Facturas SRI / RIDE / correo: implementacion del plan

Fecha: 30 de octubre de 2026. Repositorios: microservicesEpmapa-T, erp_epmapat_be,
erp_epmapat_fe. Alcance principal: facturas ERP almacenadas en public.fec_factura.
Los otros tipos de documento requieren sus propios adaptadores. No se alteran
importes, recaudacion, claves originales ni secuenciales.

## Arquitectura y compatibilidad

1. El preparador valida, firma y confirma el XML firmado en una transaccion corta.
2. Otra transaccion registra P (intencion de despacho), antes de usar la red.
   El POST SOAP ocurre sin mantener una transaccion abierta. Una caida deja P
   y el mismo XML firmado para consultar primero la autorizacion.
3. RECIBIDA / 43 / 70 habilitan consulta, nunca nueva clave. Una recepcion incierta
   consulta antes de reenviar exactamente el XML conservado; un timeout de consulta
   no demuestra ausencia de recepcion. Los rechazos definitivos permanecen visibles.
4. Autorizacion tardia, revision manual y recuperacion de M confirman juntos XML,
   estado O y evento_outbox. Si falla la transaccion no queda la factura sin evento.
5. El worker outbox reclama con FOR UPDATE SKIP LOCKED, token y reserva de 10 min.
   Prepara/reutiliza RIDE y encola en emails. Al vencer la reserva puede retomarse;
   un worker antiguo no puede confirmar un token sustituido.
6. msvc-emails registra intento y reserva de 15 min antes de SMTP. Solo una reserva
   activa envia el mensaje. A solo se confirma despues de observar SENT en emails.
7. El ERP delega sus API de correo al microservicio. Su motor SMTP local queda
   desactivado por defecto. La cola antigua fec_mail_queue y el postprocesador X
   se adaptan a evento_outbox; ya no construyen solicitudes con claves de tiempo
   ni envian directamente las facturas por otra ruta.

No se incorpora T en fec_factura: el codigo existente usa C para consulta y M para
recepcion devuelta, y el ERP tambien usa X en su ruta anterior. Cambiar esos significados
romperia consumidores actuales. Se conserva G para el XML firmado confirmado y se
separa la dimension tributaria en sri_autorizacion_control.estado:
FIRMADO, ENVIANDO_RECEPCION, ESPERANDO_AUTORIZACION, AUTORIZADO, DEVUELTA,
DATOS_INVALIDOS y ERROR_PREPARACION. La cola antigua X pasa a O al delegar su aviso.
El scheduler SOAP de facturas del ERP queda apagado por defecto para evitar carreras;
se mantiene independiente el procesamiento de retenciones.

## Persistencia

- sri_autorizacion_control: clave, estado tributario, intentos, proxima consulta y error.
  Los campos de reintentos existentes de fec_factura siguen siendo la fuente del backoff.
- comprobante_documento: XML completo, PDF en bytea y SHA-256 de ambos. Una variacion
  de XML invalida el RIDE almacenado. Se verifica integridad al reutilizar/descargar.
  Un XML historico sin metadatos no se publica como documento completo.
- evento_outbox: clave unica (factura_id, tipo, version), estado, reserva y reintentos.
- email_message / email_attachment se reutilizan como notificacion y adjuntos; no se
  crea otra tabla correo_notificacion que duplique la cola existente.
- correo_intento: inicio/fin y resultado de cada intento de envio, en la misma
  transaccion que el cambio de estado del correo.
- factura_operacion y correo_operacion: auditoria de correcciones y conciliaciones.

La idempotencia de factura:<id> y factura:<id>:v<version> se serializa con bloqueo
transaccional PostgreSQL en emails, antes de buscar o crear la notificacion.
Las correcciones de destinatario crean otra version y cancelan la anterior. Se rechazan
si hay un trabajo activo, si SMTP ya acepto el mensaje o si falta conciliar incertidumbre.
Los A historicos no se reenvian masivamente sin evidencia de entrega previa.

## Errores y reintentos

- TRANSIENT: rechazo temporal conocido (p.ej. 4xx SMTP) o fallo antes de conectar.
  Esperas configurables MAIL_RETRY_DELAYS_SECONDS: 60,300,900,3600 segundos. Las
  facturas no se descartan por cantidad de intentos transitorios.
- CONFIGURATION: autenticacion 535 o credenciales API rechazadas. FAILED y alerta;
  corregir la cuenta o probarla con exito reactiva los mensajes de esa cuenta.
- PERMANENT: destinatario rechazado, datos MIME invalidos o adjunto ausente.
  FAILED conserva adjuntos y error, hasta correccion/reintento autorizado.
- UNCERTAIN: EOF/timeout durante la entrega, entrega parcial o caida despues de
  que SMTP acepto y antes de confirmar SENT en BD. Nunca se reenvia automaticamente.
  La reserva vencida genera alerta y requiere conciliacion. Para autorizar otro envio
  se exige confirmDuplicateRisk=true y evidencia; para confirmar aceptacion tambien
  se exige evidencia. Todo queda auditado. No se promete entrega exactamente una vez.

La reserva de outbox puede recuperarse automaticamente porque encolar es idempotente;
la reserva SMTP se concilia porque SMTP no ofrece esa misma garantia.
SENT/A indican aceptacion SMTP/API, no llegada a bandeja: rebotes/webhooks aun no se
implementan. Las alertas Telegram son asincronas y limitadas por logger; no son una
cola durable. Una caida completa del proceso requiere monitoreo externo.

Las consultas SRI usan backoff exponencial con jitter, sin nueva clave, base 30s y
techo 10 minutos configurables por SRI_AUTORIZACION_RETRY_BASE_MS / MAX_MS. El barrido
se ejecuta cada 15s por defecto; cada fila respeta su vencimiento. No se abandona una
consulta solo por pasar cinco intentos. Casos historicos sin XML firmado y respuestas
fuera del rango de recuperacion SRI pueden requerir recuperacion documental manual.

## Monitor y permisos

En /#/admin/correos se incorpora Seguimiento de facturas SRI y correo:
conteos por rango, estado tributario y de notificacion, proxima ejecucion, errores,
historial, descarga XML/RIDE, consulta SRI, reintento, correccion de destinatario y
confirmacion de aceptacion basada en evidencia del proveedor.

El frontend sigue llamando al ERP. El proxy del ERP valida JWT WEB, usuario activo y
ventana admin-correos (nivel >=1 lectura, >=2 escritura; administrador 1 exceptuado
solo del nivel). El actor de auditoria lo impone el backend desde el JWT validado.
El ERP agrega una clave privada entre servicios; no se introduce en el navegador.
msvc-emails protege todas sus rutas /api/v1 con esa clave y actor. El monitor de SRI
tambien la exige. Sin clave configurada las operaciones estan cerradas.

Endpoint ERP: /api/v1/sri-control. Reenvia a /api/v1/facturas/monitor de SRI.
Las rutas /api/v1/email-accounts, /api/v1/emails y /api/v1/email-blacklist del ERP
usan el mismo adaptador y sus permisos WEB. La prueba de conexion valida la cuenta
SMTP almacenada, no envia correo, registra el resultado y no devuelve secretos.
Editar una cuenta conserva la clave cuando se omite; el GET ya no entrega password.
El resultado de una prueba se guarda con UPDATE parcial y version para no sobreescribir
credenciales que otro operador haya editado mientras la prueba estaba en curso.

## Despliegue coordinado

No se han ejecutado migraciones, reenvios ni despliegues en produccion durante este trabajo.
La comprobacion SMTP de produccion sigue dependiendo de que CMAGINET acepte la cuenta.

1. Respaldar la BD. Programar una pausa coordinada de los trabajadores viejos del ERP,
   msvc-sri y msvc-emails. Conciliar cualquier envio que estuviera en curso al detenerlos.
2. Generar UNA clave aleatoria privada OPERATIONS_API_KEY y distribuir el mismo valor
   a ERP, SRI y emails mediante un archivo fuera de Git (por ejemplo internal.env).
   .env.operations.example es solo plantilla. No usar su valor de ejemplo.
3. Aplicar, con el administrador de BD, en 192.168.0.46 / ErpEpmapaT:
   - db/2026-10-29_factura_ciclo_confiable.sql
   - emails/src/main/resources/sql/2026-10-29_email_retry.sql
   - sri-files/src/main/resources/sql/2026-10-30_flujo_persistente.sql
   - emails/src/main/resources/sql/2026-10-30_correo_intentos.sql
   Las migraciones nuevas se repiten tambien al arrancar; requieren permisos DDL.
   Hibernate mantiene ddl-auto=none y no reconstruye las tablas de negocio.
4. En el servidor de microservicios, usar el archivo privado compartido:

   ```bash
   docker compose --env-file .env --env-file /ruta/privada/internal.env --env-file .env.telegram.local up -d --build --force-recreate msvc-emails msvc-sri
   ```

   Mantener DB_URL en 192.168.0.46. emails ya no usa la configuracion global spring.mail:
   las cuentas, claves y pruebas usan email_account como fuente unica. Se retiro el
   chequeo global que intentaba autenticar otra cuenta. El health de cola consulta
   fallos CONFIGURATION y resultados UNCERTAIN y devuelve DOWN si requieren atencion;
   no inicia autenticaciones SMTP repetidas. UP no sustituye una prueba de la cuenta.
   No intercambiar credenciales de CMAGINET y Elastic Email.
5. Publicar el ERP nuevo con la MISMA OPERATIONS_API_KEY, ERP_MAIL_BASE_URL y
   ERP_SRI_BASE_URL apuntando a los microservicios (por defecto .33:9099 y .33:9096).
   Mantener erp.mail.local-worker-enabled=false y erp.sri.local-processing-enabled=false.
   No activar a la vez el worker antiguo y el nuevo. Para el compose del ERP:

   ```bash
   docker compose --env-file .env --env-file /ruta/privada/internal.env up -d --build --force-recreate erp_epmapat_be
   ```

   El compose del ERP monta deploy/erp_epmapat-v0.1.war: actualizar ese WAR con el
   artefacto generado antes de recrear. Construir con mvn package -DskipTests y copiar
   target/erp_epmapat-v0.1.war al archivo montado. No basta reconstruir una imagen con
   un WAR antiguo montado encima.
6. Publicar el frontend compilado. Entrar con sesion WEB y permiso admin-correos.
   Probar conexion de MAIL_NOT_01 y MAIL_FACT_01 individualmente desde ese servidor.
7. En una factura de prueba autorizada verificar: O + evento, RIDE/XML, email_message_id,
   SENT en correo, A + mail_enviado=true. Reiniciar un worker, simular SMTP caido y
   verificar historial, reintento/UNCERTAIN y Telegram. Usar rango de fechas del monitor.
   No declarar exito global hasta conciliar todas las facturas del periodo.

Compatibilidad: API de documentos v1 (documento_electronico) no tiene un adaptador de
notificacion equivalente al de fec_factura. Su batch anterior marcaba mail_enviado=true
sin enviar nada; ahora registra NOTIFICACION_REQUIERE_ADAPTADOR y nunca inventa exito.
La ampliacion de ese adaptador y los webhooks de entrega quedan fuera del flujo de
facturas ERP implementado aqui.

## Pruebas reproducibles

Sin tocar produccion, se creo un cluster PostgreSQL 18 temporal ligado a 127.0.0.1:55439.
Cada suite crea una BD de pruebas con nombre aleatorio y la elimina al terminar. Las
pruebas integrales rechazan otra URL administrativa mediante condicion de ejecucion.

Para crear/detener automaticamente el cluster aislado en Windows (PostgreSQL instalado):
`scripts/test-facturas-workflow.ps1`. No usa produccion ni contenedores. Conserva
su carpeta temporal al terminar. Para un cluster aislado ya iniciado:

```powershell
$env:EPMAPA_TEST_PG_ADMIN='jdbc:postgresql://127.0.0.1:55439/postgres'
mvn -pl emails,sri-files -am package '-Dtest=*Test,!*ApplicationTests' '-Dsurefire.failIfNoSpecifiedTests=false'
```

ERP: mvn test -Dtest=EmailMicroserviceProxyFilterTest,EmailAdminAccessServiceTest
Frontend: ng test --configuration=correos --watch=false --browsers=ChromeHeadless
          --include=src/app/features/admin-central/pages/administracion/correos/email-admin.service.spec.ts
Compilacion de plantillas: ngc -p tsconfig.app.json --noEmit.

Escenarios cubiertos: commit/rollback conjunto XML+evento; duplicacion de evento/version;
ocho reclamaciones concurrentes; reserva abandonada y fencing; firma/intencion confirmadas
antes de la red; autorizacion tardia sin SMTP; dos trabajadores de correo y un solo envio;
rechazo permanente; temporal con proximo intento; EOF incierto; aceptacion SMTP seguida
de fallo real simulado mediante trigger PostgreSQL; recuperacion de esa reserva sin
reenviar; correccion versionada de destinatario; rechazo de correccion durante una reserva;
JWT ausente, usuario inactivo y lectura sin escritura; proxy ERP y conservacion de errores;
formulario sin secreto anterior ni cambio implicito de host y preservacion de UNCERTAIN.

Las pruebas SMTP/SRI usan dobles de red. No sustituyen la validacion de credenciales,
certificados, firewall y aceptacion del proveedor desde produccion.
