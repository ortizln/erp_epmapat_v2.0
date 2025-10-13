package com.erp.controlador;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.erp.excepciones.ResourceNotFoundExcepciones;
import com.erp.modelo.Fec_factura;
import com.erp.servicio.Fec_facturaService;
import com.erp.sri.interfaces.fecFacturaDatos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/fec_factura")


public class Fec_facturaApi {

   @Autowired
   private Fec_facturaService fecfacServicio;

   @GetMapping
   public List<Fec_factura> getAll() {
      return fecfacServicio.findAll();
   }

   @GetMapping("/estado")
   public List<Fec_factura> getByEstado(@RequestParam("estado") String estado, @RequestParam("limit") Long limit) {
      return fecfacServicio.findByEstado(estado, limit);
   }

   @GetMapping("/referencia")
   public List<Fec_factura> getByCuenta(@RequestParam("referencia") String referencia) {
      return fecfacServicio.findByCuenta(referencia);
   }

   @GetMapping("/cliente")
   public List<Fec_factura> getByNombreCliente(@RequestParam("cliente") String cliente) {
      return fecfacServicio.findByNombreCliente(cliente);
   }

   @GetMapping("/factura")
   public ResponseEntity<Optional<Fec_factura>> getByIdFactura(@RequestParam("idfactura") Long idfactura) {
      return ResponseEntity.ok(fecfacServicio.findById(idfactura));
   }

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public ResponseEntity<Fec_factura> saveFec_factura(@RequestBody Fec_factura x) {
      Optional<Fec_factura> fecfactura = fecfacServicio.findById(x.getIdfactura());
      if (fecfactura.isPresent()) {
         if ("A".equals(fecfactura.get().getEstado()) || "O".equals(fecfactura.get().getEstado())) {
            x = fecfacServicio.save(fecfactura.get());
         } else {
            x = fecfacServicio.save(x);
         }
         return ResponseEntity.ok(x);
      } else {
         // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
         return ResponseEntity.ok(fecfacServicio.save(x));
      }
   }

   @PutMapping
   public ResponseEntity<Fec_factura> updateFecFactura(@RequestParam("idfactura") Long idfactura,
         @RequestBody Fec_factura fecfactura) {
      Fec_factura factura = fecfacServicio.findById(idfactura)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("Not found Id: " + idfactura));
      factura.setClaveacceso(fecfactura.getClaveacceso());
      factura.setSecuencial(fecfactura.getSecuencial());
      factura.setXmlautorizado(fecfactura.getXmlautorizado());
      factura.setErrores(fecfactura.getErrores());
      factura.setEstado(fecfactura.getEstado());
      factura.setEstablecimiento(fecfactura.getEstablecimiento());
      factura.setPuntoemision(fecfactura.getPuntoemision());
      factura.setDireccionestablecimiento(fecfactura.getDireccionestablecimiento());
      factura.setFechaemision(fecfactura.getFechaemision());
      factura.setTipoidentificacioncomprador(fecfactura.getTipoidentificacioncomprador());
      factura.setGuiaremision(fecfactura.getGuiaremision());
      factura.setRazonsocialcomprador(fecfactura.getRazonsocialcomprador());
      factura.setIdentificacioncomprador(fecfactura.getIdentificacioncomprador());
      factura.setDireccioncomprador(fecfactura.getDireccioncomprador());
      factura.setTelefonocomprador(fecfactura.getTelefonocomprador());
      factura.setEmailcomprador(fecfactura.getEmailcomprador());
      factura.setConcepto(fecfactura.getConcepto());
      factura.setReferencia(fecfactura.getReferencia());
      factura.setRecaudador(fecfactura.getRecaudador());
      Fec_factura upfecfactura = fecfacServicio.save(factura);
      return ResponseEntity.ok(upfecfactura);
   }

   @GetMapping("/fecFacturaDatos")
   public fecFacturaDatos getDatosFecFactura(@RequestParam Long idfactura) {
      fecFacturaDatos fecFactura = fecfacServicio.getNroFactura(idfactura);
      return fecFactura;
   }

   @GetMapping("/createFacElectro")
   public ResponseEntity<Map<String, Object>> generarFecFactura(@RequestParam Long idfactura) {
       return ResponseEntity.ok(fecfacServicio.generarFecFactura(idfactura));
   }
   @GetMapping("/{idfactura}")
    public ResponseEntity<Optional<Fec_factura>> findByIdFactura(@PathVariable Long idfactura){
       return ResponseEntity.ok(fecfacServicio.findById(idfactura));
    }


   }
