package com.erp.login.services;

import com.erp.login.models.Erpmodulos;
import com.erp.login.repositories.ErpmodulosR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErpmodulosService {
    @Autowired
    private ErpmodulosR dao;

    public List<Erpmodulos> findAll() {
        return dao.findAll();
    }
}
