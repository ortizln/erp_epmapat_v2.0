package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Suspensiones;
import com.erp.comercializacion.repositories.SuspensionesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SuspensionesService {
    @Autowired
    private SuspensionesR suspensionesR;
    public List<Suspensiones> findAll() {
        return suspensionesR.findAll();
    }
    public <S extends Suspensiones> S save(S entity) {
        return suspensionesR.save(entity);
    }
    public Optional<Suspensiones> findById(Long id) {
        return suspensionesR.findById(id);
    }
    public void deleteById(Long id) {
        suspensionesR.deleteById(id);
    }
    public List<Suspensiones> findByFecha(LocalDate desde, LocalDate hasta) {
        return suspensionesR.findByFecha(desde, hasta);
    }

    public List<Suspensiones> findLastTen() {
        return suspensionesR.findLastTen();
    }

    public List<Suspensiones> findByNumero(Long numero) {

        return suspensionesR.findByNumero(numero);
    }
    public List<Suspensiones> findHabilitaciones(){
        return suspensionesR.findHabilitaciones();
    }

    public List<Suspensiones> findByFechaHabilitaciones(LocalDate desde, LocalDate hasta) {
        return suspensionesR.findByFechaHabilitaciones(desde, hasta);
    }

    public Suspensiones findFirstByOrderByIdsuspensionDesc() {
        return suspensionesR.findFirstByOrderByIdsuspensionDesc();
    }
}
