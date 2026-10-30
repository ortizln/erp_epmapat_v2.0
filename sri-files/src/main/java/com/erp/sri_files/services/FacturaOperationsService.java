package com.erp.sri_files.services;
import com.erp.sri_files.repositories.FacturaR;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;
@Service
public class FacturaOperationsService {
    private final FacturaR facturas;private final FacturaOutboxService outbox;
    private final JdbcTemplate jdbc;private final EmailMsClientService emails;
    public FacturaOperationsService(FacturaR f,FacturaOutboxService o,JdbcTemplate j,EmailMsClientService e){facturas=f;outbox=o;jdbc=j;emails=e;}
    public record Command(String email,String evidence,boolean accepted,boolean confirmDuplicateRisk) {}
    @Transactional
    public void ejecutar(Long id,String accion,Command cmd,String key,String actor){
        var f=facturas.findParaProcesar(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        Boolean activo=jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM public.evento_outbox WHERE factura_id=? AND estado='PROCESSING' AND reservado_hasta>now())",Boolean.class,id);
        if(Boolean.TRUE.equals(activo))throw new ResponseStatusException(HttpStatus.CONFLICT,"Hay un trabajo activo; intente al finalizar la reserva");
        switch(accion){
            case "consultar" -> {
                if(f.getXmlautorizado()!=null && !f.getXmlautorizado().isBlank())throw new ResponseStatusException(HttpStatus.CONFLICT,"La factura ya tiene XML autorizado");
                f.setEstado("C");f.setFecha_ultimo_intento(null);f.setRecepcionConfirmada(true);
                f.setErrores("Consulta administrativa solicitada; no reenviar recepcion");
                facturas.saveAndFlush(f);outbox.estadoSri(f,"ESPERANDO_AUTORIZACION");
            }
            case "corregir-correo" -> {
                if(cmd.email()==null || cmd.email().length()>254 || !cmd.email().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Direccion de correo invalida");
                if(f.getXmlautorizado()==null || f.getXmlautorizado().isBlank())throw new ResponseStatusException(HttpStatus.CONFLICT,"Primero recuperar XML autorizado");
                if(f.getEmailMessageId()!=null){
                    var state=emails.consultarEstado(f.getEmailMessageId());
                    if(!Set.of("FAILED","CANCELLED").contains(state.status()))
                        throw new ResponseStatusException(HttpStatus.CONFLICT,"Solo se corrigen correos FAILED/CANCELLED; concilie enviados o inciertos");
                    if("FAILED".equals(state.status()))emails.accion(f.getEmailMessageId(),"cancel",Map.of(),key,actor);
                }
                jdbc.update("UPDATE public.evento_outbox SET estado='CANCELLED',token=null,reservado_hasta=null,actualizado=now() WHERE factura_id=?",id);
                f.setEmailcomprador(cmd.email().trim());f.setEmailMessageId(null);f.setMail_enviado(false);f.setEstado("O");f.setErrores(null);
                f.setNotificacionVersion(Math.addExact(f.getNotificacionVersion(),1));facturas.saveAndFlush(f);outbox.registrar(f);
            }
            case "reintentar-correo" -> {
                if(Boolean.TRUE.equals(f.getMail_enviado()))throw new ResponseStatusException(HttpStatus.CONFLICT,"El envio ya fue confirmado");
                if(f.getXmlautorizado()==null || f.getXmlautorizado().isBlank())throw new ResponseStatusException(HttpStatus.CONFLICT,"Falta XML autorizado");
                if(f.getEmailMessageId()!=null){
                    var state=emails.consultarEstado(f.getEmailMessageId());
                    if("UNCERTAIN".equals(state.status()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Conciliar el resultado incierto primero");
                    if("SENT".equals(state.status()))throw new ResponseStatusException(HttpStatus.CONFLICT,"SMTP ya acepto el correo; esperar conciliacion automatica");
                    if("FAILED".equals(state.status()))emails.accion(f.getEmailMessageId(),"retry",Map.of(),key,actor);
                    if("CANCELLED".equals(state.status()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Corregir direccion para generar una nueva version");
                }
                f.setEstado("O");facturas.saveAndFlush(f);outbox.registrar(f);
                jdbc.update("UPDATE public.evento_outbox SET estado='RETRY',proximo_intento=now(),actualizado=now() WHERE factura_id=? AND version=?",id,f.getNotificacionVersion());
            }
            case "conciliar" -> {
                if(f.getEmailMessageId()==null)throw new ResponseStatusException(HttpStatus.CONFLICT,"No hay correo encolado");
                emails.accion(f.getEmailMessageId(),"resolve",Map.of("accepted",cmd.accepted(),"confirmDuplicateRisk",cmd.confirmDuplicateRisk(),"evidence",Objects.toString(cmd.evidence(),"")),key,actor);
                jdbc.update("UPDATE public.evento_outbox SET estado='WAITING_EMAIL',proximo_intento=now(),actualizado=now() WHERE factura_id=? AND version=?",id,f.getNotificacionVersion());
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Accion desconocida");
        }
        jdbc.update("INSERT INTO public.factura_operacion(factura_id,actor,accion,detalle) VALUES (?,?,?,?)",id,actor,accion,
                "corregir-correo".equals(accion)?cmd.email():Objects.toString(cmd.evidence(),"Solicitado desde monitor"));
    }
}
