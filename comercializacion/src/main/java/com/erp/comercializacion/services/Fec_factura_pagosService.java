package com.erp.comercializacion.services;
import java.util.List;

import com.erp.comercializacion.models.Fec_factura_pagos;
import com.erp.comercializacion.repositories.Fec_factura_pagosR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class Fec_factura_pagosService {
    @Autowired
    private Fec_factura_pagosR dao;

    public List<Fec_factura_pagos> findAll(){
        return dao.findAll();
    }
    public <S extends Fec_factura_pagos> S save(S entity) {
        return dao.save(entity);
    }
    public List<Fec_factura_pagos> findByIdFactura(Long idfactura){
        return dao.getByIdfactura(idfactura);
    
    }
}
