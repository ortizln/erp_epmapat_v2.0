package com.erp.sri.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fec_factura")
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfactura;
    
    private String claveacceso;
    private String secuencial;
    private String xmlautorizado;
    private String errores;
    private String estado;
    private String establecimiento;
    private String puntoemision;
    private String direccionestablecimiento;
    private LocalDateTime fechaemision;
    private String tipoidentificacioncomprador;
    private String identificacioncomprador;
    private String guiaremision;
    private String razonsocialcomprador;
    private String telefonocomprador;
    private String emailcomprador;
    private String concepto;
    private String recaudador;
    private String referencia;
    private String direccioncomprador;
    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<FacturaDetalle> detalles = new ArrayList<>();
    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<FacturaPago> pagos = new ArrayList<>();
    
    // Getters y Setters
}