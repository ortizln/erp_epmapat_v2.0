# MAPA_PAQUETES_V2.md

Fecha: 2026-03-01
Proyecto: erp_epmapat_v2.0 / microservicio comercializacion

## Estructura actual por dominio

- `com.erp.comercializacion`
  - `controlador`
  - `modelo`
  - `repositorio`
  - `servicio`

- `com.erp.rrhh`
  - `controlador`
  - `modelo`
  - `repositorio`
  - `servicio`

- `com.erp.contabilidad`
  - `controlador`
  - `modelo`
  - `repositorio`
  - `servicio`

## Otros paquetes transversales (se mantienen)

- `com.erp.DTO`
- `com.erp.config`
- `com.erp.interfaces`
- `com.erp.excepciones`
- `com.erp.mappers`
- `com.erp.jasperReports`
- `com.erp.reportes`
- `com.erp.sri`
- `com.erp.commons`
- `com.erp.legacy`

## Notas

- Se conservaron rutas API para evitar romper frontend/gateway.
- Se realizó refactor de `package` + `imports` y compilación final OK.
- Build de verificación: `mvn -pl comercializacion -DskipTests compile` => BUILD SUCCESS.
