package com.erp.login.models;

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
@Table(name = "tabla4")
public class Tabla4 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtabla4;
    private String tipocomprobante;
    private String nomcomprobante;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;

}
