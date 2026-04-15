package com.erp.rrhh.modelo;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rrhh_employee_files_v1")
public class RrhhEmployeeFileRecord extends RrhhAuditableEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private RrhhEmployee employee;

    @Column(nullable = false, length = 60)
    private String fileType;

    @Column(nullable = false, length = 160)
    private String fileName;

    @Column(length = 255)
    private String fileUrl;

    @Column
    private LocalDate issueDate;

    @Column
    private LocalDate expiryDate;

    @Column(nullable = false, length = 40)
    private String status;
}

