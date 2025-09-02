package com.erp.modelo.administracion;

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
@Table(name = "erpmodulos")
public class Erpmodulos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iderpmodulo;
    private String descripcion;
}
