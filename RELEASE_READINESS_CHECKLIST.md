# RELEASE_READINESS_CHECKLIST.md

Fecha: 2026-03-01
Repo: erp_epmapat_v2.0
Commit base: 0f4a465

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
- [ ] `docker compose config` válido
- [ ] `docker compose ps` sin reinicios en loop
- [ ] Healthchecks en verde

## 5) Aprobación
- [ ] Smoke tests OK
- [ ] Validación funcional con usuario clave
- [ ] Go/No-Go firmado
