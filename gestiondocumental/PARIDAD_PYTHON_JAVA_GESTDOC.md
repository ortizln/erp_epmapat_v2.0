# Paridad Python -> Java (Gestión Documental)

Fecha: 2026-03-02
Módulo Java: `erp_epmapat_v2.0/gestiondocumental`

## 1) Endpoints Python detectados (api/*.py)

Base Python observada:
- `documentos_api.py`
- `emision_api.py`
- `recepcion_api.py`

Endpoints:
- `GET /documents/dashboard`
- `GET /documents/{doc_id}`
- `PUT /documents/{doc_id}`
- `DELETE /documents/{doc_id}`
- `POST /documents/{doc_id}/issue`
- `POST /documents/{doc_id}/receive`
- `GET /documents/receptions/pending`

## 2) Endpoints Java disponibles

### Documentos
Base Java:
- `/api/documents`
- alias `/api/documentos`

Incluye:
- `GET /dashboard`
- `GET /{docId}`
- `PUT /{docId}`
- `DELETE /{docId}`
- `POST /{docId}/issue` (alias `/emitir`)
- `POST /{docId}/receive` (alias `/recibir`)
- `GET /receptions/pending` (alias `/recepciones/pendientes`)

### Más allá de Python base (extendido en Java)
- Catálogos: entidades/dependencias/tipos/lookups
- Workflow: assign/derive/bulk/read/responses/timeline/relations
- Files: add/list/get metadata
- Alerts: generate/list/dispatch
- Configuración: system-settings, CCD, TRD, case-files

## 3) Matriz de paridad mínima (Python -> Java)

- `GET /documents/dashboard` -> `GET /api/documents/dashboard` ✅
- `GET /documents/{doc_id}` -> `GET /api/documents/{docId}` ✅
- `PUT /documents/{doc_id}` -> `PUT /api/documents/{docId}` ✅
- `DELETE /documents/{doc_id}` -> `DELETE /api/documents/{docId}` ✅
- `POST /documents/{doc_id}/issue` -> `POST /api/documents/{docId}/issue` ✅
- `POST /documents/{doc_id}/receive` -> `POST /api/documents/{docId}/receive` ✅
- `GET /documents/receptions/pending` -> `GET /api/documents/receptions/pending` ✅

## 4) Estado técnico actual

- Compilación Java (`mvn -pl gestiondocumental -DskipTests compile`): ✅
- Arquitectura por capas aplicada en progreso:
  - `controller/` ✅
  - `service/` ✅
  - `model/repository/dto` ✅ para catálogo
- Pendiente de cierre recomendado:
  1. smoke E2E de endpoints de escritura (issue/receive/derive/files/alerts)
  2. ajustar `lookups/usuarios` si vuelve a presentar 500 por tipos/domain en BD
  3. conectar por gateway con rutas finales y validar con frontend
