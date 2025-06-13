package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Tpidentifica;
import com.erp.comercializacion.services.TpidentificaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe Tipo de Identificacion Id: " + idtpidentifica)));
        return ResponseEntity.ok(x);
    }

    @PutMapping("/{idtpidentifica}")
    public ResponseEntity<Tpidentifica> update(@PathVariable Long idtpidentifica, @RequestBody Tpidentifica x) {
        Tpidentifica y = TpidentificaServicio.findById(idtpidentifica)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe Tipo de Identificacion Id: " + idtpidentifica)));
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
