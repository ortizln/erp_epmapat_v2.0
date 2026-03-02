package com.erp.contabilidad.servicio;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.contabilidad.modelo.Tramipresu;
import com.erp.contabilidad.repositorio.TramipresuR;

@Service
public class TramipresuServicio {

   @Autowired
   private TramipresuR dao;

   public Tramipresu findFirstByOrderByNumeroDesc() {
      return dao.findFirstByOrderByNumeroDesc();
   }

   //Validar número de Trámite
   public boolean valNumero(Long numero) {
      return dao.valNumero(numero);
   }

   public <S extends Tramipresu> S save(S entity) {
      return dao.save(entity);
   }

   public Optional<Tramipresu> findById(Long id) {
      return dao.findById(id);
   }

   public List<Tramipresu> findDesdeHasta(Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha) {
      return dao.findDesdeHasta(desdeNum, hastaNum, desdeFecha, hastaFecha);
   }

}

