package com.erp.login.services;

import com.erp.login.interfaces.ErpModulosI;
import com.erp.login.models.Usrxmodulos;
import com.erp.login.repositories.UsrxmodulosR;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsrxmodulosService {
    private static final Logger logger = LoggerFactory.getLogger(UsrxmodulosService.class);

    @Autowired
    private UsrxmodulosR dao;

    public List<ErpModulosI> findModulosEnabledByUser(Long idusuario) {
        return dao.findModulosEnabledByUser(idusuario);
    }

    public List<Usrxmodulos> FindByUser(Long iduser) {
        return dao.FindByUser(iduser);
    }

    @Transactional
    public Usrxmodulos save(Usrxmodulos usrxmodulos) {
        if (usrxmodulos == null || usrxmodulos.getIdusuario_usuarios() == null
                || usrxmodulos.getIderpmodulo_erpmodulos() == null) {
            throw new IllegalArgumentException("El objeto Usrxmodulos o sus relaciones no pueden ser nulos");
        }

        Long idUsuario = usrxmodulos.getIdusuario_usuarios().getIdusuario();
        Long idErpModulo = usrxmodulos.getIderpmodulo_erpmodulos().getIderpmodulo();

        Usrxmodulos um = dao.findModulos(idUsuario, idErpModulo);

        if (um == null) {
            logger.info("Datos no encontrados, creando nuevo registro para usuario {} y módulo {}", idUsuario,
                    idErpModulo);
            um = usrxmodulos;
        }
        if (um != null) {
            logger.info("Datos encontrados, actualizando registro para usuario {} y módulo {}", idUsuario, idErpModulo);
            um.setEnabled(usrxmodulos.getEnabled());
        }
        return dao.save(um);
    }
}
