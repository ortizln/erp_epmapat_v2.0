package com.erp.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.CondMultasIntereses;
import com.erp.repositorio.CondMultasInteresesR;

@Service
public class CondMultasInteresesServicio {
    @Autowired
    CondMultasInteresesR dao;

    public <S extends CondMultasIntereses> S save(S entity) {
        return dao.save(entity);
    }
}
