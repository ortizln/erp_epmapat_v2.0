package com.erp.sri_files.services;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.utils.FirmaComprobantesService;
import com.erp.sri_files.validation.FacturaPrevalidacionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDateTime;
@Service
public class FacturaPreparationService {
    private final FacturaR facturas;private final FacturaXmlGeneratorService xml;
    private final FirmaComprobantesService firma;private final SriAutorizacionRetryPolicy retry;
    private final FacturaOutboxService outbox;
    @Value("${sri.firma.modo:XADES_BES}") private String modo="XADES_BES";
    public FacturaPreparationService(FacturaR f,FacturaXmlGeneratorService x,FirmaComprobantesService s,SriAutorizacionRetryPolicy r,FacturaOutboxService o){facturas=f;xml=x;firma=s;retry=r;outbox=o;}
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public boolean preparar(Long id){
        var f=facturas.findParaProcesar(id).orElse(null);
        if(f==null || !("I".equalsIgnoreCase(f.getEstado()) || "G".equalsIgnoreCase(f.getEstado())))return false;
        if(f.getXmlautorizado()!=null && !f.getXmlautorizado().isBlank()){
            f.setEstado("O");facturas.saveAndFlush(f);outbox.registrar(f);return false;
        }
        if(!retry.corresponde(f,LocalDateTime.now()))return false;
        try {
            if(f.getXmlFirmadoEnvio()==null || f.getXmlFirmadoEnvio().isBlank()){
                String plano=xml.generarXmlFactura(f);
                String firmado=firma.firmarFactura(plano,"XMLDSIG".equalsIgnoreCase(modo)?FirmaComprobantesService.ModoFirma.XMLDSIG:FirmaComprobantesService.ModoFirma.XADES_BES);
                if(firmado==null || firmado.isBlank())throw new IllegalStateException("XML firmado vacio");
                f.setXmlFirmadoEnvio(firmado);
            }
            f.setEstado("G");f.setErrores(null);facturas.saveAndFlush(f);outbox.estadoSri(f,"FIRMADO");return true;
        }catch(org.springframework.dao.DataAccessException | jakarta.persistence.PersistenceException persistence){throw persistence;
        }catch(FacturaPrevalidacionException invalid){
            f.setEstado("E");f.setErrores(invalid.getMessage());facturas.saveAndFlush(f);outbox.estadoSri(f,"DATOS_INVALIDOS");return false;
        }catch(Exception transientError){
            retry.pendiente(f,transientError.getMessage());f.setEstado("I");facturas.saveAndFlush(f);outbox.estadoSri(f,"ERROR_PREPARACION");return false;
        }
    }
}
