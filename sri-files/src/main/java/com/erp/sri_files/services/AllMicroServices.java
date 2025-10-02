package com.erp.sri_files.services;

import com.erp.sri_files.exceptions.MicroserviceException;
import com.erp.sri_files.interfaces.fecFacturaDatos;
import com.erp.sri_files.models.Factura;
import com.erp.sri_files.models.Facturas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AllMicroServices {
    private static final Logger LOGGER = LoggerFactory.getLogger(AllMicroServices.class);

    private final RestTemplate restTemplate;
    private final String comercializacionBaseUrl;
    private final String fecFacturaBaseUrl;

    public AllMicroServices(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.comercializacionBaseUrl = "http://localhost:9093/facturas";
        this.fecFacturaBaseUrl = "http://localhost:9093/fec_factura";
    }

    public Factura findById(Long idFactura) {
        try {
            String url = "%s/%d".formatted(fecFacturaBaseUrl, idFactura);
            LOGGER.debug("Llamando a endpoint: {}", url);
            return restTemplate.getForObject(url, Factura.class);
        } catch (RestClientException e) {
            LOGGER.error("Error al obtener factura con id: {}", idFactura, e);
            throw new MicroserviceException("Error al consultar factura", e);
        }
    }

    public fecFacturaDatos getNroFactura(Long idfactura) {
        try {
            String url = UriComponentsBuilder.fromUriString(fecFacturaBaseUrl)
                    .path("/fecFacturaDatos")
                    .queryParam("idfactura", idfactura)
                    .toUriString();

            LOGGER.debug("Llamando a endpoint FEC: {}", url);

            // Opción 1: Usando getForObject directamente
            return restTemplate.getForObject(url, fecFacturaDatos.class);

            /* Opción 2: Más detallada con verificación
            ResponseEntity<fecFacturaDatos> response = restTemplate.getForEntity(
                url, fecFacturaDatos.class);

            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                return response.getBody();
            }
            throw new MicroserviceException("Respuesta inválida del servicio FEC");
            */
        } catch (RestClientException e) {
            LOGGER.error("Error al obtener datos FEC para factura: {}", idfactura, e);
            throw new MicroserviceException("Error al consultar datos FEC", e);
        }
    }
}