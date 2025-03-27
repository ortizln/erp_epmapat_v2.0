package com.erp.comercializacion.services;

import com.erp.comercializacion.DTO.FacturaToInteresDTO;
import com.erp.comercializacion.models.Intereses;
import com.erp.comercializacion.repositories.InteresesR;
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
        double totalInteres = 0.0;
        // Determinar fecha de inicio según forma de pago
        LocalDate fecInicio = factura.getFormapago() == 4
                ? factura.getFectransferencia()
                : factura.getFeccrea();

        LocalDate fecFinal = LocalDate.now();

        // Si las fechas son iguales o la final es anterior, no hay interés
        if (fecFinal.isBefore(fecInicio) || fecFinal.isEqual(fecInicio)) {
            return totalInteres;
        }

        // Caso especial: meses consecutivos en años consecutivos (dic-ene)
        if (isConsecutiveMonthsAcrossYears(fecInicio, fecFinal)) {
            return totalInteres;
        }

        List<Float> todosPorcentajes = calcularPorcentajesInteres(fecInicio, fecFinal);

        // Calcular interés compuesto
        for (Float porcentaje : todosPorcentajes) {
            double interesCalculado = (porcentaje * (factura.getSubtotal() + totalInteres)) / 100;
            totalInteres += interesCalculado;
        }
        return totalInteres;
    }

    private boolean isConsecutiveMonthsAcrossYears(LocalDate start, LocalDate end) {
        return start.getMonthValue() == 12
                && end.getMonthValue() == 1
                && start.getYear() + 1 == end.getYear();
    }

    private List<Float> calcularPorcentajesInteres(LocalDate fecInicio, LocalDate fecFinal) {
        List<Float> porcentajes = new ArrayList<>();
        int anioI = fecInicio.getYear();
        int anioF = fecFinal.getYear();

        if (anioI < anioF) {
            // Procesar años completos
            for (int anio = anioI; anio <= anioF; anio++) {
                int mesInicio = (anio == anioI) ? fecInicio.getMonthValue() : 1;
                int mesFin = (anio == anioF) ? fecFinal.getMonthValue() - 2 : 12;

                if (mesInicio <= mesFin) {
                    porcentajes.addAll(dao.porcentajes(anio, mesInicio, mesFin));
                }
            }
        } else {
            // Mismo año
            int mesInicio = fecInicio.getMonthValue();
            int mesFin = fecFinal.getMonthValue() - 2;

            if (mesInicio <= mesFin) {
                porcentajes.addAll(dao.porcentajes(anioI, mesInicio, mesFin));
            } else if (mesInicio == (fecFinal.getMonthValue() - 1)) {
                porcentajes.add(0.00f);
            }
        }

        return porcentajes;
    }

}
