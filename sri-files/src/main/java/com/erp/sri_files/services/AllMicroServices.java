package com.erp.sri_files.services;

import com.erp.sri_files.interfaces.fecFacturaDatos;
import com.erp.sri_files.models.Factura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AllMicroServices {
    @Autowired
    private RestTemplate restTemplate;
    private final String URL_COMERCIALIZACION_FACTURAS = "http://192.168.0.69:8080/facturas";
    private final String URL_COMERCIALIZACION_FEC_FACTURAS = "http://192.168.0.69:8080/fec_factura";
    public Factura findById(Long idFactura){
        Factura factura = restTemplate.getForObject(URL_COMERCIALIZACION_FACTURAS+"/"+idFactura, Factura.class);
        return  factura;
    }
    public fecFacturaDatos getNroFactura(Long idfactura){
        fecFacturaDatos fec_factura = restTemplate.getForObject(URL_COMERCIALIZACION_FACTURAS+"/fecFacturaDatos"+idfactura, fecFacturaDatos.class);
        return  fec_factura;
    }
}
