package com.erp.pagosonline.services;

import com.erp.pagosonline.DTO.FacturaDTO;
import com.erp.pagosonline.interfaces.FacturasSinCobroInter;
import com.erp.pagosonline.repositories.FacturasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.StandardSocketOptions;
import java.util.ArrayList;
import java.util.List;

@Service
public class FacturasService {
    @Autowired
    private FacturasR dao;
    @Autowired
    private RestTemplate rt;
    private List<Long> idfacturas;
    private final String URL_INTERESES = "http://localhost:9093/intereses";

    public FacturaDTO findFacturasSinCobro(Long cuenta) {
        // Obtener la lista de facturas desde el DAO
        List<FacturasSinCobroInter> facturas = dao.findFacturasSinCobro(cuenta);
        FacturaDTO facturaDTO = new FacturaDTO(); // Crear el DTO
        List<Long> idfacturas = new ArrayList<>(); // Inicializar lista de IDs
        BigDecimal total = BigDecimal.ZERO;
        // Llenar la lista con los IDs de las facturas
        facturas.forEach(item -> {
            System.out.println(item.getIdfactura());
            System.out.println(getInteres(item));
            total.add(item.getSubtotal());
            idfacturas.add(item.getIdfactura()); // Agregar el ID a la lista
        });
        facturaDTO.setResponsablepago(facturas.get(0).getNombre());
        facturaDTO.setCuenta(cuenta);
        facturaDTO.setTotal(total);//CORREGIR EL VALOR TOTAL DE LA FACTURA
        // Asignar la lista al DTO
        facturaDTO.setFacturas(idfacturas);

        // Devolver el DTO con la lista de facturas
        return facturaDTO;
    }
    public Object getInteres(FacturasSinCobroInter factura){
        Object _factura = rt.getForObject(this.URL_INTERESES+"/calcularInteres?formapago="+factura.getFormapago()+"&subtotal="+factura.getSubtotal()+"&feccrea="+factura.getFeccrea(), Object.class);
        return _factura;
    }

}
