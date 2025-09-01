package com.erp.comercializacion
.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.interfaces.ConvenioOneData;
import com.erp.comercializacion.interfaces.EstadoConvenios;
import com.erp.comercializacion.models.Convenios;
import com.erp.comercializacion.repositories.ConveniosR;

@Service
public class ConvenioServicio {

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

    public List<Convenios> findByReferencia(Long referencia) {
        return dao.findByReferencia(referencia);
    }

    public List<EstadoConvenios> getEstadoByConvenios() {
        return dao.getEstadoByConvenios();
    }

    public Page<EstadoConvenios> getByFacPendientes(Long d, Long h, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return dao.getByFacPendientes(d, h, pageable);
    }

    public List<EstadoConvenios> gePendienteByConvenio(Long idconvenio) {
        return dao.gePendienteByConvenio(idconvenio);
    }

    public List<ConvenioOneData> findDatosConvenio(Long idconvenio) {
        return dao.findDatosConvenio(idconvenio);
    }

}