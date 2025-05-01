package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Convenios;
import com.erp.comercializacion.repositories.ConveniosR;
import org.springframework.beans.factory.annotation.Autowired;
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

    @SuppressWarnings("null")
    public <S extends Convenios> boolean existsByNroconvenio() {
        return dao.exists(null);
    }

    @SuppressWarnings("null")
    public <S extends Convenios> S save(S entity) {
        return dao.save(entity);
    }

    @SuppressWarnings("null")
    public Optional<Convenios> findById(Long id) {
        return dao.findById(id);
    }

    @SuppressWarnings("null")
    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    public List<Convenios> findByReferencia(Long referencia){
        return dao.findByReferencia(referencia);
    }
}
