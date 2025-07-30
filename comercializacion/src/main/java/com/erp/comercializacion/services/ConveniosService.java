package com.erp.comercializacion.services;

import com.erp.comercializacion.interfaces.EstadoConvenios;
import com.erp.comercializacion.models.Convenios;
import com.erp.comercializacion.repositories.ConveniosR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConveniosService {
    @Autowired
    private ConveniosR dao;

    public List<Convenios> conveniosDesdeHasta(Integer desde, Integer hasta) {
        return dao.findByNroconvenioBetweenOrderByNroconvenioAsc(desde, hasta);
    }

    public List<Convenios> findNroconvenio(Long nroconvenio) {
        return dao.findNroconvenio(nroconvenio);
    }

    // Último Nroconvenio
    public Convenios ultimoNroconvenio() {
        return dao.findFirstByOrderByNroconvenioDesc();
    }

    // Siguiente Nroconvenio
    public Integer siguienteNroconvenio() {
        Convenios x = dao.findTopByOrderByNroconvenioDesc();
        if (x != null) {
            Integer ultConvenio = x.getNroconvenio();
            return ultConvenio + 1;
        } else {
            return 1;
        }
    }

    // Valida Nroconvenio
    public boolean valNroconvenio(Integer nroconvenio) {
        return dao.valNroconvenio(nroconvenio);
    }

    public <S extends Convenios> boolean existsByNroconvenio() {
        return dao.exists(null);
    }

    public <S extends Convenios> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Convenios> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    public List<Convenios> findByReferencia(Long referencia){
        return dao.findByReferencia(referencia);
    }
    public List<EstadoConvenios> getEstadoByConvenios(){
        return dao.getEstadoByConvenios();
    }
    public Page<EstadoConvenios> getByFacPendientes(Long d, Long h, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return dao.getByFacPendientes(d, h, pageable);
    }
    public List<EstadoConvenios> gePendienteByConvenio(Long idconvenio){
        return dao.gePendienteByConvenio(idconvenio);
    }
}
