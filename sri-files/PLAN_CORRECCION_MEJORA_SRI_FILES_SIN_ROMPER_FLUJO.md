# PLAN DE CORRECCIÓN Y MEJORA — `sri-files`

## Evolución segura del microservicio de comprobantes electrónicos sin romper el flujo actual

**Proyecto:** EPMAPA-T — `sri-files`  
**Microservicio actual:** `msvc-sri`  
**Tecnología:** Java 17 + Spring Boot 3.4.1 + PostgreSQL  
**Objetivo:** Corregir, ordenar y ampliar el procesamiento de comprobantes electrónicos manteniendo operativo el flujo existente.  
**Principio:** **NO realizar una reescritura total. Evolucionar por capas, con compatibilidad hacia atrás y migración progresiva.**

---

# 1. OBJETIVO GENERAL

Transformar progresivamente `sri-files` en un servicio especializado para administración integral de comprobantes electrónicos.

El servicio deberá ser capaz de recibir un JSON normalizado por tipo de comprobante y ejecutar:

```text
JSON
  ↓
VALIDACIÓN
  ↓
PERSISTENCIA
  ↓
GENERACIÓN XML
  ↓
VALIDACIÓN XML
  ↓
FIRMA XAdES-BES
  ↓
RECEPCIÓN SRI
  ↓
AUTORIZACIÓN SRI
  ↓
ALMACENAMIENTO XML AUTORIZADO
  ↓
GENERACIÓN RIDE/PDF
  ↓
ENVÍO DE CORREO
  ↓
FINALIZADO
```

El cambio debe realizarse **sin eliminar inicialmente los endpoints, tablas, estados, schedulers y servicios que actualmente funcionan para facturas y retenciones**.

---

# 2. SITUACIÓN ACTUAL QUE DEBE CONSERVARSE

Actualmente existen flujos funcionales para:

- Facturas.
- Comprobantes de retención.
- Firma XAdES-BES.
- Recepción SRI.
- Consulta de autorización.
- Polling.
- Generación de PDF.
- Correo electrónico.
- Scheduler de facturas.
- Recuperación de XML autorizado.
- Certificado PKCS12.
- Integración con `msvc-emails`.

Por lo tanto:

> **La primera regla de la mejora es no reemplazar estos procesos hasta que el flujo nuevo esté probado en paralelo.**

---

# 3. COMPROBANTES A SOPORTAR

La arquitectura final deberá permitir administrar:

| Código SRI | Documento | Prioridad |
|---|---|---|
| `01` | Factura | EXISTENTE / PRIORIDAD 1 |
| `03` | Liquidación de compra de bienes y prestación de servicios | NUEVO |
| `04` | Nota de crédito | NUEVO |
| `05` | Nota de débito | NUEVO |
| `06` | Guía de remisión | NUEVO |
| `07` | Comprobante de retención | EXISTENTE / PRIORIDAD 1 |

No crear seis aplicaciones independientes.

Todos los documentos deben compartir una **infraestructura transversal** y mantener implementaciones específicas únicamente para las diferencias de cada XML.

---

# 4. PRINCIPIO DE MIGRACIÓN

Aplicar estrategia:

```text
LEGACY FUNCIONAL
      │
      ├───────────────┐
      │               │
      ▼               ▼
Endpoints actuales   API V1 nueva
      │               │
      └───────┬───────┘
              ▼
      Servicios compartidos
              │
              ▼
         Motor SRI nuevo
```

Durante la transición:

```text
NO eliminar endpoint actual
NO cambiar contratos actuales
NO cambiar inmediatamente estados legacy
NO eliminar scheduler actual
NO cambiar tablas legacy sin migración
NO modificar la recaudación ERP
```

---

# 5. REGLA FUNDAMENTAL: SRI-FILES NO DEBE MODIFICAR RECAUDACIÓN

El nuevo módulo debe separar claramente:

```text
HECHO ECONÓMICO / RECAUDACIÓN
              │
              │ referencia
              ▼
COMPROBANTE ELECTRÓNICO
```

`sri-files` debe administrar el comprobante tributario.

No debe:

- modificar una recaudación histórica;
- cambiar la fecha real de cobro;
- alterar valores contables para hacer coincidir un XML;
- eliminar registros del ERP origen;
- reconstruir silenciosamente transacciones anteriores.

Debe almacenar referencias como:

```text
externalId
idOrigen
tipoOrigen
fechaTransaccionOrigen
referenciaOrigen
```

---

# 6. FECHAS — CORRECCIÓN PRIORITARIA

No utilizar un único campo `fechaemision` para representar todo el proceso.

Crear fechas independientes:

```text
fechaTransaccionOrigen
fechaRecepcionJson
fechaEmision
fechaGeneracionXml
fechaFirma
fechaEnvioSri
fechaRecepcionSri
fechaConsultaAutorizacion
fechaAutorizacionSri
fechaGeneracionRide
fechaEnvioCorreo
fechaAnulacion
fechaUltimaActualizacion
```

## Regla

Nunca reemplazar:

```text
fechaEmision
```

con:

```text
fechaAutorizacionSri
```

ni viceversa.

La fecha de autorización debe provenir de la respuesta del SRI.

---

# 7. VALIDACIÓN DE FECHA DE EMISIÓN

Antes de generar y enviar un XML:

```text
JSON
 ↓
validar fechaEmision
 ↓
¿fecha permitida según reglas configuradas?
 ├─ Sí → continuar
 └─ No → REQUIERE_REVISION
```

No corregir automáticamente una fecha histórica.

Registrar:

```text
codigoError
mensaje
fechaDetectada
usuario/proceso
payloadOriginal
```

La política tributaria concreta debe quedar parametrizada y respaldada por el criterio institucional correspondiente.

---

# 8. NO SOBRESCRIBIR COMPROBANTES

Un comprobante autorizado, rechazado, anulado o reemplazado no debe ser destruido.

Mantener:

```text
DOCUMENTO ORIGINAL
      │
      ├── XML generado
      ├── XML firmado
      ├── respuesta recepción
      ├── XML autorizado
      ├── RIDE
      ├── errores
      └── historial
             │
             ▼
      DOCUMENTO CORRECTIVO
```

Agregar relaciones:

```text
documentoOrigenId
documentoSustitutoId
tipoRelacion
motivoCorreccion
```

---

# 9. NUEVO MODELO CENTRAL DE DOCUMENTO

Crear una entidad transversal:

```text
DocumentoElectronico
```

Campos mínimos sugeridos:

```text
id
uuid
externalId

tipoDocumento
estado
subestado

rucEmisor
establecimiento
puntoEmision
secuencial
numeroDocumento
claveAcceso
ambiente

fechaTransaccionOrigen
fechaEmision
fechaRecepcionJson
fechaGeneracionXml
fechaFirma
fechaEnvioSri
fechaAutorizacionSri
fechaGeneracionRide
fechaEnvioCorreo

identificacionReceptor
razonSocialReceptor
emailReceptor

subtotal
totalImpuestos
totalDescuento
importeTotal

jsonOriginal
xmlGenerado
xmlFirmado
xmlAutorizado

numeroAutorizacion
mensajeSri

origenSistema
idOrigen

documentoOrigenId
documentoSustitutoId
motivoCorreccion

intentosEnvio
intentosAutorizacion
intentosCorreo

createdAt
updatedAt
createdBy
```

---

# 10. NO MIGRAR `fec_factura` DE GOLPE

`fec_factura` actualmente participa en el flujo productivo.

Por lo tanto:

### Fase inicial

```text
fec_factura
    │
    ├── flujo actual continúa
    │
    └── adaptador sincroniza/crea DocumentoElectronico
```

### Fase posterior

```text
DocumentoElectronico
      ↓
se convierte en fuente principal
```

Solo después de pruebas y conciliación se evaluará retirar dependencias legacy.

---

# 11. ESTADOS NUEVOS

Evitar depender únicamente de:

```text
I P A N C O M U
```

Crear estados descriptivos:

```text
RECIBIDO
VALIDANDO
VALIDADO
GENERANDO_XML
XML_GENERADO
FIRMANDO
FIRMADO
ENVIANDO_SRI
RECIBIDO_SRI
PENDIENTE_AUTORIZACION
AUTORIZADO
NO_AUTORIZADO
GENERANDO_RIDE
RIDE_GENERADO
PENDIENTE_CORREO
ENVIANDO_CORREO
CORREO_ENVIADO
FINALIZADO
REQUIERE_REVISION
ERROR
ANULADO
```

---

# 12. COMPATIBILIDAD CON ESTADOS LEGACY

Durante la migración crear un mapper:

```text
EstadoNuevo → EstadoLegacy
```

Ejemplo:

```text
RECIBIDO                 → I
VALIDANDO                → P
GENERANDO_XML            → P
FIRMANDO                 → P
ENVIANDO_SRI             → P
PENDIENTE_AUTORIZACION   → C
AUTORIZADO               → A
PENDIENTE_CORREO         → O
NO_AUTORIZADO            → N
ERROR                     → U
```

No cambiar consumidores legacy hasta que se migren.

---

# 13. HISTORIAL DE ESTADOS

Crear:

```text
documento_estado_historial
```

Campos:

```text
id
documento_id
estado_anterior
estado_nuevo
etapa
mensaje
codigo
fecha
usuario
request_id
metadata
```

Nunca depender únicamente del estado actual.

---

# 14. AUDITORÍA

Registrar acciones como:

```text
DOCUMENTO_RECIBIDO
XML_GENERADO
XML_VALIDADO
DOCUMENTO_FIRMADO
ENVIO_SRI
RESPUESTA_RECEPCION
CONSULTA_AUTORIZACION
AUTORIZADO
NO_AUTORIZADO
RIDE_GENERADO
CORREO_ENCOLADO
CORREO_ENVIADO
REPROCESADO
ANULACION_REGISTRADA
DOCUMENTO_RELACIONADO
CERTIFICADO_CAMBIADO
```

---

# 15. IDEMPOTENCIA

El API JSON debe recibir:

```text
externalId
```

único por sistema origen.

Ejemplo:

```json
{
  "externalId": "ERP-FAC-348396"
}
```

Si el ERP reintenta accidentalmente:

```text
POST documento
POST documento
POST documento
```

no deben crearse tres comprobantes.

Respuesta esperada:

```text
externalId existente
→ devolver documento existente
→ no consumir nuevo secuencial
→ no enviar nuevamente al SRI
```

---

# 16. REQUEST ID

Cada procesamiento debe tener:

```text
requestId
```

Ejemplo:

```text
7bb51021-8fd0-4c24-86dd-8b7f...
```

Propagarlo por:

```text
Controller
Service
XML
Firma
SRI
PDF
Correo
Logs
Auditoría
```

---

# 17. ARQUITECTURA OBJETIVO

```text
                 API REST
                    │
          DocumentoController
                    │
            DocumentoService
                    │
            ProcessManager
                    │
     ┌──────────────┼───────────────┐
     │              │               │
 VALIDACIÓN      XML ENGINE       STORAGE
     │              │               │
     │         XmlGenerator         │
     │              │               │
     └──────────────┼───────────────┘
                    │
               SIGN ENGINE
                    │
               SRI CLIENT
                    │
          AUTHORIZATION ENGINE
                    │
              RIDE ENGINE
                    │
              EMAIL ENGINE
```

---

# 18. CONTROLADOR

El actual `SRI_Controller` es demasiado grande.

No eliminarlo inicialmente.

Crear progresivamente:

```text
DocumentoController
FacturaControllerV1
RetencionControllerV1
LiquidacionCompraControllerV1
NotaCreditoControllerV1
NotaDebitoControllerV1
GuiaRemisionControllerV1
AutorizacionControllerV1
ArchivoControllerV1
CertificadoControllerV1
AdministracionControllerV1
```

Los endpoints legacy continúan funcionando y delegan gradualmente en servicios nuevos.

---

# 19. ORQUESTADOR CENTRAL

Crear:

```java
DocumentoProcessManager
```

Responsabilidad:

```text
recibir
validar
persistir
generar XML
firmar
enviar
consultar autorización
generar RIDE
encolar correo
actualizar estados
registrar historial
```

No debe contener la implementación XML específica de cada documento.

---

# 20. ESTRATEGIA POR TIPO DE DOCUMENTO

Crear contrato:

```java
public interface DocumentoProcessor {
    TipoDocumento soporta();
    ValidationResult validar(...);
    String generarXml(...);
}
```

Implementaciones:

```text
FacturaProcessor
LiquidacionCompraProcessor
NotaCreditoProcessor
NotaDebitoProcessor
RetencionProcessor
GuiaRemisionProcessor
```

---

# 21. GENERADORES XML

Separar:

```text
XmlGenerator
├── FacturaXmlGenerator
├── LiquidacionCompraXmlGenerator
├── NotaCreditoXmlGenerator
├── NotaDebitoXmlGenerator
├── RetencionXmlGenerator
└── GuiaRemisionXmlGenerator
```

El `FacturaXmlGeneratorService` actual debe adaptarse, no eliminarse de inmediato.

---

# 22. VALIDACIÓN TRANSVERSAL

Crear:

```text
CommonSriValidator
```

Validar:

```text
RUC emisor
ambiente
tipo emisión
codDoc
establecimiento
punto emisión
secuencial
fecha emisión
clave acceso
dígito verificador
campos obligatorios
totales
```

---

# 23. VALIDACIÓN ESPECÍFICA

```text
FacturaValidator
LiquidacionCompraValidator
NotaCreditoValidator
NotaDebitoValidator
RetencionValidator
GuiaRemisionValidator
```

Reutilizar y evolucionar `SriRetencionValidationService`.

---

# 24. VALIDACIONES DE TOTALES

Antes de firmar validar:

```text
subtotal
descuento
base imponible
impuestos
importe total
pagos
```

Nunca modificar silenciosamente un total para hacerlo cuadrar.

Si existe diferencia:

```text
REQUIERE_REVISION
```

y registrar:

```text
valorRecibido
valorCalculado
diferencia
reglaAplicada
```

---

# 25. PRECISIÓN Y REDONDEO

Todos los valores monetarios deben utilizar:

```java
BigDecimal
```

Evitar:

```java
float
double
```

para cálculos tributarios.

El campo actual:

```text
definir.iva / porciva
```

debe revisarse porque existe mezcla `float/BigDecimal`.

No migrar automáticamente sin pruebas.

---

# 26. POLÍTICA DE REDONDEO

Centralizar:

```text
MoneyCalculationService
```

No permitir que cada documento use una regla diferente.

Debe controlar:

```text
scale de cálculo
scale monetaria
RoundingMode
distribución de residuos
comparación de totales
```

La política concreta deberá ser parametrizable y aprobada institucionalmente.

---

# 27. CASOS DE CENTAVOS

Crear tests específicos para:

```text
0.10 / 12
1.00 / 3
porcentajes con más de 2 decimales
IVA distribuido entre múltiples detalles
descuentos proporcionales
intereses
convenios
notas de crédito parciales
```

El sistema debe garantizar:

```text
SUMA COMPONENTES == TOTAL DOCUMENTO
```

según la política aprobada.

---

# 28. FACTURA — MEJORAS

Mantener el flujo actual y agregar:

- DTO JSON V1.
- Validación previa completa.
- validación de fecha.
- validación de totales.
- historial.
- idempotencia.
- almacenamiento separado de XML generado, firmado y autorizado.
- relación con documento correctivo.
- RIDE versionado.
- auditoría.
- reprocesamiento por etapa.

---

# 29. LIQUIDACIÓN DE COMPRA — NUEVO

Implementar como módulo independiente sobre el motor común.

Debe contemplar:

```text
infoTributaria
infoLiquidacionCompra
proveedor
dirección proveedor
detalles
impuestos
pagos
infoAdicional
```

Crear:

```text
LiquidacionCompraRequest
LiquidacionCompraValidator
LiquidacionCompraXmlGenerator
LiquidacionCompraRideGenerator
```

---

# 30. NOTA DE CRÉDITO — NUEVO

Debe conservar relación obligatoria con el comprobante modificado.

Campos de control:

```text
codDocModificado
numDocModificado
fechaEmisionDocSustento
motivo
valorModificacion
documentoOrigenId
```

Validar que el documento de referencia exista cuando sea administrado internamente.

Nunca sobrescribir la factura original.

---

# 31. NOTA DE DÉBITO — NUEVO

Controlar:

```text
documento modificado
fecha documento sustento
motivos
valor
impuestos
total
```

Mantener relación con documento origen.

---

# 32. RETENCIÓN — MEJORAS

Conservar los endpoints actuales.

Evolucionar el flujo para que utilice:

```text
DocumentoProcessManager
RetencionProcessor
CommonSriValidator
RetencionValidator
SignService
SriGateway
RideService
EmailService
```

No retirar `SriRetencionValidationService` hasta disponer de cobertura equivalente.

---

# 33. GUÍA DE REMISIÓN — NUEVO

Debe modelar:

```text
transportista
RUC/identificación
placa
fecha inicio transporte
fecha fin transporte
dirección partida
destinatarios
dirección destino
motivo traslado
documentos sustento
productos
```

Una guía puede contener varios destinatarios.

Diseñar el modelo desde el inicio para esta relación.

---

# 34. CLAVE DE ACCESO

Crear un único:

```text
ClaveAccesoService
```

Responsable de:

```text
construcción
49 dígitos
validación
módulo 11
```

No duplicar algoritmo en cada generador.

---

# 35. SECUENCIALES

Crear control transaccional:

```text
SecuencialService
```

Clave lógica:

```text
empresa
establecimiento
puntoEmision
tipoDocumento
```

Evitar secuenciales duplicados bajo concurrencia.

No consumir un nuevo secuencial en un reintento idempotente del mismo documento.

---

# 36. FIRMA DIGITAL

Mantener inicialmente:

```text
FirmaComprobantesService
XadesBesService
```

Crear posteriormente una fachada:

```text
SignService
```

Todos los documentos deben usar la misma infraestructura XAdES-BES.

---

# 37. CERTIFICADO P12/PFX

Separar el certificado de la tabla monolítica `definir`.

Nueva entidad futura:

```text
certificado_digital
```

Campos:

```text
id
empresa_id
alias
archivo_cifrado / storage_ref
password_cifrado
fecha_inicio
fecha_expiracion
emisor
titular
serial
fingerprint
activo
created_at
updated_at
```

Durante la primera fase, mantener compatibilidad con:

```text
definir.firma
definir.clave_firma
```

---

# 38. SEGURIDAD DEL CERTIFICADO

Existe un riesgo crítico: la clave AES hardcoded no debe continuar como mecanismo definitivo.

Plan:

```text
FASE 1
Mantener lectura legacy para no romper firma.

FASE 2
Introducir SecretEncryptionService.

FASE 3
Clave maestra desde secret/env seguro.

FASE 4
Migrar credenciales cifradas.

FASE 5
Eliminar fallback hardcoded.
```

Nunca migrar todas las claves en una sola ejecución sin backup y validación.

---

# 39. CARGA DE CERTIFICADO

Crear endpoint administrativo:

```text
POST /api/v1/certificados
```

Multipart:

```text
empresaId
alias
archivo .p12/.pfx
password
activar
```

Antes de guardar:

```text
abrir PKCS12
validar contraseña
validar clave privada
leer titular
leer emisor
leer serial
leer fechas
calcular fingerprint
```

Si falla:

```text
NO guardar certificado
NO cambiar certificado activo
```

---

# 40. CAMBIO DE CERTIFICADO SIN INTERRUPCIÓN

Proceso:

```text
CERTIFICADO A activo
       │
       ▼
Cargar CERTIFICADO B
       │
       ▼
Validar B
       │
       ├── error → A continúa activo
       │
       └── correcto
              ↓
        activar B
              ↓
        desactivar A
```

No eliminar A.

---

# 41. SRI GATEWAY

Evolucionar `SendXmlToSriService` hacia una fachada:

```text
SriGateway
```

Métodos:

```text
enviarRecepcion()
consultarAutorizacion()
consultarAutorizacionConPolling()
```

Mantener internamente el cliente SOAP actual durante la migración.

---

# 42. RESPUESTAS SRI

Persistir cada interacción.

Tabla:

```text
documento_sri_intento
```

Campos:

```text
id
documento_id
operacion
intento
fecha_inicio
fecha_fin
duracion_ms
resultado
codigo
mensaje
respuesta
request_id
```

---

# 43. POLLING

No realizar polling infinito.

Configurar:

```text
maxIntentos
delayInicial
backoff
delayMaximo
```

Al superar límite:

```text
PENDIENTE_AUTORIZACION
```

y permitir que scheduler continúe posteriormente.

---

# 44. SCHEDULER

No eliminar `EnvioSriBatchService`.

Primero dividir responsabilidades:

```text
LegacyFacturaScheduler
NuevoDocumentoScheduler
```

El nuevo scheduler debe trabajar únicamente sobre documentos migrados/nuevos.

Evitar que ambos procesen el mismo comprobante.

---

# 45. LOCK DE PROCESAMIENTO

Para evitar doble procesamiento:

```text
processingLock
lockedAt
lockedBy
```

o mecanismo transaccional equivalente.

Un documento no debe ser enviado simultáneamente por:

```text
API
scheduler
reproceso manual
```

---

# 46. REPROCESAMIENTO

No implementar:

```text
REPROCESAR TODO
```

como única opción.

Permitir reproceso desde etapa:

```text
VALIDACIÓN
XML
FIRMA
RECEPCIÓN SRI
AUTORIZACIÓN
RIDE
CORREO
```

Ejemplo:

```text
ERROR_RIDE
→ regenerar RIDE
→ NO volver a enviar al SRI
```

---

# 47. RIDE / PDF

Crear fachada:

```text
RideService
```

Implementaciones:

```text
FacturaRideGenerator
LiquidacionCompraRideGenerator
NotaCreditoRideGenerator
NotaDebitoRideGenerator
RetencionRideGenerator
GuiaRemisionRideGenerator
```

Mantener `XmlToPdfService` y `RetencionPdfService` como adaptadores inicialmente.

---

# 48. VERSIONADO DE PLANTILLAS

Actualmente factura ya maneja versiones.

Formalizar:

```text
tipoDocumento
versionPlantilla
fechaVigenciaDesde
fechaVigenciaHasta
activa
```

Nunca modificar una plantilla histórica y regenerar silenciosamente un RIDE antiguo con formato incompatible.

---

# 49. ARCHIVOS

Crear abstracción:

```text
DocumentStorageService
```

Tipos:

```text
JSON_ORIGINAL
XML_GENERADO
XML_FIRMADO
XML_AUTORIZADO
RIDE
RESPUESTA_SRI
```

Inicialmente puede persistirse en BD donde ya exista.

Después podrá migrarse a filesystem/object storage sin cambiar el dominio.

---

# 50. HASH DE ARCHIVOS

Registrar:

```text
SHA-256
```

para XML autorizado y RIDE cuando sea conveniente.

Objetivo:

```text
integridad
auditoría
detección de alteraciones
```

---

# 51. CORREO

Mantener:

```text
MailService
EmailMsClientService
msvc-emails
```

No crear un segundo SMTP dentro de `sri-files`.

Evolucionar hacia:

```text
EmailDocumentService
```

que encole:

```text
XML autorizado
RIDE
destinatario
tipo documento
id documento
```

---

# 52. CORREO NO DEBE BLOQUEAR AUTORIZACIÓN

Si:

```text
SRI = AUTORIZADO
Correo = ERROR
```

el documento no debe regresar a estado de envío SRI.

Debe quedar:

```text
AUTORIZADO
+
PENDIENTE_CORREO
```

---

# 53. BASE DE DATOS PROPIA — MIGRACIÓN PROGRESIVA

Objetivo final:

```text
sri_files_db
```

pero NO mover todo inicialmente.

Etapas:

```text
1. Crear nuevo esquema/tablas.
2. Mantener lectura legacy.
3. Doble referencia controlada.
4. Migrar nuevos documentos.
5. Conciliar.
6. Cambiar fuente principal.
7. Retirar dependencia legacy cuando sea seguro.
```

---

# 54. TABLAS OBJETIVO

```text
empresa
establecimiento
punto_emision
secuencial
certificado_digital

documento_electronico
documento_estado_historial
documento_archivo
documento_sri_intento
documento_error
documento_correo
documento_auditoria

factura
factura_detalle
factura_impuesto
factura_pago

liquidacion_compra
liquidacion_detalle
liquidacion_impuesto

nota_credito
nota_credito_detalle
nota_credito_impuesto

nota_debito
nota_debito_motivo
nota_debito_impuesto

retencion
retencion_documento_sustento
retencion_impuesto

guia_remision
guia_destinatario
guia_detalle
```

---

# 55. JSON COMO CONTRATO DE ENTRADA

Objetivo:

```text
ERP / sistema externo
       │
       │ JSON
       ▼
    sri-files
```

No obligar al sistema origen a generar XML.

`sri-files` será responsable de:

```text
validar JSON
generar XML
firmar
enviar
autorizar
RIDE
correo
```

---

# 56. ENDPOINT V1 GENERAL

```text
POST /api/v1/documentos
```

Body:

```json
{
  "externalId": "ERP-FAC-348396",
  "tipoDocumento": "FACTURA",
  "data": {}
}
```

Respuesta inicial:

```json
{
  "id": "uuid",
  "externalId": "ERP-FAC-348396",
  "estado": "RECIBIDO"
}
```

---

# 57. ENDPOINTS ESPECÍFICOS

También pueden mantenerse contratos claros:

```text
POST /api/v1/facturas
POST /api/v1/liquidaciones-compra
POST /api/v1/notas-credito
POST /api/v1/notas-debito
POST /api/v1/retenciones
POST /api/v1/guias-remision
```

Internamente todos delegan al motor común.

---

# 58. CONSULTA

```text
GET /api/v1/documentos/{id}
GET /api/v1/documentos/external/{externalId}
GET /api/v1/documentos/{id}/historial
GET /api/v1/documentos/{id}/archivos
GET /api/v1/documentos/{id}/intentos-sri
GET /api/v1/documentos/{id}/errores
```

---

# 59. DESCARGAS

```text
GET /api/v1/documentos/{id}/xml
GET /api/v1/documentos/{id}/ride
GET /api/v1/documentos/{id}/zip
```

No generar nuevamente el XML autorizado si ya existe.

---

# 60. OPERACIONES

```text
POST /api/v1/documentos/{id}/consultar-autorizacion
POST /api/v1/documentos/{id}/regenerar-ride
POST /api/v1/documentos/{id}/reenviar-correo
POST /api/v1/documentos/{id}/reprocesar
```

Cada acción debe:

```text
validar estado
validar permiso
registrar auditoría
evitar duplicados
```

---

# 61. ANULACIÓN Y CORRECCIONES

No implementar una función genérica:

```text
ANULAR EN BD
```

El sistema debe distinguir:

```text
estado interno
estado SRI
relación con nota de crédito
procedimiento administrativo
```

Nunca borrar el comprobante original.

---

# 62. DOCUMENTOS CORRECTIVOS

Para una corrección:

```text
Documento A
    │
    └── relación
          ↓
Nota de Crédito / Documento B
```

Registrar:

```text
documentoOrigenId
tipoRelacion
motivo
fecha
usuario
```

---

# 63. CASO ESPECIAL: FECHA HISTÓRICA

Si un JSON solicita una fecha que requiere revisión:

```text
fechaEmision < fecha operativa permitida
```

no modificarla automáticamente.

Flujo:

```text
RECIBIDO
 ↓
VALIDACIÓN
 ↓
REQUIERE_REVISION
```

Registrar la causa y permitir decisión autorizada.

---

# 64. ERRORES

Crear clasificación:

```text
VALIDATION_ERROR
XML_GENERATION_ERROR
XML_SCHEMA_ERROR
SIGNATURE_ERROR
CERTIFICATE_ERROR
SRI_RECEPTION_ERROR
SRI_AUTHORIZATION_ERROR
RIDE_ERROR
EMAIL_ERROR
INTERNAL_ERROR
```

---

# 65. ERRORES RECUPERABLES

Ejemplo:

```text
EMAIL_ERROR
→ sí recuperable
→ reenviar correo
```

```text
RIDE_ERROR
→ sí recuperable
→ regenerar RIDE
```

```text
VALIDATION_ERROR
→ requiere corregir entrada
```

No repetir etapas exitosas innecesariamente.

---

# 66. TRANSACCIONES

No envolver:

```text
BD + SOAP SRI + PDF + EMAIL
```

en una única transacción de BD de larga duración.

Persistir checkpoints.

Ejemplo:

```text
guardar RECIBIDO
commit

generar XML
guardar XML_GENERADO
commit

firmar
guardar FIRMADO
commit

enviar SRI
guardar respuesta
commit
```

---

# 67. OBSERVABILIDAD

Agregar logs estructurados con:

```text
requestId
documentId
externalId
tipoDocumento
claveAcceso
estado
etapa
duracion
```

No registrar:

```text
password certificado
clave email
contenido sensible innecesario
```

---

# 68. MÉTRICAS

Medir:

```text
documentos recibidos
autorizados
no autorizados
errores
tiempo promedio autorización
pendientes autorización
correos pendientes
errores por etapa
certificados próximos a vencer
```

---

# 69. HEALTH

Separar:

```text
application
database
SRI recepción
SRI autorización
msvc-emails
certificado
storage
```

No considerar toda la aplicación caída únicamente porque el SRI esté temporalmente indisponible.

---

# 70. TESTS — NO NEGOCIABLE

Actualmente existe poca cobertura.

Antes de reemplazar flujo legacy crear tests de caracterización.

Objetivo:

> **Capturar cómo funciona hoy antes de refactorizarlo.**

---

# 71. TESTS DE CARACTERIZACIÓN DE FACTURA

Tomar facturas reales anonimizadas:

```text
IVA 0
IVA gravado
varios detalles
varios impuestos
descuento
varias formas de pago
consumidor final
RUC
cédula
```

Comparar:

```text
XML LEGACY
vs
XML NUEVO
```

---

# 72. TESTS DE RETENCIÓN

Cubrir:

```text
validación actual
firma
recepción
autorización
PDF
correo
```

No retirar el flujo viejo hasta alcanzar equivalencia.

---

# 73. GOLDEN FILE TESTS

Guardar XML esperados anonimizados:

```text
src/test/resources/golden/
```

Ejemplo:

```text
factura_iva.xml
factura_sin_iva.xml
retencion.xml
nota_credito.xml
...
```

Comparar estructura, valores y campos críticos.

---

# 74. TESTS DE FIRMA

Probar:

```text
P12 válido
password correcto
password incorrecto
certificado vencido
archivo inválido
sin clave privada
```

---

# 75. TESTS SRI

Separar:

```text
unitarios → mock
integración → ambiente pruebas
producción → nunca como test automatizado
```

---

# 76. FEATURE FLAGS

Agregar flags:

```text
sri.v1.factura.enabled
sri.v1.retencion.enabled
sri.v1.nota-credito.enabled
...
```

Permite activar el nuevo motor por tipo documental.

---

# 77. SHADOW MODE

Antes de enviar una factura con el motor nuevo:

```text
LEGACY genera XML
NUEVO genera XML
       │
       ▼
comparar
```

El nuevo XML no se envía todavía.

Registrar diferencias.

Esto permite validar sin afectar producción.

---

# 78. CANARY

Después de Shadow Mode:

```text
1% documentos
5%
10%
25%
50%
100%
```

o por:

```text
establecimiento
punto de emisión
tipo documento
```

Debe existir rollback inmediato.

---

# 79. ROLLBACK

Mientras dure la migración:

```text
feature flag OFF
       ↓
endpoint legacy
       ↓
flujo actual
```

No eliminar código legacy antes de estabilización.

---

# 80. FASE 0 — BACKUP Y LÍNEA BASE

Antes de cambiar:

- backup BD;
- inventario endpoints;
- inventario estados;
- ejemplos XML;
- ejemplos RIDE;
- configuración;
- cron;
- certificado;
- dependencias;
- métricas actuales.

Crear tag Git:

```text
sri-files-before-v1-refactor
```

---

# 81. FASE 1 — SEGURIDAD Y TESTS

Sin cambiar comportamiento:

1. tests de caracterización;
2. requestId;
3. logs seguros;
4. ocultar secretos;
5. preparar reemplazo de AES hardcoded;
6. tests certificado;
7. health mejorado.

---

# 82. FASE 2 — DOMINIO TRANSVERSAL

Crear sin reemplazar legacy:

```text
DocumentoElectronico
Historial
IntentoSri
ErrorDocumento
ArchivoDocumento
CorreoDocumento
Auditoria
```

---

# 83. FASE 3 — SERVICIOS COMUNES

Crear:

```text
ClaveAccesoService
CommonSriValidator
SignService
SriGateway
DocumentStorageService
RideService
EmailDocumentService
MoneyCalculationService
```

Inicialmente pueden envolver servicios existentes.

---

# 84. FASE 4 — FACTURA EN SHADOW MODE

Factura es el mejor candidato porque ya existe flujo completo.

Pasos:

```text
JSON nuevo
 ↓
nuevo generador
 ↓
comparación XML
 ↓
NO enviar
```

Corregir diferencias hasta equivalencia.

---

# 85. FASE 5 — FACTURA V1 CONTROLADA

Activar nuevo flujo en ambiente de pruebas.

Después canary en producción.

Mantener endpoint legacy.

---

# 86. FASE 6 — RETENCIÓN

Migrar reutilizando la infraestructura ya estabilizada con factura.

No perder validaciones actuales.

---

# 87. FASE 7 — NOTA DE CRÉDITO

Priorizar después de factura/retención porque será fundamental para correcciones documentales.

Implementar relación con documento origen desde el inicio.

---

# 88. FASE 8 — NOTA DE DÉBITO

Implementar sobre infraestructura común.

---

# 89. FASE 9 — LIQUIDACIÓN DE COMPRA

Agregar DTO, validación, XML, RIDE y tests.

---

# 90. FASE 10 — GUÍA DE REMISIÓN

Implementar al final por su estructura particular de transportista, destinatarios y productos.

---

# 91. FASE 11 — BASE DE DATOS PROPIA

Solo después de estabilizar procesamiento:

```text
crear sri_files_db
migrar configuración
migrar documentos nuevos
conciliar
cambiar datasource progresivamente
```

No mezclar refactor funcional y migración completa de BD en una misma entrega.

---

# 92. FASE 12 — RETIRO LEGACY

Únicamente cuando:

```text
✓ todos los tipos documentales están probados
✓ consumidores migrados
✓ conciliación correcta
✓ monitoreo estable
✓ rollback probado
✓ aprobación técnica
```

entonces deprecar:

```text
/api/singsend/*
```

No eliminarlos sin período de transición.

---

# 93. ORDEN DE IMPLEMENTACIÓN RECOMENDADO

```text
01 Backup / baseline
02 Tests legacy
03 Seguridad AES/P12
04 RequestId / logs
05 DocumentoElectronico
06 Historial
07 Intentos SRI
08 Errores
09 Archivos
10 ClaveAccesoService
11 CommonSriValidator
12 MoneyCalculationService
13 SignService
14 SriGateway
15 RideService
16 EmailDocumentService
17 API JSON V1
18 Factura shadow
19 Factura V1
20 Retención V1
21 Nota crédito
22 Nota débito
23 Liquidación compra
24 Guía remisión
25 Base propia
26 Frontend administración
27 Deprecación legacy
```

---

# 94. ARCHIVOS / CLASES QUE NO DEBEN ELIMINARSE INICIALMENTE

Conservar mientras exista dependencia:

```text
SRI_Controller
SendXmlToSriService
FirmaComprobantesService
XadesBesService
FacturaXmlGeneratorService
XmlToPdfService
EnvioSriBatchService
MailService
EmailMsClientService
SriRetencionValidationService
RetencionPdfService
RetencionEmailService
DefinirService
```

Refactorizar mediante adaptadores.

---

# 95. PATRÓN ADAPTER

Ejemplo:

```text
Nuevo SignService
      │
      ▼
LegacyFirmaAdapter
      │
      ▼
FirmaComprobantesService
```

Después podrá reemplazarse internamente sin modificar consumidores.

---

# 96. ESTRUCTURA DE PAQUETES OBJETIVO

```text
com.erp.srifiles

├── api
│   ├── controller
│   ├── request
│   └── response
│
├── application
│   ├── service
│   ├── processor
│   └── mapper
│
├── domain
│   ├── document
│   ├── company
│   ├── certificate
│   ├── sri
│   └── shared
│
├── infrastructure
│   ├── persistence
│   ├── sri
│   ├── signature
│   ├── storage
│   ├── ride
│   └── email
│
├── legacy
│   └── adapter
│
└── config
```

---

# 97. FRONTEND ADMINISTRATIVO

El frontend Angular debe administrar:

```text
Dashboard
Documentos
Facturas
Liquidaciones
Notas de crédito
Notas de débito
Retenciones
Guías
Empresas
Establecimientos
Puntos emisión
Secuenciales
Certificados
Plantillas RIDE
Configuración SRI
Usuarios
Roles
Auditoría
Errores
Correos
Monitoreo
```

---

# 98. DETALLE DOCUMENTAL

Mostrar:

```text
Resumen
JSON
XML
Archivos
SRI
Historial
Errores
Correo
Auditoría
```

Timeline:

```text
RECIBIDO
  ↓
VALIDADO
  ↓
XML
  ↓
FIRMADO
  ↓
RECIBIDO SRI
  ↓
AUTORIZADO
  ↓
RIDE
  ↓
CORREO
```

---

# 99. ACCIONES DEL FRONTEND

Según estado:

```text
Consultar autorización
Descargar XML
Descargar RIDE
Regenerar RIDE
Reenviar correo
Reprocesar etapa
Ver error
Ver historial
```

No mostrar acciones incompatibles con el estado.

---

# 100. CERTIFICADO EN FRONTEND

Módulo:

```text
Administración
└── Certificados Digitales
```

Funciones:

```text
listar
cargar P12/PFX
password
validar
activar
desactivar
reemplazar
ver expiración
ver emisor
ver titular
```

La contraseña:

```text
NO localStorage
NO sessionStorage
NO logs
NO devolver por API
```

---

# 101. CRITERIOS DE NO REGRESIÓN

Antes de desplegar una fase:

```text
✓ endpoint legacy sigue respondiendo
✓ XML legacy no cambió accidentalmente
✓ firma funciona
✓ SRI recepción funciona
✓ autorización funciona
✓ PDF funciona
✓ correo funciona
✓ scheduler funciona
✓ estados legacy continúan compatibles
✓ no hay duplicación
✓ no se altera recaudación
```

---

# 102. CHECKLIST FACTURA

- [ ] JSON validado.
- [ ] External ID.
- [ ] Idempotencia.
- [ ] Fecha emisión validada.
- [ ] Clave acceso.
- [ ] Totales.
- [ ] IVA.
- [ ] XML.
- [ ] Firma.
- [ ] Recepción.
- [ ] Autorización.
- [ ] XML autorizado.
- [ ] RIDE.
- [ ] Correo.
- [ ] Historial.
- [ ] Auditoría.
- [ ] Documento correctivo.
- [ ] No altera recaudación.

---

# 103. CHECKLIST RETENCIÓN

- [ ] Mantener validaciones existentes.
- [ ] JSON V1.
- [ ] XML.
- [ ] codDoc 07.
- [ ] clave acceso.
- [ ] documentos sustento.
- [ ] impuestos.
- [ ] firma.
- [ ] SRI.
- [ ] RIDE.
- [ ] correo.
- [ ] historial.

---

# 104. CHECKLIST NOTA DE CRÉDITO

- [ ] Documento origen.
- [ ] Número documento modificado.
- [ ] Fecha sustento.
- [ ] Motivo.
- [ ] Valor modificación.
- [ ] Impuestos.
- [ ] XML.
- [ ] Firma.
- [ ] SRI.
- [ ] RIDE.
- [ ] Relación histórica.

---

# 105. CHECKLIST NOTA DE DÉBITO

- [ ] Documento origen.
- [ ] Motivos.
- [ ] Valores.
- [ ] Impuestos.
- [ ] XML.
- [ ] Firma.
- [ ] SRI.
- [ ] RIDE.
- [ ] Historial.

---

# 106. CHECKLIST LIQUIDACIÓN DE COMPRA

- [ ] Proveedor.
- [ ] Identificación.
- [ ] Dirección.
- [ ] Detalles.
- [ ] Impuestos.
- [ ] Pagos.
- [ ] XML.
- [ ] Firma.
- [ ] SRI.
- [ ] RIDE.
- [ ] Correo.

---

# 107. CHECKLIST GUÍA DE REMISIÓN

- [ ] Transportista.
- [ ] Identificación.
- [ ] Placa.
- [ ] Fecha inicio.
- [ ] Fecha fin.
- [ ] Punto partida.
- [ ] Destinatarios.
- [ ] Destinos.
- [ ] Motivos.
- [ ] Documentos sustento.
- [ ] Productos.
- [ ] XML.
- [ ] Firma.
- [ ] SRI.
- [ ] RIDE.

---

# 108. CRITERIOS DE SEGURIDAD

- [ ] Eliminar progresivamente AES hardcoded.
- [ ] Secretos fuera del repositorio.
- [ ] Password P12 nunca en logs.
- [ ] Password correo nunca en logs.
- [ ] HTTPS.
- [ ] Roles/permisos.
- [ ] Auditoría.
- [ ] Sanitización de errores.
- [ ] Backup.
- [ ] Rotación de secretos.
- [ ] Certificados históricos protegidos.

---

# 109. CRITERIOS DE CALIDAD

- [ ] Unit tests.
- [ ] Integration tests.
- [ ] Golden XML tests.
- [ ] Tests de redondeo.
- [ ] Tests P12.
- [ ] Tests idempotencia.
- [ ] Tests concurrencia secuencial.
- [ ] Tests scheduler.
- [ ] Tests reproceso.
- [ ] Tests rollback.

---

# 110. DEFINICIÓN DE TERMINADO POR DOCUMENTO

Un tipo documental se considera migrado cuando:

```text
1. recibe JSON;
2. valida;
3. genera XML;
4. firma;
5. envía al SRI;
6. recupera autorización;
7. conserva XML autorizado;
8. genera RIDE;
9. envía correo;
10. registra historial;
11. registra errores;
12. soporta idempotencia;
13. soporta reproceso por etapa;
14. tiene tests;
15. tiene frontend;
16. no afecta el flujo legacy.
```

---

# 111. PRIMER SPRINT RECOMENDADO

No empezar creando los cuatro comprobantes faltantes.

Primero:

```text
1. Tests de caracterización factura.
2. Tests de caracterización retención.
3. Request ID.
4. DocumentoElectronico.
5. Historial de estados.
6. Intentos SRI.
7. Errores.
8. ClaveAccesoService.
9. MoneyCalculationService.
10. SignService como fachada.
11. SriGateway como fachada.
12. API V1 sin activar producción.
```

---

# 112. SEGUNDO SPRINT

```text
1. FacturaRequest V1.
2. FacturaProcessor.
3. FacturaValidator.
4. Adaptar FacturaXmlGeneratorService.
5. Shadow Mode.
6. Comparación XML.
7. Tests IVA/totales/fechas.
8. Pruebas SRI ambiente de pruebas.
```

---

# 113. TERCER SPRINT

```text
1. Certificados P12/PFX.
2. Migración segura de cifrado.
3. Administración certificado.
4. Factura V1 canary.
5. Historial frontend.
6. Intentos SRI frontend.
7. Errores frontend.
```

---

# 114. CUARTO SPRINT

```text
1. RetencionRequest V1.
2. RetencionProcessor.
3. Reutilizar validación actual.
4. Shadow/compatibilidad.
5. Migrar progresivamente.
```

---

# 115. QUINTO SPRINT

```text
Nota de Crédito
```

Priorizarla como primer comprobante nuevo por su importancia en procesos de corrección.

---

# 116. REGLA PARA CAMBIOS EN PRODUCCIÓN

Nunca desplegar simultáneamente:

```text
nuevo modelo BD
+
nuevo motor XML
+
nuevo cifrado
+
nuevo scheduler
+
nuevos endpoints
```

Separar cambios para identificar rápidamente una regresión.

---

# 117. PROCEDIMIENTO DE DESPLIEGUE

```text
BACKUP
  ↓
MIGRACIÓN ADITIVA
  ↓
DEPLOY
  ↓
HEALTH
  ↓
SMOKE TEST
  ↓
DOCUMENTO DE PRUEBA
  ↓
MONITOREO
  ↓
CANARY
  ↓
AMPLIAR
```

---

# 118. MIGRACIONES DE BASE DE DATOS

Todas deben ser aditivas inicialmente:

```text
CREATE TABLE
ADD COLUMN nullable
CREATE INDEX
```

Evitar en primera fase:

```text
DROP COLUMN
DROP TABLE
RENAME destructivo
ALTER incompatible
```

---

# 119. ÍNDICES RECOMENDADOS

```text
external_id
clave_acceso
numero_documento
estado
tipo_documento
fecha_emision
fecha_autorizacion_sri
identificacion_receptor
documento_origen_id
```

---

# 120. RESTRICCIONES ÚNICAS

Considerar:

```text
UNIQUE(external_id, origen_sistema)
UNIQUE(clave_acceso)
UNIQUE(empresa, establecimiento, punto_emision, tipo_documento, secuencial)
```

Aplicar después de revisar datos históricos para evitar que una migración falle por duplicados legacy.

---

# 121. PRINCIPIO DE TRAZABILIDAD

Para cualquier comprobante debe poder responderse:

```text
¿Quién lo originó?
¿Cuándo llegó?
¿Qué JSON llegó?
¿Qué XML se generó?
¿Cuándo se firmó?
¿Qué certificado se utilizó?
¿Cuándo se envió?
¿Qué respondió el SRI?
¿Cuándo se autorizó?
¿Qué RIDE se generó?
¿A quién se envió?
¿Falló alguna etapa?
¿Fue reprocesado?
¿Tiene un documento correctivo?
```

---

# 122. PRINCIPIO DE INMUTABILIDAD

Después de autorización:

```text
XML autorizado = INMUTABLE
clave acceso = INMUTABLE
número autorización = INMUTABLE
fecha autorización = INMUTABLE
```

Cualquier corrección debe producir una nueva acción/documento relacionado, nunca reescribir la historia.

---

# 123. RESULTADO FINAL ESPERADO

```text
 ERP / SISTEMAS EXTERNOS
          │
          │ JSON
          ▼
┌─────────────────────────────┐
│        SRI-FILES V1         │
├─────────────────────────────┤
│ API                         │
│ Validación                  │
│ Documento electrónico       │
│ Historial                   │
│ Motor XML                   │
│ Firma XAdES                 │
│ Gateway SRI                 │
│ Autorización                │
│ Storage                     │
│ RIDE                        │
│ Email                       │
│ Auditoría                   │
└──────────────┬──────────────┘
               │
               ▼
              SRI

Tipos:

01 FACTURA
03 LIQUIDACIÓN COMPRA
04 NOTA CRÉDITO
05 NOTA DÉBITO
06 GUÍA REMISIÓN
07 RETENCIÓN
```

---

# 124. REGLA FINAL PARA EL AGENTE DE PROGRAMACIÓN

```text
NO reescribas sri-files desde cero.

Antes de modificar una clase:
1. revisa quién la utiliza;
2. revisa endpoints dependientes;
3. revisa estados;
4. revisa tablas;
5. revisa scheduler;
6. revisa tests;
7. identifica impacto.

Todo cambio debe ser backward-compatible inicialmente.

Preferir:
- wrappers;
- adapters;
- nuevas tablas;
- nuevas columnas nullable;
- feature flags;
- shadow mode;
- canary.

No eliminar flujo legacy hasta demostrar equivalencia.

No modificar recaudación desde sri-files.

No sobrescribir XML autorizado.

No reenviar al SRI una etapa ya exitosa si el error pertenece a PDF o correo.

No guardar secretos en logs.

Usar BigDecimal para dinero.

Registrar todas las transiciones.

Mantener idempotencia.

Después de cada fase:
- compilar;
- ejecutar tests;
- ejecutar smoke test;
- comparar XML;
- verificar BD;
- verificar logs;
- documentar cambios.
```

---

# 125. CONCLUSIÓN

La mejora de `sri-files` debe tratarse como una **evolución controlada de un servicio productivo**, no como una reescritura.

El sistema actual ya posee componentes valiosos:

```text
firma XAdES-BES
SOAP SRI
polling
facturas
retenciones
JasperReports
correo
scheduler
```

Estos componentes deben reutilizarse inicialmente detrás de interfaces y adaptadores.

La prioridad no es agregar inmediatamente más código, sino construir primero una capa transversal de:

```text
DocumentoElectronico
Estados
Historial
Idempotencia
Validación
Fechas
Precisión monetaria
Auditoría
Seguridad
Errores
Reprocesamiento
```

Una vez estabilizada esta base, Factura y Retención pueden migrarse sin interrupción y los cuatro comprobantes restantes podrán incorporarse sobre el mismo motor, reduciendo duplicación y riesgo.

> **Objetivo final: recibir un JSON, procesarlo de forma trazable, generar y firmar el XML, enviarlo al SRI, recuperar su autorización, generar el RIDE y enviar el correo, sin alterar el hecho económico original ni perder la historia de ninguna operación.**
