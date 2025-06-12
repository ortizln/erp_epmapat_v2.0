package com.erp.comercializacion.controllers;

import com.erp.comercializacion.models.Ctramites;
import com.erp.comercializacion.services.CtramitesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/ctramites")
@CrossOrigin("*")
public class CtramitesApi {
    @Autowired
    private CtramitesService tramitesS;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Ctramites> getAllTramites() {
        return tramitesS.findAll();
    }

    @GetMapping("/tptramite/{idtptramite}")
    @ResponseStatus(HttpStatus.OK)
    public List<Ctramites> getByTpTramite(@PathVariable("idtptramite") Long idtptramite) {
        return tramitesS.findByTpTramite(idtptramite);
    }

    @GetMapping("/descripcion/{descripcion}")
    @ResponseStatus(HttpStatus.OK)
    public List<Ctramites> getByDescripcion(@PathVariable("descripcion") String descripcion) {
        return tramitesS.findByDescripcion(descripcion.toLowerCase());
    }

    @GetMapping("/feccrea/{feccrea}")
    @ResponseStatus(HttpStatus.OK)
    public List<Ctramites> getByFeccrea(@PathVariable("feccrea") String feccrea) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaConvertida = null;
        try {
            fechaConvertida = (Date) dateFormat.parse(feccrea);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tramitesS.findByfeccrea(fechaConvertida);
    }
    // Trámites por Cliente
    @GetMapping("/idcliente/{idcliente}")
    @ResponseStatus(HttpStatus.OK)
    public List<Ctramites> getByIdcliente(@PathVariable("idcliente") Long idcliente) {
        return tramitesS.findByIdcliente(idcliente);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ctramites saveTramites(@RequestBody Ctramites tramitesM) {
        return tramitesS.save(tramitesM);
    }

    @GetMapping("/{idtramite}")
    public ResponseEntity<Ctramites> getByIdTramite(@PathVariable Long idtramite) {
        Ctramites tramitesM = tramitesS.findById(idtramite)
                .orElseThrow(null);
        return ResponseEntity.ok(tramitesM);
    }

    @PutMapping("/{idtramite}")
    public ResponseEntity<Ctramites> updateTramite(@PathVariable Long idtramite, @RequestBody Ctramites tramitesm) {
        Ctramites tramitesM = tramitesS.findById(idtramite)
                .orElseThrow(null);
        tramitesM.setEstado(tramitesm.getEstado());
        tramitesM.setTotal(tramitesm.getTotal());
        tramitesM.setDescripcion(tramitesm.getDescripcion());
        tramitesM.setCuotas(tramitesm.getCuotas());
        tramitesM.setValidohasta(tramitesm.getValidohasta());
        tramitesM.setIdtptramite_tptramite(tramitesm.getIdtptramite_tptramite());
        tramitesM.setIdcliente_clientes(tramitesm.getIdcliente_clientes());
        tramitesM.setUsucrea(tramitesm.getUsucrea());
        tramitesM.setFeccrea(tramitesm.getFeccrea());
        tramitesM.setUsumodi(tramitesm.getUsumodi());
        tramitesM.setFecmodi(tramitesm.getFecmodi());
        Ctramites updateTramite = tramitesS.save(tramitesM);
        return ResponseEntity.ok(updateTramite);
    }

    @DeleteMapping(value = "/{idtramite}")
    public ResponseEntity<Boolean> deleteTramites(@PathVariable("idtramite") Long idtramite) {
        tramitesS.deleteById(idtramite);
        return ResponseEntity.ok(!(tramitesS.findById(idtramite) != null));
    }
}
