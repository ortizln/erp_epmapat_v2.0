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

## Verificación rápida
1. Reiniciar stack de config+gateway+microservicios:
   - `docker compose restart config-server msvc-gateway msvc-login msvc-comercializacion msvc-rrhh msvc-contabilidad`
2. Abrir una UI y validar que lista endpoints.
3. Probar un endpoint GET de cada micro.

## Notas
- Si una UI no abre, revisar primero `config-server` y luego `msvc-gateway`.
- Si responde directo por puerto del micro pero no por 8080, es ruta de gateway/config cache.
