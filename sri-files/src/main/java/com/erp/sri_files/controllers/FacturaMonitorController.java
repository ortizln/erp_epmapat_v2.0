package com.erp.sri_files.controllers;
import com.erp.sri_files.services.*;
import com.erp.sri_files.config.OperationsGuard;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.*;
@RestController
@RequestMapping("/api/v1/facturas/monitor")
public class FacturaMonitorController {
    @org.springframework.beans.factory.annotation.Autowired
    private EmailMsClientService emails;
    private final JdbcTemplate jdbc;private final OperationsGuard guard;private final FacturaOperationsService operations;
    public FacturaMonitorController(JdbcTemplate j,OperationsGuard g,FacturaOperationsService o){jdbc=j;guard=g;operations=o;}
    @GetMapping
    public Object list(@RequestHeader(value="X-Operations-Key",required=false)String key,
            @RequestHeader(value="X-Operations-Actor",required=false)String actor,
            @RequestParam(required=false)LocalDate desde,@RequestParam(required=false)LocalDate hasta,
            @RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="30")int size){
        guard.check(key,actor);LocalDate from=desde==null?LocalDate.now().minusDays(30):desde,to=hasta==null?LocalDate.now().plusDays(1):hasta;
        if(!from.isBefore(to)||page<0||size<1||size>100)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Rango o pagina invalido");
        var counts=jdbc.queryForMap("""
          SELECT count(*) AS total,count(*) FILTER(WHERE nullif(trim(xmlautorizado),'') IS NULL) AS pendientes_sri,
          count(*) FILTER(WHERE upper(trim(estado))='O') AS pendientes_correo,
          count(*) FILTER(WHERE mail_enviado IS TRUE) AS confirmadas,
          count(*) FILTER(WHERE upper(trim(estado)) IN ('E','M','N','L','U')) AS revision
          FROM public.fec_factura WHERE fechaemision>=? AND fechaemision<?
          """,from.atStartOfDay(),to.atStartOfDay());
        var rows=jdbc.queryForList("""
          SELECT f.idfactura,f.claveacceso,f.secuencial,f.emailcomprador,f.estado,f.errores,
          f.fechaemision,f.fecha_autorizacion,f.email_message_id,f.mail_enviado,
          f.intentos_autorizacion,f.fecha_ultimo_intento,c.estado AS estado_sri,c.proxima_consulta,e.estado AS notificacion,
          e.intentos AS intentos_notificacion,e.proximo_intento,e.ultimo_error,d.ride IS NOT NULL AS ride_disponible
          FROM public.fec_factura f LEFT JOIN public.sri_autorizacion_control c ON c.factura_id=f.idfactura
          LEFT JOIN public.evento_outbox e ON e.factura_id=f.idfactura AND e.version=f.notificacion_version
          LEFT JOIN public.comprobante_documento d ON d.factura_id=f.idfactura
          WHERE f.fechaemision>=? AND f.fechaemision<? ORDER BY f.idfactura DESC LIMIT ? OFFSET ?
          """,from.atStartOfDay(),to.atStartOfDay(),size,Math.multiplyExact(page,size));
        return Map.of("summary",counts,"rows",rows,"page",page,"size",size);
    }
    @GetMapping("/{id}/historial")
    public Object history(@PathVariable Long id,@RequestHeader(value="X-Operations-Key",required=false)String key,
            @RequestHeader(value="X-Operations-Actor",required=false)String actor){
        guard.check(key,actor);
        Object attempts=java.util.List.of();
        var ids=jdbc.queryForList("SELECT email_message_id FROM public.fec_factura WHERE idfactura=?",id);
        if(!ids.isEmpty() && ids.get(0).get("email_message_id")!=null) {
            try { attempts=emails.intentos((java.util.UUID)ids.get(0).get("email_message_id")); }
            catch(org.springframework.web.client.RestClientException unavailable){attempts=Map.of("error","Historial de emails temporalmente no disponible");}
        }
        return Map.of("intentos_correo",attempts,"eventos",jdbc.queryForList("SELECT id,version,estado,intentos,proximo_intento,ultimo_error,creado,actualizado FROM public.evento_outbox WHERE factura_id=? ORDER BY id",id),
                "operaciones",jdbc.queryForList("SELECT actor,accion,detalle,creado FROM public.factura_operacion WHERE factura_id=? ORDER BY id",id));
    }
    @GetMapping("/{id}/documento/{tipo}")
    public ResponseEntity<byte[]> document(@PathVariable Long id,@PathVariable String tipo,
            @RequestHeader(value="X-Operations-Key",required=false)String key,
            @RequestHeader(value="X-Operations-Actor",required=false)String actor){
        guard.check(key,actor);
        if(!Set.of("xml","pdf").contains(tipo))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        var rows=jdbc.queryForList("SELECT xml_autorizado,ride,xml_sha256,ride_sha256 FROM public.comprobante_documento WHERE factura_id=?",id);
        if(rows.isEmpty())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Documentos aun no preparados");
        byte[] bytes="pdf".equals(tipo)?(byte[])rows.get(0).get("ride"):rows.get(0).get("xml_autorizado").toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if(bytes==null)throw new ResponseStatusException(HttpStatus.CONFLICT,"RIDE pendiente");
        if(!FacturaOutboxService.sha(bytes).equals(rows.get(0).get("pdf".equals(tipo)?"ride_sha256":"xml_sha256")))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"La integridad del documento no coincide; requiere regeneracion");
        return ResponseEntity.ok().contentType("pdf".equals(tipo)?MediaType.APPLICATION_PDF:MediaType.APPLICATION_XML)
                .header("Content-Disposition","attachment; filename=factura_"+id+"."+tipo).body(bytes);
    }
    @PostMapping("/{id}/{accion}")
    public ResponseEntity<?> action(@PathVariable Long id,@PathVariable String accion,
            @RequestBody FacturaOperationsService.Command cmd,
            @RequestHeader(value="X-Operations-Key",required=false)String key,
            @RequestHeader(value="X-Operations-Actor",required=false)String actor){
        guard.check(key,actor);operations.ejecutar(id,accion,cmd,key,actor);return ResponseEntity.accepted().build();
    }
}
