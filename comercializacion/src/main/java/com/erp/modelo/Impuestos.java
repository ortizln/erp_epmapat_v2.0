package com.erp.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "impuestos")
public class Impuestos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idimpuesto;
    private Long codigo;
    private String descripcion;
    private Long valor;
    private Boolean estado;
}
