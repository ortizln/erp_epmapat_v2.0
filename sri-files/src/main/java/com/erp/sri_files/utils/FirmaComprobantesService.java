package com.erp.sri_files.utils;

import com.erp.sri_files.config.AESUtil;
import com.erp.sri_files.models.Definir;
import com.erp.sri_files.repositories.DefinirR;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class FirmaComprobantesService {

    public enum ModoFirma { XMLDSIG, XADES_BES }

    private final DefinirR certRepo;
    private final XmlDsigService xmlDsigService;
    private final XadesBesService xadesBesService;

    public FirmaComprobantesService(DefinirR certRepo,
                                    XmlDsigService xmlDsigService,
                                    XadesBesService xadesBesService) {
        this.certRepo = certRepo;
        this.xmlDsigService = xmlDsigService;
        this.xadesBesService = xadesBesService;
    }

    /** Firma usando el registro DEFINIR.id=1 por defecto. */
    @Transactional
    public String firmarFactura(String xmlPlano, ModoFirma modo) throws Exception {
        System.out.println("SIN DEFINIR");
        Definir cert = certRepo.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Certificado no encontrado (id=1)"));

        byte[] pkcs12 = cert.getFirma(); // BLOB crudo
        if (pkcs12 == null || pkcs12.length == 0)
            throw new IllegalStateException("El campo DEFINIR.firma está vacío");
String password = AESUtil.descifrar(cert.getClave_firma());
        var km = Pkcs12Loader.load(pkcs12, password != null ? password.toCharArray() : new char[0]);

        return (modo == ModoFirma.XMLDSIG)
                ? xmlDsigService.signFacturaComprobante(xmlPlano, km)
                : xadesBesService.signFacturaComprobanteXades(xmlPlano, km);
    }

    /** Variante para indicar el id del registro DEFINIR a usar. */
    @Transactional
    public String firmarFactura(String xmlPlano, long definirId, ModoFirma modo) throws Exception {
        System.out.println("CON DEFINIR");

        Definir cert = certRepo.findById(definirId)
                .orElseThrow(() -> new IllegalStateException("Certificado no encontrado (id=" + definirId + ")"));

        byte[] pkcs12 = cert.getFirma(); // BLOB crudo
        if (pkcs12 == null || pkcs12.length == 0)
            throw new IllegalStateException("El campo DEFINIR.firma está vacío");
        String password = AESUtil.descifrar(cert.getClave_firma());

        var km = Pkcs12Loader.load(pkcs12, password != null ? password.toCharArray() : new char[0]);

        return (modo == ModoFirma.XMLDSIG)
                ? xmlDsigService.signFacturaComprobante(xmlPlano, km)
                : xadesBesService.signFacturaComprobanteXades(xmlPlano, km);
    }
}
