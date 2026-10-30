package com.erp.sri_files.services;

import com.erp.sri_files.models.Factura;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Service
public class FacturaOutboxService {
    private final JdbcTemplate jdbc;
    public FacturaOutboxService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Transactional
    public void registrar(Factura f) {
        if (f.getXmlautorizado() == null || f.getXmlautorizado().isBlank())
            throw new IllegalArgumentException("No se notifica sin XML autorizado");
        jdbc.update("""
            INSERT INTO public.sri_autorizacion_control(factura_id,clave_acceso,estado)
            VALUES (?,?,'AUTORIZADO') ON CONFLICT(factura_id) DO UPDATE
            SET estado='AUTORIZADO',actualizado=now(),ultimo_error=null
            """, f.getIdfactura(), f.getClaveacceso());
        boolean completo=false;
        try {
            var parsed=FacturaPdfXml.parse(f.getXmlautorizado());
            completo=f.getClaveacceso().equals(FacturaPdfXml.text(parsed.factura,"claveAcceso"))
                    && !parsed.numeroAutorizacion.isBlank() && !parsed.fechaAutorizacion.isBlank();
        }catch(Exception invalid){ /* La tarea intentara recuperar los metadatos oficiales antes de preparar el RIDE. */ }
        if(completo) jdbc.update("""
            INSERT INTO public.comprobante_documento(factura_id,xml_autorizado,xml_sha256) VALUES (?,?,?)
            ON CONFLICT(factura_id) DO UPDATE SET xml_autorizado=excluded.xml_autorizado,
            xml_sha256=excluded.xml_sha256,
            ride=CASE WHEN comprobante_documento.xml_sha256=excluded.xml_sha256 THEN comprobante_documento.ride ELSE null END,
            ride_sha256=CASE WHEN comprobante_documento.xml_sha256=excluded.xml_sha256 THEN comprobante_documento.ride_sha256 ELSE null END
            """, f.getIdfactura(), f.getXmlautorizado(), sha(f.getXmlautorizado().getBytes(StandardCharsets.UTF_8)));
        jdbc.update("""
            INSERT INTO public.evento_outbox(factura_id,version) VALUES (?,?)
            ON CONFLICT(factura_id,tipo,version) DO NOTHING
            """, f.getIdfactura(), Math.max(1, f.getNotificacionVersion()));
    }

    @Transactional
    public void estadoSri(Factura f, String estado) {
        if (f.getClaveacceso() == null || !f.getClaveacceso().matches("[0-9]{49}")) return;
        int intentos=f.getIntentos_autorizacion()==null?0:f.getIntentos_autorizacion();
        java.time.OffsetDateTime proxima=null;
        if(f.getFecha_ultimo_intento()!=null && ("ESPERANDO_AUTORIZACION".equals(estado)||"ENVIANDO_RECEPCION".equals(estado)))
            proxima=f.getFecha_ultimo_intento().atZone(java.time.ZoneId.of("America/Guayaquil"))
                    .plusNanos(retry.demoraMs(f.getIdfactura(),intentos)*1000000).toOffsetDateTime();
        jdbc.update("""
            INSERT INTO public.sri_autorizacion_control(factura_id,clave_acceso,estado,ultimo_error,intentos,proxima_consulta)
            VALUES (?,?,?,?,?,?) ON CONFLICT(factura_id) DO UPDATE SET estado=excluded.estado,
            ultimo_error=excluded.ultimo_error,intentos=excluded.intentos,proxima_consulta=excluded.proxima_consulta,actualizado=now()
            """, f.getIdfactura(), f.getClaveacceso(), estado, f.getErrores(),intentos,proxima);
    }

    // Una sola sentencia reclama una tarea; no se mantiene la transaccion durante HTTP/PDF.
    @Transactional
    public Map<String,Object> reclamar() {
        var rows = jdbc.queryForList("""
            WITH candidato AS (
              SELECT id FROM public.evento_outbox
              WHERE ((estado IN ('PENDING','RETRY','WAITING_EMAIL') AND proximo_intento<=now())
                OR (estado='PROCESSING' AND reservado_hasta<now())
                OR (estado IN ('BLOCKED','UNCERTAIN') AND proximo_intento<=now() AND EXISTS(
                  SELECT 1 FROM public.fec_factura f WHERE f.idfactura=evento_outbox.factura_id AND f.email_message_id IS NOT NULL)))
              ORDER BY proximo_intento,id FOR UPDATE SKIP LOCKED LIMIT 1
            ) UPDATE public.evento_outbox e SET estado='PROCESSING',token=?,
              reservado_hasta=now()+interval '10 minutes',intentos=LEAST(e.intentos+1,2147483647),actualizado=now()
              FROM candidato c WHERE e.id=c.id RETURNING e.id,e.factura_id,e.version,e.token,e.intentos
            """, UUID.randomUUID());
        return rows.isEmpty() ? null : rows.get(0);
    }

    @Transactional
    public void terminar(Map<String,Object> trabajo, String estado, String error) {
        int intentos = ((Number) trabajo.get("intentos")).intValue();
        long espera = "WAITING_EMAIL".equals(estado) ? 30 : ("BLOCKED".equals(estado) || "UNCERTAIN".equals(estado)) ? 300 : Math.min(3600, 30L << Math.min(7,intentos));
        jdbc.update("""
            UPDATE public.evento_outbox SET estado=?,ultimo_error=?,token=null,reservado_hasta=null,
            proximo_intento=now()+(? * interval '1 second'),actualizado=now() WHERE id=? AND token=?
            """, estado, error, espera, trabajo.get("id"), trabajo.get("token"));
    }

    @Transactional(readOnly=true)
    public byte[] ride(Long id, String xml) {
        var rows = jdbc.queryForList("SELECT ride,ride_sha256 FROM public.comprobante_documento WHERE factura_id=? AND xml_sha256=?", id,sha(xml.getBytes(StandardCharsets.UTF_8)));
        if(rows.isEmpty())return null;
        byte[] bytes=(byte[])rows.get(0).get("ride");
        return bytes!=null && sha(bytes).equals(rows.get(0).get("ride_sha256"))?bytes:null;
    }
    @Transactional
    public void guardarRide(Factura f, byte[] pdf) {
        registrar(f);
        jdbc.update("UPDATE public.comprobante_documento SET ride=?,ride_sha256=?,generado=now() WHERE factura_id=?",
                pdf,sha(pdf),f.getIdfactura());
    }
    @org.springframework.beans.factory.annotation.Autowired
    private com.erp.sri_files.repositories.FacturaR facturas;
    @org.springframework.beans.factory.annotation.Autowired
    private SriAutorizacionRetryPolicy retry;
    @Transactional
    public int recuperarDevuelta(Long id, String clave, String xml, java.time.LocalDateTime fecha) {
        int n=facturas.guardarXmlRecuperadoDevuelta(id,clave,xml,fecha);
        if(n>0) registrar(facturas.findById(id).orElseThrow());
        return n;
    }
    public static String sha(byte[] bytes) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)); }
        catch (java.security.NoSuchAlgorithmException impossible) { throw new IllegalStateException(impossible); }
    }
    @Transactional
    public void reconciliar(Long desde, int limite) {
        // Repara O anteriores al despliegue; A historicos nunca se reenvian automaticamente.
        jdbc.update("""
            INSERT INTO public.evento_outbox(factura_id,version)
            SELECT idfactura,notificacion_version FROM public.fec_factura
            WHERE upper(trim(estado))='O' AND nullif(trim(xmlautorizado),'') IS NOT NULL AND idfactura>?
            ORDER BY idfactura LIMIT ? ON CONFLICT(factura_id,tipo,version) DO NOTHING
            """, desde, limite);
    }
}
