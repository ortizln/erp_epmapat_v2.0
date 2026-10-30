> Arquitectura y despliegue actualizados: [plan del 30 de octubre](../docs/PLAN_IMPLEMENTADO_FACTURAS_2026-10-30.md).
> Las esperas activas ahora son MAIL_RETRY_DELAYS_SECONDS y los errores de autenticacion
> quedan suspendidos; un resultado UNCERTAIN nunca se reintenta automaticamente.

# Envio y alertas de emails

El flujo completo y las instrucciones actualizadas estan en
[../sri-files/CICLO_FACTURAS_CONFIABLE.md](../sri-files/CICLO_FACTURAS_CONFIABLE.md).

La prueba real de Telegram realizada el 29 de octubre de 2026 fue aceptada por el bot.
La configuracion proporcionada esta en .env.telegram.local, excluido mediante
.git/info/exclude. No se ha desplegado la nueva version en produccion.

Errores ERROR posteriores al arranque se notifican mediante un hilo independiente,
cola de 50 mensajes y limite de una alerta por logger cada 300 segundos. El ID de
correo se incluye cuando esta disponible; se omiten los argumentos con datos personales.
Las fallas Telegram no registran URL/token y no bloquean el procesamiento. No se cubren
caidas completas del proceso ni errores previos a registrar el componente de alertas.

Antes de arrancar emails aplicar src/main/resources/sql/2026-10-29_email_retry.sql.
Las facturas DOC_ELECTRONICO reintentan sin limite de cantidad para fallos de envio,
con espera exponencial de 30s hasta 1h; lista negra y adjuntos perdidos quedan FAILED.
Los otros tipos mantienen max-attempts. POST /api/v1/emails/{id}/retry solo acepta FAILED.
GET /api/v1/emails/{id}/status devuelve el estado sin cuerpo ni adjuntos.
SMTP usa email_account en BD: corregir alli el rechazo 535 del log.
SENT confirma aceptacion del proveedor y requiere seguimiento de rebotes para entrega final.

Telegram API: https://core.telegram.org/bots/api#sendmessage

## Migracion automatica de la cola

Desde esta correccion, Spring SQL init ejecuta sql/2026-10-29_email_retry.sql
sobre el DataSource efectivo antes de inicializar JPA y los schedulers. Crea
next_attempt_at e indice si faltan. Si no tiene permisos o falta email_message,
el arranque falla para impedir que se anuncie operativo con la cola bloqueada.
El usuario de BD necesita permisos ALTER sobre email_message. No modifica filas.
Si Config Server define spring.sql.init.*, conservar mode=always,
continue-on-error=false y la ubicacion del script de esta version.

## Configuracion Telegram en el servidor

.env.telegram.local contiene un secreto y esta excluido de Git. git pull no lo copia.
El archivo versionable .env.telegram.example sirve de plantilla sin el token.
En el servidor, desde la raiz del repositorio:

```bash
cp -n .env.telegram.example .env.telegram.local
chmod 600 .env.telegram.local
nano .env.telegram.local
```

Sustituir REEMPLAZAR_TOKEN_DEL_BOT por el token real. Alternativamente transferir el
archivo local existente por SCP/SSH. No quitarlo de .gitignore ni agregarlo con git add -f.
Despues recrear:

```bash
docker compose --env-file .env --env-file .env.telegram.local up -d --force-recreate msvc-emails
```

Comprobar variables sin mostrar el token:

```bash
docker compose exec msvc-emails sh -c 'printf "Alertas=%s\nChat=%s\n" "$TELEGRAM_ALERTS_ENABLED" "$TELEGRAM_CHAT_ID"; if [ -n "$TELEGRAM_BOT_TOKEN" ]; then echo "Token configurado"; else echo "Token ausente"; fi'
```
