# CHECKLIST_MIGRACION_MICROSERVICIOS.md

Fecha de actualización: 2026-03-01 22:22

## 1) Infraestructura base
- [x] Config Server estable
- [x] Eureka estable
- [x] Gateway estable
- [x] Rutas de comercialización sin `/api` (con rewrite interno)
- [x] Rutas de login sin `/api` (con rewrite interno)

## 2) Split por dominios (backend)
- [x] Reorganización por paquetes (`comercializacion`, `rrhh`, `contabilidad`)
- [x] Crear `msvc-rrhh` independiente
- [x] Crear `msvc-contabilidad` independiente
- [x] Desacoplar RRHH de `msvc-comercializacion`
- [x] Desacoplar Contabilidad de `msvc-comercializacion`

## 3) Gateway routing
- [x] RRHH por 8080 con y sin `/api`
- [x] Contabilidad por 8080 con y sin `/api`
- [x] Comercialización por 8080 sin `/api` (compat interna)
- [x] Auth/login por 8080 sin `/api`

## 4) Swagger / OpenAPI
- [x] `msvc-rrhh` con Swagger
- [x] `msvc-contabilidad` con Swagger
- [x] `msvc-login` con Swagger
- [x] `msvc-comercializacion` con Swagger
- [x] Índice markdown (`API_DOCS_INDEX.md`)
- [x] Portal HTML (`docs/index.html`)

## 5) Talento Humano en v2
- [x] CRUD base de personal (GET/POST/PUT/inactivar)
- [x] Validación identificación única
- [x] Validación de fechas (`fecinicio <= fecfin`)
- [ ] Filtros + paginación en personal
- [ ] Módulo `th_actions` (acciones de personal)
- [ ] Módulo novedades (vacaciones/permisos/licencias)

## 6) Pendientes inmediatos (siguiente bloque)
- [ ] Smoke tests end-to-end por gateway (login/comercialización/rrhh/contabilidad)
- [ ] Dashboard Swagger v2 con estado UP/DOWN por microservicio
- [ ] Limpieza final de variables sensibles y `.env`
- [ ] Documento de despliegue sin downtime (runbook final)
- [ ] Ajuste frontend para consumo definitivo sin `/api` (cuando decidas)

## 7) URLs rápidas de docs
- Login: http://localhost:8080/login/swagger-ui.html
- Comercialización: http://localhost:8080/comercializacion/swagger-ui.html
- RRHH: http://localhost:8080/rrhh/swagger-ui.html
- Contabilidad: http://localhost:8080/contabilidad/swagger-ui.html
