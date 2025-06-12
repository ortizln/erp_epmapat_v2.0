package com.erp.comercializacion.services;

import com.erp.comercializacion.interfaces.*;
import com.erp.comercializacion.models.Lecturas;
import com.erp.comercializacion.repositories.LecturasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class LecturasService {

    @Autowired
    private LecturasR dao;

    // Lectura por Planilla
    public Lecturas findOnefactura(Long idfactura) {
        return dao.findOnefactura(idfactura);
    }

    public List<Lecturas> findByIdrutaxemision(Long idrutaxemision) {
        return dao.findByIdrutaxemision(idrutaxemision);
    }

    public List<Lecturas> findByIdabonado(Long idabonado, Long limit) {
        return dao.findByIdabonado(idabonado, limit);
    }

    public List<Lecturas> findByMonth() {
        return dao.findByMonth();
    }

    public List<Lecturas> findByIdRutasxEmision(Long idrutaxemision) {
        return dao.findByIdRutasxEmision(idrutaxemision);
    }

    public List<Lecturas> findLecturasByIdAbonados(Long idabonado) {
        return dao.findLecturasByIdAbonados(idabonado);
    }

    public List<Lecturas> findByRutas(Long idrutas) {
        return dao.findByRutas(idrutas);
    }

    public List<Lecturas> findByIdAbonado(Long idabonado) {
        return dao.findByIdAbonado(idabonado);
    }

    public List<Lecturas> findByNCliente(String nombre) {
        return dao.findByNCliente(nombre);
    }

    // Lectura por Planilla
    public List<Lecturas> findByIdfactura(Long idfactura) {
        return dao.findByIdfactura(idfactura);
    }

    // Lecuras de una Emision
    public List<Lecturas> findByIdemision(Long idemision) {
        return dao.findByIdemision(idemision);
    }

    public List<Lecturas> findByIdemisionIdAbonado(Long idemision, Long idabonado) {
        return dao.findByIdemisionIdAbonado(idemision, idabonado);
    }

    public Lecturas getById(Long id) {
        return null;
    }

    public Optional<Lecturas> findById(Long id) {
        return dao.findById(id);
    }

    public <S extends Lecturas> S saveLectura(S entity) {
        return dao.save(entity);
    }

    // Ultima lectura de un Abonado
    public Long ultimaLectura(Long idabonado) {
        return dao.ultimaLectura(idabonado);
    }

    public Long ultimaLecturaByIdemision(Long idabonado, Long idemision) {
        return dao.ultimaLecturaByIdemision(idabonado, idemision);
    }

    public BigDecimal totalEmisionXFactura(Long idemision) {
        return dao.totalEmisionXFactura(idemision);
    }

    public List<Object[]> RubrosEmitidos(Long idemision) {
        return dao.RubrosEmitidos(idemision);
    }

    public List<Object[]> R_EmisionFinal(Long idemision) {
        return dao.R_EmisionFinal(idemision);
    }

    public List<Object[]> R_EmisionActual(Long idemision) {
        return dao.R_EmisionActual(idemision);
    }

    /* OBTENER LISTADOD DE FACTURAS DE CONSUMO DE AGUA POR RUTAS, DEUDORES */
    public List<Lecturas> findDeudoresByRuta(Long idrutas) {
        return dao.findDeudoresByRuta(idrutas);
    }

    /* buscar la fecha de una emision por el id de una factura */
    public LocalDate findDateByIdfactura(Long idfactura) {
        return dao.findDateByIdfactura(idfactura);
    }

    public List<FecEmision> getEmisionByIdfactura(Long idfactura) {
        return dao.getEmisionByIdfactura(idfactura);
    }

    public List<Lecturas> findByIdEmisiones(Long idemision) {
        return dao.findByIdEmisiones(idemision);
    }
    public List<RepFacEliminadasByEmision> findByIdEmisionesR(Long idemision) {
        return dao.findByIdEmisionesR(idemision);
    }

    public CompletableFuture<List<RubroxfacIReport>> getAllRubrosEmisionInicial(Long idemision) {
        return dao.getAllRubrosEmisionInicial(idemision);
    }
    public CompletableFuture<List<RubroxfacIReport>> getCuentaM3AllEmiInicial(Long idemision) {
        return dao.getCuentaM3AllEmiInicial(idemision);
    }

    public CompletableFuture<List<RubroxfacIReport>> getAllNewLecturas(Long idemision) {
        return dao.getAllNewLecturas(idemision);
    }

    public CompletableFuture<List<RubroxfacIReport>> getAllDeleteLecturas(Long idemision) {
        return dao.getAllDeleteLecturas(idemision);
    }

    public CompletableFuture<List<RubroxfacIReport>> getAllActual(Long idemision) {
        return dao.getAllActual(idemision);
    }

    public List<FacIntereses> getForIntereses(Long idfactura) {
        return dao.getForIntereses(idfactura);
    }

    public List<RepEmisionEmi> getReporteValEmitidosxEmision(Long idemision) {
        return dao.getReporteValEmitidosxEmision(idemision);
    }

    public List<ConsumoxCat_int> getConsumoxCategoria(Long idemision){
        return dao.getConsumoxCategoria(idemision);
    }

    public List<CountRubrosByEmision>getCuentaRubrosByEmision(long idemision ){
        return dao.getCuentaRubrosByEmision(idemision);
    }
}
