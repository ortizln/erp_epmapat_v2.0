# REORGANIZACION_MICROSERVICIO_CHECKLIST.md

Fecha: 2026-03-02

## Reorganización por dominios
- [x] Crear paquete `com.erp.rrhh` (controlador/modelo/repositorio/servicio)
- [x] Mover clases RRHH y ajustar package/imports
- [x] Crear paquete `com.erp.contabilidad` (controlador/modelo/repositorio/servicio)
- [x] Mover clases contabilidad y ajustar package/imports
- [x] Crear paquete `com.erp.comercializacion` (controlador/modelo/repositorio/servicio)
- [x] Mover núcleo de comercialización y ajustar package/imports

## Validaciones técnicas
- [x] Compilar luego de RRHH
- [x] Compilar luego de contabilidad
- [x] Compilar luego de comercialización
- [x] Build final OK (`mvn -pl comercializacion -DskipTests compile`)

## Pendientes recomendados
- [~] Ejecutar smoke tests por dominio vía gateway (parcial: RRHH OK parcial; faltan /api/th-audit y /api/th-files en gateway)
- [ ] Revisar referencias legacy y plan de retiro controlado
- [ ] Documentar contratos API críticos (OpenAPI/Postman)


## Evidencia adicional RRHH
- [x] Microservicio `msvc-rrhh` levantado y endpoints funcionales directos (`:9101`).
- [x] Esquema BD RRHH completo en `ErpEpmapaT` (5 tablas TH verificadas).
- [x] Validación de reglas TH Leave (solicitud + aprobación + descuento de saldo).
- [x] Auditoría TH y expediente digital validados en microservicio.
- [ ] Cierre de paridad de rutas RRHH por gateway (`:8080`) pendiente de recarga efectiva de configuración.
