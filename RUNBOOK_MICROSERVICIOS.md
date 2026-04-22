ok # RUNBOOK_MICROSERVICIOS.md
ERP EPMAPAT v2.0 - Guía operativa rápida

## 1) Objetivo
Este runbook describe cómo levantar, validar y diagnosticar los microservicios del ERP EPMAPAT v2.0 en Docker Compose.

Ruta del proyecto:
- `C:\ERP\erp_epmapat_v2.0`

---

## 2) Arquitectura (resumen)
Servicios principales:
- `config-server` (8888)
- `msvc-eureka` (8761)
- `msvc-gateway` (8080)
- `msvc-login` (9091)
- `msvc-recaudacion` (9092)
- `msvc-comercializacion` (9093)
- `msvc-pagosonline` (32)  <-- requerido por negocio, NO CAMBIAR
- `msvc-gestiondocumental` (9095)
- `msvc-sri` (9096)
- `msvc-reportesjr` (9097)
- `msvc-epmapaapi` (9098)
- `msvc-emails` (9099)

### SRI: retenciones y facturas
- El servicio `msvc-sri` concentra el flujo de comprobantes electrónicos.
- Retenciones:
  - envío y autorización: `POST /api/singsend/retencion`
  - envío desde XML plano: `POST /api/singsend/retencion/string`
  - descarga autorizada: `GET /api/singsend/retenciones/download`
  - PDF: `GET /api/singsend/retenciones/pdf`
  - XML: `GET /api/singsend/retenciones/xml`
  - correo: `POST /api/singsend/retenciones/mail`
- Ver guía completa:
  - `SRI_RETENCIONES_FLUJO.md`

---

## 3) Pre-requisitos
- Docker + Docker Compose instalados y accesibles por terminal.
- Puerto 32 habilitado para `msvc-pagosonline` (requisito confirmado).
- Archivo `.env` creado a partir de `.env.example`.

### 3.1 Crear .env
1. Copiar:
   - `.env.example` -> `.env`
2. Definir variables reales:
   - `MAIL_USERNAME`
   - `MAIL_PASSWORD`
   - `DB_USER`
   - `DB_PASS_2024`
   - `DB_PASS_2025`
   - `DB_PASS`

---

## 4) Orden recomendado de arranque
1. `config-server`
2. `msvc-eureka`
3. `msvc-gateway`
4. Resto de microservicios de negocio

Comandos sugeridos:
```bash
cd C:/ERP/erp_epmapat_v2.0

docker compose up -d config-server

docker compose up -d msvc-eureka

docker compose up -d msvc-gateway

docker compose up -d msvc-login msvc-recaudacion msvc-comercializacion msvc-pagosonline msvc-gestiondocumental msvc-sri msvc-reportesjr msvc-epmapaapi msvc-emails
```

---

## 5) Validación rápida de salud
### 5.1 Estado general
```bash
docker compose ps
```

### 5.2 Health por servicio (desde host)
- Eureka: `http://localhost:8761/actuator/health`
- Gateway: `http://localhost:8080/actuator/health`
- Login: `http://localhost:9091/actuator/health`
- Recaudación: `http://localhost:9092/actuator/health`
- Comercialización: `http://localhost:9093/actuator/health`
- Pagos online: `http://localhost:32/actuator/health`
- Gestión documental: `http://localhost:9095/actuator/health`
- SRI: `http://localhost:9096/actuator/health`
- Reportes: `http://localhost:9097/actuator/health`
- EPMAPA API: `http://localhost:9098/actuator/health`
- Emails: `http://localhost:9099/actuator/health`

> Nota: si algún servicio no expone `/actuator/health`, habilitar Actuator en ese microservicio.

---

## 6) Diagnóstico rápido (troubleshooting)

### 6.1 Un servicio no levanta
1. Ver logs:
```bash
docker compose logs -f <servicio>
```
2. Verificar variables `.env`.
3. Verificar conexión DB (`DB_URL`, usuario, password).
4. Confirmar que `config-server` y `msvc-eureka` estén sanos.

### 6.2 Gateway responde pero endpoint falla
1. Revisar ruta en:
   - `config/src/main/resources/config-data/msvc-gateway.yml`
2. Verificar nombre del servicio destino (`uri: http://msvc-...`).
3. Revisar que el microservicio esté en estado `healthy`.

### 6.3 Error de credenciales o mail
1. Revisar `.env` real (no `.env.example`).
2. Confirmar que Docker Compose tomó variables nuevas:
```bash
docker compose down
docker compose up -d
```

### 6.4 Error por puerto ocupado
- Verificar puertos en uso y conflictos.
- Confirmar que `9098` (epmapaapi) y `9099` (emails) no se crucen.
- Mantener `32` para pagosonline por requerimiento operativo.

---

## 7) Comandos de operación frecuentes
```bash
# Levantar todo
docker compose up -d

# Detener todo
docker compose down

# Reiniciar un servicio
docker compose restart msvc-comercializacion

# Ver logs de un servicio
docker compose logs -f msvc-gateway

# Reconstruir imagen de un servicio
docker compose build --no-cache msvc-gateway
```

---

## 8) Checklist operativo diario (microservicios)
- [ ] `docker compose ps` sin servicios caídos
- [ ] Eureka disponible
- [ ] Gateway disponible
- [ ] Salud de servicios críticos OK (`login`, `comercializacion`, `recaudacion`, `pagosonline`)
- [ ] Endpoints críticos responden vía gateway
- [ ] Sin errores repetitivos en logs (DB timeout / auth / route not found)

---

## 9) Seguridad y buenas prácticas
- No subir `.env` real al repositorio.
- Rotar contraseñas expuestas históricamente.
- Evitar hardcode de secretos en `docker-compose.yml` o `application.yml`.
- En siguiente fase, usar secret manager (Vault, Docker secrets o equivalente).

---

## 10) Próximas mejoras recomendadas
1. Estandarizar DB host por ambiente (dev/test/prod).
2. Activar observabilidad mínima (actuator + métricas + trazas básicas).
3. Definir pipeline CI/CD por microservicio.
4. Documentar contratos API críticos (OpenAPI).
