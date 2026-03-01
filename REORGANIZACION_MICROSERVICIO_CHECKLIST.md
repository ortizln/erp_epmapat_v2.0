# REORGANIZACION_MICROSERVICIO_CHECKLIST.md

Fecha: 2026-03-01

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
- [ ] Ejecutar smoke tests por dominio vía gateway
- [ ] Revisar referencias legacy y plan de retiro controlado
- [ ] Documentar contratos API críticos (OpenAPI/Postman)
