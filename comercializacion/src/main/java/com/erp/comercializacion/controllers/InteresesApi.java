package com.erp.comercializacion.controllers;

import com.erp.comercializacion.DTO.FacturaToInteresDTO;
import com.erp.comercializacion.models.Intereses;
import com.erp.comercializacion.services.InteresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/intereses")
@CrossOrigin("*")
public class InteresesApi {
    @Autowired
    private InteresService inteServicio;

    @GetMapping
    public List<Intereses> getAllLista(@Param(value = "anio") Number anio, @Param(value = "mes") Number mes) {
        if (anio != null && mes != null) {
            return inteServicio.findByAnioMes(anio, mes);
        } else {
            return inteServicio.findAll(Sort.by(Sort.Order.desc("anio"), Sort.Order.desc("mes")));
        }
    }

    // Busca el último
    @GetMapping("/ultimo")
    public List<Intereses> getUltimo() {
        return inteServicio.findUltimo();
    }

    @PostMapping
    public ResponseEntity<Intereses> saveIntereses(@RequestBody Intereses x) {
        return ResponseEntity.ok(inteServicio.save(x));
    }

    @GetMapping("/{idinteres}")
    public ResponseEntity<Intereses> getByIdIntereses(@PathVariable Long idinteres) {
        Intereses interesesM = inteServicio.findById(idinteres)
                .orElseThrow(null);
        return ResponseEntity.ok(interesesM);
    }

    @PutMapping("/{idinteres}")
    public ResponseEntity<Intereses> updateIntereses(@PathVariable Long idinteres, @RequestBody Intereses interesM) {
        Intereses interesesM = inteServicio.findById(idinteres)
                .orElseThrow(null);
        interesesM.setAnio(interesM.getAnio());
        interesesM.setMes(interesM.getMes());
        interesesM.setPorcentaje(interesM.getPorcentaje());
        interesesM.setUsucrea(interesM.getUsucrea());
        interesesM.setFeccrea(interesM.getFeccrea());
        interesesM.setUsumodi(interesM.getUsumodi());
        interesesM.setFecmodi(interesM.getFecmodi());
        Intereses updatedInteres = inteServicio.save(interesesM);
        return ResponseEntity.ok(updatedInteres);
    }

    @DeleteMapping(value = "/{idinteres}")
    public ResponseEntity<Boolean> deleteInteres(@PathVariable("idinteres") Long idinteres) {
        inteServicio.deleteById(idinteres);
        return ResponseEntity.ok(!(inteServicio.findById(idinteres) != null));
    }
   /* @GetMapping("/calcular")
    public ResponseEntity<Object> calcularIntereses(@RequestParam Long idfactura) {
        return ResponseEntity.ok(inteServicio.facturaid(idfactura));
    }*/
    @GetMapping("/calcularInteres")
    public ResponseEntity<?> getInteresTotal(FacturaToInteresDTO factura){
        return ResponseEntity.ok(inteServicio.interesToFactura(factura));
    }
}
