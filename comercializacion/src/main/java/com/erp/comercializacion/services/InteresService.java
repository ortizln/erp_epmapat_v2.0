package com.erp.comercializacion.services;

import com.erp.comercializacion.DTO.FacturaToInteresDTO;
import com.erp.comercializacion.interfaces.FacturasSinCobroInter;
import com.erp.comercializacion.models.Intereses;
import com.erp.comercializacion.repositories.InteresesR;
import com.erp.comercializacion.repositories.LecturasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InteresService {
    @Autowired
    private InteresesR dao;
    @Autowired
    private LecturasR dao_lecturas;
    public List<Intereses> findAll() {
        return dao.findAll();
    }

    public List<Intereses> findAll(Sort sort) {
        return dao.findAll(sort);
    }

    public List<Intereses> findByAnioMes(Number anio, Number mes) {
        return dao.findByAnioMes(anio, mes);
    }

    public List<Intereses> findUltimo() {
        return dao.findUltimo();
    }

    public <S extends Intereses> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Intereses> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    public void delete(Intereses entity) {
        dao.delete(entity);
    }

    public Object interesToFactura(FacturaToInteresDTO factura) {
        FacturasSinCobroInter _factura = dao_lecturas.findFechaEmision(factura.getIdfactura());
        LocalDate fecInicio;

          //if(_factura == null){
              //BUSCAR DATOS DE FACTURA EN FACTURA DE SERVICIOS
         // }
         if(_factura.getFormapago() == 4){
             fecInicio = _factura.getFechatransferencia();
         }else{
             fecInicio = _factura.getFeccrea();
         }
        final double[] totalInteres = { 0.0 };

        LocalDate fecFinal = LocalDate.now();
        int anioI = fecInicio.getYear();
        int anioF = fecFinal.getYear();
        int mesF = fecFinal.getMonthValue();
        List<Float> todosPorcentajes = new ArrayList<>();
        if (anioI < anioF) {
            int mesI = fecInicio.getMonthValue();
            if (mesI == 12 && mesF == 1 && anioI + 1 == anioF) {
            } else {
                while (anioI <= anioF) {
                    if (anioI < anioF) {
                        List<Float> porcentaje = dao.porcentajes(anioI, mesI, 12);
                        todosPorcentajes.addAll(porcentaje); // Añadir los porcentajes a la lista total
                    } else if (anioI == anioF) {
                        List<Float> porcentaje = new ArrayList<>(); // Inicializa la lista
                        if (fecInicio.getMonthValue() == (fecFinal.getMonthValue() - 1)) {
                            porcentaje.add(0.00f);
                        } else {
                            porcentaje = dao.porcentajes(anioF, 1, fecFinal.getMonthValue() - 2);
                            todosPorcentajes.addAll(porcentaje);
                        }
                    }
                    mesI = 1;
                    anioI++;
                }
            }

        } else if (anioF < anioI) {
        } else {
            List<Float> porcentaje = new ArrayList<>(); // Inicializa la lista
            if (fecInicio.getMonthValue() == (fecFinal.getMonthValue() - 1)) {
                porcentaje.add(0.00f);
            } else {

                porcentaje = dao.porcentajes(fecFinal.getYear(), fecInicio.getMonthValue(),
                        fecFinal.getMonthValue() - 2);
                todosPorcentajes.addAll(porcentaje);
            }
        }
        todosPorcentajes.forEach(interes -> {
            double interesCalculado = (interes * (factura.getSubtotal() + totalInteres[0])) / 100;
            totalInteres[0] += interesCalculado; // Sumar al interés total
        });
        return totalInteres[0];
    }

}
