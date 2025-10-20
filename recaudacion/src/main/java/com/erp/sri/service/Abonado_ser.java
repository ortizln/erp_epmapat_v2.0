package com.erp.sri.service;

import com.erp.sri.DTO.FacturaDTO;
import com.erp.sri.interfaces.Abonado_int;
import com.erp.sri.interfaces.Factura_int;
import com.erp.sri.model.Factura_interes;
import com.erp.sri.model.Tmpinteresxfac;
import com.erp.sri.repository.Abonado_rep;
import com.erp.sri.repository.Facturas_rep;
import com.erp.sri.repository.TmpinteresxfacR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Abonado_ser {
    @Autowired
    private Abonado_rep a_dao;
    @Autowired
    private Facturas_rep f_dao;
    @Autowired
    private Impuestos_ser s_impuestos;

    public List<FacturaDTO> findSinCobrarByAbonado(Long idabonado) {
        Abonado_int abonado = a_dao.findClienteInAbonado(idabonado);
        if (abonado == null) {
            System.out.println("Abonado no encontrado con el ID: " + idabonado);
            return Collections.emptyList();
        }

        List<Factura_int> facturas = new ArrayList<>();

        if (Objects.equals(abonado.getCliente(), abonado.getResponsable())) {
            facturas.addAll(f_dao.findSinCobrarByCuenta(idabonado));
            facturas.addAll(f_dao.findSincobroNotConsumo(abonado.getResponsable()));
        } else {
            facturas.addAll(f_dao.findSinCobrar(abonado.getCliente()));
            facturas.addAll(f_dao.findSinCobrar(abonado.getResponsable()));
        }

        // 👇 convertimos a DTOs
        List<FacturaDTO> facturasDTO = facturas.stream()
                .map(f -> {
                    FacturaDTO dto = new FacturaDTO();
                    dto.setIdfactura(f.getIdfactura());
                    dto.setIdmodulo(f.getIdmodulo());
                    dto.setTotal(f.getTotal());
                    dto.setIdcliente(f.getIdcliente());
                    dto.setIdabonado(f.getIdabonado());
                    dto.setFeccrea(f.getFeccrea());
                    dto.setFormaPago(f.getFormaPago());
                    dto.setEstado(f.getEstado());
                    dto.setPagado(f.getPagado());
                    dto.setSwcondonar(f.getSwcondonar());
                    dto.setFechacobro(f.getfechacobro());
                    dto.setUsuariocobro(f.getUsuariocobro());
                    dto.setNrofactura(f.getNrofactura());
                    dto.setNombre(f.getNombre());
                    dto.setInteres(f.getInteres());
                    dto.setDireccion(f.getDireccion());
                    dto.setModulo(f.getModulo());

                    // 👇 calcular IVA
                    BigDecimal iva = s_impuestos.calcularIva(f.getIdfactura());
                    dto.setIva(iva);

                    return dto;
                })
                .collect(Collectors.toList());

        return facturasDTO;
    }

}
