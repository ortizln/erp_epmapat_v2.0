package com.epmapat.erp_epmapat.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "facxnc")
public class Facxnc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfacxnc;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfactura_facturas")
    private Facturas idfactura_facturas;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idvaloresnc_valoresnc")
    private Valoresnc idvaloresnc_valoresnc;
}
