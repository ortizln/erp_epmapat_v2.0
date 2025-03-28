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

    public Object _interesToFactura(FacturaToInteresDTO factura) {
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


    public Object interesToFactura(FacturaToInteresDTO factura) {

        // Variable para almacenar el interés total de todas las facturas
        final double[] totalInteres = { 0.0 };
        // Uso de Java Streams para mapear la lista
        //factura.stream().forEach(_factura -> {});
        // Convertir la fecha de creación a LocalDate
        LocalDate fecInicio;
        if (factura.getFormapago() == 4) {
            fecInicio = factura.getFectransferencia();

        } else {
            fecInicio = (factura.getFeccrea());

        }
        // LocalDate fecInicio = LocalDate.parse(_factura.getFeccrea());
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
