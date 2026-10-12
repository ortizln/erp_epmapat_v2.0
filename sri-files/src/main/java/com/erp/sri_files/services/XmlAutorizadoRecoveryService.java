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
@RequiredArgsConstructor
public class XmlAutorizadoRecoveryService {
    private final DocumentoElectronicoR documentos;
    private final FacturaR facturas;
    private final SriGateway gateway;

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
            var documento = documentos.findByClaveAcceso(key).orElse(null);
            if (documento != null && xmlValido(documento.getXmlAutorizado(), key)) {
                return new Resultado(documento.getXmlAutorizado(), "BD_documento_electronico", documento.getEstado(),
                        documento.getNumeroAutorizacion(), documento.getFechaAutorizacionSri(), null);
            }
            var factura = facturas.findByClaveacceso(key).orElse(null);
            if (factura != null && xmlValido(factura.getXmlautorizado(), key)) {
                return new Resultado(factura.getXmlautorizado(), "BD_fec_factura", factura.getEstado(),
                        null, factura.getFecha_autorizacion(), null);
            }
        }
        var sri = gateway.consultarAutorizacion(key);
        if (!sri.autorizado() || !xmlValido(sri.xmlAutorizado(), key)) {
            return new Resultado(null, "SRI", null, null, null,
                    sri.autorizado() ? "SRI devolvio un XML invalido o de otra clave" : sri.mensaje());
        }
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
            var claves = document.getElementsByTagNameNS("*", "claveAcceso");
            if (claves.getLength() > 0) return key.equals(claves.item(0).getTextContent().trim());
            var comprobantes = document.getElementsByTagName("comprobante");
            return comprobantes.getLength() > 0 && xmlValido(comprobantes.item(0).getTextContent(), key);
        } catch (Exception invalid) { return false; }
    }
}
