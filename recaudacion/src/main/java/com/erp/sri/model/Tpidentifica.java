package com.erp.sri.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "tpidentifica")
public class Tpidentifica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtpidentifica;
    private String codigo;
    private String nombre;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;
}
