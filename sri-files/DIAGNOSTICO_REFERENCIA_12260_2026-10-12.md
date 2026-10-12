# Prueba real de referencia 12260 - 2026-10-12

Se consultaron las 32 filas de `fec_factura` de referencia 12260 y se realizaron 32 llamadas al API desplegado y 32 consultas SOAP directas al SRI, usando la clave almacenada sin modificarla. Las consultas SQL fueron de solo lectura. Los GET del API pueden actualizar su cache por el comportamiento existente del endpoint; no se reemitieron comprobantes ni se cambiaron claves o estados masivamente.

## Resultado

| Comprobacion | XML obtenidos | Sin XML |
|---|---:|---:|
| API desplegado 192.168.0.33:9096 | 2 | 30 |
| Webservice SRI directo | 2 | 30 |
| API corregido, reproduccion local de los datos reales | 10 | 22 |

El API desplegado devolvio 10 timeouts, 18 respuestas RECHAZADA, 2 respuestas sin autorizaciones y 2 XML. Las 32 claves tienen formato y digito verificador validos.

El SRI directo devolvio 26 respuestas RECHAZADA con codigo 80 y detalle: `No es posible validar la clave de acceso ya que la fecha de emision esta fuera del rango permitido.` Otras 4 claves devolvieron una lista vacia de autorizaciones. Solo 2 devolvieron XML. Esto no demuestra que los comprobantes historicos no esten autorizados en el portal; demuestra que este servicio no entrego sus XML en esta prueba.

Se uso el servicio oficial de produccion documentado en la [ficha tecnica del SRI](https://www.sri.gob.ec/o/sri-portlet-biblioteca-alfresco-internet/descargar/bd280acc-3e16-429a-809b-fa7eb5c6f855/FICHA%20T%C3%89CNICA%20COMPROBANTES%20ELECTR%C3%93NICOS%20ESQUEMA%20OFFLINE%20Versi%C3%B3n%202.22.pdf): `https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline`. No se infiere una ventana de fechas concreta a partir del error.

## Fallo corregido

En la base ya existen 10 XML para estas facturas. Ocho estan en un sobre SOAP y contienen estado AUTORIZADO y la clave correcta; el validador anterior descartaba ese formato y volvia a consultar al SRI. No hay registros correspondientes en `documento_electronico`.

La validacion ahora reconoce sobres SOAP 1.1/1.2, comprueba que el estado AUTORIZADO pertenezca al comprobante de la misma clave, y admite comprobantes embebidos, CDATA y Base64. Devuelve el XML original sin serializar de nuevo ni modificar sus firmas o entidades.

Tambien se conserva el codigo y el detalle de los mensajes del SRI. Para el codigo 80 con fecha fuera de rango, el API informa `requiereRecuperacionHistorica=true`, `reintentable=false`, omite Retry-After y no repite la misma consulta dentro del polling. El estado permanece PENDIENTE_CONSULTA, sin afirmar un rechazo fiscal definitivo.

La prueba local carga los XML reales y las respuestas SOAP descargadas y ejecuta el controller y los servicios corregidos. Simula solamente el transporte y las escrituras; no cambia produccion. Comprueba las 32 claves, recupera los 10 XML almacenados y verifica que se devuelvan intactos. Las otras 22 se distribuyen en 18 con limite de fecha y 4 sin autorizaciones.

## Detalle por factura

| ID factura | Fecha de clave | XML en base | API desplegado | SRI directo | API corregido local |
|---|---|---|---|---|---|
| 2483748 | 2024-03-12 | No | 202: timeout | 80: fecha fuera de rango | 202: sin XML |
| 2501455 | 2024-04-30 | Si | 202: RECHAZADA | 80: fecha fuera de rango | 200: XML |
| 2515573 | 2024-05-28 | Si | 202: timeout | 80: fecha fuera de rango | 200: XML |
| 2533137 | 2024-06-27 | Si | 202: timeout | 80: fecha fuera de rango | 200: XML |
| 2550905 | 2024-07-31 | Si | 202: timeout | 80: fecha fuera de rango | 200: XML |
| 2568797 | 2024-08-19 | Si | 202: timeout | 80: fecha fuera de rango | 200: XML |
| 2587238 | 2024-09-20 | Si | 202: timeout | 80: fecha fuera de rango | 200: XML |
| 2605334 | 2024-10-24 | Si | 202: RECHAZADA | 80: fecha fuera de rango | 200: XML |
| 2623356 | 2024-12-05 | Si | 202: RECHAZADA | 80: fecha fuera de rango | 200: XML |
| 2641361 | 2025-01-09 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2658964 | 2025-01-09 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2676903 | 2025-02-17 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2694887 | 2025-03-14 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2712900 | 2025-04-16 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2730959 | 2025-05-20 | No | 202: timeout | 80: fecha fuera de rango | 202: sin XML |
| 2749019 | 2025-06-12 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2767089 | 2025-07-11 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2785175 | 2025-08-08 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2803345 | 2025-09-09 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2821565 | 2025-10-24 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2839044 | 2025-11-13 | No | 202: timeout | 80: fecha fuera de rango | 202: sin XML |
| 2858175 | 2025-12-19 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2876500 | 2026-01-21 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2894788 | 2026-02-20 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2913397 | 2026-03-23 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2931980 | 2026-04-23 | No | 202: RECHAZADA | 80: fecha fuera de rango | 202: sin XML |
| 2950652 | 2026-05-22 | No | 202: timeout | Sin autorizaciones | 202: sin XML |
| 2969201 | 2026-06-08 | No | 202: Sin autorizaciones | Sin autorizaciones | 202: sin XML |
| 2988499 | 2026-07-07 | No | 202: Sin autorizaciones | Sin autorizaciones | 202: sin XML |
| 3008266 | 2026-08-07 | No | 202: timeout | Sin autorizaciones | 202: sin XML |
| 3033882 | 2026-09-08 | Si | 200: XML | AUTORIZADO | 200: XML |
| 3063307 | 2026-10-07 | Si | 200: XML | AUTORIZADO | 200: XML |

## Entregables y activacion

- `diagnosticos/12260_xml_disponibles.zip`: los 10 XML almacenados, con manifiesto de claves y hashes SHA-256.
- `diagnosticos/12260_endpoint_resultados.json`: resultados de las 32 llamadas reales al API.
- `diagnosticos/12260_directo_resultados.json`: resultados de las 32 consultas directas al SRI.
- `diagnosticos/12260_api_corregido_local.json`: resultados con el codigo corregido y evidencia real.
- `target/sri-files-0.0.1.jar`: artefacto actualizado.

Los diagnosticos y XML reales quedan fuera del control de versiones. La correccion no esta desplegada. Para las 22 facturas restantes hace falta contrastar la consulta usada en el portal y recuperar su XML historico desde el portal/archivo cuando este disponible; mas reintentos no resuelven el codigo 80 observado. No se ha obtenido evidencia XML autorizada para esas 22 en esta prueba.

## Verificacion reproducible

```powershell
mvn -q "-Dtest=ReferenciaXmlEvidenceTest,XmlAutorizadoRecoveryServiceTest,SriGatewayRecoveryTest,AutorizacionRecoveryControllerTest,SriSoapTimeoutTest,RecuperarDevueltasSchedulerTest,FacturaRecoveryQueryTest,SriAutorizacionRetryPolicyTest,SriControllerHelperTest" "-Dsri.fixture-dir=diagnosticos" test
mvn -q -DskipTests package
```

Pasaron 55 pruebas; fallos 0, errores 0. El test con evidencia requiere los archivos locales de diagnosticos; no ejecuta SQL ni llamadas de red.
