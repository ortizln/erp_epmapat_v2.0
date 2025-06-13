package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Pliego24;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Pliego24R extends JpaRepository<Pliego24, Long> {

    //Nuevo Pliego Trarifario
    @Query(value = "SELECT * FROM pliego24 order by idcategoria, desde", nativeQuery=true)
    public List<Pliego24> findTodos();

    //Tarifas de todas las Categorias de un determinado consumo (m3) Se usa solo en  la Simulación
    @Query(value = "SELECT * FROM pliego24 WHERE desde <=?1 and hasta >=?1 order by idcategoria, desde", nativeQuery=true)
    public List<Pliego24> findConsumos(Long m3);

    //Tarifa de un determinado conusmo(m3) de una Categoria y de una Gradualidad
    @Query(value = "SELECT * FROM pliego24 WHERE idcategoria=?1 and desde<=?2 and hasta>=?2", nativeQuery=true)
    public List<Pliego24> findBloque(Long idcategoria, Long m3);
}
