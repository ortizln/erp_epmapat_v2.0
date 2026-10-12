# Recuperacion automatica de XML para facturas en estado M

Correccion local, compilada y empaquetada. No se ha desplegado ni ejecutado el scheduler contra produccion.

## Diferencias encontradas

El endpoint manual `/api/v1/autorizacion/{clave}/xml` buscaba primero en `documento_electronico` y `fec_factura`, mientras `RecuperarDevueltasScheduler` consultaba siempre al SRI. Que la descarga manual responda inmediatamente no demuestra que una consulta nueva al SRI responda igual: puede devolver el XML almacenado.

El scheduler convertia M a E despues de cinco respuestas sin XML. Los logs locales del 26 de agosto muestran lotes con 20 pendientes, ninguna recuperada y posteriores mensajes de maximo de intentos alcanzado. Es evidencia del comportamiento anterior; no confirma el motivo de todos los errores del despliegue actual.

La anotacion del cron leia `sri.scheduler.recuperar-devueltas`, pero la configuracion define `sri.scheduler.recuperar-devueltas.cron`.

## Comportamiento corregido

`XmlAutorizadoRecoveryService` es compartido por la consulta manual y el scheduler. Busca XML almacenado primero; cuando falta o es invalido consulta al SRI mediante `SriGateway`. `forzarSri=true` sigue permitiendo omitir la cache en el endpoint manual.

Se exige XML bien formado, de un tipo de comprobante admitido y con la misma clave de acceso. La respuesta remota debe ser AUTORIZADO. El contenido XML se conserva, incluidas sus entidades; el parseo valida estructura/clave, sin verificar criptograficamente la firma.

Al recuperar una factura M se actualizan `xmlautorizado`, estado A, errores y seguimiento de intentos mediante una actualizacion condicional. No se guarda la entidad completa. Si el estado o la clave cambian durante la consulta, no se sobrescriben. Se conserva la fecha de autorizacion existente; cuando falta se usa la fecha disponible del SRI/cache o, en su ausencia, la hora de recuperacion.

Si falta XML, hay problemas de red o la clave es invalida, se persisten el motivo y el intento y se conserva M. No se convierte automaticamente a E por cantidad de intentos. Los reintentos usan el backoff existente (base 60 segundos, maximo 30 minutos, con jitter); el lote prioriza pendientes antiguos para evitar insistir siempre en las mismas primeras facturas.

El scheduler evita ejecuciones simultaneas dentro de la misma instancia. Las actualizaciones condicionales tambien protegen cambios concurrentes, aunque varias replicas pueden duplicar una consulta al SRI. La recuperacion no genera, firma ni reenvia comprobantes, ni envia correos.

## Configuracion

- `SRI_SCHEDULER_RECUPERAR_DEVUELTAS_CRON`: predeterminado `0 */5 * * * *`.
- `SRI_SCHEDULER_RECUPERAR_DEVUELTAS_LOTE`: predeterminado 20; el scheduler limita el valor a 1–200.
- Backoff: `sri.autorizacion.retry.base-ms` y `sri.autorizacion.retry.max-ms`.

Revisar tambien las propiedades efectivas del Config Server al desplegar. No se agregan columnas ni se requiere una migracion para el codigo nuevo.

## Validacion

34 pruebas pasaron: recuperacion desde ambas tablas y desde SRI, clave con espacios, XML de otra clave/invalido, consulta forzada, preservacion del XML, errores/reintentos, concurrencia local, endpoint manual y consultas JPQL sobre los mapeos reales de Hibernate. Las pruebas no se conectan a produccion; no se ejecuto la seleccion SQL nativa contra PostgreSQL. Docker no estaba activo para levantar una base de prueba aislada.

```powershell
mvn -q "-Dtest=AutorizacionRecoveryControllerTest,FacturaRecoveryQueryTest,XmlAutorizadoRecoveryServiceTest,RecuperarDevueltasSchedulerTest,SriAutorizacionRetryPolicyTest,SriControllerHelperTest" test
mvn -q -DskipTests package
```

Artefacto generado: `target/sri-files-0.0.1.jar`.

## Comprobacion despues del despliegue

El log `Recuperacion XML idfactura=... fuente=... filasActualizadas=1` confirma la actualizacion. `filasActualizadas=0` indica que la factura ya no cumplia el estado/clave esperado. Revisar ademas el resumen de recuperadas, pendientes y fallidas.

Consulta de solo lectura para verificar cobertura:

```sql
SELECT UPPER(TRIM(estado)) AS estado,
       COUNT(*) AS facturas,
       COUNT(*) FILTER (WHERE NULLIF(TRIM(xmlautorizado), '') IS NOT NULL) AS con_xml
FROM fec_factura
WHERE UPPER(TRIM(estado)) IN ('M', 'A', 'E')
GROUP BY UPPER(TRIM(estado));

SELECT idfactura, estado, intentos_autorizacion, fecha_ultimo_intento, errores
FROM fec_factura
WHERE UPPER(TRIM(estado)) = 'E'
  AND errores LIKE 'Máximo de intentos alcanzado:%'
ORDER BY idfactura;
```

Las facturas que el scheduler anterior ya paso a E no entran en la seleccion de M. Identificarlas y revisar cada grupo antes de reactivarlo; no cambiar masivamente todas las E, que tambien pueden contener rechazos reales o errores de datos. Ninguna factura E se ha reactivado automaticamente con esta correccion.
