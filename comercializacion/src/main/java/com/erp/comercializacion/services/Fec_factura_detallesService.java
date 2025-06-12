package com.erp.comercializacion.services;
import java.util.List;

import com.erp.comercializacion.models.Fec_factura_detalles;
import com.erp.comercializacion.repositories.Fec_factura_detallesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Fec_factura_detallesService {
	@Autowired
	private Fec_factura_detallesR dao;

	public List<Fec_factura_detalles> findAll() {
		return dao.findAll();
	}

	public <S extends Fec_factura_detalles> S save(S entity) {
		return dao.save(entity);
	}

	public List<Fec_factura_detalles> findFecDetalleByIdFactura(Long idfactura) {
		return dao.getFecDetalleByIdFactura(idfactura);
	}
}
