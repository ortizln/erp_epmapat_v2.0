# Análisis del Microservicio `sri-files`

## Identificación

| Campo | Valor |
|-------|-------|
| **Nombre Spring** | `msvc-sri` |
| **ArtifactId** | `sri-files` |
| **Grupo** | `com.erp` |
| **Versión** | `0.0.1` |
| **Puerto** | `9096` (dev) / `9090` (prod) |
| **Java** | 17 |
| **Spring Boot** | 3.4.1 |
| **Spring Cloud** | 2024.0.0 |
| **Descripción** | Generación, firma, envío al SRI y generación de PDFs de comprobantes electrónicos (facturas y retenciones) del ERP EPMAPA-T |

---

## Endpoints Disponibles

### Facturas

| Método | Path | Descripción |
|--------|------|-------------|
| POST | `/api/singsend/factura/xml` | Firmar y enviar factura (MultipartFile XML) |
| POST | `/api/singsend/factura/string` | Firmar y enviar factura (XML como String body) |
| POST | `/api/singsend/factura` | Firmar y enviar factura (retorna XML autorizado) |
| GET | `/api/singsend/factura_electronica` | Flujo completo: crear XML + firmar + enviar + email |
| GET | `/api/singsend/factura/xml-previo` | Generar XML previo para revisión (sin firmar ni enviar) |
| GET | `/api/singsend/generar-pdf` | Generar PDF de factura autorizada por idfactura |
| GET | `/api/singsend/health` | Health check SMTP |

### Retenciones

| Método | Path | Descripción |
|--------|------|-------------|
| POST | `/api/singsend/retencion` | Firmar y enviar retención (MultipartFile + validación previa) |
| POST | `/api/singsend/retencion/string` | Firmar y enviar retención (String body XML) |
| POST | `/api/singsend/retencion/procesar` | Procesar retención con respuesta ampliada |
| POST | `/api/singsend/retenciones/pdf` | Generar PDF retención desde XML autorizado |
| GET | `/api/singsend/retencion/download` | Descargar retención (zip/pdf/xml) con polling SRI |
| POST | `/api/singsend/retencion/mail` | Enviar retención por correo con adjuntos |
| GET | `/api/singsend/retenciones/download` | Alias de `/retencion/download` |
| GET | `/api/singsend/retenciones/pdf` | Alias de descarga PDF retención |
| GET | `/api/singsend/retenciones/xml` | Alias de descarga XML retención |
| POST | `/api/singsend/retenciones/mail` | Alias de envío retención por correo |

### Autorización SRI

| Método | Path | Descripción |
|--------|------|-------------|
| GET | `/api/singsend/autorizacion` | Consultar autorización por clave de acceso (con/sin polling) |
| POST | `/api/singsend/autorizacion/by-xml` | Consultar autorización desde XML (extrae claveAcceso) |

### Correo

| Método | Path | Descripción |
|--------|------|-------------|
| POST | `/api/singsend/send` | Enviar correo directo |
| POST | `/api/singsend/send-template` | Enviar correo con plantilla Thymeleaf |

---

## Servicios

| Servicio | Función |
|----------|---------|
| `SendXmlToSriService` | Cliente SOAP principal al SRI. Envía comprobantes a recepción, consulta autorizaciones, polling con backoff exponencial. |
| `FirmaComprobantesService` | Orquestador de firmas digitales. Carga certificado PKCS12 de la tabla `definir`, descifra la clave con AES y delega a XadesBesService o XmlDsigService. |
| `XadesBesService` | Firma XAdES-BES usando librería xades4j. Firma ENVELOPED sobre cualquier comprobante SRI. |
| `XmlDsigService` | Firma XMLDSIG básica como alternativa a XAdES-BES. |
| `FacturaXmlGeneratorService` | Genera el XML completo de factura electrónica SRI v1.1.0 desde la entidad `Factura`. Incluye clave de acceso (49 dígitos con dígito verificador mod 11), infoTributaria, infoFactura, detalles con impuestos, infoAdicional. |
| `XmlToPdfService` | Genera PDFs de facturas usando JasperReports (plantillas .jrxml) e iText/html2pdf. Soporta 2 versiones de plantilla (v2 pre-mayo-2025, v3 post-mayo-2025). |
| `EnvioSriBatchService` | Servicio batch programado con `@Scheduled`. Dos tareas: (1) envío automático de facturas estado "I" al SRI, (2) recuperación de XMLs autorizados pendientes (estados "C" y "O"). |
| `MailService` | Servicio de correo con retry (max 3 intentos, backoff 1.5s). Delega a EmailMsClientService. Valida tamaño de adjuntos (max 20MB). |
| `EmailMsClientService` | Cliente REST hacia el microservicio `msvc-emails` (puerto 9099). Encola documentos de correo via `POST /api/v1/emails/documents`. |
| `FacturasService` | Verifica si una factura está pagada consultando la tabla legacy `facturas`. |
| `DefinirService` | CRUD de configuración de empresa. Permite guardar/cargar certificado de firma digital y cifrar/descifrar claves con AES. |
| `RetencionPdfService` | Genera PDF de retenciones usando JasperReports (plantilla `retencion_template.jrxml`). |
| `RetencionEmailService` | Envía retenciones por correo con XML y PDF como adjuntos. |
| `SriRetencionValidationService` | Validación previa de XML de retenciones: verifica raíz, atributos, infoTributaria, codDoc=07, RUC 13 dígitos, clave de acceso 49 dígitos con mod 11, campos obligatorios de impuestos. |
| `XmlParserService` | Servicio auxiliar de parsing XML. |
| `XmlSignerService` | Servicio auxiliar de firmado XML. |
| `PdfGenerationService` | Servicio auxiliar de generación PDF. |
| `KeystoreProbe` | Servicio de diagnóstico de keystores. |
| `CAG_FacturasService` | Servicio auxiliar de facturas. |

---

## Modelos / Base de Datos (PostgreSQL)

### Tabla: `fec_factura` (entidad `Factura`)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| idfactura | Long (PK, auto) | ID factura electrónica |
| claveacceso | String | Clave acceso SRI (49 dígitos) |
| secuencial | String | Secuencial comprobante |
| xmlautorizado | String | XML autorizado completo |
| errores | String | Mensajes de error |
| estado | String | I=Pendiente, P=Procesando, A=Autorizada, N=No autorizada, C=Consulta pendiente, O=Correo pendiente, M=Devuelta, U=Error |
| establecimiento | String | Cod establecimiento |
| puntoemision | String | Cod punto emisión |
| direccionestablecimiento | String | Dirección emisor |
| fechaemision | LocalDateTime | Fecha emisión |
| tipoidentificacioncomprador | String | Tipo ID comprador |
| identificacioncomprador | String | RUC/CI comprador |
| razonsocialcomprador | String | Razón social |
| emailcomprador | String | Email comprador |
| telefonocomprador | String | Teléfono |
| concepto | String | Concepto |
| recaudador | String | Recaudador |
| referencia | String | Referencia/cuenta |
| direccioncomprador | String | Dirección |
| detalles | List\<FacturaDetalle\> | OneToMany cascade ALL |
| pagos | List\<FacturaPago\> | OneToMany cascade ALL |

### Tabla: `fec_factura_detalles` (entidad `FacturaDetalle`)

| Campo | Tipo |
|-------|------|
| idfacturadetalle | Long (PK) |
| idfactura | Long (FK -> Factura) |
| codigoprincipal | String |
| descripcion | String |
| cantidad | BigDecimal |
| preciounitario | BigDecimal |
| descuento | BigDecimal |
| impuestos | List\<FacturaDetalleImpuesto\> |

### Tabla: `fec_factura_detalles_impuestos` (entidad `FacturaDetalleImpuesto`)

| Campo | Tipo |
|-------|------|
| idfacturadetalleimpuestos | Long (PK) |
| idfacturadetalle | Long (FK) |
| codigoimpuesto | String |
| codigoporcentaje | String |
| baseimponible | BigDecimal |

### Tabla: `fec_factura_pagos` (entidad `FacturaPago`)

| Campo | Tipo |
|-------|------|
| idfacturapagos | Long (PK) |
| idfactura | Long (FK) |
| formapago | String |
| total | BigDecimal |
| plazo | Integer |
| unidadtiempo | String |

### Tabla: `definir` (entidad `Definir`) — Configuración de empresa

| Campo | Tipo | Descripción |
|-------|------|-------------|
| iddefinir | Long (PK) | Siempre ID=1 |
| razonsocial | String | Razón social emisor |
| nombrecomercial | String | Nombre comercial |
| ruc | String | RUC emisor (13 dígitos) |
| direccion | String | Dirección matriz |
| tipoambiente | Byte | 1=Pruebas, 2=Producción |
| iva / porciva | float/BigDecimal | Porcentaje IVA |
| firma | byte[] | Certificado PKCS12 (bytes) |
| clave_firma | String | Clave firma cifrada AES |
| email | String | Email configuración |
| clave_email | String | Clave email cifrada |

### Tablas adicionales

- **`facturas`** (entidad `Facturas`) — Tabla legacy de facturación
- **`tabla15`** (entidad `Tabla15`) — Catálogo general

---

## Variables de Entorno Requeridas

### Obligatorias

| Variable | Ejemplo | Descripción |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://192.168.0.46:5432/ErpEpmapaT` | URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Usuario BD |
| `SPRING_DATASOURCE_PASSWORD` | `***` | Password BD |

### Operacionales

| Variable | Default | Descripción |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `dev` | Perfil (dev/prod/docker) |
| `CONFIG_SERVER_URL` | `http://config-server:8888` | URL Config Server |
| `SERVER_PORT` | `9090` (prod) / `9096` (dev) | Puerto |

### SRI

| Variable | Default | Descripción |
|----------|---------|-------------|
| `SRI_AMBIENTE` | `2` | 1=Pruebas, 2=Producción |
| `SRI_SCHEDULER_ENVIO_FACTURAS_CRON` | `0 */1 * * * *` | Cron envío facturas |
| `SRI_SCHEDULER_RECUPERACION_XML_CRON` | `30 */3 * * * *` | Cron recuperación XMLs |

### Eureka

| Variable | Default | Descripción |
|----------|---------|-------------|
| `EUREKA_CLIENT_ENABLED` | `true` | Habilitar Eureka |
| `EUREKA_REGISTER_WITH_EUREKA` | `true` | Registrarse en Eureka |
| `EUREKA_SERVICEURL` | `http://msvc-eureka:8761/eureka/` | URL Eureka |

### App / Backend

| Variable | Default | Descripción |
|----------|---------|-------------|
| `ERP_BACKEND_BASE_URL` | `http://192.168.0.165:9080` | URL backend ERP |
| `APP_REPORTS_PATH` | `/home/epmapaadmin/reportsEpmapat` | Ruta reportes |
| `EMAIL_MS_BASE_URL` | `http://msvc-emails:9099` | URL microservicio email |

### Correo

| Variable | Default | Descripción |
|----------|---------|-------------|
| `APP_MAIL_FROM` | `facturacion@epmapatulcan.gob.ec` | Remitente |
| `APP_MAIL_USERNAME` | `facturacion` | Usuario SMTP |
| `APP_MAIL_DISPLAY_NAME` | `EPMAPA-T` | Nombre remitente |
| `APP_MAIL_REPLY_TO` | `facturacion@epmapatulcan.gob.ec` | Reply-To |

---

## Flujo de Procesamiento

### Flujo completo de FACTURA electrónica

```
1. FacturaXmlGeneratorService.generarXmlFactura()
   └─> Genera XML v1.1.0 desde entidad Factura
       (clave acceso 49 dígitos, infoTributaria, infoFactura, detalles, impuestos)

2. FirmaComprobantesService.firmarFactura()
   └─> Carga certificado PKCS12 de tabla "definir"
   └─> Descifra clave con AES
   └─> Firma XAdES-BES (XadesBesService)

3. SendXmlToSriService.enviarFacturaFirmadaTxt()
   └─> Envía a Recepción SRI (SOAP)
   └─> Si RECIBIDA → consultarAutorizacionConEspera()
       └─> Polling con backoff exponencial

4. XmlToPdfService.generarFacturaPDF_v2/v3()
   └─> Genera PDF desde XML autorizado (JasperReports)

5. MailService.send()
   └─> EmailMsClientService
       └─> Envía PDF+XML adjuntos por correo
```

### Flujo de RETENCIÓN electrónica

```
1. SriRetencionValidationService.validate()
   └─> Validación previa: raíz, atributos, infoTributaria,
       codDoc=07, RUC 13 dígitos, clave 49 dígitos mod 11,
       campos obligatorios de impuestos

2. FirmaComprobantesService.firmarRetencion()
   └─> Firma XAdES-BES

3. SendXmlToSriService
   └─> Envío a SRI + polling autorización

4. RetencionPdfService
   └─> PDF via JasperReports (retencion_template.jrxml)

5. RetencionEmailService
   └─> Correo con XML y PDF como adjuntos
```

### Scheduler batch (`EnvioSriBatchService`)

| Tarea | Cron | Descripción |
|-------|------|-------------|
| Envío facturas | `0 */1 * * * *` | Busca facturas estado "I" → XML → firma → envío → autorización → PDF → correo |
| Recuperación XML | `30 */3 * * * *` | Busca facturas estados "C"/"O" → consulta SRI → reenvío correo si pendiente |

---

## Dependencias Principales

### Framework
- Spring Boot Starter Web, Data JPA, Validation, Thymeleaf
- Spring Cloud: Config, Eureka Client, Bootstrap
- PostgreSQL (runtime)
- Spring Retry

### SOAP / SRI
- Jakarta XML WS API 4.0.2 + jaxws-rt 4.0.2
- JAXB Runtime 4.0.5 (Glassfish)
- sri-efactura-core 0.1.2 (uk.co.xprl.efactura)

### Firma digital
- Apache Santuario xmlsec 2.3.0
- XAdES4j 2.4.0
- BouncyCastle bcprov-jdk15on 1.70

### PDF / Reportes
- JasperReports 6.21.4 (core + functions + fonts + chart-themes)
- iText core 9.1.0 + html2pdf 4.0.3
- Apache PDFBox 2.0.27
- Apache Batik 1.14 (SVG)
- Barcode4j 2.1 + Barbecue 1.5-beta1

### XML
- Jackson Dataformat XML
- Apache XMLSchema Core 2.3.0

### API Docs
- Springdoc OpenAPI (Swagger) 2.8.5

---

## Docker

- **Base image:** `eclipse-temurin:17-jdk-jammy`
- **Librerías instaladas:** libfreetype6, libfontconfig1, libxrender1, libxext6 (para JasperReports)
- **Puerto expuesto:** 9096
- **Entrypoint:** `java -jar app_sri-files.jar`
- **Healthcheck:** `curl -f http://msvc-sri:9096/actuator/health`
- **Red:** `epmapat-nw` (alias: `msvc-sri`)
- **Dependencias:** config-server, msvc-eureka (con healthcheck)

---

## Observaciones y Riesgos

| # | Severidad | Descripción |
|---|-----------|-------------|
| 1 | **Alta** | `AESUtil` usa una clave hardcoded `1234567890123456` (AES-128) para cifrar/descifrar certificados y claves de correo. Esto es un riesgo de seguridad en producción. |
| 2 | **Media** | El controlador `SRI_Controller` es monolítico (~2020 líneas) con toda la lógica de facturas, retenciones, autorización y correo. |
| 3 | **Media** | El servicio comparte la BD `ErpEpmapaT` con otros microservicios (tablas `fec_factura`, `definir`, `facturas`, `tabla15`). |
| 4 | **Baja** | WSDLs del SRI están incluidos localmente en `resources/wsdl/` para evitar depender de la conexión a `celcer.sri.gob.ec` en tiempo de compilación. |
| 5 | **Baja** | No hay tests unitarios o de integración significativos (solo `SriFilesApplicationTests`). |
