package com.erp.comercializacion
.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.models.CondMultasIntereses;
import com.erp.comercializacion.repositories.CondMultasInteresesR;

@Service
public class CondMultasInteresesServicio {
    @Autowired
    CondMultasInteresesR dao;

    public <S extends CondMultasIntereses> S save(S entity) {
        return dao.save(entity);
    }
}
