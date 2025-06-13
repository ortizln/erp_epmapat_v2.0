package com.erp.comercializacion.controllers;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.interfaces.REcaudaFacturasI;
import com.erp.comercializacion.interfaces.RecaudadorI;
import com.erp.comercializacion.models.Recaudacion;
import com.erp.comercializacion.services.RecaudacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/recaudacion")
@CrossOrigin(origins = "*")

public class RecaudacionApi {

   @Autowired
   private RecaudacionService recaServicio;

   @GetMapping("/{idrecaudacion}")
   public ResponseEntity<Recaudacion> getByIdrecaudacion(@PathVariable Long idrecaudacion) {
      Recaudacion x = recaServicio.findById(idrecaudacion)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(("No extiste la Recaudacion ID: " + idrecaudacion)));
      return ResponseEntity.ok(x);
   }

   @PostMapping
   public Recaudacion saveRecaudacion(@RequestBody Recaudacion x) {
      return recaServicio.save(x);
   }

   @GetMapping("/reporte/totalxrecaudor")
   public Double totalRecaudado(@RequestParam("idrecaudador") Long idrecaudador,
         @RequestParam("fechacobro") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechacobro) {
      return recaServicio.totalRecaudado(idrecaudador, fechacobro);
   }

   @GetMapping("/reporte/recaudador")
   public List<Recaudacion> getByRecaudadorFecha(@RequestParam("idrecaudador") Long idrecaudador,
         @RequestParam("d") @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
         @RequestParam("h") @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
      return recaServicio.findByRecFec(idrecaudador, d, h);
   }

   @GetMapping("/reporte/fecha")
   public List<Recaudacion> getByFecha(
         @RequestParam("d") @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
         @RequestParam("h") @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
      return recaServicio.findByFecha(d, h);
   }

   @GetMapping("/reporte/recaudadores")
   public List<RecaudadorI> getListRecaudador(
         @RequestParam("d") @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
         @RequestParam("h") @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
      return recaServicio.findListRecaudador(d, h);
   }

   @PutMapping("/{idrecaudacion}")
   public ResponseEntity<Recaudacion> upDateRecaudacion(@PathVariable Long idrecaudacion,
         @RequestBody Recaudacion recaudacion) {
      Recaudacion rec = recaServicio.findById(idrecaudacion)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuentra esta recaudacion:" + idrecaudacion));
      rec.setRecaudador(recaudacion.getRecaudador());
      rec.setTotalpagar(recaudacion.getTotalpagar());
      rec.setRecibo(recaudacion.getRecibo());
      rec.setCambio(recaudacion.getCambio());
      rec.setFormapago(recaudacion.getFormapago());
      rec.setValor(recaudacion.getValor());
      rec.setEstado(recaudacion.getEstado());
      rec.setFechaeliminacion(recaudacion.getFechaeliminacion());
      rec.setUsuarioeliminacion(recaudacion.getUsuarioeliminacion());
      rec.setObservaciones(recaudacion.getObservaciones());
      rec.setNcnumero(recaudacion.getNcnumero());
      rec.setNcvalor(recaudacion.getNcvalor());
      rec.setUsucrea(recaudacion.getUsucrea());
      Recaudacion updateRecaudacion = recaServicio.save(rec);
      return ResponseEntity.ok(updateRecaudacion);
   }
   @GetMapping("/reportes/facturas")
   public ResponseEntity<List<REcaudaFacturasI>>findFacturasToReport(@RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime d, @RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime h){
      return ResponseEntity.ok(recaServicio.findFacturasToReport(d, h));
   }
   @GetMapping("/reportes/rubanteriores")
   public ResponseEntity<Object[]>findRubrosAnterioresToReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime d, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime h, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate t){
      return ResponseEntity.ok(recaServicio.findRubrosAnterioresToReport(d, h, t));
   }
   @GetMapping("/reportes/rubactuales")
   public ResponseEntity<Object[]>findRubrosActualesToReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime d, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam LocalDateTime h, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate t){
      return ResponseEntity.ok(recaServicio.findRubrosActualesToReport(d, h, t));
   }

}