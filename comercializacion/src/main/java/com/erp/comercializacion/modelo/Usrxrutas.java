package com.erp.comercializacion.modelo;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.erp.comercializacion.modelo.administracion.Usuarios;

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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usrxrutas")
public class Usrxrutas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idusrxruta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuario_usuarios")
    private Usuarios idusuario_usuarios;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemision_emisiones")
    private Emisiones idemision_emisiones;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<Rutas> rutas;
}


