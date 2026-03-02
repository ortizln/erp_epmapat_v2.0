package com.erp.rrhh.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.rrhh.modelo.ThEmployeeFile;

public interface ThEmployeeFileR extends JpaRepository<ThEmployeeFile, Long> {
    List<ThEmployeeFile> findByIdpersonal_personal_IdpersonalOrderByVersion_docDesc(Long idpersonal);
}
