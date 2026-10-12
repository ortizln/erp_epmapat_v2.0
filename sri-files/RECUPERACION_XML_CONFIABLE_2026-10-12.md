# Recuperacion confiable de XML autorizado

Endpoint: `GET /api/v1/autorizacion/{claveAcceso}/xml`. Cambios compilados, probados y empaquetados; no desplegados.

## Hallazgos

La consulta de la clave indicada por el usuario respondio HTTP 404 con `java.net.SocketTimeoutException: Connect timed out`. Ese resultado representa una consulta fallida, no evidencia de que la factura no este autorizada. No se obtuvo XML ni se actualizo la factura durante esa comprobacion.

El codigo local hacia una sola consulta al SRI cuando no encontraba cache. Cuando encontraba XML en `fec_factura`, devolvia como `estado` el estado operativo de la fila (por ejemplo M o C), lo que podia confundirse con el estado de autorizacion. Ademas, si habia varias autorizaciones, una entrada AUTORIZADO sin comprobante podia ocultar otra entrada con XML.

## Comportamiento

- XML valido almacenado: HTTP 200 con `estado=AUTORIZADO`, sin consultar al SRI por defecto. Se conserva el XML firmado, incluidas sus entidades. La columna de estado de la factura y el resultado de autorizacion son conceptos distintos.
- Sin cache: hasta tres consultas al SRI, con espera progresiva y timeouts por intento. La primera respuesta vacia o negativa no corta la recuperacion.
- Varias autorizaciones en la respuesta: se prioriza AUTORIZADO con comprobante XML disponible. Se admiten XML, CDATA y Base64; se verifica que el XML corresponda a la clave solicitada.
- Sin XML confirmado al agotar los intentos: HTTP 202, `estado=PENDIENTE_CONSULTA`, XML vacio y `Retry-After: 5`. `detalleUltimaConsulta` conserva el motivo observado. Esta respuesta no afirma autorizacion ni rechazo definitivo.
- `forzarSri=true`: consulta SRI incluso con cache. Si la consulta resulta inconclusa, conserva el XML autorizado almacenado e informa su fuente y el motivo del respaldo en `mensaje`.
- Solicitudes simultaneas por la misma clave comparten la consulta en curso dentro de una instancia. No se almacenan respuestas negativas como cache.
- Fallos al leer la cache no impiden intentar la consulta al SRI. Si se recupera XML pero falla su guardado, se devuelve el XML con `actualizadoEnFecFactura=false` y una `advertencia`. No se presenta como persistencia exitosa.
- Clave invalida: HTTP 400. Errores internos inesperados: HTTP 500.

El scheduler mantiene sus reintentos durables anteriores. El polling breve se aplica al endpoint de consulta para no multiplicar las esperas de cada lote del scheduler. No se generan, firman ni reenvian comprobantes.

## Configuracion

| Variable | Predeterminado |
|---|---:|
| `SRI_XML_RECOVERY_MAX_ATTEMPTS` | 3 |
| `SRI_XML_RECOVERY_WAIT_BUDGET_MS` | 12000 |
| `SRI_XML_RECOVERY_ATTEMPT_TIMEOUT_MS` | 3500 |
| `SRI_XML_RECOVERY_RETRY_DELAY_MS` | 250 |

Los timeouts SOAP se aplican al puerto de esa solicitud; no alteran las configuraciones globales de otras consultas/envios. El presupuesto limita nuevos intentos y esperas; no es un SLA absoluto de tiempo HTTP: inicializacion SOAP, DNS/TLS, base de datos y otros componentes pueden agregar latencia. Los valores se validan al arrancar y tambien pueden venir del Config Server.

## Verificacion

48 pruebas pasaron, incluyendo cache con estado M/C, respuesta negativa seguida de XML autorizado, agotamiento de intentos, respaldo ante fallos, consultas compartidas, priorizacion de autorizaciones con XML, timeouts SOAP, persistencia fallida, consultas JPQL y scheduler.

```powershell
mvn -q "-Dtest=XmlAutorizadoRecoveryServiceTest,AutorizacionRecoveryControllerTest,SriSoapTimeoutTest,SriGatewayRecoveryTest,RecuperarDevueltasSchedulerTest,FacturaRecoveryQueryTest,SriAutorizacionRetryPolicyTest,SriControllerHelperTest" test
mvn -q -DskipTests package
```

Artefacto: `target/sri-files-0.0.1.jar`.

Despues del despliegue, comprobar la clave de ejemplo usando la misma URL. Un HTTP 200 debe incluir XML y AUTORIZADO; un HTTP 202 debe conservarse como consulta pendiente y reintentarse. Revisar `fuente` y `actualizadoEnFecFactura`, ademas del XML. Los consumidores que antes interpretaban cualquier respuesta no autorizada/404 como rechazo deben tratar ahora el 202 como inconcluso.

No se puede garantizar un 100 % de recuperacion inmediata mientras SRI o la conectividad no esten disponibles. La mejora evita estados contradictorios, aprovecha evidencia almacenada y aumenta la recuperacion ante fallos temporales. No acredita que la version desplegada ya incorpore la correccion.
