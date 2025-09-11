package com.erp.reportes_jr.DTO;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JasperDTO {
    private String reportName;
    private Map<String, Object> parameters = new HashMap<>();
    private String extencion;

}
