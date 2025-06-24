package com.erp.sri_files.repositories;


import com.erp.sri_files.interfaces.TotalSinImpuestos;
import com.erp.sri_files.models.FacturaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FacturaDetalleR extends JpaRepository<FacturaDetalle, Long> {
    @Query(value = "select sum(cantidad * preciounitario) as totalsinimpuestos, sum (descuento) as descuento from fec_factura_detalles where idfactura = ?1", nativeQuery = true)
    TotalSinImpuestos getTotalSinImpuestos(Long idfactura);
}