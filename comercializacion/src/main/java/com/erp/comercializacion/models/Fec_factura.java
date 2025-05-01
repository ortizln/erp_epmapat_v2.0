package com.erp.comercializacion.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fec_factura")
public class Fec_factura {
    @Id
    private Long idfactura;
    private String claveacceso;
    private String secuencial;
    private String xmlautorizado;
    private String errores;
    private String estado;
    private String establecimiento;
    private String puntoemision;
    private String direccionestablecimiento;
    private LocalDate fechaemision;
    private String tipoidentificacioncomprador;
    private String guiaremision;
    private String razonsocialcomprador;
    private String identificacioncomprador;
    private String direccioncomprador;
    private String telefonocomprador;
    private String emailcomprador;
    private String concepto;
    private String referencia;
    private String recaudador;
}
