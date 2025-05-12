package com.erp.sri.service;

import com.erp.sri.interfaces.Abonado_int;
import com.erp.sri.interfaces.Factura_int;
import com.erp.sri.model.Factura_interes;
import com.erp.sri.repository.Abonado_rep;
import com.erp.sri.repository.Facturas_rep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class Abonado_ser {
    @Autowired
    private Abonado_rep a_dao;
    @Autowired
    private Facturas_rep f_dao;
    @Autowired
    private Interes_ser s_interes;
    @Autowired
    private Impuestos_ser s_impuestos;

   public List<Factura_interes> findSinCobrarByAbonado(Long idabonado) {
       // Buscamos el cliente en la entidad Abonado_int.
       Abonado_int abonado = a_dao.findClienteInAbonado(idabonado);
       List<Factura_int> facturas = new ArrayList<>();
       List<Factura_interes> _f = new ArrayList<>();
       if (abonado != null) {
           // Si el cliente y el responsable son la misma persona
           if (Objects.equals(abonado.getCliente(), abonado.getResponsable())) {
               List<Factura_int> facturaConsumo = f_dao.findSinCobrarByCuenta(idabonado) ;
               List<Factura_int> facturaNotConsumo = f_dao.findSincobroNotConsumo(abonado.getResponsable());
               facturas.addAll(facturaNotConsumo);
               facturas.addAll(facturaConsumo);
              _f = addInteresToFactura(facturas);

           } else {
               // Si el cliente y el responsable son diferentes
               List<Factura_int> facturasCliente = f_dao.findSinCobrar(abonado.getCliente());
               List<Factura_int> facturasResponsable = f_dao.findSinCobrar(abonado.getResponsable());
               // Agregar las facturas de ambos
               facturas.addAll(facturasCliente);
               facturas.addAll(facturasResponsable);
              _f = addInteresToFactura(facturas);

           }
       } else {
           System.out.println("Abonado no encontrado con el ID: " + idabonado);
       }
       return _f;
   }
    public List<Factura_interes> addInteresToFactura(List<Factura_int> facturas) {
        return facturas.stream()
                .map(f -> {
                    Long idFactura = f.getIdfactura();
                    BigDecimal _iva = s_impuestos.calcularIva(idFactura);
                    BigDecimal interes = BigDecimal.valueOf(0.00);
                    Factura_interes _facInteres = new Factura_interes();
                    if(f.getSwcondonar()==null|| !f.getSwcondonar()){
                        interes = (s_interes.interesOfFactura(idFactura));  // Asegúrate que este método devuelve double
                        BigDecimal interesRounded = interes.setScale(2, BigDecimal.ROUND_HALF_UP);
                        _facInteres.setInteresacobrar(interesRounded);  // Convertir double a BigDecimal
                    }else{
                        _facInteres.setInteresacobrar(BigDecimal.valueOf(0.00));  // Convertir double a BigDecimal

                    }

                    DecimalFormat df = new DecimalFormat("#.##"); // Round to 2 decimal places
                    BigDecimal totalRounded = f.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal impuestoRounded = _iva.setScale(2, BigDecimal.ROUND_HALF_UP);
                    // Obtener el interés relacionado con la factura
                    _facInteres.setIdfactura(idFactura);
                    _facInteres.setFeccrea(f.getFeccrea());
                    _facInteres.setEstado(f.getEstado());
                    _facInteres.setIdmodulo(f.getIdmodulo());
                    _facInteres.setTotal(totalRounded);
                    _facInteres.setIdcliente(f.getIdCliente());
                    _facInteres.setIdabonado(f.getIdAbonado());
                    _facInteres.setFormapago(f.getFormaPago());
                    _facInteres.setPagado(f.getPagado());
                    _facInteres.setSwcondonar(f.getSwcondonar());
                    _facInteres.setFechacobro(f.getfechacobro());
                    _facInteres.setUsuariocobro(f.getUsuariocobro());
                    _facInteres.setNrofactura(f.getNrofactura());
                    _facInteres.setSwiva(impuestoRounded);
                    _facInteres.setNombre(f.getNombre());
                    return _facInteres;  // Transformamos la Factura_int en Factura_interes
                })
                .collect(Collectors.toList());  // Recolectamos el resultado en una lista
    }
    public static double round(double value, int places) {
        double factor = Math.pow(10, places);
        return Math.round(value * factor) / factor;
    }

}
