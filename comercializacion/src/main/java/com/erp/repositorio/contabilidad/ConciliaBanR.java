package com.erp.repositorio.contabilidad;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.modelo.contabilidad.ConciliaBan;
import com.erp.modelo.contabilidad.Cuentas;

public interface ConciliaBanR extends JpaRepository<ConciliaBan, Long>{

   ConciliaBan findByIdcuentaAndMes(Cuentas idcuenta, Integer mes);

}
