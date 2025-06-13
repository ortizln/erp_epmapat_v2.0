package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Rubroadicional;
import com.erp.comercializacion.repositories.RubroadicionalR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RubroadicionalService {
    @Autowired
    private RubroadicionalR rubroadicionalR;
    public List<Rubroadicional> findAll() {
        return rubroadicionalR.findAll();
    }
    public <S extends Rubroadicional> S save(S entity) {
        return rubroadicionalR.save(entity);
    }
    public Optional<Rubroadicional> findById(Long id) {
        return rubroadicionalR.findById(id);
    }
    public void deleteById(Long id) {
        rubroadicionalR.deleteById(id);
    }
    public List<Rubroadicional> findByIdTpTramtie(Long idtptramite) {
        return rubroadicionalR.findByIdTpTramtie(idtptramite);
    }

}
