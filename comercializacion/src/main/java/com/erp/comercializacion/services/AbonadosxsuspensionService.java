package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Abonadosxsuspension;
import com.erp.comercializacion.repositories.AbonadosxsuspensionR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AbonadosxsuspensionService {
    @Autowired
    private AbonadosxsuspensionR aboxsuspensionR;

    public List<Abonadosxsuspension> findByIdsuspension(Long idsuspension) {
        return aboxsuspensionR.findByIdsuspension(idsuspension);
    }

    public List<Abonadosxsuspension> findAll() {
        return aboxsuspensionR.findAll();
    }

    public <S extends Abonadosxsuspension> S save(S entity) {
        return aboxsuspensionR.save(entity);
    }

    public Optional<Abonadosxsuspension> findById(Long id) {
        return aboxsuspensionR.findById(id);
    }
    public void deleteById(Long id) {
        aboxsuspensionR.deleteById(id);
    }
    public void delete(Abonadosxsuspension entity) {
        aboxsuspensionR.delete(entity);
    }
}
