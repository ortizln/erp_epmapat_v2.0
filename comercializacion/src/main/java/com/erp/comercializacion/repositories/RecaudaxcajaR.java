package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Recaudaxcaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface RecaudaxcajaR extends JpaRepository<Recaudaxcaja, Long> {

    //Busca por Caja
    @Query(value = "SELECT * FROM recaudaxcaja WHERE idcaja_cajas=?1 AND fechainiciolabor BETWEEN (?2) AND (?3) order by fechainiciolabor desc", nativeQuery = true)
    public List<Recaudaxcaja> findByCaja(Long idcaja, Date desde, Date hasta);
    //Ultima conexión
    @Query(value = "SELECT * FROM recaudaxcaja WHERE idcaja_cajas=?1 order by idrecaudaxcaja desc limit 1", nativeQuery = true)
    public Recaudaxcaja findLastConexion(Long idcaja);
}
