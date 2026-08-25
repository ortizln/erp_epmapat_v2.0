package com.erp.sri_files.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErpDocumentoRequest {

    private String tipoDocumento;
    private String externalId;
    private String origenSistema;

    private String rucEmisor;
    private String ambiente;
    private String claveAcceso;

    private String identificacionReceptor;
    private String razonSocialReceptor;
    private String emailReceptor;

    private BigDecimal subtotal;
    private BigDecimal totalImpuestos;
    private BigDecimal totalDescuento;
    private BigDecimal importeTotal;

    private List<DetalleItem> detalles;

    @Data
    @NoArgsConstructor
    public static class DetalleItem {
        private String codigoPrincipal;
        private String codigoAuxiliar;
        private String descripcion;
        private BigDecimal cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuento;
        private BigDecimal precioTotalSinImpuesto;
        private String codigoImpuesto;
        private String codigoPorcentaje;
        private BigDecimal baseImponible;
        private BigDecimal valorImpuesto;
    }
}
