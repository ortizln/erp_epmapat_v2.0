package com.erp.DTO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public final class TarifasConst {
    private TarifasConst(){}
    public static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);
    public static final int SCALE_MONEY = 2;
    public static final BigDecimal HALF = new BigDecimal("0.5");
    public static final BigDecimal TEN_CENTS = new BigDecimal("0.10");
    public static final BigDecimal FIFTY_CENTS = new BigDecimal("0.50");

    // Rubros
    public static final long RUBRO_AP   = 1001L;
    public static final long RUBRO_ALC  = 1002L;
    public static final long RUBRO_SAN  = 1003L;
    public static final long RUBRO_CF   = 1004L;
    public static final long RUBRO_EXC  = 1005L;
    public static final long RUBRO_INT  = 6L;     // Interés/multa si usas ese id
}