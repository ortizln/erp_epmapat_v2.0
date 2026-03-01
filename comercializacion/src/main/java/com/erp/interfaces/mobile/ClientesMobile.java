package com.erp.interfaces.mobile;

import java.time.LocalDate;

import com.erp.interfaces.NacionalidadView;
import com.erp.interfaces.TpidentificaView;

public interface ClientesMobile {
    Long getIdcliente();
    String getCedula();
    String getNombre();
    String getDireccion();
    String getTelefono();
    String getEmail();
    LocalDate getFechanacimiento();
    TpidentificaView getIdtpidentifica_tpidentifica();
    NacionalidadView getIdnacionalidad_nacionalidad();
    String getUsername();
    Boolean getActivo();
    String getRol();
}
