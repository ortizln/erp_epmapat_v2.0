package com.erp.reportes_jr.DTO;


import lombok.Data;

@Data
public class ReportParameterDTO {
    private String name;   // nombre del parámetro
    private String type;   // tipo de dato (String, Integer, Date, etc.)
    private Object value;  // valor del parámetro

    public ReportParameterDTO() {
    }

    public ReportParameterDTO(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}