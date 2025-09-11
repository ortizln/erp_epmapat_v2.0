package com.erp.pagosonline.controllers;

import com.erp.pagosonline.DTO.FacturaRequestDTO;
import com.erp.pagosonline.DTO.RecaudacionDTO;
import com.erp.pagosonline.interfaces.LastConection_int;
import com.erp.pagosonline.models.Facturas;
import com.erp.pagosonline.models.Facxrecauda;
import com.erp.pagosonline.models.Recaudacion;
import com.erp.pagosonline.models.Usuarios;
import com.erp.pagosonline.repositories.TmpinteresxfacR;
import com.erp.pagosonline.services.CajasService;
import com.erp.pagosonline.services.FacturasService;
import com.erp.pagosonline.services.ImpuestoService;
import com.erp.pagosonline.services.RecaudacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @Autowired
    private CajasService cajasService;
    @Autowired
    private RecaudacionService recaudacionService;
    @Autowired
    private TmpinteresxfacR tmpinteresxfacR;
    @Autowired
    private ImpuestoService impuestoService;
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


    @PutMapping("/cobrar")
    public ResponseEntity<Map<String, Object>> cobrarFactura(@RequestBody FacturaRequestDTO facturaRequest) {
        Map<String, Object> respuesta = new HashMap<>();
        LastConection_int lastConection = cajasService.getLastConectionByUduario(facturaRequest.getAutentification());
        //DECLARAR NUEVA RECAUDACION
        Recaudacion recaudacion = new Recaudacion();
        LocalDateTime date = LocalDateTime.now();
        LocalTime hora = LocalTime.now();
        if(lastConection.getEstado() == 1){
            try {
                // Imprimir las claves "facturas"
                List<Long> facturas = facturaRequest.getFacturas();
                //Aqui voy a crear la lógica para crear una nueva recaudacion
                Usuarios recuadador = new Usuarios();
                recuadador.setIdusuario(facturaRequest.getAutentification());
                recaudacion.setEstado(1L);
                recaudacion.setFechacobro(date);
                recaudacion.setRecaudador(facturaRequest.getAutentification());
                recaudacion.setTotalpagar(facturaRequest.getRecaudacion().getTotalpagar());
                recaudacion.setValor(facturaRequest.getRecaudacion().getValor());
                recaudacion.setFormapago(facturaRequest.getRecaudacion().getFormapago());
                recaudacion.setRecibo(facturaRequest.getRecaudacion().getRecibo());
                recaudacion.setCambio(facturaRequest.getRecaudacion().getCambio());
                recaudacion.setFeccrea(date);
                recaudacion.setUsucrea(facturaRequest.getAutentification());
                Recaudacion recaudacion_saved = recaudacionService.save(recaudacion);
                if(recaudacion_saved != null) {
                    // Aquí podrías agregar la lógica para cobrar cada factura
                    for (Long facturaId : facturas) {
                        Facturas _factura = facturasService.findFacturaById(facturaId).orElseThrow();
                        if (_factura.getPagado() == 0 || (_factura.getEstado() == 3 && _factura.getPagado() == 1)) {
                            //DECLARAR NUEVA OBJETO FACXRECAUDA
                            Facxrecauda facxrecauda = new Facxrecauda();
                            if (_factura.getEstado() == 3) {
                                _factura.setEstado(1L);
                            }
                            _factura.setPagado(1);
                            _factura.setFechacobro(date);
                            _factura.setHoracobro(hora);
                            _factura.setUsuariocobro(facturaRequest.getAutentification());
                            _factura.setInterescobrado(tmpinteresxfacR.interesapagar(facturaId));
                            _factura.setSwiva(impuestoService.calcularIva(facturaId));
                            Facturas facCobrada = facturasService.cobrarFactura(_factura);
                            facxrecauda.setEstado(1L);
                            facxrecauda.setIdfactura(facCobrada);
                            facxrecauda.setIdrecaudacion(recaudacion_saved);
                            respuesta.put("Facturas cobradas:", facturas);
                        } else {
                            respuesta.put("Facturas no cobradas: ", _factura.getIdfactura());
                        }
                    }
                    respuesta.put("mensaje", "Facturas cobradas con éxito");
                }else{
                    respuesta.put("mensaje", "No se puede cobrar porque la forma de cobro no existe");
                }
                return ResponseEntity.ok(respuesta);
            } catch (Exception e) {
                respuesta.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }
        }else{
            respuesta.put("mensaje", "Caja cerrada no se puede cobrar");
            return ResponseEntity.ok(respuesta);
        }
    }

}
