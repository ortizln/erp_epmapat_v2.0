# Flujo SRI de Retenciones

Este documento resume las rutas disponibles en `sri-files` para retenciones electrónicas.

## Servicio
- Módulo: `sri-files`
- Ruta local del proyecto: `C:\Users\Alexi\Documents\PROYECTOS_EPMAPA-T\microservicesEpmapa-T\sri-files`
- URL base vía gateway: `http://localhost:8080/sri`
- Puerto directo del servicio: `9096`

## Endpoints principales

### Envío y autorización
- `POST /api/singsend/retencion`
- `POST /api/singsend/retencion/string`

### Consulta de autorización
- `GET /api/singsend/autorizacion?claveAcceso=...`
- `GET /api/singsend/autorizacion/by-xml`

### Descarga de autorizada
- `GET /api/singsend/retencion/download?claveAcceso=...&downloadType=zip|pdf|xml`
- `GET /api/singsend/retenciones/download?claveAcceso=...&downloadType=zip|pdf|xml`
- `GET /api/singsend/retenciones/pdf?claveAcceso=...`
- `GET /api/singsend/retenciones/xml?claveAcceso=...`

### PDF desde XML autorizado
- `POST /api/singsend/retenciones/pdf`
- Consume `application/xml`

### Correo electrónico
- `POST /api/singsend/retencion/mail`
- `POST /api/singsend/retenciones/mail`

## Qué hace cada opción
- XML: devuelve el XML autorizado o lo descarga como archivo.
- PDF: genera el PDF a partir del XML autorizado.
- Mail: adjunta PDF + XML y envía la retención al correo indicado.

## Notas operativas
- El servicio ya incluye la lógica de recepción, autorización y espera (`polling`).
- Las rutas alias `retenciones/*` existen para que el consumo sea más claro desde otros módulos.
- El módulo de facturación usa el mismo patrón de envío y autorización, pero con sus endpoints propios.

