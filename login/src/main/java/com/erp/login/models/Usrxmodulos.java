package com.erp.login.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="usrxmodulos")
public class Usrxmodulos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idusrxmodulos;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuario_usuarios")
    private Usuarios idusuario_usuarios;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iderpmodulo_erpmodulos")
    private Erpmodulos iderpmodulo_erpmodulos;
    private Boolean enabled;
}
