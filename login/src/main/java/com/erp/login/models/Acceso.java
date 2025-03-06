package com.erp.login.models;

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
@Table(name = "acceso")
public class Acceso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idacc;
    private String codacc;
    private String nomacc;
    private Integer regacc;
}
