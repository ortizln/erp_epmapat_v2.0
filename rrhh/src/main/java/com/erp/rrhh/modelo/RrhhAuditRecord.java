package com.erp.rrhh.modelo;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rrhh_audits_v1")
public class RrhhAuditRecord extends RrhhAuditableEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 60)
    private String auditType;

    @Column(nullable = false, length = 160)
    private String title;

    @Lob
    private String findings;

    @Lob
    private String actionPlan;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(nullable = false)
    private LocalDate auditDate;

    @Column(nullable = false, length = 120)
    private String responsible;
}

