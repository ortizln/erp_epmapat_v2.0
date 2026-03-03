# RELEASE_READINESS_CHECKLIST.md

Fecha: 2026-03-02
Repo: erp_epmapat_v2.0
Commit base: f7b11f0

## 1) Build
- [x] `mvn -pl comercializacion -DskipTests compile` -> BUILD SUCCESS

## 2) Gateway / Rutas críticas
- [x] `/api/clientes/**`
- [x] `/api/abonados/**`
- [x] `/api/recargosxcuenta/**`
- [x] `/api/usrxrutas/**`
- [x] `/auth/**`
- [x] Alias compat: `/api/documentos/**`
- [x] Alias compat: `/api/ventanas/**`

## 3) Smoke tests sugeridos (Postman/curl)
> Reemplazar `HOST_GW` y token según ambiente

### Auth
- [ ] `POST http://HOST_GW/auth/login`

### Clientes
- [ ] `GET http://HOST_GW/api/clientes?nombreIdentifi=test`
- [ ] `GET http://HOST_GW/api/clientes/duplicados?page=0&size=10`
- [ ] `GET http://HOST_GW/api/clientes/duplicados-agrupado?q=&page=0&size=10`

### Abonados
- [ ] `GET http://HOST_GW/api/abonados/allabonadosmobile`
- [ ] `GET http://HOST_GW/api/abonados/cuentasOfCliente?idcliente=1`

### Recargos por cuenta
- [ ] `GET http://HOST_GW/api/recargosxcuenta/byEmision?idemision=1`
- [ ] `POST http://HOST_GW/api/recargosxcuenta/validar`
- [ ] `POST http://HOST_GW/api/recargosxcuenta/batch`

### Usrxrutas
- [ ] `GET http://HOST_GW/api/usrxrutas`
- [ ] `GET http://HOST_GW/api/usrxrutas/emision/1/rutas-ocupadas`

## 4) Infra mínima antes de deploy
- [ ] `.env` completo en servidor
- [x] Build técnico verificado (`mvn -pl comercializacion -DskipTests compile`)
- [ ] `docker compose ps` sin reinicios en loop
- [ ] Healthchecks en verde

## 5) Aprobación
- [ ] Smoke tests OK (pendiente ejecución en gateway)
- [ ] Validación funcional con usuario clave
- [ ] Go/No-Go firmado



## 6) RRHH (estado actual 2026-03-02)
### Base de datos (ErpEpmapaT)
- [x] `th_actions`
- [x] `th_leave_requests`
- [x] `th_leave_balances`
- [x] `th_audit_log`
- [x] `th_employee_files`

### Smoke RRHH directo a microservicio (`msvc-rrhh:9101`)
- [x] `POST /api/th-actions` (creación acción)
- [x] `POST /api/th-leave/requests` (creación solicitud)
- [x] `POST /api/th-leave/requests/{id}/aprobar` (aprobación)
- [x] Validación de negocio: descuento de saldo VACACION aplicado
- [x] `GET /api/th-audit?entidad=TH_LEAVE_REQUEST&idregistro={id}`
- [x] `POST /api/th-files` + `GET /api/th-files/persona/{id}`

### Smoke RRHH vía gateway (`:8080`)
- [x] `GET /api/th-actions/persona/{id}`
- [x] `GET /api/th-leave/requests/persona/{id}`
- [x] `GET /api/th-audit?...`
- [x] `GET /api/th-files/persona/{id}`

### Notas
- Se aplicó fix de rutas en `config-data/msvc-gateway.yml` para incluir `/api/th-audit/**` y `/api/th-files/**`.
- Validado después de reinicio de config+gateway: rutas operativas por :8080 (200 en th-audit y th-files).

