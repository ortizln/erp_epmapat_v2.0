package com.erp.comercializacion.controllers;

import com.erp.comercializacion.models.Tpidentifica;
import com.erp.comercializacion.services.TpidentificaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tpidentifica")
@CrossOrigin("*")
public class TpidentificaApi {
    @Autowired
    private TpidentificaService TpidentificaServicio;

    @PostMapping
    public Tpidentifica updateOrSave(@RequestBody Tpidentifica x) {
        return TpidentificaServicio.save(x);
    }

    @GetMapping
    public List<Tpidentifica> getAllNovedades() {
        return TpidentificaServicio.findAll();
    }

    @GetMapping("/{idtpidentifica}")
    public ResponseEntity<Tpidentifica> getByIdNovedad(@PathVariable Long idtpidentifica) {
        Tpidentifica x = TpidentificaServicio.findById(idtpidentifica)
                .orElseThrow(null);
        return ResponseEntity.ok(x);
    }

    @PutMapping("/{idtpidentifica}")
    public ResponseEntity<Tpidentifica> update(@PathVariable Long idtpidentifica, @RequestBody Tpidentifica x) {
        Tpidentifica y = TpidentificaServicio.findById(idtpidentifica)
                .orElseThrow(null);
        y.setCodigo(x.getCodigo());
        y.setNombre(x.getNombre());
        y.setUsucrea(x.getUsucrea());
        y.setFeccrea(x.getFeccrea());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());

        Tpidentifica actualizar = TpidentificaServicio.save(y);
        return ResponseEntity.ok(actualizar);
    }

    @DeleteMapping("/{idtpidentifica}")
    private ResponseEntity<Boolean> deleteTpidentifica(@PathVariable("idtpidentifica") Long idtpidentifica) {
        TpidentificaServicio.deleteById(idtpidentifica);
        return ResponseEntity.ok(!(TpidentificaServicio.findById(idtpidentifica) != null));
    }
}
