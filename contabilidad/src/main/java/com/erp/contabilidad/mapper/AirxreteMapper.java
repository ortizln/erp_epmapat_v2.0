package com.erp.contabilidad.mapper;

import org.springframework.stereotype.Component;

import com.erp.contabilidad.dto.AirxreteCreateDTO;
import com.erp.contabilidad.modelo.Airxrete;
import com.erp.contabilidad.modelo.Retenciones;
import com.erp.contabilidad.modelo.Tabla10;

@Component
public class AirxreteMapper {

   public Airxrete toEntity(AirxreteCreateDTO dto, Retenciones retencion, Tabla10 tabla10) {
      Airxrete a = new Airxrete();
      a.setBaseimpair0(dto.baseimpair0());
      a.setBaseimpair12(dto.baseimpair12());
      a.setBaseimpairno(dto.baseimpairno());
      a.setBaseimpair(dto.baseimpair());
      a.setValretair(dto.valretair());
      a.setIdrete(retencion);
      a.setIdtabla10(tabla10);
      return a;
   }
}

