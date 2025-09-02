package com.erp.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.modelo.contabilidad.Pagoscobros;

public interface PagoscobrosR extends JpaRepository<Pagoscobros, Long>{

   @Query(value = "SELECT * FROM pagoscobros WHERE idbenxtra = ?1", nativeQuery = true)
	public List<Pagoscobros> getByIdbenxtra(Long Idbenxtra);

}
