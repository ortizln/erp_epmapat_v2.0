package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Itemxfact;
import com.erp.comercializacion.repositories.ItemxfactR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemxfactService {
    @Autowired
    private ItemxfactR dao;

    // Productos de una Facturación
    public List<Itemxfact> getByIdfacturacion(Long idfacturacion) {
        return dao.findByIdfacturacion(idfacturacion);
    }

    //Movimientos de un Producto
    public List<Itemxfact> getByIdcatalogoitems(Long idcatalogoitems) {
        return dao.findByIdcatalogoitems(idcatalogoitems);
    }

    // Grabar
    public <S extends Itemxfact> S save(S entity) {
        return dao.save(entity);
    }

}
