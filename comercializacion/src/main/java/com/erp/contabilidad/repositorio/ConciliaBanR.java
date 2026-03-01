package com.erp.contabilidad.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.contabilidad.modelo.ConciliaBan;
import com.erp.contabilidad.modelo.Cuentas;

public interface ConciliaBanR extends JpaRepository<ConciliaBan, Long>{

   ConciliaBan findByIdcuentaAndMes(Cuentas idcuenta, Integer mes);

}

