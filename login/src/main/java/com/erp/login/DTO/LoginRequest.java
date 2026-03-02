package com.erp.login.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String platform;
}
