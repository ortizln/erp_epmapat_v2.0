package com.erp.sri.service;

import com.erp.sri.interfaces.Interes_int;
import com.erp.sri.repository.Interes_rep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class Interes_ser {
    @Autowired
    private Interes_rep dao;
    @Autowired
    private Lecturas_ser s_lectura;
    @Autowired
    private Factura_ser s_factura;

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
