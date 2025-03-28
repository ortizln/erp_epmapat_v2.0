package com.erp.pagosonline.services;

import com.erp.pagosonline.DTO.FacturaDTO;
import com.erp.pagosonline.interfaces.FacturasSinCobroInter;
import com.erp.pagosonline.repositories.FacturasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FacturasService {
    @Autowired
    private FacturasR dao;
    @Autowired
    private RestTemplate rt;

    private List<Long> idfacturas;
    private final String URL = "http://localhost:8080";
    public FacturaDTO findFacturasSinCobro(Long cuenta) {
        // Obtener la lista de facturas desde el DAO
        List<FacturasSinCobroInter> facturas = dao.findFacturasSinCobro(cuenta);
        FacturaDTO facturaDTO = new FacturaDTO(); // Crear el DTO
        List<Long> idfacturas = new ArrayList<>(); // Inicializar lista de IDs
        final BigDecimal[] total = {BigDecimal.ZERO};
        final BigDecimal[] _interes = {BigDecimal.ZERO};

        // Llenar la lista con los IDs de las facturas
        facturas.forEach(item -> {
            _interes[0] = _interes[0].add(getInteres(item));
            total[0] = total[0].add(item.getSubtotal()); // CORREGIDO
            idfacturas.add(item.getIdfactura()); // Agregar el ID a la lista
        });

        if (!facturas.isEmpty()) {
            facturaDTO.setResponsablepago(facturas.get(0).getNombre());
        }
        facturaDTO.setCuenta(cuenta);
        facturaDTO.setTotal(total[0].add(_interes[0])); // Se asigna el total corregido
        facturaDTO.setFacturas(idfacturas); // Asignar la lista al DTO

        // Devolver el DTO con la lista de facturas
        return facturaDTO;
    }

    public BigDecimal getInteres(FacturasSinCobroInter factura){
        //LocalDate fecEmision = getFecEmision(factura.getIdfactura());
        return rt.getForObject(this.URL+"/intereses/calcularInteres?idfactura="+factura.getIdfactura()+"&subtotal="+factura.getSubtotal(), BigDecimal.class);
    }
    public LocalDate getFecEmision(Long idfactura){
        return rt.getForObject(this.URL+"/lecturas/fecEmision?idfactura="+idfactura, LocalDate.class);
    }

}