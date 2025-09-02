package com.erp.comercializacion.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.models.Fec_factura;
import com.erp.comercializacion.repositories.Fec_facturaR;

@Service
public class Fec_facturaService {

   @Autowired
   private Fec_facturaR dao;

   public List<Fec_factura> findAll() {
      return dao.findAll();
   }

   public List<Fec_factura> findByEstado(String estado, Long limit) {
      return dao.findByEstado(estado, limit);
   }

   public List<Fec_factura> findByCuenta(String referencia) {
      return dao.findByCuenta(referencia);
   }

   public List<Fec_factura> findByNombreCliente(String cliente) {
      return dao.findByNombreCliente(cliente);
   }

   public <S extends Fec_factura> S save(S entity) {
      return dao.save(entity);
   }

   public Optional<Fec_factura> findById(Long id) {
      return dao.findById(id);
   }

/*    public fecFacturaDatos getNroFactura(Long idfactura) {
      return dao.getNroFactura(idfactura);
   } */
}
