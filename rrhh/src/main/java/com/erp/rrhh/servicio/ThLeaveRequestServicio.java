package com.erp.rrhh.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.erp.rrhh.modelo.ThLeaveRequest;
import com.erp.rrhh.repositorio.PersonalR;
import com.erp.rrhh.repositorio.ThLeaveRequestR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThLeaveRequestServicio {

    private final ThLeaveRequestR dao;
    private final PersonalR personalR;
    private static final Set<String> TIPOS = Set.of("VACACION", "PERMISO", "LICENCIA");

    @Transactional
    public ThLeaveRequest save(ThLeaveRequest r) {
        validar(r);
        r.setIdpersonal_personal(personalR.findById(r.getIdpersonal_personal().getIdpersonal())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Personal no existe: " + r.getIdpersonal_personal().getIdpersonal())));
        if (r.getEstado() == null || r.getEstado().isBlank()) r.setEstado("SOLICITADA");
        if (r.getFeccrea() == null) r.setFeccrea(LocalDate.now());
        if (r.getActivo() == null) r.setActivo(true);
        if (r.getDias_solicitados() == null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(r.getFechainicio(), r.getFechafin()) + 1;
            r.setDias_solicitados(BigDecimal.valueOf(days));
        }
        return dao.save(r);
    }

    @Transactional(readOnly = true)
    public List<ThLeaveRequest> byPersonal(Long idpersonal) {
        return dao.findByIdpersonal_personal_IdpersonalOrderByFeccreaDesc(idpersonal);
    }

    @Transactional(readOnly = true)
    public List<ThLeaveRequest> byEstado(String estado) {
        return dao.findByEstadoOrderByFeccreaDesc(estado.toUpperCase());
    }

    private void validar(ThLeaveRequest r) {
        if (r == null || r.getIdpersonal_personal() == null || r.getIdpersonal_personal().getIdpersonal() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idpersonal_personal es obligatorio");
        if (r.getTipolicencia() == null || r.getTipolicencia().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tipolicencia es obligatorio");
        String tipo = r.getTipolicencia().trim().toUpperCase();
        if (!TIPOS.contains(tipo))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tipolicencia inválido: " + TIPOS);
        r.setTipolicencia(tipo);
        if (r.getFechainicio() == null || r.getFechafin() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechainicio y fechafin son obligatorias");
        if (r.getFechainicio().isAfter(r.getFechafin()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechainicio no puede ser mayor a fechafin");
    }
}
