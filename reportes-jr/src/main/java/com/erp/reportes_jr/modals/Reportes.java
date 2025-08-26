package com.erp.reportes_jr.modals;

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
@Table(name = "reportes")
public class Reportes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idreporte;

    private String nombre;

    @Lob
    private byte[] archivo;

    // Getters y setters
}