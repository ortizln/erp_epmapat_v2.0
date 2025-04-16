package com.erp.comercializacion.services;

import com.erp.comercializacion.interfaces.CarteraVencidaRubros_int;
import com.erp.comercializacion.interfaces.RubroxfacI;
import com.erp.comercializacion.interfaces.RubroxfacIReport;
import com.erp.comercializacion.models.Rubroxfac;
import com.erp.comercializacion.repositories.RubroxfacR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RubroxfacService {
    @Autowired
    private RubroxfacR dao;

    // Campos Rubro y valor de una Planilla
    public List<Map<String, Object>> rubrosByIdfactura(Long idfactura) {
        return dao.rubrosByIdfactura(idfactura);
    }

    public Double findRubroxfac(Long idfactura) {
        return dao.findSuma(idfactura);
    }

    public Double getSumaRubros(Long idfactura) {
        return dao.sumaRubros(idfactura);
    }

    public List<RubroxfacI> getByFechaCobro(Date d, Date h) {
        return dao.getByFechaCobro(d, h);
    }

    public List<Rubroxfac> findByFecha(Date d, Date h) {
        return dao.findByFecha(d, h);
    }

    public List<Rubroxfac> findSinCobroRF(Long cuenta) {
        return dao.findSinCobroRF(cuenta);
    }

    // Rubros de una Planilla
    public List<Rubroxfac> getByIdfactura(Long idfactura) {
        return dao.findByIdfactura(idfactura);
    }

    // Rubros de una Planilla
    public List<Rubroxfac> getByIdfactura1(Long idfactura) {
        return dao.findByIdfactura1(idfactura);
    }

    // Campos Rubro.descripcion y rubroxfac.valorunitario de una Planilla
    public List<Object[]> findRubros(Long idFactura) {
        return dao.findRubros(idFactura);
    }

    // Movimientos de un Rubro
    public List<Rubroxfac> getByIdrubro(Long idrubro) {
        return dao.findByIdrubro(idrubro);
    }

    // Multa de una Factura
    public boolean getMulta(Long idfactura) {
        return dao.findMulta(idfactura);
    }

    // Recaudacion diaria - Total por Rubros (Todos)
    public List<Object[]> getRubroTotalsByFechaCobro(LocalDate fechaCobro) {
        return dao.findRubroTotalByRubroxfacAndFechacobro(fechaCobro);
    }

    // Recaudacion diaria - Total por Rubros (Desde Facturas) A.Anterior
    public List<Object[]> totalRubrosAnteriorRangos(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta) {
        List<Object[]> resultados = dao.totalRubrosAnteriorRangos(d_fecha, h_fecha, hasta);
        return resultados;
    }

    // Recaudacion diaria - Total por Rubros (Desde Facturas) Año actual
    public List<Object[]> totalRubrosActualRangos(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta) {
        List<Object[]> resultados = dao.totalRubrosActualRangos(d_fecha, h_fecha, hasta);
        return resultados;
    }

    // Recaudacion diaria - Total por Rubros (Desde Facturas) A.Anterior
    public List<Object[]> totalRubrosAnteriorByRecaudador(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta,
                                                          Long idrecaudador) {
        List<Object[]> resultados = dao.totalRubrosAnteriorByRecaudador(d_fecha, h_fecha, hasta, idrecaudador);
        return resultados;
    }

    // Recaudacion diaria - Total por Rubros (Desde Facturas) Año actual
    public List<Object[]> totalRubrosActualByRecaudador(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta,
                                                        Long idrecaudador) {
        List<Object[]> resultados = dao.totalRubrosActualByRecaudador(d_fecha, h_fecha, hasta, idrecaudador);
        return resultados;
    }

    // Recaudacion diaria - Total por Rubros (Desde Facturas) A.Anterior
    public List<Object[]> totalRubrosAnterior(LocalDate fecha, LocalDate hasta) {
        List<Object[]> resultados = dao.totalRubrosAnterior(fecha, hasta);
        return resultados;
    }

    // Recaudacion diaria - Total por Rubros (Desde Facturas) Año actual
    public List<Object[]> totalRubrosActual(LocalDate fecha, LocalDate hasta) {
        List<Object[]> resultados = dao.totalRubrosActual(fecha, hasta);
        return resultados;
    }

    // Grabar
    @Async
    public <S extends Rubroxfac> S save(S entity) {
        Long idFactura = entity.getIdfactura_facturas().getIdfactura();
        Long idRubro = entity.getIdrubro_rubros().getIdrubro();
        // Buscar rubros existentes para la factura y rubro específicos
        List<Rubroxfac> rxfList = dao.getOneFxR(idFactura, idRubro);

        if (rxfList.isEmpty() || rxfList.size() == 0 || rxfList == null) {
            if (entity.getValorunitario() == null) {
                entity.setValorunitario(new BigDecimal(0));
            }
            return dao.save(entity);
        } else {
            // Eliminar duplicados, manteniendo solo el primero
            rxfList.stream().skip(1).forEach(duplicado -> {
                if (dao.existsById(duplicado.getIdrubroxfac())) {
                    dao.deleteById(duplicado.getIdrubroxfac());
                } else {
                    System.out.println("El registro ya fue eliminado o no existe: " + duplicado.getIdrubroxfac());
                }
            });

            Rubroxfac existente = rxfList.get(0);
            // Actualización lógica según el caso
            if (idRubro == 5) {
                if (existente.getValorunitario() != null
                        && !existente.getValorunitario().equals(entity.getValorunitario())) {
                    existente.setValorunitario(existente.getValorunitario().add(entity.getValorunitario()));

                }
            } else {
                if (entity.getValorunitario() == null) {

                    entity.setValorunitario(new BigDecimal(0));
                }
                if (existente.getValorunitario() == null) {
                    existente.setValorunitario(new BigDecimal(0));
                }

                existente.setValorunitario(entity.getValorunitario());
            }
            return (S) dao.save(existente);

        }
    }

    public BigDecimal getTotalInteres(Long idfactura) {
        return dao.getTotalInteres(idfactura);
    }

    public Optional<Rubroxfac> findById(Long id) {
        return dao.findById(id);
    }

    public List<Object[]> getIva(BigDecimal iva, Long idfactura) {
        return dao.getIva(iva, idfactura);
    }

    /* FACTURACIÓN ELECTRONICA */
    public List<Rubroxfac> getRubrosByFactura(Long idfactura) {
        return dao.getRubrosByFactura(idfactura);
    }

    public List<RubroxfacIReport> getRubrosByAbonado(Long idabonado) {
        return dao.getRubrosByAbonado(idabonado);
    }

    /* CONSULTAR MULTAS POR FACTURA */
    public List<Rubroxfac> getMultaByIdFactura(Long idfactura) {
        return dao.getMultaByIdFactura(idfactura);
    }

    /* REPORTE DE CARTERA VENCIDA POR RUBROS */
    public List<CarteraVencidaRubros_int> getCarteraVencidaxRubros(Date fechacobro) {
        return dao.getCarteraVencidaxRubros(fechacobro);
    }

    public List<RubroxfacI> getRubrosForRemisiones(Long idcliente, LocalDate topefecha) {
        return dao.getRubrosForRemisiones(idcliente, topefecha);
    }
}
