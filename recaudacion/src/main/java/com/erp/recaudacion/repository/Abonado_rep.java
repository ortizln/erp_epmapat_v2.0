package com.erp.recaudacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.recaudacion.interfaces.Abonado_int;
import com.erp.recaudacion.model.Abonados;

public interface Abonado_rep extends JpaRepository<Abonados, Long> {
	@Query(value = "select a.idcliente_clientes as cliente, a.idresponsable as responsable from abonados a where a.idabonado = ?1", nativeQuery = true)
	Abonado_int findClienteInAbonado(Long idcuenta);
}
