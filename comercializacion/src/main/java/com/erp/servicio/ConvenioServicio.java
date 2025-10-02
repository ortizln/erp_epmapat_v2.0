package com.erp.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.erp.interfaces.ConvenioOneData;
import com.erp.interfaces.EstadoConvenios;
import com.erp.modelo.Convenios;
import com.erp.repositorio.ConveniosR;

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