package com.erp.pagosonline.controllers;

import com.erp.pagosonline.DTO.FacturaRequestDTO;
import com.erp.pagosonline.DTO.RecaudacionDTO;
import com.erp.pagosonline.DTO.ReportdataDTO;
import com.erp.pagosonline.interfaces.FacturasCobradas;
import com.erp.pagosonline.interfaces.LastConection_int;
import com.erp.pagosonline.models.*;
import com.erp.pagosonline.repositories.RubroxfacR;
import com.erp.pagosonline.repositories.TmpinteresxfacR;
import com.erp.pagosonline.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pagonline/facturas")
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
    @Autowired
    private RubroxfacR rubroxfacR;

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> home() {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>📋 API FacturasController</title>");
        html.append("<style>");
        html.append("body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f7f9fb; padding: 20px; color: #333; }");
        html.append("h1 { color: #2c3e50; }");
        html.append(".endpoint { background: white; padding: 15px 20px; margin: 15px 0; border-radius: 10px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }");
        html.append(".method { font-weight: bold; padding: 3px 8px; border-radius: 5px; color: white; }");
        html.append(".GET { background: #27ae60; }");
        html.append(".POST { background: #2980b9; }");
        html.append(".PUT { background: #f39c12; }");
        html.append("code { background: #eef; padding: 3px 5px; border-radius: 5px; }");
        html.append("a { color: #3498db; text-decoration: none; }");
        html.append("a:hover { text-decoration: underline; }");
        html.append(".example { background: #f8f9fa; border-left: 4px solid #3498db; padding: 10px; font-size: 0.9em; border-radius: 5px; margin-top: 8px; }");
        html.append("</style></head><body>");

        html.append("<h1>📋 Endpoints disponibles en <i>FacturasController</i></h1>");
        html.append("<p>Última actualización: ").append(LocalDateTime.now()).append("</p>");

        // --- Endpoint 1 ---
        html.append("<div class='endpoint'>");
        html.append("<span class='method GET'>GET</span> ");
        html.append("<code>/facturas/sincobrar?user={idUsuario}&cuenta={idCuenta}</code>");
        html.append("<p>Obtiene las facturas pendientes de cobro para un usuario y cuenta. Retorna <b>'Caja no iniciada'</b> si no hay sesión activa.</p>");
        html.append("<p><b>Ejemplo:</b></p>");
        html.append("<div class='example'>");
        html.append("GET <a href='/facturas/sincobrar?user=1&cuenta=1001' target='_blank'>/facturas/sincobrar?user=1&cuenta=1001</a>");
        html.append("</div>");
        html.append("</div>");

        // --- Endpoint 2 ---
        html.append("<div class='endpoint'>");
        html.append("<span class='method PUT'>PUT</span> ");
        html.append("<code>/facturas/cobrar</code>");
        html.append("<p>Cobra una o varias facturas. Requiere un objeto <b>FacturaRequestDTO</b> con autenticación, recaudación y lista de facturas.</p>");
        html.append("<p><b>Ejemplo de cuerpo JSON:</b></p>");
        html.append("<div class='example'>");
        html.append("{<br>");
        html.append("&nbsp;&nbsp;\"autentification\": 1,<br>");
        html.append("&nbsp;&nbsp;\"facturas\": [101, 102, 103],<br>");
        html.append("&nbsp;&nbsp;\"recaudacion\": { \"totalpagar\": 45.50 }<br>");
        html.append("}");
        html.append("</div>");
        html.append("<p>Consumo (cURL):</p>");
        html.append("<div class='example'>");
        html.append("curl -X PUT http://localhost:8080/facturas/cobrar -H \"Content-Type: application/json\" -d '{\"autentification\":1,\"facturas\":[101,102],\"recaudacion\":{\"totalpagar\":45.50}}'");
        html.append("</div>");
        html.append("</div>");

        // --- Endpoint 3 ---
        html.append("<div class='endpoint'>");
        html.append("<span class='method POST'>POST</span> ");
        html.append("<code>/facturas/reporte</code>");
        html.append("<p>Genera un reporte de facturas cobradas en un rango de fechas. Requiere un cuerpo con <b>idusuario</b>, <b>df</b>, <b>hf</b>, <b>dh</b>, <b>hh</b>.</p>");
        html.append("<p><b>Ejemplo de cuerpo JSON:</b></p>");
        html.append("<div class='example'>");
        html.append("{<br>");
        html.append("&nbsp;&nbsp;\"idusuario\": 1,<br>");
        html.append("&nbsp;&nbsp;\"df\": \"2025-10-01\",<br>");
        html.append("&nbsp;&nbsp;\"hf\": \"2025-10-25\",<br>");
        html.append("&nbsp;&nbsp;\"dh\": \"08:00:00\",<br>");
        html.append("&nbsp;&nbsp;\"hh\": \"17:00:00\"<br>");
        html.append("}");
        html.append("</div>");
        html.append("<p>Consumo (cURL):</p>");
        html.append("<div class='example'>");
        html.append("curl -X POST http://localhost:8080/facturas/reporte -H \"Content-Type: application/json\" -d '{\"idusuario\":1,\"df\":\"2025-10-01\",\"hf\":\"2025-10-25\",\"dh\":\"08:00:00\",\"hh\":\"17:00:00\"}'");
        html.append("</div>");
        html.append("</div>");

        // --- Endpoint 4 ---
        html.append("<div class='endpoint'>");
        html.append("<span class='method GET'>GET</span> ");
        html.append("<code>/facturas/reporteCobradas</code>");
        html.append("<p>Retorna las facturas cobradas (pendiente de implementación).</p>");
        html.append("<p><b>Ejemplo:</b></p>");
        html.append("<div class='example'>");
        html.append("GET <a href='/facturas/reporteCobradas' target='_blank'>/facturas/reporteCobradas</a>");
        html.append("</div>");
        html.append("</div>");

        html.append("</body></html>");
        return ResponseEntity.ok(html.toString());
    }


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

    @PutMapping("/cobrar")
    public ResponseEntity<Map<String, Object>> cobrar_Factura(@RequestBody FacturaRequestDTO facturaRequest) {
        Map<String, Object> respuesta = new HashMap<>();
        LastConection_int lastConection = cajasService.getLastConectionByUduario(facturaRequest.getAutentification());
        //DECLARAR NUEVA RECAUDACION
        Recaudacion recaudacion = new Recaudacion();
        LocalDateTime date = LocalDateTime.now();
        LocalTime hora = LocalTime.now();
        if(lastConection == null){
            respuesta.put("mensaje", "Caja cerrada no se puede cobrar");
            return ResponseEntity.ok(respuesta);
        }
        if(lastConection.getEstado() == 1) {
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
                recaudacion.setValor(facturaRequest.getRecaudacion().getTotalpagar());
                recaudacion.setFormapago(1L);
                recaudacion.setRecibo(BigDecimal.valueOf(0));
                recaudacion.setCambio(BigDecimal.valueOf(0));
                recaudacion.setFeccrea(date);
                recaudacion.setUsucrea(facturaRequest.getAutentification());
                Recaudacion recaudacion_saved = recaudacionService.save(recaudacion);
                if (recaudacion_saved != null) {
                    // Aquí podrías agregar la lógica para cobrar cada factura
                    for (Long facturaId : facturas) {
                        Facturas _factura = facturasService.findFacturaById(facturaId).orElseThrow();
                        if (_factura.getPagado() == 0 || (_factura.getEstado() == 3 && _factura.getPagado() == 1)) {
                            //DECLARAR NUEVA OBJETO FACXRECAUDA
                            Facxrecauda facxrecauda = new Facxrecauda();
                            if (_factura.getEstado() == 3) {
                                _factura.setEstado(1L);
                            }
                            BigDecimal interesapagar = (tmpinteresxfacR.interesapagar(facturaId));
                            _factura.setPagado(1);
                            _factura.setFechacobro(date);
                            _factura.setHoracobro(hora);
                            _factura.setUsuariocobro(facturaRequest.getAutentification());
                            _factura.setInterescobrado(interesapagar);
                            _factura.setSwiva(impuestoService.calcularIva(facturaId));
                            if (interesapagar.compareTo(BigDecimal.ZERO) > 0) {
                                Rubros rubro = new Rubros();
                                Facturas factura = new Facturas();
                                factura.setIdfactura(facturaId);
                                Rubroxfac rxf = new Rubroxfac();
                                rubro.setIdrubro(5L);
                                rxf.setIdfactura_facturas(factura);
                                rxf.setCantidad(BigDecimal.ONE);
                                rxf.setValorunitario(interesapagar);
                                rxf.setIdrubro_rubros(rubro);
                                rubroxfacR.save(rxf);
                            }
                            Facturas facCobrada = facturasService.cobrarFactura(_factura);
                            facxrecauda.setEstado(1L);
                            facxrecauda.setIdfactura(facCobrada);
                            facxrecauda.setIdrecaudacion(recaudacion_saved);
                            respuesta.put("status", "00");
                            respuesta.put("message", "TRANSACCIÓN CON ÉXITO");
                        } else {
                            respuesta.put("Facturas no cobradas: ", _factura.getIdfactura());
                        }
                    }
                    respuesta.put("mensaje", "Facturas cobradas con éxito");
                } else {
                    respuesta.put("mensaje", "No se puede cobrar porque la forma de cobro no existe");
                }
                return ResponseEntity.ok(respuesta);
            } catch (Exception e) {
                respuesta.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }
        }
        else{
            respuesta.put("mensaje", "Caja cerrada no se puede cobrar");
            return ResponseEntity.ok(respuesta);
        }
    }

    @PostMapping("/reporte")
    public ResponseEntity<List<FacturasCobradas>> getReporteFacturasCobradas(@RequestBody ReportdataDTO datos){
        Long idusuario = datos.getIdusuario();
        LocalDate df = datos.getDf();
        LocalTime dh = datos.getDh();
        LocalDate hf = datos.getHf();
        LocalTime hh = datos.getHh();
        return ResponseEntity.ok(facturasService.getReporteFacturasCobradas(idusuario,df,hf,dh, hh));
    }

    @GetMapping("/reporteCobradas")
    public ResponseEntity<List<FacturasCobradas>> getFacturasCobradas(){ return null;}

}
