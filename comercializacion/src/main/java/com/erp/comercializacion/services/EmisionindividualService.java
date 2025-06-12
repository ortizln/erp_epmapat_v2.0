package com.erp.comercializacion.services;

import com.erp.comercializacion.interfaces.*;
import com.erp.comercializacion.models.Emisionindividual;
import com.erp.comercializacion.repositories.EmisionindividualR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
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

    public List<IemiIndividual> findLecturasNuevas(Long idemision) {
        return dao.findLecturasNuevas(idemision);
    }

    public List<IemiIndividual> findLecturasAnteriores(Long idemision) {
        return dao.findLecturasAnteriores(idemision);
    }

    public List<EmisionIndividualRI> getLecReport(Integer idemision) {
        return dao.getLecReport(idemision);
    }

    public List<EmisionIndividualRia> emisionIndividualAnterior(Integer idemision) {
        return dao.emisionIndividualAnterior(idemision);
    }

    public List<EmisionIndividualRin> emisionIndividualNueva(Integer idemision) {
        return dao.emisionIndividualNueva(idemision);
    }

    public List<R_refacturacion_int> getRefacturacionxEmision(Long idemision) {
        return dao.getRefacturacionxEmision(idemision);
    }

    public List<R_refacturacion_int> getRefacturacionxFecha(Date d, Date h) {
        return dao.getRefacturacionxFecha(d, h);
    }

    public List<RubroxfacI> getRefacturacionxEmisionRubrosAnteriores(Long idemision) {
        return dao.getRefacturacionxEmisionRubrosAnteriores(idemision);
    }

    public List<RubroxfacI> getRefacturacionxEmisionRubrosNuevos(Long idemision) {
        return dao.getRefacturacionxEmisionRubrosNuevos(idemision);
    }

    public List<RubroxfacI> getRefacturacionxFechaRubrosAnteriores(Date d, Date h) {
        return dao.getRefacturacionxFechaRubrosAnteriores(d, h);
    }

    public List<RubroxfacI> getRefacturacionxFechaRubrosNuevos(Date d, Date h) {
        return dao.getRefacturacionxFechaRubrosNuevos(d, h);
    }

    public List<FacEliminadas> getFacElimByFechaElimina(LocalDate d, LocalDate h) {
        return dao.getFacElimByFechaElimina(d, h);
    }

    public List<FacEliminadas> getFacElimByEmision(Long idemision) {
        return dao.getFacElimByEmision(idemision);
    }

}
