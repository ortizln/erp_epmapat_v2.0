package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tramites1")
public class Tramites1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtramite;

    private Float valor;
    private String descripcion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcliente_clientes")
    private Clientes idcliente_clientes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idabonado_abonados")
    private Abonados idabonado_abonados;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha")
    private Date fecha;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "validohasta")
    private Date validohasta;
    @ManyToMany
    @JoinTable(name = "rubrosxtramite", joinColumns = @JoinColumn(name = "idtramite_tramites"), inverseJoinColumns = @JoinColumn(name = "idrubro_rubros"))
    Set<Rubros> rubrosSeleccionados = new HashSet<>();
    private Long usucrea;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "feccrea")
    private Date feccrea;
    private Long usumodi;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso =DateTimeFormat.ISO.DATE)
    @Column(name = "fecmodi")
    private Date fecmodi;
    public void addRubros(Rubros rubrosM) {
        rubrosSeleccionados.add(rubrosM);
    }
}
