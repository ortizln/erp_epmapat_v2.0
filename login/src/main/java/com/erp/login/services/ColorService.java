package com.erp.login.services;

import com.erp.login.models.Colores;
import com.erp.login.repositories.ColoresR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorService {
    @Autowired
    ColoresR dao;

    public List<Colores> findTonos() {
        return dao.findTonos();
    }

    public List<Colores> findByTono(String codigo) {
        return dao.findByTono(codigo);
    }
}
