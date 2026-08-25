package com.erp.sri_files.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import com.erp.sri_files.utils.FirmaComprobantesService;
import com.erp.sri_files.utils.FirmaComprobantesService.ModoFirma;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignService {

    private static final Logger log = LoggerFactory.getLogger(SignService.class);

    private final FirmaComprobantesService firmaComprobantesService;

    public enum TipoFirma {
        XADES_BES,
        XMLDSIG
    }

    public record FirmaResult(
        boolean exitoso,
        String xmlFirmado,
        String mensaje,
        TipoFirma tipoFirma
    ) {}

    public FirmaResult firmar(String xmlPlano, TipoFirma tipoFirma) {
        String requestId = MDC.get("requestId");
        ModoFirma modo = (tipoFirma == TipoFirma.XMLDSIG) ? ModoFirma.XMLDSIG : ModoFirma.XADES_BES;
        
        log.info("Firmando comprobante con modo {} [requestId={}]", tipoFirma, requestId);
        
        try {
            String xmlFirmado = firmaComprobantesService.firmarFactura(xmlPlano, modo);
            log.info("Comprobante firmado exitosamente [requestId={}]", requestId);
            return new FirmaResult(true, xmlFirmado, "Firma exitosa", tipoFirma);
        } catch (Exception e) {
            log.error("Error firmando comprobante [requestId={}]", requestId, e);
            return new FirmaResult(false, null, "Error en firma: " + e.getMessage(), tipoFirma);
        }
    }

    public FirmaResult firmar(String xmlPlano) {
        return firmar(xmlPlano, TipoFirma.XADES_BES);
    }

    public FirmaResult firmar(byte[] xmlBytes, TipoFirma tipoFirma) {
        String requestId = MDC.get("requestId");
        ModoFirma modo = (tipoFirma == TipoFirma.XMLDSIG) ? ModoFirma.XMLDSIG : ModoFirma.XADES_BES;
        
        log.info("Firmando comprobante (bytes) con modo {} [requestId={}]", tipoFirma, requestId);
        
        try {
            String xmlFirmado = firmaComprobantesService.firmarFactura(xmlBytes, modo);
            log.info("Comprobante firmado exitosamente [requestId={}]", requestId);
            return new FirmaResult(true, xmlFirmado, "Firma exitosa", tipoFirma);
        } catch (Exception e) {
            log.error("Error firmando comprobante [requestId={}]", requestId, e);
            return new FirmaResult(false, null, "Error en firma: " + e.getMessage(), tipoFirma);
        }
    }
}
