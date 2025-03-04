package com.erp.comercializacion.controllers;

import com.erp.comercializacion.models.Categorias;
import com.erp.comercializacion.services.CategoriasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/categorias")
@CrossOrigin("*")
public class CategoriasApi {
    @Autowired
    private CategoriasService cateServicio;

    @GetMapping
    public List<Categorias> getAllCategorias(@RequestParam String descripcion,
                                             @RequestParam Long idused) {
        if (descripcion != null) {
            return cateServicio.findByDescri(descripcion);
        } else {
            if (idused == null) {
                return cateServicio.findAll(Sort.by(Sort.Order.asc("codigo")));
            } else {
                return cateServicio.used(idused);
            }
        }
    }

    // Categorias habilitadas
    @GetMapping("/categorias")
    public List<String> obtenerCategorias() {
        return cateServicio.listaCategorias();
    }

    @GetMapping("/suma-totaltarifa")
    public ResponseEntity<BigDecimal> sumTotalTarifa() {
        BigDecimal suma = cateServicio.sumTotalTarifa();
        return ResponseEntity.ok(suma);
    }

    @PostMapping
    public Categorias saveCategoria(@RequestBody Categorias categoriaM) {
        return cateServicio.save(categoriaM);
    }

    @GetMapping("/{idcategoria}")
    public ResponseEntity<Categorias> getByIdCategoria(@PathVariable Long idcategoria) {
        Categorias categoriaM = cateServicio.findById(idcategoria)
                .orElseThrow(null);
        return ResponseEntity.ok(categoriaM);
    }

    @PutMapping(value = "/{idcategoria}")
    public ResponseEntity<Categorias> updateCategoria(@PathVariable Long idcategoria,
                                                      @RequestBody Categorias categoriam) {
        Categorias categoriaM = cateServicio.findById(idcategoria)
                .orElseThrow(null);
        categoriaM.setDescripcion(categoriam.getDescripcion());
        categoriaM.setCodigo(categoriam.getCodigo());
        categoriaM.setHabilitado(categoriam.getHabilitado());
        categoriaM.setFijoagua(categoriam.getFijoagua());
        categoriaM.setFijosanea(categoriam.getFijosanea());
        categoriaM.setUsucrea(categoriam.getUsucrea());
        categoriaM.setFeccrea(categoriam.getFeccrea());
        categoriaM.setUsumodi(categoriam.getUsumodi());
        Categorias updateCategoria = cateServicio.save(categoriaM);
        return ResponseEntity.ok(updateCategoria);
    }

    @DeleteMapping(value = "/{idcategoria}")
    public ResponseEntity<Object> deleteCategoria(@PathVariable("idcategoria") Long idcategoria) {
        cateServicio.deleteById(idcategoria);
        return ResponseEntity.ok(Boolean.TRUE);
    }

}
