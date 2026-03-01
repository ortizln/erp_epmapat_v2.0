package com.erp.DTO;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class RecargoXCtaReq {
    private Long idabonado;
    private Long idemision;
    private Long idrubro;
    private Integer tipo;
    private String observacion;
    private Long usucrea;
    private Long usuresp;
    private Timestamp fecha;
}
