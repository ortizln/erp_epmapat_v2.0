package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "precioxcat")
public class Precioxcat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idprecioxcat;
    private Long m3;
    private BigDecimal preciobase;
    private BigDecimal precioadicional;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcategoria_categorias")
    private Categorias idcategoria_categorias;
    private Long usucrea;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    @Column(name = "feccrea")
    private Date feccrea;
    private Long usumodi;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    @Column(name = "fecmodi")
    private Date fecmodi;
}
