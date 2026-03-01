# PARIDAD ACTUALIZADA: erp_epmapat_be vs v2.0/comercializacion
Fecha: 2026-03-01 17:33

## Resumen actualizado
- Endpoints faltantes en micro (normalizados): 2
- Endpoints extra/renombrados en micro (normalizados): 3
- Archivos presentes en monolito y ausentes en micro: 17
- Archivos presentes en micro y ausentes en monolito: 8

## Bloques ya migrados en esta sesión
- [x] RecargosxcuentaApi.java
- [x] RecargosxcuentaService.java
- [x] RecargosxcuentaR.java
- [x] UsrxrutasApi.java
- [x] UsrxrutaService.java
- [x] UsrxrutasR.java
- [x] ClienteMergeService.java
- [x] ClienteMergeR.java
- [x] ClienteMergeClienteR.java
- [x] ClienteMergeAbonadoR.java
- [x] ClienteMergeFacturaR.java
- [x] ClienteMergeLecturaR.java
- [x] AbonadosMobile.java
- [x] CategoriasMobile.java
- [x] EstadomMobile.java

## Endpoints aún faltantes (normalizados)
- /documentos
- /ventanas

## Archivos aún faltantes en micro (top 40)
- AbonadoDTO.java
- ClienteDto.java
- ClienteDuplicadoDTO.java
- ClienteMergePreviewDTO.java
- CorsConfig.java
- EmisionServicioOptimizado_anterior.java
- ErpEpmapatApplication.java
- FacturaPendienteDTO.java
- FecFacturaUpdateDto.java
- HttpStatus.java
- JasperReportLoader.java
- LecturaDto.java
- LecturaMapper.java
- LecturasByRutasRequest.java
- LoginResponse.java
- ReportCache.java
- RutaDTO.java

## Próximo paquete sugerido
- Revisar y migrar clases sueltas de compatibilidad: LoginResponse, ClienteDuplicadoDTO, ClienteMergePreviewDTO, RutaDTO, LecturaDto/LecturaMapper.
- Verificar rutas renombradas (/documentos, /ventanas) y compatibilidad FE/Gateway.
- Ejecutar pruebas smoke por endpoint crítico (clientes, abonados, recargosxcuenta, usrxrutas).
