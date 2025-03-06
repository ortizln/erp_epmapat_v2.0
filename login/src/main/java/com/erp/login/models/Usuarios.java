package com.erp.login.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idusuario;
    private String identificausu;
    private String codusu;
    private String nomusu;
    private String email;
    private Boolean estado;
    private LocalDate fdesde;
    private LocalDate fhasta;
    private ZonedDateTime feccrea;
    private Long usumodi;
    private ZonedDateTime fecmodi;
    private Boolean otrapestania;
    private String alias;
    private String priusu;
    private String perfil;
    private Long toolbarframe;
    private Long toolbarsheet;
}
