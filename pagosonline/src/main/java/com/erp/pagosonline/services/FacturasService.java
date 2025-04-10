package com.erp.pagosonline.services;

import ch.qos.logback.classic.Logger;
import com.erp.pagosonline.DTO.FacturaDTO;
import com.erp.pagosonline.DTO.FacturaRequestDTO;
import com.erp.pagosonline.interfaces.FacturasSinCobroInter;
import com.erp.pagosonline.repositories.FacturasR;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacturasService {
    private static final Logger log = (Logger) LoggerFactory.getLogger(FacturasService.class);

    private final FacturasR dao;
    private final RestTemplate restTemplate;
    private final String apiBaseUrl;

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
            // Send request to external API
            restTemplate.put(apiBaseUrl + "/facturas/cobrar", datos);

            // Update response to indicate success
            respuesta.put("status", "SUCCESS");
            respuesta.put("detalle", "Pago realizado correctamente");
            respuesta.put("body", datos);

        } catch (RestClientException e) {
            // Handle errors if the request fails
            respuesta.put("status", "ERROR");
            respuesta.put("detalle", "No se pudo realizar el pago: " + e.getMessage());
        }

        return respuesta;
    }


    public Object findFacturasSinCobro(Long user, Long cuenta) {
        validateInput(user, cuenta);
        Map<String, Object> connection = getConnectionStatus(user);
        Boolean test = validateCajaStatus(connection);
        if(test){
            List<FacturasSinCobroInter> facturas = dao.findFacturasSinCobro(cuenta);
            return buildResponse(cuenta, facturas);

        }else{
            Map<String, Object> respuesta = new HashMap<>();
            return respuesta.put("mensaje","Caja no iniciada");
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
                    apiBaseUrl + "/cajas/test_connection?user="+user,
                    Map.class,
                    user);
        } catch (RestClientException e) {
            log.error("Error al obtener estado de conexión", e);
            throw new ServiceUnavailableException("No se pudo verificar el estado de la caja");
        }
    }

    public Boolean validateCajaStatus(Map<String, Object> connection) {
        // Validación de entrada
        Boolean respuesta= false;
        if (connection == null || !connection.containsKey("estado")) {
            return false;
        }
        Object estado = connection.get("estado");
        // Manejo de tipos de estado
        if (estado instanceof Boolean) {
            respuesta = (Boolean) estado;
        }
        else if (estado instanceof Integer) {
            respuesta = ((Integer) estado) == 1;
        }
        else if (estado instanceof String) {
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
        if (facturas == null || facturas.isEmpty()) {
            return createEmptyResponse(cuenta);
        }

        BigDecimal subtotal = calculateSubtotal(facturas);
        BigDecimal interes = calculateTotalInteres(facturas);
        List<Long> facturaIds = extractFacturaIds(facturas);

        return FacturaDTO.builder()
                .cuenta(cuenta)
                .responsablepago(facturas.get(0).getNombre())
                .total(subtotal.add(interes))
                .facturas(facturaIds)
                .build();
    }

    private BigDecimal calculateSubtotal(List<FacturasSinCobroInter> facturas) {
        return facturas.stream()
                .map(FacturasSinCobroInter::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalInteres(List<FacturasSinCobroInter> facturas) {
        return facturas.stream()
                .map(this::getInteres)
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

    private BigDecimal getInteres(FacturasSinCobroInter factura) {
        try {
            return restTemplate.getForObject(
                    apiBaseUrl + "/intereses/calcularInteres?idfactura={id}&subtotal={subtotal}",
                    BigDecimal.class,
                    factura.getIdfactura(),
                    factura.getSubtotal());
        } catch (RestClientException e) {
            log.error("Error al calcular interés para factura {}", factura.getIdfactura(), e);
            return BigDecimal.ZERO;
        }
    }
    public static class ServiceUnavailableException extends RuntimeException {
        public ServiceUnavailableException(String message) {
            super(message);
        }
    }
}