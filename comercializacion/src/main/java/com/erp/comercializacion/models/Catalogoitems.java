package com.erp.comercializacion.models;

import jakarta.persistence.*;
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
@Table(name = "catalogoitems")
public class Catalogoitems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcatalogoitems;
    private String descripcion;
    private Float cantidad;
    private Integer facturable;
    private Boolean estado;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="idusoitems_usoitems")
    private Usoitems idusoitems_usoitems;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="idrubro_rubros")
    private Rubros idrubro_rubros;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;
}
