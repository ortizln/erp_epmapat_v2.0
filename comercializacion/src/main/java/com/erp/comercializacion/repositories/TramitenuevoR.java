package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Tramitenuevo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TramitenuevoR extends JpaRepository<Tramitenuevo, Long> {

    @Query(value = "SELECT * FORM tramitenuevo LIMIT 10", nativeQuery=true)
    List<Tramitenuevo> findAll();

    //@Transactional
    //@Modifying(clearAutomatically = true)
    //@Query(value = "DELETE FROM tramitenuevo AS tn WHERE NOT EXISTS(SELECT * FROM clientes AS c WHERE c.idnacionalidad_nacionalidad=n.idnacionalidad)AND n.idnacionalidad=?1 ", nativeQuery = true)
    //void deleteByIdQ(Long id);
    //@Query(value = "SELECT * FROM nacionalidad AS n WHERE EXISTS(SELECT * FROM clientes AS c WHERE c.idnacionalidad_nacionalidad=n.idnacionalidad)AND n.idnacionalidad=?1 ", nativeQuery = true)
    //List<TramiteNuevoM> used(Long id);

    @Query(value = "select * from tramitenuevo t inner join aguatramite a on t.idaguatramite_aguatramite = a.idaguatramite where  t.idaguatramite_aguatramite = ?1", nativeQuery= true)
    public List<Tramitenuevo> findByIdAguaTramite(Long idaguatramite);
}
