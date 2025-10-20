package com.erp.servicio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.erp.utils.InteresUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.erp.DTO.ValorFactDTO;
import com.erp.interfaces.FacIntereses;
import com.erp.modelo.Intereses;
import com.erp.repositorio.InteresesR;

import java.time.*;

@Service
public class InteresServicio {

	@Autowired
	private InteresesR dao;
	@Autowired
	private FacturaServicio s_factura;
	@Autowired
	private LecturaServicio s_lectura;
    @Autowired
    private InteresRepositoryAdapter repo;

	public List<Intereses> findAll() {
		return dao.findAll();
	}

	public List<Intereses> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	public List<Intereses> findByAnioMes(Number anio, Number mes) {
		return dao.findByAnioMes(anio, mes);
	}

	public List<Intereses> findUltimo() {
		return dao.findUltimo();
	}

	public <S extends Intereses> S save(S entity) {
		return dao.save(entity);
	}

	public Optional<Intereses> findById(Long id) {
		return dao.findById(id);
	}

	public void deleteById(Long id) {
		dao.deleteById(id);
	}

	public void delete(Intereses entity) {
		dao.delete(entity);
	}

	public Object facturaid(Long idfactura) {
		List<FacIntereses> factura = s_lectura.getForIntereses(idfactura);

		if (factura.isEmpty()) {
			factura = s_factura.getForIntereses(idfactura);
		}
		// Variable para almacenar el interés total de todas las facturas
		final double[] totalInteres = { 0.0 };
		// Uso de Java Streams para mapear la lista
		factura.stream().forEach(_factura -> {
			// Convertir la fecha de creación a LocalDate
			LocalDate fecInicio;
			if (_factura.getFormapago() == 4) {
				fecInicio = _factura.getFechatransferencia();

			} else {
				fecInicio = _factura.getFeccrea();

			}
			// LocalDate fecInicio = LocalDate.parse(_factura.getFeccrea());
			LocalDate fecFinal = LocalDate.now();
			int anioI = fecInicio.getYear();
			int anioF = fecFinal.getYear();
			int mesF = fecFinal.getMonthValue();
			List<Float> todosPorcentajes = new ArrayList<>();
			if (anioI < anioF) {
				int mesI = fecInicio.getMonthValue();
				if (mesI == 12 && mesF == 1 && anioI + 1 == anioF) {
				} else {
					while (anioI <= anioF) {
						if (anioI < anioF) {
							List<Float> porcentaje = dao.porcentajes(anioI, mesI, 12);
							todosPorcentajes.addAll(porcentaje); // Añadir los porcentajes a la lista total
						} else if (anioI == anioF) {
							List<Float> porcentaje = new ArrayList<>(); // Inicializa la lista
							if (fecInicio.getMonthValue() == (fecFinal.getMonthValue() - 1)) {
								porcentaje.add(0.00f);
							} else {
								porcentaje = dao.porcentajes(anioF, 1, fecFinal.getMonthValue() - 2);
								todosPorcentajes.addAll(porcentaje);
							}
						}
						mesI = 1;
						anioI++;
					}
				}

			} else if (anioF < anioI) {
			} else {
				List<Float> porcentaje = new ArrayList<>(); // Inicializa la lista
				if (fecInicio.getMonthValue() == (fecFinal.getMonthValue() - 1)) {
					porcentaje.add(0.00f);
				} else {

					porcentaje = dao.porcentajes(fecFinal.getYear(), fecInicio.getMonthValue(),
							fecFinal.getMonthValue() - 2);
					todosPorcentajes.addAll(porcentaje);
				}
			}
			todosPorcentajes.forEach(interes -> {
				double interesCalculado = (interes * (_factura.getSuma() + totalInteres[0])) / 100;
				totalInteres[0] += interesCalculado; // Sumar al interés total
			});
		});
		return totalInteres[0];
	}
    public InteresRepositoryAdapter getRepoAdapter() { return this.repo; }


    public Object interesToFactura(ValorFactDTO factura) {

		// Variable para almacenar el interés total de todas las facturas
		final double[] totalInteres = { 0.0 };
		// Uso de Java Streams para mapear la lista
		//factura.stream().forEach(_factura -> {});
			// Convertir la fecha de creación a LocalDate
			LocalDate fecInicio;
			if (factura.getFormapago() == 4) {
				fecInicio = factura.getFectransferencia();

			} else {
				fecInicio = (factura.getFeccrea());

			}
			// LocalDate fecInicio = LocalDate.parse(_factura.getFeccrea());
			LocalDate fecFinal = LocalDate.now();
			int anioI = fecInicio.getYear();
			int anioF = fecFinal.getYear();
			int mesF = fecFinal.getMonthValue();
			List<Float> todosPorcentajes = new ArrayList<>();
			if (anioI < anioF) {
				int mesI = fecInicio.getMonthValue();
				if (mesI == 12 && mesF == 1 && anioI + 1 == anioF) {
				} else {
					while (anioI <= anioF) {
						if (anioI < anioF) {
							List<Float> porcentaje = dao.porcentajes(anioI, mesI, 12);
							todosPorcentajes.addAll(porcentaje); // Añadir los porcentajes a la lista total
						} else if (anioI == anioF) {
							List<Float> porcentaje = new ArrayList<>(); // Inicializa la lista
							if (fecInicio.getMonthValue() == (fecFinal.getMonthValue() - 1)) {
								porcentaje.add(0.00f);
							} else {
								porcentaje = dao.porcentajes(anioF, 1, fecFinal.getMonthValue() - 2);
								todosPorcentajes.addAll(porcentaje);
							}
						}
						mesI = 1;
						anioI++;
					}
				}

			} else if (anioF < anioI) {
			} else {
				List<Float> porcentaje = new ArrayList<>(); // Inicializa la lista
				if (fecInicio.getMonthValue() == (fecFinal.getMonthValue() - 1)) {
					porcentaje.add(0.00f);
				} else {

					porcentaje = dao.porcentajes(fecFinal.getYear(), fecInicio.getMonthValue(),
							fecFinal.getMonthValue() - 2);
					todosPorcentajes.addAll(porcentaje);
				}
			}
			todosPorcentajes.forEach(interes -> {
				double interesCalculado = (interes * (factura.getSubtotal() + totalInteres[0])) / 100;
				totalInteres[0] += interesCalculado; // Sumar al interés total
			});
		
		return totalInteres[0];
	}

    public BigDecimal calcularInteresFactura(Long idfactura, LocalDate hoy) {
        List<FacIntereses> factura = s_lectura.getForIntereses(idfactura);
        if (factura.isEmpty()) factura = s_factura.getForIntereses(idfactura);
        if (factura == null || factura.isEmpty()) return BigDecimal.ZERO;

        BigDecimal total = BigDecimal.ZERO;

        for (FacIntereses f : factura) {
            LocalDate fecInicio = (f.getFormapago() == 4 ? f.getFechatransferencia() : f.getFeccrea());
            if (fecInicio == null) continue;

            BigDecimal principal = InteresUtils.bd(f.getSuma());

            YearMonth ymStart = YearMonth.from(fecInicio);
            YearMonth ymEnd   = YearMonth.from(hoy).minusMonths(1); // hasta mes anterior a hoy

            if (ymStart.isAfter(ymEnd)) continue;

            List<YearMonth> meses = InteresUtils.range(ymStart, ymEnd);
            Map<YearMonth, BigDecimal> pctMap = repo.getPorcentajesPorRango(meses.get(0), meses.get(meses.size() - 1));

            List<BigDecimal> ratios = new ArrayList<>(meses.size());
            for (YearMonth ym : meses) {
                BigDecimal pct = pctMap.getOrDefault(ym, BigDecimal.ZERO); // % mensual
                ratios.add(InteresUtils.pctToRatio(pct));                   // a razón
            }

            BigDecimal interes = InteresUtils.compoundInterest(principal, ratios);
            total = total.add(interes, InteresUtils.MC);
        }

        return total.setScale(InteresUtils.SCALE_MONEY, java.math.RoundingMode.HALF_UP);
    }

}
