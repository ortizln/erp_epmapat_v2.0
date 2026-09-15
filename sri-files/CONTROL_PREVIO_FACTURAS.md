Control previo de facturas y recuperación de septiembre
=====================================================

El filtro se ejecuta antes de generar el XML desde fec_factura, antes de firmar
y nuevamente antes de invocar recepción. Una validación fallida bloquea el envío.
El diagnóstico no firma, no envía y no modifica los importes.

Comprobaciones implementadas
---------------------------

- Datos obligatorios de emisor, comprador, establecimiento y detalles.
- Clave de 49 dígitos, módulo 11, fecha, RUC, ambiente, tipo de documento,
  establecimiento, punto, secuencial, tipo de emisión y duplicados locales.
- Cantidad positiva, precios/descuentos no negativos y descuento no superior al bruto.
- Base de cada impuesto = cantidad × precio − descuento, redondeada a centavos.
- IVA explícito por línea; códigos desconocidos o impuestos ausentes bloquean.
- Totales y grupos de impuestos del XML coinciden con los detalles.
- Pagos reales de fec_factura_pagos suman subtotal neto + IVA.
- Rubro 5 (intereses) se contrasta con facturas.interescobrado si está disponible.
  No se recalculan intereses a la fecha del reintento. Si falta la fuente,
  el diagnóstico muestra que no pudo realizar ese contraste.

El generador conserva clave y fecha originales; no inventa otra clave en un
reintento. Los pagos ya no se sustituyen por un pago calculado con forma 20.
Se conserva la consolidación de rubros 1006/1007 únicamente si no pierde
descuentos ni impuestos.

Alcance: facturas locales de este emisor y XML de factura 1.0.0/1.1.0 con IVA
de los códigos soportados (0,2,3,4,5,6,7), sin propina. Otras tarifas/impuestos
o variantes deben implementarse expresamente; no se convierten a IVA cero.
La validación comprueba consistencia, no determina por sí sola la procedencia
tributaria de una tarifa ni sustituye una validación completa del XSD o la
autorización del SRI. Referencia oficial:
https://www.sri.gob.ec/facturacion-electronica

Diagnóstico real consultado el 15 de septiembre de 2026
------------------------------------------------------

La consulta se ejecutó con transacción PostgreSQL de solo lectura contra la base
configurada para msvc-sri. No se modificaron registros ni se enviaron documentos.
Las cifras son una fotografía de esa consulta, no un conteo permanente.

Estados de septiembre: A=8254, E=1199, I=17, O=354.

Motivos de las 1199 en E:

- 1053: máximo de intentos, sin autorizaciones.
- 145: máximo de intentos, Connection reset.
- 1: error de correo posterior a autorización.

Contraste de cabecera, clave, detalles, impuestos y pagos mediante consulta SQL:

- 945 sin hallazgos en estas comprobaciones preliminares; aún requieren el filtro
  completo del XML y conciliación SRI.
- 254 con pagos que no coinciden con detalles más IVA.
- En 249 de esas 254, la diferencia coincide exactamente con intereses más IVA.
  Es evidencia de pagos incompletos, no autorización para cambiar lo cobrado.
- 2 de las 254 también presentan detalles con valores inválidos:
  2426428 y 2443883.
- 1 también presenta base de impuesto inconsistente: 3055554.
- Otras diferencias que no corresponden exactamente a intereses + IVA:
  2961284, 2980486, 2980487, 2999926 y 3055554.
- Dos E ya contienen XML autorizado: 3035944 y 3045746. Deben revisarse
  como recuperación/postautorización, sin reenviar a recepción.

Ejemplo: 2543150 tiene subtotal 9.08, IVA 1.20 y total 10.28, mientras el pago
almacenado es 7.98. La diferencia 2.30 coincide con 1.10 de intereses + 1.20 de IVA.
El filtro informa el descuadre y bloquea.

El reporte por factura se genera con tools/diagnostico_facturas.py. No es una
confirmación de autorización ni reemplaza el endpoint de validación.

Operación
---------

1. Desplegar el JAR actualizado de sri-files en el entorno previsto. Desde
   microservicesEpmapa-T:

       mvn -f sri-files/pom.xml "-Dtest=*,!SriFilesApplicationTests" package
       docker compose build msvc-sri
       docker compose up -d --no-deps msvc-sri

   El test de contexto omitido requiere configuración y base externas.
   Revisar los schedulers de comercializacion que comparten fec_factura:
   allí existen interpretaciones distintas de C/P y también escrituras de E.
   Los bloqueos añadidos protegen los envíos de sri-files; no coordinan código
   externo que modifique la misma factura sin participar del bloqueo.

2. Consultar el diagnóstico paginado. hasta es exclusivo:

       GET /api/v1/facturas/diagnostico?desde=2026-09-01&hasta=2026-10-01&estado=E&pagina=0&limite=50
       GET /api/v1/facturas/2543150/validacion

   Sin fechas, usa el mes actual. Límite máximo: 200. El resultado expone
   errores anteriores, validación por campo, totales e incidencias del XML.
   datosCompletos no significa AUTORIZADA ni confirma ausencia en el SRI.

3. Corregir los datos identificados a partir del recibo y las tablas de origen.
   Para las diferencias de pago, contrastar el total efectivamente cobrado,
   intereses y tarifa aplicable antes de actualizar fec_factura_pagos.
   No sumar intereses nuevamente al detalle: en la muestra ya están incluidos.
   Las claves faltantes deben recuperarse del documento original y del historial.

4. Recuperar individualmente:

       POST /api/v1/facturas/2543150/recuperar

   - YA_TIENE_XML_AUTORIZADO: no se envía; revisar estado/correo.
   - AUTORIZADA_RECUPERADA: se guarda XML del SRI y pasa a A.
   - REQUIERE_CORRECCION: no se modifica ni se habilita envío.
   - CONSULTA_INCONCLUSA / REQUIERE_REVISION_SRI: no se habilita envío.
   - REQUIERE_CONCILIACION_CLAVE: datos completos y consulta vacía.
     Contrastar clave con XML/RIDE/historial original. El generador anterior
     podía haber usado otra clave y otra fecha sin guardarlas en fec_factura.

   Solo después de verificar la identidad original:

       POST /api/v1/facturas/2543150/recuperar?claveOriginalVerificada=true

   Vuelve a consultar el SRI y validar. Únicamente una respuesta explícita con
   cero comprobantes, sin evidencia de código 43/70, y datos completos permite
   pasar E/M/N a I. El scheduler realiza el envío después, con el filtro activo.
   Esta opción no omite ningún control y no regenera claves.

5. Verificar autorización y XML resultantes, además del eventual correo.
   No ejecutar un UPDATE masivo E→I: E también incluye fallos posteriores
   a autorización.

Estados y transporte
--------------------

Los rechazos locales quedan E con errores VALIDACION_PREVIA. Los errores 43/70
y cortes de conexión después de intentar recepción quedan C para consulta.
Fallos posteriores a guardar autorización quedan O. Un documento con XML
autorizado no se reenvía aunque su estado indique I.

El batch usa transacción independiente por factura y bloqueo de fila; el API de
envío usa el mismo bloqueo. El contador exitosas exige XML autorizado.
La llamada POST original /api/v1/facturas acepta solo I sin XML autorizado;
para E/M/N usar el flujo de recuperación. Devuelve 422 ante validación fallida,
409 si no está pendiente, y 202 si la recepción quedó pendiente de consulta.

Reproducir auditoría sin desplegar
---------------------------------

    python -m pip install psycopg2-binary
    python tools/diagnostico_facturas.py --compose ../docker-compose.yml --desde 2026-09-01 --hasta 2026-10-01 --salida diagnostico-septiembre.json

También acepta DB_URL, DB_USER y DB_PASS del entorno sin --compose. Todas las
consultas se ejecutan en modo de solo lectura y tienen límite de duración.
El JSON contiene identificadores y errores: guardarlo en una ubicación interna,
no en un repositorio público.
