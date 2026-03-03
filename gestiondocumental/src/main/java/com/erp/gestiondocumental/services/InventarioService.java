package com.erp.gestiondocumental.services;

import com.erp.gestiondocumental.models.Inventario;
import com.erp.gestiondocumental.repositories.InventarioR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {
    @Autowired
    private InventarioR dao;
    public List<Inventario> findAll(){
        return dao.findAll();
    }
    public Optional<Inventario> findById(Long id){
        return dao.findById(id);
    }
    public Inventario save(Inventario inventario){
        return dao.save(inventario);
    }
}

