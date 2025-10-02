package com.erp.sri.service;

import com.erp.sri.interfaces.Factura_int;
import com.erp.sri.interfaces.Interes_int;
import com.erp.sri.repository.Interes_rep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Interes_ser {
    @Autowired
    private Interes_rep dao;
    @Autowired
    private Lecturas_ser s_lectura;
    @Autowired
    private Factura_ser s_factura;


    public Map<Long, BigDecimal> interesesOfFacturas(List<Factura_int> facturas) {
        Map<Long, BigDecimal> result = new HashMap<>();
        LocalDate fechaHoy = LocalDate.now();
        for (Factura_int factura : facturas) {
            Long id = factura.getIdfactura();
            List<Interes_int> detalles = s_lectura.getForIntereses(id);
            if (detalles.isEmpty()) {
                detalles = s_factura.getForIntereses(id);
            }

            double totalInteres = 0.0;

            for (Interes_int _factura : detalles) {
                if (_factura.getSwcondonar() != null && _factura.getSwcondonar()) {
                    continue;
                }

                LocalDate fecInicio = LocalDate.parse(_factura.getFeccrea());
                int anioI = fecInicio.getYear();
                int anioF = fechaHoy.getYear();

                List<Float> todosPorcentajes = new ArrayList<>();

                if (anioI < anioF) {
                    int mesI = fecInicio.getMonthValue();
                    while (anioI <= anioF) {
                        if (anioI < anioF) {
                            todosPorcentajes.addAll(dao.porcentajes(anioI, mesI, 12));
                        } else {
                            if (fecInicio.getMonthValue() == (fechaHoy.getMonthValue() - 1)) {
                                todosPorcentajes.add(0.00f);
                            } else {
                                todosPorcentajes.addAll(dao.porcentajes(anioF, 1, fechaHoy.getMonthValue() - 2));
                            }
                        }
                        mesI = 1;
                        anioI++;
                    }
                } else if (anioI == anioF) {
                    if (fecInicio.getMonthValue() == (fechaHoy.getMonthValue() - 1)) {
                        todosPorcentajes.add(0.00f);
                    } else {
                        todosPorcentajes.addAll(dao.porcentajes(fechaHoy.getYear(),
                                fecInicio.getMonthValue(), fechaHoy.getMonthValue() - 2));
                    }
                }

                BigDecimal suma = BigDecimal.valueOf(_factura.getSuma());
                for (Float p : todosPorcentajes) {
                    BigDecimal interesCalculado = (suma.add(BigDecimal.valueOf(totalInteres)))
                            .multiply(BigDecimal.valueOf(p))
                            .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
                    totalInteres += interesCalculado.doubleValue();
                }
            }

            result.put(id, BigDecimal.valueOf(totalInteres).setScale(2, RoundingMode.HALF_UP));
        }

        return result;
    }

    public BigDecimal interesOfFactura(Long idfactura) {
        Long id;
        id = idfactura;
        List<Interes_int> factura = s_lectura.getForIntereses(id);
        if (factura.isEmpty()) {
            factura = s_factura.getForIntereses(id);//Hacer la consulta para las facturas que no son de consumo de agua
        }
        // Variable para almacenar el interés total de todas las facturas
        final double[] totalInteres = { 0.0 };
        // Uso de Java Streams para mapear la lista
        factura.forEach(_factura -> {
            // Convertir la fecha de creación a LocalDate
            if(_factura.getSwcondonar() == null || !_factura.getSwcondonar()){
            LocalDate fecInicio = LocalDate.parse(_factura.getFeccrea());
            LocalDate fecFinal = LocalDate.now();
            int anioI = fecInicio.getYear();
            int anioF = fecFinal.getYear();
            List<Float> todosPorcentajes = new ArrayList<>();
            if (anioI < anioF) {
                int mesI = fecInicio.getMonthValue();
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
            }else if(anioF < anioI){
                System.out.println("Imprimiendo segundo if de: "+ _factura.getIdFactura());
            }else {
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
                double interesCalculado = (interes * (_factura.getSuma() + totalInteres[0])) / 100;
                totalInteres[0] += interesCalculado; // Sumar al interés total
            });
            }
        });
        return BigDecimal.valueOf(totalInteres[0]);
    }
}
