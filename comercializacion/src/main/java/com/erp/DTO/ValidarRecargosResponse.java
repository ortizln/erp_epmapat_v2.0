package com.erp.DTO;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ValidarRecargosResponse {
    private List<ReglaBloqueo> bloqueados = new ArrayList<>();

    public void addBloqueado(Long idabonado, Integer tipo, String motivo) {
        bloqueados.add(new ReglaBloqueo(idabonado, tipo, motivo));
    }

    @Data
    public static class ReglaBloqueo {
        private Long idabonado;
        private Integer tipo;
        private String motivo;

        public ReglaBloqueo(Long idabonado, Integer tipo, String motivo) {
            this.idabonado = idabonado;
            this.tipo = tipo;
            this.motivo = motivo;
        }
    }
}
