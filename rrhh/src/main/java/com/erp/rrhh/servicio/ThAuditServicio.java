package com.erp.rrhh.servicio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.rrhh.modelo.ThAuditLog;
import com.erp.rrhh.repositorio.ThAuditLogR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThAuditServicio {

    private final ThAuditLogR dao;

    @Transactional
    public void log(String entidad, Long idregistro, String accion, String detalle, Long usuario) {
        ThAuditLog a = new ThAuditLog();
        a.setEntidad(entidad);
        a.setIdregistro(idregistro);
        a.setAccion(accion);
        a.setDetalle(detalle);
        a.setUsuario(usuario);
        dao.save(a);
    }
}
