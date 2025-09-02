package com.erp.reportes.facturas.interfaces;

import java.io.IOException;
import java.util.Map;
import java.sql.SQLException;

import com.erp.modelo.administracion.ReporteModelDTO;

import net.sf.jasperreports.engine.JRException;

public interface i_ReporteFacturasCobradas_G {
    /**
     * @param params
     * @return
     */
    ReporteModelDTO obtenerFacturasCobradas_G(Map<String, Object> params) throws JRException, IOException, SQLException;

}
