package com.erp.sri_files.services;

import com.erp.sri_files.repositories.DefinirR;
import jakarta.persistence.EntityNotFoundException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import  com.erp.sri_files.models.Definir;
import com.erp.sri_files.exceptions.FacturaElectronicaException;
import com.erp.sri_files.interfaces.TotalSinImpuestos;
import com.erp.sri_files.models.Comprobante;
import com.erp.sri_files.models.Detalle;
import com.erp.sri_files.models.Factura;
import com.erp.sri_files.models.FacturaDetalle;
import com.erp.sri_files.models.FacturaDetalleImpuesto;
import com.erp.sri_files.models.InfoFactura;
import com.erp.sri_files.models.InfoTributaria;
import com.erp.sri_files.models.TotalConImpuestos;
import com.erp.sri_files.models.Detalle.Impuesto;
import com.erp.sri_files.models.TotalConImpuestos.TotalImpuesto;
import com.erp.sri_files.repositories.FacturaDetalleR;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FacturaSRIService {
    @Autowired
    private DefinirR definirR;

    @Autowired
    private ClaveAccesoGenerator claveAccesoGenerator;

    @Autowired
    private EmailService emailService;
    @Autowired
    private FacturaDetalleR fDetalleR;
    @Autowired

    private static final String VERSION = "1.1.0";

    public String generarXmlFactura(Factura factura) throws FacturaElectronicaException {

        try {
            // 1. Crear objeto raíz del comprobante
            Comprobante comprobante = new Comprobante();
            comprobante.setVersion(VERSION);
            comprobante.setId("comprobante");
            /*
             * comprobante.setFechaEmision(new Date());
             * comprobante.setMoneda("DOLAR");
             * comprobante.setAmbiente(AMBIENTE);
             */

            // 2. Configurar información tributaria
            comprobante.setInfoTributaria(crearInfoTributaria(factura));

            // 3. Configurar información de la factura
            comprobante.setInfoFactura(crearInfoFactura(factura));

            // 4. Configurar detalles
            comprobante.setDetalles(mapearDetalles(factura.getDetalles()));

            // 5. Configurar totales con impuestos

            // 6. Convertir a XML
            return convertirObjetoAXml(comprobante);

        } catch (Exception e) {
            throw new FacturaElectronicaException("Error al generar XML para el SRI", e);
        }
    }

    public static void saveXml(String xmlContent, String filePath) {
        try {
            Files.write(Paths.get(filePath), xmlContent.getBytes(), StandardOpenOption.CREATE);
            System.out.println("Archivo XML guardado en: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InfoTributaria crearInfoTributaria(Factura factura) {
        Definir def = getDefinir();
        String claveAcceso;
        if (factura.getClaveacceso() == null) {
            claveAcceso = claveAccesoGenerator.generarClaveAcceso(factura, def);
        } else {
            claveAcceso = factura.getClaveacceso();
        }

        InfoTributaria infoTributaria = new InfoTributaria();
        infoTributaria.setAmbiente(def.getTipoambiente());
        infoTributaria.setTipoEmision((byte) 1);
        infoTributaria.setRazonSocial(def.getRazonsocial());
        infoTributaria.setNombreComercial(def.getNombrecomercial());
        infoTributaria.setRuc(def.getRuc());
        infoTributaria.setClaveAcceso(claveAcceso);
        infoTributaria.setCodDoc("01"); // "01" para factura
        infoTributaria.setEstab(factura.getEstablecimiento());
        infoTributaria.setPtoEmi(factura.getPuntoemision());
        infoTributaria.setSecuencial(factura.getSecuencial());
        infoTributaria.setDirMatriz(factura.getDireccionestablecimiento());
        return infoTributaria;
    }

    private InfoFactura crearInfoFactura(Factura factura) {
        TotalSinImpuestos tSinImpuestos = fDetalleR.getTotalSinImpuestos(factura.getIdfactura());
        InfoFactura infoFactura = new InfoFactura();
        infoFactura.setFechaEmision(formatToDDMMYYYY(factura.getFechaemision()));
        infoFactura.setObligadoContabilidad("SI");
        infoFactura.setTipoIdentificacionComprador(factura.getTipoidentificacioncomprador());
        infoFactura.setRazonSocialComprador(factura.getRazonsocialcomprador());
        infoFactura.setIdentificacionComprador(factura.getIdentificacioncomprador());
        infoFactura.setDireccionComprador(factura.getDireccioncomprador());
        // infoFactura.setContribuyenteEspecial(factura.getContribuyenteEspecial());
        infoFactura.setTotalSinImpuestos(tSinImpuestos.getTotalsinimpuestos().setScale(2, RoundingMode.HALF_UP));
        infoFactura.setTotalDescuento(tSinImpuestos.getDescuento().setScale(2, RoundingMode.HALF_UP));
        infoFactura.setTotalConImpuestos(crearTotalConImpuestos(factura));

        infoFactura.setPropina(BigDecimal.ZERO);
        infoFactura.setImporteTotal(tSinImpuestos.getTotalsinimpuestos().add(tSinImpuestos.getDescuento()).setScale(2,
                RoundingMode.HALF_UP));
        infoFactura.setMoneda("DOLAR");

        /* totalConImpuestos */
        return infoFactura;
    }

    private List<Detalle> mapearDetalles(List<FacturaDetalle> detallesFactura) {
        return detallesFactura.stream().map(d -> {
            Detalle detalle = new Detalle();
            detalle.setCodigoPrincipal(d.getCodigoprincipal());
            detalle.setDescripcion(d.getDescripcion());
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(d.getPreciounitario());
            detalle.setDescuento(d.getDescuento());
            detalle.setPrecioTotalSinImpuesto(new BigDecimal(0));

            // Mapear impuestos
            detalle.setImpuestos(d.getImpuestos().stream().map(i -> {
                Impuesto impuesto = new Impuesto();
                impuesto.setCodigo(i.getCodigoimpuesto());
                impuesto.setCodigoPorcentaje(i.getCodigoporcentaje());
                impuesto.setTarifa(new BigDecimal(0));
                impuesto.setBaseImponible(i.getBaseimponible());
                impuesto.setValor(new BigDecimal(0));
                return impuesto;
            }).collect(Collectors.toList()));

            return detalle;
        }).collect(Collectors.toList());
    }

    public static String formatToDDMMYYYY(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
        return dateTime.format(formatter);
    }

    private TotalConImpuestos crearTotalConImpuestos(Factura factura) {
        TotalConImpuestos totalConImpuestos = new TotalConImpuestos();

        Map<String, BigDecimal> totalesPorImpuesto = factura.getDetalles().stream()
                .flatMap(d -> d.getImpuestos().stream()) // aplanar lista de impuestos por detalle
                .collect(Collectors.groupingBy(
                        FacturaDetalleImpuesto::getCodigoimpuesto, // clave del mapa
                        Collectors.reducing( // reduce (suma) los valores por clave
                                BigDecimal.ZERO, // valor inicial
                                FacturaDetalleImpuesto::getBaseimponible, // lo que se suma
                                BigDecimal::add // cómo se suman
                        )));

        // Crear lista de totales por impuesto
        List<TotalImpuesto> totales = totalesPorImpuesto.entrySet().stream()
                .map(entry -> {
                    TotalImpuesto totalImpuesto = new TotalImpuesto();
                    totalImpuesto.setCodigo(entry.getKey());
                    totalImpuesto.setCodigoPorcentaje(obtenerCodigoPorcentaje(entry.getKey()));
                    totalImpuesto.setBaseImponible(calcularBaseImponible(factura, entry.getKey()));
                    totalImpuesto.setValor(entry.getValue());
                    return totalImpuesto;
                }).collect(Collectors.toList());

        totalConImpuestos.setTotalImpuestos(totales);
        return totalConImpuestos;
    }

    private String convertirObjetoAXml(Comprobante comprobante) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Comprobante.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter writer = new StringWriter();
        marshaller.marshal(comprobante, writer);

        return writer.toString();
    }

    private Definir getDefinir() {
        Long id = 1L;
        Definir definir = definirR.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro no encontrado con ID: " + id));
        return definir;
    }

    // Métodos auxiliares
    private String obtenerCodigoPorcentaje(String codigoImpuesto) {
        // Lógica para determinar el código de porcentaje según el impuesto
        return "2"; // IVA 12%
    }

    private BigDecimal calcularBaseImponible(Factura factura, String codigoImpuesto) {
        if (factura == null || factura.getDetalles() == null || codigoImpuesto == null) {
            return BigDecimal.ZERO;
        }

        return factura.getDetalles().stream()
                .filter(Objects::nonNull)
                .map(FacturaDetalle::getImpuestos)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(i -> codigoImpuesto.equals(i.getCodigoimpuesto()))
                .map(FacturaDetalleImpuesto::getBaseimponible)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void processAndSendInvoice(String toEmail, String subject, String body, MultipartFile xmlFile)
            throws Exception {
        System.out.println(xmlFile.getSize());
        // Convertir el archivo XML a bytes
        byte[] xmlData = xmlFile.getBytes();
        System.out.println(xmlData);
        // Generar PDF a partir del XML
        //byte[] pdfData = PdfGenerationService.generatePdfFromXml(xmlData);

        // Enviar por email
        String attachmentName = "factura_" + System.currentTimeMillis() + ".pdf";
/*         emailService.se(
                toEmail,
                subject,
                body,
                pdfData,
                attachmentName); */
    }
}