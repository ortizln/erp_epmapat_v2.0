package com.epmapat.erp_epmapat.modelo.rrhh;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "contemergencia")
public class Contemergencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcontemergencia;
    private String nombre;
    private String celular;
    private String parentesco;

}
