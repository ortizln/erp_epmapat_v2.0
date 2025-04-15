package com.erp.comercializacion.controllers;

import com.erp.comercializacion.models.Abonadosxsuspension;
import com.erp.comercializacion.services.AbonadosxsuspensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/aboxsuspension")
@CrossOrigin(origins = "*")
public class AbonadoxsuspensionApi {
    @Autowired
    private AbonadosxsuspensionService aboxsuspensionS;

    @GetMapping
    public List<Abonadosxsuspension> getAllAboxSuspension(){
        return aboxsuspensionS.findAll();
    }

    @PostMapping
    public Abonadosxsuspension saveAboxSuspension(@RequestBody Abonadosxsuspension aboxsuspensionM) {
        return aboxsuspensionS.save(aboxsuspensionM);
    }

    @GetMapping("suspension/{idsuspension}")
    public List<Abonadosxsuspension> getByIdsuspension(@PathVariable Long idsuspension){
        return aboxsuspensionS.findByIdsuspension(idsuspension);
    }
}
