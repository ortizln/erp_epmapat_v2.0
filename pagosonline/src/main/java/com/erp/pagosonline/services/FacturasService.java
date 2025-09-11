package com.erp.pagosonline.services;

import ch.qos.logback.classic.Logger;
import com.erp.pagosonline.DTO.FacturaDTO;
import com.erp.pagosonline.DTO.FacturaRequestDTO;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacturasService {
    private static final Logger log = (Logger) LoggerFactory.getLogger(FacturasService.class);

    private final FacturasR dao;
    private final RestTemplate restTemplate;
    private final String apiBaseUrl;
    @Autowired
    private TmpinteresxfacR tmpinteresxfacR;
    @Autowired
    private CajasR cajasR;
    @Autowired
    private RecaudaxcajaService rxc_service;
    @Autowired
    private CajasService cajasService;
    @Autowired
    public FacturasService(FacturasR dao,
            RestTemplate restTemplate,
            @Value("${app.api.base-url}") String apiBaseUrl) {
        this.dao = dao;
        this.restTemplate = restTemplate;
        this.apiBaseUrl = apiBaseUrl;
    }

    public Object savePagos(Long user, FacturaRequestDTO datos) {
        System.out.println(datos);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "COBRANDO...");

        try {
            // Verificar conexión antes de cobrar
            String healthUrl = apiBaseUrl + "/actuator/health"; // o cualquier endpoint disponible
            ResponseEntity<String> healthResponse = restTemplate.getForEntity(healthUrl, String.class);
            System.out.println(healthResponse);
            if (healthResponse.getStatusCode().is2xxSuccessful()) {
                // Si hay conexión, procedemos con el pago
                restTemplate.put(apiBaseUrl + "/api/rec/facturas/cobrar", datos);

                respuesta.put("status", "SUCCESS");
                respuesta.put("detalle", "Pago realizado correctamente");
                respuesta.put("body", datos);
            } else {
                respuesta.put("status", "ERROR");
                respuesta.put("detalle", "No hay conexión con el servicio de recaudación");
            }

        } catch (RestClientException e) {
            respuesta.put("status", "ERROR");
            respuesta.put("detalle", "Error de conexión con recaudación: " + e.getMessage());
        }

        return respuesta;
    }

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

    public Map<String, Object> getConnectionStatus(Long user) {
        try {
            return restTemplate.getForObject(
                    apiBaseUrl + "/api/rec/cajas/test_connection?user=" + user,
                    Map.class,
                    user);
        } catch (RestClientException e) {
            log.error("Error al obtener estado de conexión", e);
            throw new ServiceUnavailableException("No se pudo verificar el estado de la caja");
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
            System.out.println("factura " + f.getInteres());
            interes = interes.add(f.getInteres() != null ? f.getInteres() : BigDecimal.ZERO);
            System.out.println("INTERES: " + interes);
        }

        BigDecimal subtotal = calculateSubtotal(facturas);
        //BigDecimal interes = calculateTotalInteres(facturas);
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

}