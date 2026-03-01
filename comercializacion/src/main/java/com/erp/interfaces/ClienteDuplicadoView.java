package com.erp.interfaces;

public interface ClienteDuplicadoView {
    Long getIdcliente();
    String getCedula();
    String getNombre();
    String getDireccion();
    String getTelefono();
    String getEmail();
    Boolean getActivo();
    Integer getRepeticiones();
}
