package com.epmapat.erp_epmapat.modelo.rrhh;

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
@Table(name = "tpcontratos")
public class Tpcontratos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtpcontratos;
    private String descripcion;
}
