package com.erp.reportes_jr.DTO;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class JasperDTO {
    String reportName;
    Map<String, Object> parameters = new HashMap<>();
    String extencion;

}
