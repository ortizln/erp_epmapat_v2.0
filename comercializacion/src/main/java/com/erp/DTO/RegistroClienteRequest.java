package com.erp.DTO;

import lombok.Data;

@Data
public class RegistroClienteRequest {
    private String nombre;
    private String email;
    private String username;
    private String password;
}
