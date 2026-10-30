package com.erp.emails.service;
import com.erp.emails.model.EmailMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service
public class EmailAttemptService {
    private final JdbcTemplate jdbc;
    public EmailAttemptService(JdbcTemplate jdbc){this.jdbc=jdbc;}
    public void iniciar(EmailMessage m){
        jdbc.update("INSERT INTO public.correo_intento(email_id,token,intento) VALUES (?,?,?)",m.getId(),m.getProcessingToken(),m.getAttempts());
    }
    public void finalizar(UUID token,String outcome,String error){
        jdbc.update("UPDATE public.correo_intento SET resultado=?,error=?,terminado=now() WHERE token=? AND terminado IS NULL",outcome,error,token);
    }
    @Transactional
    public int recuperarAbandonados(){
        var rows=jdbc.queryForList("""
          UPDATE public.email_message SET status='UNCERTAIN',failure_kind='UNCERTAIN',
          last_error='Proceso interrumpido durante envio; conciliar proveedor antes de reintentar',
          next_attempt_at=null,processing_until=null,processing_token=null
          WHERE status='PENDING' AND processing_token IS NOT NULL AND processing_until<now()
          RETURNING id
          """);
        for(var row:rows)jdbc.update("""
          UPDATE public.correo_intento SET resultado='UNCERTAIN',terminado=now(),error='Reserva de envio vencida'
          WHERE email_id=? AND terminado IS NULL
          """,row.get("id"));
        return rows.size();
    }
    @Transactional
    public void registrarPrueba(Long id,java.time.OffsetDateTime version,boolean ok){
        int n=jdbc.update("UPDATE public.email_account SET last_connection_check=now(),last_connection_success=? WHERE id=? AND updated_at IS NOT DISTINCT FROM CAST(? AS timestamptz)",ok,id,version);
        if(n>0 && ok)reactivarConfiguracion(id);
    }
    @Transactional
    public void reactivarConfiguracion(Long account){
        jdbc.update("""
          UPDATE public.email_message SET status='PENDING',failure_kind=null,last_error=null,next_attempt_at=now()
          WHERE email_account_id=? AND status='FAILED' AND failure_kind='CONFIGURATION'
          """,account);
    }
}
