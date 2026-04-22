# API_DOCS_INDEX.md

Portal rápido de documentación Swagger (vía Gateway)

Base Gateway:
- `http://localhost:8080`

## Microservicios

- **Login**
  - UI: `http://localhost:8080/login/swagger-ui.html`
  - JSON: `http://localhost:8080/login/v3/api-docs`

- **Comercialización**
  - UI: `http://localhost:8080/comercializacion/swagger-ui.html`
  - JSON: `http://localhost:8080/comercializacion/v3/api-docs`

- **RRHH**
  - UI: `http://localhost:8080/rrhh/swagger-ui.html`
  - JSON: `http://localhost:8080/rrhh/v3/api-docs`

- **Contabilidad**
  - UI: `http://localhost:8080/contabilidad/swagger-ui.html`
  - JSON: `http://localhost:8080/contabilidad/v3/api-docs`

- **Recaudación**
  - UI: `http://localhost:8080/recaudacion/swagger-ui.html`
  - JSON: `http://localhost:8080/recaudacion/v3/api-docs`

- **Gestión Documental**
  - UI: `http://localhost:8080/gestiondocumental/swagger-ui.html`
  - JSON: `http://localhost:8080/gestiondocumental/v3/api-docs`

- **SRI**
  - UI: `http://localhost:8080/sri/swagger-ui.html`
  - JSON: `http://localhost:8080/sri/v3/api-docs`
  - Guía retenciones: [`SRI_RETENCIONES_FLUJO.md`](./SRI_RETENCIONES_FLUJO.md)

- **Reportes JR**
  - UI: `http://localhost:8080/reportes/swagger-ui.html`
  - JSON: `http://localhost:8080/reportes/v3/api-docs`

- **Pagos Online**
  - UI: `http://localhost:8080/pagosonline/swagger-ui.html`
  - JSON: `http://localhost:8080/pagosonline/v3/api-docs`

- **EPMAPA API**
  - UI: `http://localhost:8080/epmapaapi/swagger-ui.html`
  - JSON: `http://localhost:8080/epmapaapi/v3/api-docs`

- **Emails**
  - UI: `http://localhost:8080/emails/swagger-ui.html`
  - JSON: `http://localhost:8080/emails/v3/api-docs`

## Verificación rápida
1. Reiniciar stack:
   - `docker compose restart config-server msvc-gateway msvc-login msvc-comercializacion msvc-rrhh msvc-contabilidad msvc-recaudacion msvc-gestiondocumental msvc-sri msvc-reportesjr msvc-pagosonline msvc-epmapaapi msvc-emails`
2. Abrir una UI y validar carga de endpoints.
3. Si falla por gateway pero funciona por puerto directo, revisar cache de config-server + restart de gateway.
