package com.erp.rrhh.modelo;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rrhh_conflict_cases")
public class RrhhConflictCase extends RrhhAuditableEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private RrhhEmployee employee;

    @Column(nullable = false, length = 160)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(nullable = false, length = 120)
    private String responsible;

    @Column(nullable = false)
    private LocalDate openedAt;

    @Column
    private LocalDate resolvedAt;

    @Lob
    private String resolution;
}

