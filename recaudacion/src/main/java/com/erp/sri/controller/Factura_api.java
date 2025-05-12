package com.erp.sri.controller;

import com.erp.sri.interfaces.Interes_int;
import com.erp.sri.interfaces.LastConection_int;
import com.erp.sri.model.*;
import com.erp.sri.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rec/facturas")
@CrossOrigin("*")
public class Factura_api {
    @Autowired
    private Cliente_ser s_cliente;
    @Autowired
    private Abonado_ser s_abonado;
    @Autowired
    private Factura_ser s_factura;
    @Autowired
    private Interes_ser s_interes;
    @Autowired
    private Cajas_ser s_cajas;
    @Autowired
    private Recaudacion_ser s_recaudacion;
    @Autowired
    private Impuestos_ser s_impuesto;

    @GetMapping("/sincobro/cliente")
    public ResponseEntity<List<Factura_interes>> getSinCobro(@RequestParam Long idcliente) {
        return ResponseEntity.ok(s_cliente.getFacturasByIdCliente(idcliente));
    }

    //find cliente in abonado
    @GetMapping("/sincobro/cuenta")
    public ResponseEntity<List<Factura_interes>> getClienteInAbonado(@RequestParam Long cuenta){
        return ResponseEntity.ok(s_abonado.findSinCobrarByAbonado(cuenta));
    }
    @GetMapping("/pruebainteres")
    public ResponseEntity<List<Interes_int>> getInteresPrueba(@RequestParam Long idfactura){
        return ResponseEntity.ok(s_factura.getForIntereses(idfactura));
    }
    //API PARA COBRAR FACTURA
    @PutMapping("/cobrar")
    public ResponseEntity<Map<String, Object>> cobrarFactura(@RequestBody FacturaRequest facturaRequest) {
        Map<String, Object> respuesta = new HashMap<>();
        LastConection_int lastConection = s_cajas.getLastConectionByUduario(facturaRequest.getAutentification());
        //DECLARAR NUEVA RECAUDACION
        Recaudacion recaudacion = new Recaudacion();
        LocalDateTime date = LocalDateTime.now();
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
                Recaudacion recaudacion_saved = s_recaudacion.save(recaudacion);
                if(recaudacion_saved != null) {
                    // Aquí podrías agregar la lógica para cobrar cada factura
                    for (Long facturaId : facturas) {
                        Facturas _factura = s_factura.findFacturaById(facturaId).orElseThrow();
                        if (_factura.getPagado() == 0 || (_factura.getEstado() == 3 && _factura.getPagado() == 1)) {
                            //DECLARAR NUEVA OBJETO FACXRECAUDA
                            Facxrecauda facxrecauda = new Facxrecauda();
                            if (_factura.getEstado() == 3) {
                                _factura.setEstado(1L);
                            }
                            _factura.setPagado(1L);
                            _factura.setFechacobro(date);
                            _factura.setUsuariocobro(facturaRequest.getAutentification());
                            _factura.setInterescobrado(s_interes.interesOfFactura(facturaId));
                            _factura.setSwiva(s_impuesto.calcularIva(facturaId));
                            Facturas facCobrada = s_factura.cobrarFactura(_factura);
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
