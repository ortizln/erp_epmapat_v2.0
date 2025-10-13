package com.erp.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erp.excepciones.ResourceNotFoundExcepciones;
import com.erp.modelo.contabilidad.Presupue;
import com.erp.servicio.contabilidad.PresupueServicio;

@RestController
@RequestMapping("/pregastos")


public class PregastosApi {

   @Autowired
   PresupueServicio presuServicio;

   @GetMapping
   public List<Presupue> getAllLista(@Param(value = "tippar") Integer tippar, @Param(value = "codpar") String codpar,
         @Param(value = "nompar") String nompar) {
      if (tippar != null && codpar != null && nompar != null) {
         return presuServicio.findByTipoCodigoyNombre(2, codpar, nompar.toLowerCase());
      } else
         return null;
   }

   // Busca por Código o Nombre (un solo campo)
   @GetMapping("/codigoNombre")
   public List<Presupue> getCodigoNombre(@Param(value = "codigoNombre") String codigoNombre) {
      return presuServicio.findCodigoNombre(codigoNombre);
   }

   // Busca por Código para validar
   @GetMapping("/valcodpar")
   public List<Presupue> buscaByCodigo(@Param(value = "codpar") String codpar) {
      return presuServicio.buscaByCodigo(codpar);
   }

   @GetMapping("/{intpre}")
   public ResponseEntity<Presupue> getByIdNovedad(@PathVariable Long intpre) {
      Presupue x = presuServicio.findById(intpre)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Partida con Intpre: " + intpre)));
      return ResponseEntity.ok(x);
   }

   // Cuenta las Partidas por Actividad
   // @GetMapping("/count")
   // public ResponseEntity<Long> countPresupueWithIdestrfunc(@RequestParam("intest") Long intest) {
   //    Long count = presuServicio.countByIdestrfunc(intest);
   //    return ResponseEntity.ok(count);
   // }

   // Cuenta las Partidas por Actividad
    @GetMapping("/actividad")
    public List<Presupue> findByActividad(@RequestParam("intest") Long intest) {
       return presuServicio.findByActividad(intest);
    }

    @PostMapping
    public ResponseEntity<Presupue> save(@RequestBody Presupue x) {
        return ResponseEntity.ok(presuServicio.save( x ));
    }

   @PutMapping("/{intpre}")
   public ResponseEntity<Presupue> update(@PathVariable Long intpre, @RequestBody Presupue x) {
      Presupue y = presuServicio.findById(intpre)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Intpre: " + intpre)));
      y.setTippar(x.getTippar());
      y.setCodpar(x.getCodpar());
      y.setCodigo(x.getCodigo());
      y.setNompar(x.getNompar());
      y.setInicia(x.getInicia());
      y.setTotmod(x.getTotmod());
      y.setTotcerti(x.getTotcerti());
      y.setTotmisos(x.getTotmisos());
      y.setTotdeven(x.getTotdeven());
      y.setFuncion(x.getFuncion());
      // y.setIdestrfunc(x.getIdestrfunc());
      // y.setIntcla(x.getIntcla());
      y.setCodacti(x.getCodacti());
      y.setCodpart(x.getCodpart());
      y.setSwpluri(x.getSwpluri());
      y.setUsucrea(x.getUsucrea());
      y.setFeccrea(x.getFeccrea());
      y.setUsumodi(x.getUsumodi());
      y.setFecmodi(x.getFecmodi());

      Presupue actualizar = presuServicio.save(y);
      return ResponseEntity.ok(actualizar);
   }

   @DeleteMapping("/{intpre}")
   private ResponseEntity<Boolean> deletePresupue(@PathVariable("intpre") Long intpre) {
      presuServicio.deleteById(intpre);
      return ResponseEntity.ok(!(presuServicio.findById(intpre) != null));
   }
   
}