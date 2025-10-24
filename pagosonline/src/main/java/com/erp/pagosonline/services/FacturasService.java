package com.erp.pagosonline.services;

import ch.qos.logback.classic.Logger;
import com.erp.pagosonline.DTO.FacturaDTO;
import com.erp.pagosonline.DTO.FacturaRequestDTO;
import com.erp.pagosonline.interfaces.FacturasCobradas;
import com.erp.pagosonline.interfaces.FacturasSinCobroInter;
import com.erp.pagosonline.interfaces.NroFactura_int;
import com.erp.pagosonline.models.Facturas;
import com.erp.pagosonline.repositories.CajasR;
import com.erp.pagosonline.repositories.FacturasR;
import com.erp.pagosonline.repositories.RecaudaxcajaR;
import com.erp.pagosonline.repositories.TmpinteresxfacR;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacturasService {
    private static final Logger log = (Logger) LoggerFactory.getLogger(FacturasService.class);

    private final FacturasR dao;
    @Autowired
    private TmpinteresxfacR tmpinteresxfacR;
    @Autowired
    private CajasR cajasR;
    @Autowired
    private RecaudaxcajaService rxc_service;
    @Autowired
    private CajasService cajasService;
    @Autowired
    public FacturasService(FacturasR dao) {
        this.dao = dao;
    }
    @Value("${eureka.service-url}")
    private String eurekaServiceUrl;
    public Object findFacturasSinCobro(Long user, Long cuenta) {
        validateInput(user, cuenta);
        Map<String, Object> connection = (Map<String, Object>) cajasService.testIfLogin(user);
        Boolean test = validateCajaStatus(connection);
        if (test) {
            List<FacturasSinCobroInter> facturas = dao.findFacturasSinCobro(cuenta);
            return buildResponse(cuenta, facturas);

        } else {
            Map<String, Object> respuesta = new HashMap<>();
            return respuesta.put("mensaje", "Caja no iniciada");
        }

    }

    private void validateInput(Long user, Long cuenta) {
        if (user == null || cuenta == null) {
            throw new IllegalArgumentException("Los parámetros 'user' y 'cuenta' no pueden ser nulos");
        }
    }

    public Optional<Facturas> findFacturaById(Long idfactura){
        return dao.findById(idfactura);
    }
    public Boolean validateCajaStatus(Map<String, Object> connection) {
        // Validación de entrada
        Boolean respuesta = false;
        if (connection == null || !connection.containsKey("estado")) {
            return false;
        }
        Object estado = connection.get("estado");
        // Manejo de tipos de estado
        if (estado instanceof Boolean) {
            respuesta = (Boolean) estado;
        } else if (estado instanceof Integer) {
            respuesta = ((Integer) estado) == 1;
        } else if (estado instanceof String) {
            String estadoStr = ((String) estado).toLowerCase();
            if ("true".equals(estadoStr) || "1".equals(estadoStr)) {
                respuesta = true;
            }
            if ("false".equals(estadoStr) || "0".equals(estadoStr)) {
                respuesta = false;
            }
        }
        return respuesta;
    }

    private FacturaDTO buildResponse(Long cuenta, List<FacturasSinCobroInter> facturas) {
        BigDecimal interes = BigDecimal.ZERO;
        if (facturas == null || facturas.isEmpty()) {
            return createEmptyResponse(cuenta);
        }
        for(FacturasSinCobroInter f: facturas){
            interes = interes.add(f.getInteres() != null ? f.getInteres() : BigDecimal.ZERO);
        }

        BigDecimal subtotal = calculateSubtotal(facturas);
        List<Long> facturaIds = extractFacturaIds(facturas);

        return FacturaDTO.builder()
                .cuenta(cuenta)
                .responsablepago(facturas.get(0).getNombre())
                .total(subtotal.add(interes))
                .facturas(facturaIds)
                .interes(interes)
                .subtotal(subtotal)
                .build();
    }

    private BigDecimal calculateSubtotal(List<FacturasSinCobroInter> facturas) {
        return facturas.stream()
                .map(FacturasSinCobroInter::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Long> extractFacturaIds(List<FacturasSinCobroInter> facturas) {
        return facturas.stream()
                .map(FacturasSinCobroInter::getIdfactura)
                .collect(Collectors.toList());
    }

    private FacturaDTO createEmptyResponse(Long cuenta) {
        return FacturaDTO.builder()
                .cuenta(cuenta)
                .total(BigDecimal.ZERO)
                .facturas(Collections.emptyList())
                .build();
    }

    public static class ServiceUnavailableException extends RuntimeException {
        public ServiceUnavailableException(String message) {
            super(message);
        }
    }
    @Autowired
    private RestTemplate restTemplate;

    public <S extends Facturas> Facturas cobrarFactura(S factura){
        if(factura.getNrofactura() == null){
            NroFactura_int _nroFactura = cajasR.buildNroFactura(factura.getUsuariocobro());
            String secuencial = fSecuencial(_nroFactura.getSecuencial());
            String puntoEmision = fPemiCaja(_nroFactura.getEstablecimiento());
            String codigo = fPemiCaja(_nroFactura.getCodigo());
            String nrofactura = puntoEmision + '-' + codigo +'-'+ secuencial;
            rxc_service.updateLastfactFinFac(factura.getUsuariocobro(), Long.valueOf(secuencial));
            factura.setNrofactura(nrofactura);
        }

        // Guardamos la factura primero
        Facturas savedFactura = dao.save(factura);

        try {
            // Construimos la URL del microservicio
            String url = eurekaServiceUrl + ":8080/fec_factura/createFacElectro?idfactura=" + savedFactura.getIdfactura();


            // Consumimos el microservicio (POST sin body, pero puedes ajustar si requiere JSON)
            ResponseEntity<String> response = restTemplate.getForEntity(url, null, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Factura electrónica creada correctamente en el microservicio: " + response.getBody());
            } else {
                System.err.println("Error al crear factura electrónica. Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("Error al llamar al microservicio de factura electrónica: " + e.getMessage());
        }

        return savedFactura;
    }

    public <S extends Facturas> Facturas _cobrarFactura(S factura){
        if(factura.getNrofactura() == null){
            NroFactura_int _nroFactura = cajasR.buildNroFactura(factura.getUsuariocobro());
            String secuencial = fSecuencial(_nroFactura.getSecuencial());
            String puntoEmision = fPemiCaja(_nroFactura.getEstablecimiento());
            String codigo = fPemiCaja(_nroFactura.getCodigo());
            String nrofactura = puntoEmision + '-' + codigo +'-'+ secuencial;
            rxc_service.updateLastfactFinFac(factura.getUsuariocobro(), Long.valueOf(secuencial));
            factura.setNrofactura(nrofactura);
        }
        //implementar un resttemplate para consumir un microservicio
        return dao.save(factura);
    }

    public static String fSecuencial(Long numero) {
        String formato = "%09d";
        return String.format(formato, numero);
    }
    public static String fPemiCaja(Long numero) {
        String formato = "%03d";
        return String.format(formato, numero);
    }

    public List<FacturasCobradas> getReporteFacturasCobradas(Long idusuario, LocalDate df, LocalDate hf, LocalTime dh, LocalTime hh){
        return dao.getReporteFacturasCobradas(idusuario,df,hf,dh, hh);
    }



}