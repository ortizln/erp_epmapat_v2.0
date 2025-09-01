package com.erp.comercializacion
.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.interfaces.RecaudaFacturasI;
import com.erp.comercializacion.interfaces.RecaudadorI;
import com.erp.comercializacion.models.Recaudacion;
import com.erp.comercializacion.repositories.RecaudacionR;

@Service
public class RecaudacionServicio {

   @Autowired
   private RecaudacionR dao;

   public <S extends Recaudacion> S save(S entity) {
      return dao.save(entity);
   }

   public Optional<Recaudacion> findById(Long id) {
      return dao.findById(id);
   }

   public Double totalRecaudado(Long idrecaudador, Date fechacobro) {
      return dao.totalRecaudado(idrecaudador, fechacobro);
   }

   public List<Recaudacion> findByRecFec(Long idrecaudador, Date d, Date h) {
      return dao.findByRecFec(idrecaudador, d, h);
   }
   public List<Recaudacion> findByFecha(Date d, Date h) {
      return dao.findByFecha( d, h);
   }
   public List<RecaudadorI> findListRecaudador(Date d, Date h) {
      return dao.findListRecaudador( d, h);
   }
   public List<RecaudaFacturasI> findFacturasToReport(LocalDateTime d, LocalDateTime h){
      return dao.findFacturasToReport(d, h);
   }
   public Object[] findRubrosAnterioresToReport(LocalDateTime d, LocalDateTime h, LocalDate t){
      return dao.findRubrosAnterioresToReport(d, h, t);
   }
   public Object[] findRubrosActualesToReport(LocalDateTime d, LocalDateTime h, LocalDate t){
      return dao.findRubrosActualesToReport(d, h, t);
   }

}