package com.erp.pagosonline.controllers;

import com.erp.pagosonline.DTO.FacturaRequestDTO;
import com.erp.pagosonline.DTO.RecaudacionDTO;
import com.erp.pagosonline.services.FacturasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pagonline/facturas")
@CrossOrigin("*")
public class FacturasApi {
    @Autowired
    private FacturasService facturasService;
    @GetMapping("/sincobrar")
    public ResponseEntity<Object> getFacturasSinCobro(@RequestParam Long user,@RequestParam Long cuenta){
        Object datos = facturasService.findFacturasSinCobro(user, cuenta);
        if(datos == null){
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje","Caja no iniciada");
            return ResponseEntity.ok(respuesta);
        }else{
            return ResponseEntity.ok(datos);

        }
    }
    @PutMapping
    public ResponseEntity<Object> postCobrar(@RequestParam Long user, @RequestBody Map<String, Object> datos) {
        Map<String, Object> connection = facturasService.getConnectionStatus(user);
        Boolean test = facturasService.validateCajaStatus(connection);
        if(test){
            // Create and populate DTOs
            FacturaRequestDTO facturaRequestDTO = new FacturaRequestDTO();
            RecaudacionDTO recaudacionDTO = new RecaudacionDTO();
            facturaRequestDTO.setAutentification(user);

            // Safely convert "total" to BigDecimal
            if (datos.containsKey("total")) {
                try {
                    Object totalObj = datos.get("total");
                    BigDecimal total = new BigDecimal(totalObj.toString()); // Convert safely
                    recaudacionDTO.setTotalpagar(total);
                    recaudacionDTO.setFormapago(1L);
                    recaudacionDTO.setRecibo(total);
                    recaudacionDTO.setValor(total);
                    recaudacionDTO.setCambio(BigDecimal.ZERO);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body("Invalid format for 'total'");
                }
            } else {
                return ResponseEntity.badRequest().body("Missing 'total' field");
            }

            // Safely extract "facturas" list
            if (datos.containsKey("facturas") && datos.get("facturas") instanceof List<?>) {
                try {
                    List<?> facturasRaw = (List<?>) datos.get("facturas");
                    List<Long> facturas = facturasRaw.stream()
                            .filter(Objects::nonNull)
                            .map(f -> Long.parseLong(f.toString()))
                            .collect(Collectors.toList());

                    facturaRequestDTO.setFacturas(facturas);
                    facturaRequestDTO.setRecaudacion(recaudacionDTO);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body("Invalid format for 'facturas' field");
                }
            } else {
                return ResponseEntity.badRequest().body("Missing or invalid 'facturas' field");
            }

            // Call service method
            Object putPagos = facturasService.savePagos(user, facturaRequestDTO);

            return ResponseEntity.ok(putPagos);
        }else{
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje","Caja no iniciada");
            return ResponseEntity.ok(respuesta);
        }

    }


}
