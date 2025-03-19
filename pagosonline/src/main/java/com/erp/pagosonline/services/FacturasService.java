package com.erp.pagosonline.services;

import com.erp.pagosonline.DTO.FacturaDTO;
import com.erp.pagosonline.interfaces.FacturasSinCobroInter;
import com.erp.pagosonline.repositories.FacturasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturasService {
    @Autowired
    private FacturasR dao;
    public List<FacturasSinCobroInter> findFacturasSinCobro(Long cuenta) {
        // Obtener la lista de facturas desde el DAO
        List<FacturasSinCobroInter> facturas = dao.findFacturasSinCobro(cuenta);

        // Procesar la lista (por ejemplo, imprimir los IDs)
        facturas.forEach(item -> System.out.println(item.getIdfactura()));
        // Devolver la lista de facturas
        return facturas;
    }
}
