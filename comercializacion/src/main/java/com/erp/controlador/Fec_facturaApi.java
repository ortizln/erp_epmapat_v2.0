package com.erp.controlador;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.erp.excepciones.ResourceNotFoundExcepciones;
import com.erp.modelo.Fec_factura;
import com.erp.servicio.Fec_facturaService;
import com.erp.sri.interfaces.fecFacturaDatos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/fec_factura")
public class Fec_facturaApi {

   @Autowired
   private Fec_facturaService fecfacServicio;
   @Autowired
   private RestTemplate restTemplate;

    @Value("${eureka.service-url}")
    private String eurekaServiceUrl;


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
    @PutMapping("/setxml")
    public ResponseEntity<Fec_factura> setXmlToFactura(@RequestParam Long idfactura, @RequestBody Fec_factura ff) {
        Fec_factura factura = fecfacServicio.findById(idfactura)
                .orElseThrow(() -> new ResourceNotFoundExcepciones("Not found Id: " + idfactura));

        try {
            String url = eurekaServiceUrl + ":8080/api/singsend/autorizacion?claveAcceso=" + factura.getClaveacceso();
            String xml = restTemplate.getForObject(url, String.class);

            factura.setXmlautorizado(xml);
            factura.setEstado("A");
            fecfacServicio.save(factura);

            return ResponseEntity.ok(factura);
        } catch (HttpServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(factura);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(factura);
        }
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
