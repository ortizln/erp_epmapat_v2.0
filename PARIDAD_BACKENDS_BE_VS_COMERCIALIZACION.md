# PARIDAD BACKEND: erp_epmapat_be vs v2.0/comercializacion
Fecha: 2026-03-01 16:21

## Resumen
- Monolito Java files: 609
- Microservicio Java files: 565
- RequestMappings monolito: 119
- RequestMappings microservicio: 118
- Endpoints faltantes en micro (normalizados): 4
- Endpoints extra/renombrados en micro (normalizados): 3
- Archivos faltantes en micro por nombre: 52
- Archivos extra en micro por nombre: 8

## Endpoints faltantes en microservicio (normalizados)
- /documentos
- /recargosxcuenta
- /usrxrutas
- /ventanas

## Endpoints extra/renombrados en microservicio (normalizados)
- /auth
- /comercializacion/documentos
- /com-ventanas

## Archivos presentes en monolito y ausentes en micro (por nombre)
- AbonadoDTO.java
- AbonadosMobile.java
- BusinessConflictException.java
- CategoriasMobile.java
- ClienteDto.java
- ClienteDuplicadoDTO.java
- ClienteDuplicadoGrupoView.java
- ClienteDuplicadoView.java
- ClienteMerge.java
- ClienteMergeAbonado.java
- ClienteMergeAbonadoR.java
- ClienteMergeCliente.java
- ClienteMergeClienteR.java
- ClienteMergeFactura.java
- ClienteMergeFacturaR.java
- ClienteMergeLectura.java
- ClienteMergeLecturaR.java
- ClienteMergePreviewDTO.java
- ClienteMergeR.java
- ClienteMergeRequest.java
- ClienteMergeService.java
- ClientesMobile.java
- CorsConfig.java
- CredencialesRequest.java
- EmisionServicioOptimizado_anterior.java
- ErpEpmapatApplication.java
- EstadomMobile.java
- FacturaPendienteDTO.java
- FecFacturaUpdateDto.java
- HttpStatus.java
- JasperReportLoader.java
- LecturaDto.java
- LecturaMapper.java
- LecturasByRutasRequest.java
- LoginResponse.java
- NacionalidadView.java
- Recargosxcuenta.java
- RecargosxcuentaApi.java
- RecargosxcuentaR.java
- RecargosxcuentaService.java
- RecargoXCtaReq.java
- ReportCache.java
- RutaDTO.java
- RutasOcupadasException.java
- TpidentificaView.java
- Usrxrutas.java
- UsrxrutasApi.java
- UsrxrutaService.java
- UsrxrutasR.java
- UsrxrutasService.java
- ValidarRecargosRequest.java
- ValidarRecargosResponse.java

## Archivos presentes en micro y ausentes en monolito (por nombre)
- AuthController.java
- AuthSevice.java
- CalculoDetalle.java
- ComercializacionApplication.java
- FacElectronicas.java
- RegistroClienteRequest.java
- RutasInterfaces.java
- TarifasConst.java

## Observaciones clave
- recargosxcuenta (modelo/controlador/repositorio/servicio/DTOs) existe en monolito y NO está en comercialización.
- Hay diferencia de prefijos de rutas (/api/* en micro).
- Existen diferencias de nombres de rutas: /documentos vs /comercializacion/documentos, /ventanas vs /com-ventanas.
- PersonalServicio del micro no incluye findById que sí existe en monolito.

## Plan de merge recomendado (orden)
- 1) Migrar bloque recargosxcuenta completo al microservicio.
- 2) Alinear rutas incompatibles de gateway/controladores.
- 3) Migrar DTOs/servicios utilitarios faltantes de alto impacto.
- 4) Pruebas de regresión endpoint por endpoint.
