package com.erp.sri_files.services;

import com.erp.sri_files.repositories.DocumentoElectronicoR;
import com.erp.sri_files.repositories.FacturaR;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Service
@lombok.extern.slf4j.Slf4j
@RequiredArgsConstructor
public class XmlAutorizadoRecoveryService {
    private final DocumentoElectronicoR documentos;
    private final FacturaR facturas;
    private final SriGateway gateway;

    @org.springframework.beans.factory.annotation.Value("${sri.xml-recovery.max-attempts:3}")
    private int maxAttempts = 3;
    @org.springframework.beans.factory.annotation.Value("${sri.xml-recovery.wait-budget-ms:12000}")
    private long waitBudgetMs = 12000;
    @org.springframework.beans.factory.annotation.Value("${sri.xml-recovery.attempt-timeout-ms:3500}")
    private int attemptTimeoutMs = 3500;
    @org.springframework.beans.factory.annotation.Value("${sri.xml-recovery.retry-delay-ms:250}")
    private long retryDelayMs = 250;
    private final java.util.concurrent.ConcurrentHashMap<String, java.util.concurrent.CompletableFuture<Resultado>> enCurso = new java.util.concurrent.ConcurrentHashMap<>();

    @jakarta.annotation.PostConstruct
    void validarConfiguracion() {
        if (maxAttempts < 1 || maxAttempts > 10 || waitBudgetMs < 100 || waitBudgetMs > 60000
                || attemptTimeoutMs < 2 || attemptTimeoutMs > waitBudgetMs || retryDelayMs < 0 || retryDelayMs > 5000)
            throw new IllegalArgumentException("Configuracion de recuperacion XML fuera de limites");
    }

    public record Resultado(String xml, String fuente, String estado, String numeroAutorizacion,
                            LocalDateTime fechaAutorizacion, String mensaje) {
        public boolean recuperado() { return xml != null && !xml.isBlank(); }
    }

    public Resultado recuperar(String clave, boolean forzarSri) {
        String key = clave == null ? "" : clave.trim();
        if (!key.matches("[0-9]{49}") || !key.substring(23, 24).matches("[12]")) {
            throw new IllegalArgumentException("Clave de acceso invalida para recuperar XML autorizado");
        }
        if (!forzarSri) {
            var cache = consultarCache(key);
            if (cache != null) return cache;
        }
        var sri = gateway.consultarAutorizacion(key);
        return resultadoSri(sri, key);
    }

    private Resultado consultarCache(String key) {
        try {
            var documento = documentos.findByClaveAcceso(key).orElse(null);
            if (documento != null && xmlValido(documento.getXmlAutorizado(), key)) {
                return new Resultado(documento.getXmlAutorizado(), "BD_documento_electronico", "AUTORIZADO",
                        documento.getNumeroAutorizacion(), documento.getFechaAutorizacionSri(), null);
            }
        } catch (org.springframework.dao.DataAccessException error) {
            log.warn("No se pudo consultar la cache documental: {}", error.getClass().getSimpleName());
        }
        try {
            var factura = facturas.findByClaveacceso(key).orElse(null);
            if (factura != null && xmlValido(factura.getXmlautorizado(), key)) {
                return new Resultado(factura.getXmlautorizado(), "BD_fec_factura", "AUTORIZADO",
                        null, factura.getFecha_autorizacion(), null);
            }
        } catch (org.springframework.dao.DataAccessException error) {
            log.warn("No se pudo consultar la cache de facturas: {}", error.getClass().getSimpleName());
        }
        return null;
    }

    private String validarClave(String clave) {
        String key = clave == null ? "" : clave.trim();
        if (!key.matches("[0-9]{49}") || !key.substring(23, 24).matches("[12]"))
            throw new IllegalArgumentException("Clave de acceso invalida para recuperar XML autorizado");
        return key;
    }

    public Resultado recuperarConEspera(String clave, boolean forzarSri) {
        String key = validarClave(clave);
        if (!forzarSri) {
            var cache = consultarCache(key);
            if (cache != null) return cache;
        }
        var nuevo = new java.util.concurrent.CompletableFuture<Resultado>();
        var activo = enCurso.putIfAbsent(key, nuevo);
        if (activo != null) {
            try { return activo.get(waitBudgetMs + 1000, java.util.concurrent.TimeUnit.MILLISECONDS); }
            catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Consulta interrumpida", interrupted);
            } catch (java.util.concurrent.TimeoutException timeout) {
                return pendiente("Hay una consulta SRI en curso. Intenta nuevamente.");
            } catch (java.util.concurrent.ExecutionException error) {
                throw new IllegalStateException("Fallo la consulta SRI compartida", error.getCause());
            }
        }
        try {
            var resultado = consultarConReintentos(key);
            if (!resultado.recuperado()) {
                var cache = consultarCache(key);
                if (cache != null) resultado = new Resultado(cache.xml(), cache.fuente(), "AUTORIZADO",
                        cache.numeroAutorizacion(), cache.fechaAutorizacion(),
                        "Se conserva el XML almacenado; la consulta nueva al SRI fue inconclusa");
            }
            nuevo.complete(resultado);
            return resultado;
        } catch (RuntimeException error) {
            nuevo.completeExceptionally(error);
            throw error;
        } finally { enCurso.remove(key, nuevo); }
    }

    private Resultado consultarConReintentos(String key) {
        long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.MILLISECONDS.toNanos(waitBudgetMs);
        Resultado ultimo = pendiente("Sin XML autorizado disponible");
        for (int intento = 1; intento <= maxAttempts; intento++) {
            long restante = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(deadline - System.nanoTime());
            if (restante < 2) break;
            ultimo = resultadoSri(gateway.consultarAutorizacionAcotada(key,
                    (int) Math.min(attemptTimeoutMs, restante)), key);
            if (ultimo.recuperado()) return ultimo;
            if (intento < maxAttempts) {
                restante = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(deadline - System.nanoTime());
                long espera = retryDelayMs * intento;
                if (restante < espera + 2) break;
                try { Thread.sleep(espera); }
                catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Consulta interrumpida", interrupted);
                }
            }
        }
        return ultimo;
    }

    private Resultado pendiente(String mensaje) {
        return new Resultado(null, "SRI", "PENDIENTE_CONSULTA", null, null, mensaje);
    }

    private Resultado resultadoSri(SriGateway.SriAutorizacionResult sri, String key) {
        if (sri == null) return pendiente("Sin respuesta del SRI");
        if (!sri.autorizado() || !xmlValido(sri.xmlAutorizado(), key))
            return pendiente(sri.autorizado() ? "SRI devolvio un XML invalido o de otra clave" : sri.mensaje());
        return new Resultado(sri.xmlAutorizado(), "SRI", "AUTORIZADO", sri.numeroAutorizacion(),
                fechaSri(sri.fechaAutorizacion()), null);
    }

    private LocalDateTime fechaSri(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return OffsetDateTime.parse(value).atZoneSameInstant(ZoneId.of("America/Guayaquil")).toLocalDateTime();
        } catch (java.time.format.DateTimeParseException ignored) {
            try { return LocalDateTime.parse(value); }
            catch (java.time.format.DateTimeParseException invalid) { return null; }
        }
    }

    private boolean xmlValido(String xml, String key) {
        if (xml == null || xml.isBlank()) return false;
        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            var builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new org.xml.sax.helpers.DefaultHandler());
            var document = builder.parse(new InputSource(new StringReader(xml.trim())));
            if (!java.util.Set.of("factura", "autorizacion", "notaCredito", "notaDebito",
                    "comprobanteRetencion", "liquidacionCompra", "guiaRemision")
                    .contains(document.getDocumentElement().getLocalName())) return false;
            if ("autorizacion".equals(document.getDocumentElement().getLocalName())) {
                var estados = document.getElementsByTagNameNS("*", "estado");
                if (estados.getLength() == 0 || !"AUTORIZADO".equalsIgnoreCase(estados.item(0).getTextContent().trim())) return false;
            }
            var claves = document.getElementsByTagNameNS("*", "claveAcceso");
            if (claves.getLength() > 0) return key.equals(claves.item(0).getTextContent().trim());
            var comprobantes = document.getElementsByTagName("comprobante");
            return comprobantes.getLength() > 0 && xmlValido(comprobantes.item(0).getTextContent(), key);
        } catch (Exception invalid) { return false; }
    }
}
