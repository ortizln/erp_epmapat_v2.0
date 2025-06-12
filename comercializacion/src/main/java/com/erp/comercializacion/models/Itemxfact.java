package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="itemxfact")
public class Itemxfact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iditemxfact;
    private Long estado;
    private Float cantidad;
    private Float valorunitario;
    private Float descuento;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfacturacion_facturacion")
    private Facturacion idfacturacion_facturacion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcatalogoitems_catalogoitems")
    private Catalogoitems idcatalogoitems_catalogoitems;
}
