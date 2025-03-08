package com.erp.comercializacion.services;

import com.erp.comercializacion.interfaces.*;
import com.erp.comercializacion.models.Emisionindividual;
import com.erp.comercializacion.repositories.EmisionindividualR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmisionindividualService {
    @Autowired
    private EmisionindividualR dao;

    public <S extends Emisionindividual> S save(S entity) {
        return dao.save(entity);
    }

    public List<Emisionindividual> findByIdEmision(Long idemision) {
        return dao.findByIdEmision(idemision);
    }

    public List<EmisionindividualInterface> findLecturasNuevas(Long idemision) {
        return dao.findLecturasNuevas(idemision);
    }

    public List<EmisionindividualInterface> findLecturasAnteriores(Long idemision) {
        return dao.findLecturasAnteriores(idemision);
    }

    public List<RepEmisionindividual> getLecReport(Integer idemision) {
        return dao.getLecReport(idemision);
    }

    public List<RepEmisionindividualAnt> emisionIndividualAnterior(Integer idemision) {
        return dao.emisionIndividualAnterior(idemision);
    }

    public List<RepEmisionindividualNue> emisionIndividualNueva(Integer idemision) {
        return dao.emisionIndividualNueva(idemision);
    }

    public List<RepRefacturacion> getRefacturacionxEmision(Long idemision) {
        return dao.getRefacturacionxEmision(idemision);
    }
    public List<RepRefacturacion> getRefacturacionxFecha(LocalDate d, LocalDate h) {
        return dao.getRefacturacionxFecha(d, h);
    }
}
