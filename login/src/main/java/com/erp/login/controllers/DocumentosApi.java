package com.erp.login.controllers;

import com.erp.login.models.Documentos;
import com.erp.login.services.DocumentosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documentos")
@CrossOrigin("*")
public class DocumentosApi {
    @Autowired
    DocumentosService docuServicio;

    @GetMapping
    public List<Documentos> getAllLista(@Param(value = "nomdoc") String nomdoc) {
        if (nomdoc != null) {
            return docuServicio.findByNomdoc(nomdoc.toLowerCase());
        } else {
            return docuServicio.findAll();
        }
    }

    @GetMapping("/{iddocumento}")
    public ResponseEntity<Documentos> getByIddocumento(@PathVariable Long iddocumento) {
        Documentos documento = docuServicio.findById(iddocumento)
                .orElseThrow(null);
        return ResponseEntity.ok(documento);
    }

    @PostMapping
    public ResponseEntity<Documentos> save(@RequestBody Documentos x) {
        return ResponseEntity.ok(docuServicio.save(x));
    }

    @PutMapping("/{iddocumento}")
    public ResponseEntity<Documentos> update(@PathVariable Long iddocumento, @RequestBody Documentos x) {
        Documentos y = docuServicio.findById(iddocumento)
                .orElseThrow(null);
        y.setNomdoc(x.getNomdoc());
        y.setTipdoc(x.getTipdoc());
        y.setIdtabla4(x.getIdtabla4());
        y.setTipocomprobante(x.getTipocomprobante());
        y.setUsucrea(x.getUsucrea());
        y.setFeccrea(x.getFeccrea());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());

        Documentos actualizar = docuServicio.save(y);
        return ResponseEntity.ok(actualizar);
    }

    @DeleteMapping("/{iddocumento}")
    private ResponseEntity<Boolean> deleteDocumento(@PathVariable("iddocumento") Long iddocumento) {
        docuServicio.deleteById(iddocumento);
        return ResponseEntity.ok(!(docuServicio.findById(iddocumento) != null));
    }
}
