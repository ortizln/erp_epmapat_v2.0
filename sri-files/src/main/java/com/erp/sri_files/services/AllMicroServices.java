package com.erp.sri_files.services;

import com.erp.sri_files.exceptions.MicroserviceException;
import com.erp.sri_files.interfaces.fecFacturaDatos;
import com.erp.sri_files.models.Factura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public AllMicroServices(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.comercializacionBaseUrl = "http://192.168.1.17:8080/facturas";
        this.fecFacturaBaseUrl = "http://192.168.1.17:8080/fec_factura";
    }

    public Factura findById(Long idFactura) {
        try {
            String url = String.format("%s/%d", comercializacionBaseUrl, idFactura);
            LOGGER.debug("Llamando a endpoint: {}", url);
            return restTemplate.getForObject(url, Factura.class);
        } catch (RestClientException e) {
            LOGGER.error("Error al obtener factura con id: {}", idFactura, e);
            throw new MicroserviceException("Error al consultar factura", e);
        }
    }

    public fecFacturaDatos getNroFactura(Long idfactura) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(fecFacturaBaseUrl)
                    .path("/fecFacturaDatos")
                    .queryParam("idfactura", idfactura)
                    .toUriString();

            LOGGER.debug("Llamando a endpoint FEC: {}", url);
            return restTemplate.getForObject(url, fecFacturaDatos.class);
        } catch (RestClientException e) {
            LOGGER.error("Error al obtener datos FEC para factura: {}", idfactura, e);
            throw new MicroserviceException("Error al consultar datos FEC", e);
        }
    }
}