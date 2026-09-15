package com.erp.sri_files.validation;

import java.util.List;

public class FacturaPrevalidacionException extends IllegalArgumentException {
    private final List<String> errores;
    public FacturaPrevalidacionException(List<String> errores) {
        super("VALIDACION_PREVIA: " + String.join(" | ", errores));
        this.errores = List.copyOf(errores);
    }
    public List<String> getErrores() { return errores; }
}
