package com.erp.controlador;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.erp.interfaces.*;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.erp.DTO.RemiDTO;
import com.erp.DTO.ValorFactDTO;
import com.erp.config.jasperConfig.ReportRequest;
import com.erp.excepciones.ResourceNotFoundExcepciones;
import com.erp.modelo.Facturas;
import com.erp.modelo.administracion.ReporteModelDTO;
import com.erp.reportes.facturas.interfaces.i_ReporteFacturasCobradas_G;
import com.erp.servicio.FacturaServicio;
import com.erp.servicio.RubroxfacServicio;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/facturas")
@CrossOrigin(origins = "*")
public class FacturasApi {

	@Autowired
	private FacturaServicio facServicio;
	@Autowired
	private RubroxfacServicio rxfServicio;
	@Autowired
	private i_ReporteFacturasCobradas_G i_reportefacturascobradas_g;

	@GetMapping
	public List<Facturas> getAll(@Param(value = "desde") Long desde, @Param(value = "hasta") Long hasta,
			@Param(value = "idcliente") Long idcliente, @Param(value = "limit") Long limit) {
		if (desde != null)
			return facServicio.findDesde(desde, hasta);
		else {
			if (idcliente != null) {
				return facServicio.findByIdcliente(idcliente, limit);
			} else
				return facServicio.findAll();
		}
	}

	@GetMapping("/validador/{codrecaudador}")
	public ResponseEntity<Facturas> validarUltimaFactura(@PathVariable String codrecaudador) {
		Facturas factura = facServicio.validarUltimafactura(codrecaudador);
		return ResponseEntity.ok(factura);
	}

	@GetMapping("/reportes/individual")
	public ResponseEntity<List<Facturas>> getByUsucobro(@RequestParam Long idusuario,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dfecha,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date hfecha) {
		List<Facturas> facturas = facServicio.findByUsucobro(idusuario, dfecha, hfecha);
		if (!facturas.isEmpty()) {

			return ResponseEntity.ok(facturas);
		} else {
			return ResponseEntity.noContent().build();
		}
	}

	@GetMapping("/reportes/fechacobro")
	public ResponseEntity<List<FacturasI>> getByFechacobro(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechacobro) {
		List<FacturasI> facturas = facServicio.findByFechacobro(fechacobro);
		if (!facturas.isEmpty()) {

			return ResponseEntity.ok(facturas);
		} else {
			return ResponseEntity.noContent().build();
		}
	}

	@GetMapping("/idabonado/{idabonado}")
	public List<Facturas> getByIdabonado(@PathVariable Long idabonado) {
		return facServicio.findByIdabonado(idabonado);
	}

	@GetMapping("/abonado/{idabonado}/{limit}")
	public List<Facturas> getByIdabonadoLimit(@PathVariable long idabonado,
			@PathVariable Long limit) {
		return facServicio.findByIdabonadoLimit(idabonado, limit);
	}

	// Una Planilla (como lista)
	@GetMapping("/planilla")
	public ResponseEntity<List<Facturas>> buscarPlanilla(@Param(value = "idfactura") Long idfactura) {
		List<Facturas> x = facServicio.buscarPlanilla(idfactura);
		if (x.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(x);
	}

	// Planillas por Abonado y Fecha
	@GetMapping("/porabonado")
	public ResponseEntity<List<Facturas>> buscarPorAbonadoYFechaCreacionRange(
			@Param(value = "idabonado") Long idabonado,
			@Param("fechaDesde") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaDesde,
			@Param("fechaHasta") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaHasta) {
		List<Facturas> x = facServicio.buscarPorAbonadoYFechaCreacionRange(idabonado, fechaDesde, fechaHasta);
		if (x.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(x);
	}

	@GetMapping("/{idfactura}")
	public ResponseEntity<Facturas> getById(@PathVariable Long idfactura) {
		Facturas x = facServicio.findById(idfactura)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe la Factura  Id: " + idfactura)));
		return ResponseEntity.ok(x);
	}

	// Planillas sin cobro de un Cliente
	@GetMapping("/idcliente/{idcliente}")
	public List<Facturas> getSinCobro(@PathVariable Long idcliente) {
		return facServicio.findSinCobro(idcliente);
	}

	@GetMapping("/factCarteraVencida")
	public List<CVFacturasNoConsumo> SinCobroOfCV(@RequestParam Long idcliente,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		return facServicio.SinCobroOfCV(idcliente, date);
	}

	/* sincobro v-2.0 */
	@GetMapping("/facSincobrar")
	public List<FacSinCobrar> findFacSincobro(@RequestParam Long idcliente) {
		return facServicio.findFacSincobro(idcliente);
	}

	@GetMapping("/facSincobrar/cuenta")
	public List<FacSinCobrar> findFacSincobroByCuetna(@RequestParam Long cuenta) {
		return facServicio.findFacSincobroByCuetna(cuenta);
	}

		@GetMapping("/byCuenta")
	public List<FacSinCobrar> findByCuenta(@RequestParam Long cuenta) {
		return facServicio.findByCuenta(cuenta);
	}

	@GetMapping("/sincobrar/cuenta")
	public List<FacSinCobrar> findSincobroByCuetna(@RequestParam Long cuenta) {
		return facServicio.findSincobroByCuetna(cuenta);
	}

	// IDs de las Planillas sin cobrar de un Abonado
	@GetMapping("/sincobro")
	public List<Long> getSinCobroAbo(@Param(value = "idabonado") Long idabonado) {
		return facServicio.findSinCobroAbo(idabonado);
	}

	// Planillas sin cobrar de un Abonado
	@GetMapping("/sincobrarAbo")
	public List<Facturas> getSinCobrarAbo(@Param(value = "idmodulo") Long idmodulo,
			@Param(value = "idabonado") Long idabonado) {
		return facServicio.findSinCobrarAbo(idmodulo, idabonado);
	}

	// Planillas sin cobrar de un Abonado
	@GetMapping("/sincobrarAboMod")
	public List<Facturas> getSinCobrarAboMod(@Param(value = "idabonado") Long idabonado) {
		return facServicio.findSinCobrarAboMod(idabonado);
	}

	@GetMapping("/sincobrarAboMod/count")
	public Long getCountSinCobrarAbo(@Param(value = "idabonado") Long idabonado) {
		return facServicio.countSinCobrarAbo(idabonado);
	}

	// Cuenta las Planillas pendientes de un Abonado
	@GetMapping("/pendientesabonado")
	public ResponseEntity<Long> getCantidadFacturasPendientes(@Param(value = "idabonado") Long idabonado) {
		return ResponseEntity.ok(facServicio.getCantidadFacturasByAbonadoAndPendientes(idabonado));
	}

	@GetMapping("/f_abonado/{idabonado}")
	public List<Facturas> getFacturaByAbonado(@PathVariable Long idabonado) {
		return facServicio.findByIdFactura(idabonado);
	}

	// Planilla por nrofactura
	@GetMapping("/nrofactura")
	public List<Facturas> getByNrofactura(@Param(value = "nrofactura") String nrofactura) {
		return facServicio.findByNrofactura(nrofactura);
	}

	// Recaudacion diaria - Facturas cobradas <Facturas>
	// @GetMapping("/cobradas")
	// public List<Facturas> findByFechacobro(@Param("fecha")
	// @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
	// return facServicio.findByFechacobro(fecha);
	// }

	// Recaudacion diaria - Facturas cobradas <Facturas>
	@GetMapping("reportes/cobradastotrangos")
	public List<RepFacGlobal> findByFechacobroTotRangos(
			@Param("d_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d_fecha,
			@Param("h_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h_fecha) {
		return facServicio.findByFechacobroTotRangos(d_fecha, h_fecha);
	}

	// Recaudacion diaria - Facturas cobradas <Facturas>
	@GetMapping("/reportes/cobradastot")
	public List<RepFacGlobal> findByFechacobroTotByRecaudador(
			@Param("d_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d_fecha,
			@Param("h_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h_fecha,
			@Param("idrecaudador") Long idrecaudador) {
		return facServicio.findByFechacobroTotByRecaudador(d_fecha, h_fecha, idrecaudador);
	}

	// Recaudacion diaria - Facturas cobradas <Facturas>
	@GetMapping("reportes/totalformacobrorangos")
	public List<Object[]> totalFechaFormacobroRangos(
			@Param("d_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d_fecha,
			@Param("h_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h_fecha) {
		return facServicio.totalFechaFormacobroRangos(d_fecha, h_fecha);
	}

	@GetMapping("/reportes/totalformacobro")
	public List<Object[]> totalFechaFormacobroByRecaudador(
			@Param("d_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d_fecha,
			@Param("h_fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h_fecha,
			@Param("idrecaudador") Long idrecaudador) {

		return facServicio.totalFechaFormacobroByRecaudador(d_fecha, h_fecha, idrecaudador);
	}

	// Recaudacion diaria - Facturas cobradas <Facturas>
	@GetMapping("/cobradastot")
	public List<RepFacGlobal> findByFechacobroTot(
			@Param("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
		return facServicio.findByFechacobroTot(fecha);
	}

	// Recaudacion diaria - Facturas cobradas <Facturas>
	@GetMapping("/totalformacobro")
	public List<Object[]> totalFechaFormacobro(
			@Param("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
		return facServicio.totalFechaFormacobro(fecha);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Facturas saveFacturas(@RequestBody Facturas x) {
		return facServicio.save(x);
	}

	@PutMapping("/{idfactura}")
	public ResponseEntity<Facturas> updateFacturas(@PathVariable long idfactura, @RequestBody Facturas x) {

		LocalTime hora;
		Facturas y = facServicio.findById(idfactura)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe esa factura con ese id" + idfactura));
		BigDecimal interes = rxfServicio.getTotalInteres(idfactura);
		if (interes == null) {
			y.setInterescobrado(x.getInterescobrado());
		} else {
			y.setInterescobrado(interes.add(x.getInterescobrado()));
		}
		if (x.getHoracobro() != null) {
			hora = LocalTime.parse(x.getHoracobro().toString()); // Esto funciona
		} else {
			hora = null;
		}

		y.setConveniopago(x.getConveniopago());
		y.setEstado(x.getEstado());
		y.setEstadoconvenio(x.getEstadoconvenio());
		y.setFechaanulacion(x.getFechaanulacion());
		y.setFechacobro(x.getFechacobro());
		y.setFechaconvenio(x.getFechaconvenio());
		y.setFechaeliminacion(x.getFechaeliminacion());
		y.setFechatransferencia(x.getFechatransferencia());
		y.setFormapago(x.getFormapago());
		y.setHoracobro(hora);
		y.setIdabonado(x.getIdabonado());
		y.setIdcliente(x.getIdcliente());
		y.setIdmodulo(x.getIdmodulo());
		y.setNrofactura(x.getNrofactura());
		y.setPagado(x.getPagado());
		y.setPorcexoneracion(x.getPorcexoneracion());
		y.setRazonanulacion(x.getRazonanulacion());
		y.setRazoneliminacion(x.getRazoneliminacion());
		y.setRazonexonera(x.getRazonexonera());
		y.setRefeformapago(x.getRefeformapago());
		y.setTotaltarifa(x.getTotaltarifa());
		y.setUsuarioanulacion(x.getUsuarioanulacion());
		y.setUsuariocobro(x.getUsuariocobro());
		y.setUsuarioeliminacion(x.getUsuarioeliminacion());
		y.setUsuariotransferencia(x.getUsuariotransferencia());
		y.setUsucrea(x.getUsucrea());
		y.setFeccrea(x.getFeccrea());
		y.setSwiva(x.getSwiva());
		y.setSecuencialfacilito(x.getSecuencialfacilito());
		// y.setFeccrea(x.getFeccrea());

		y.setUsumodi(x.getUsumodi());
		y.setFecmodi(x.getFecmodi());

		y.setValorbase(x.getValorbase());
		y.setSwcondonar(x.getSwcondonar());
		y.setValornotacredito(x.getValornotacredito());
		Facturas updateFacturas = facServicio.save(y);
		return ResponseEntity.ok(updateFacturas);
	}

	/*
	 * ============================== *********REPORTES*************
	 * ==============================
	 */

	@PostMapping(value = "/generate-custom", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> generateCustomReport(@RequestBody ReportRequest request) {

		if (request.getReportName() == null || request.getReportName().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de reporte requerido");
		}

		try (InputStream jrxmlStream = getClass()
				.getResourceAsStream("/reports/" + request.getReportName() + ".jrxml")) {

			if (jrxmlStream == null) {
				throw new FileNotFoundException("Reporte no encontrado: " + request.getReportName());
			}

			JasperReport report = JasperCompileManager.compileReport(jrxmlStream);

			JasperPrint jasperPrint = JasperFillManager.fillReport(
					report,
					request.getParameters() != null ? request.getParameters() : new HashMap<>(),
					new JRBeanCollectionDataSource(request.getData() != null ? request.getData() : List.of()));

			ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, pdfStream);

			return ResponseEntity.ok()
					.header("Content-Disposition", "attachment; filename=\"" + request.getReportName() + ".pdf\"")
					.contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(new ByteArrayInputStream(pdfStream.toByteArray())));

		} catch (Exception e) {
			throw new ResponseStatusException(
					HttpStatus.INTERNAL_SERVER_ERROR,
					"Error generando reporte: " + e.getMessage(),
					e);
		}
	}

	@GetMapping("/reportes/facturascobradas")
	public ResponseEntity<Resource> reporteFacturasCobradas(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_dfecha,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_hfecha)
			throws JRException, IOException, SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("v_dfecha", v_dfecha);
		params.put("v_hfecha", v_hfecha);
		params.put("fileName", "facturasCobradas");

		ReporteModelDTO dto = i_reportefacturascobradas_g.obtenerFacturasCobradas_G(params);
		InputStreamResource streamResource = new InputStreamResource(dto.getStream());
		MediaType mediaType = null;
		mediaType = MediaType.APPLICATION_PDF;

		return ResponseEntity.ok().header("Content-Disposition", "inline; filename=\"" + dto.getFileName() + "\"")
				.contentLength(dto.getLength()).contentType(mediaType).body(streamResource);
	}

	@GetMapping("/reportes/facturascobradascaja")
	public ResponseEntity<Resource> reporteFacturasCobradasCaja(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_dfecha,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_hfecha,
			@RequestParam Long usuariocobro) throws JRException, IOException, SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("v_dfecha", v_dfecha);
		params.put("v_hfecha", v_hfecha);
		params.put("usuariocobro", usuariocobro);
		params.put("fileName", "facturasCobradasCaja");

		ReporteModelDTO dto = i_reportefacturascobradas_g.obtenerFacturasCobradas_G(params);
		InputStreamResource streamResource = new InputStreamResource(dto.getStream());
		MediaType mediaType = null;
		mediaType = MediaType.APPLICATION_PDF;

		return ResponseEntity.ok().header("Content-Disposition", "inline; filename=\"" + dto.getFileName() + "\"")
				.contentLength(dto.getLength()).contentType(mediaType).body(streamResource);
	}

	@GetMapping("/reportes/facturasrubros")
	public ResponseEntity<Resource> reporteFacturaRubros(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_dfecha,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_hfecha,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date c_feccrea)
			throws JRException, IOException, SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("v_dfecha", v_dfecha);
		params.put("v_hfecha", v_hfecha);
		params.put("c_feccrea", c_feccrea);
		params.put("fileName", "facturasCobradasRubros");
		ReporteModelDTO dto = i_reportefacturascobradas_g.obtenerFacturasCobradas_G(params);
		InputStreamResource streamResource = new InputStreamResource(dto.getStream());
		MediaType mediaType = MediaType.APPLICATION_PDF;
		;
		/*
		 * if (tipo == "excel") { mediaType = MediaType.APPLICATION_OCTET_STREAM; } else
		 * { }
		 */
		// mediaType = MediaType.APPLICATION_PDF;

		return ResponseEntity.ok().header("Content-Disposition", "inline; filename=\"" + dto.getFileName() + "\"")
				.contentLength(dto.getLength()).contentType(mediaType).body(streamResource);
	}

	@GetMapping("/reportes/facturasrubroscaja")
	public ResponseEntity<Resource> reporteFacturaRubrosCaja(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_dfecha,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_hfecha,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date c_feccrea,
			@RequestParam Long usuariocobro) throws JRException, IOException, SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("v_dfecha", v_dfecha);
		params.put("v_hfecha", v_hfecha);
		params.put("c_feccrea", c_feccrea);
		params.put("usuariocobro", usuariocobro);
		params.put("fileName", "facturasCobradasRubrosCaja");
		ReporteModelDTO dto = i_reportefacturascobradas_g.obtenerFacturasCobradas_G(params);
		InputStreamResource streamResource = new InputStreamResource(dto.getStream());
		MediaType mediaType = MediaType.APPLICATION_PDF;
		;
		/*
		 * if (tipo == "excel") { mediaType = MediaType.APPLICATION_OCTET_STREAM; } else
		 * { }
		 */
		// mediaType = MediaType.APPLICATION_PDF;

		return ResponseEntity.ok().header("Content-Disposition", "inline; filename=\"" + dto.getFileName() + "\"")
				.contentLength(dto.getLength()).contentType(mediaType).body(streamResource);
	}

	@GetMapping("/comprobante/pago")
	public ResponseEntity<Resource> comprobantePago(
			@RequestParam Long idfactura) throws JRException, IOException, SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idfactura", idfactura);
		params.put("fileName", "ComprobantePago");
		ReporteModelDTO dto = i_reportefacturascobradas_g.obtenerFacturasCobradas_G(params);
		InputStreamResource streamResource = new InputStreamResource(dto.getStream());
		MediaType mediaType = MediaType.APPLICATION_PDF;
		;
		/*
		 * if (tipo == "excel") { mediaType = MediaType.APPLICATION_OCTET_STREAM; } else
		 * { }
		 */
		// mediaType = MediaType.APPLICATION_PDF;

		return ResponseEntity.ok().header("Content-Disposition", "inline; filename=\"" + dto.getFileName() + "\"")
				.contentLength(dto.getLength()).contentType(mediaType).body(streamResource);
	}

	// FACTURAS ANULACIÓN
	@GetMapping("/anulaciones")
	public ResponseEntity<List<Facturas>> getFacturasAnuladas(@RequestParam Long limit) {
		List<Facturas> facturas = facServicio.fingAllFacturasAnuladas(limit);
		return ResponseEntity.ok(facturas);
	}

	@GetMapping("/anulaciones/fechas")
	public ResponseEntity<List<Facturas>> getFacturasByFecAnulaciones(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
		List<Facturas> facturas = facServicio.findByFecAnulacion(d, h);
		return ResponseEntity.ok(facturas);
	}

	@GetMapping("/cobradas/cliente")
	public ResponseEntity<List<Facturas>> getFacturasAnuladasxac(@RequestParam Long idcliente) {
		List<Facturas> facturas = facServicio.findCobradasByCliente(idcliente);
		return ResponseEntity.ok(facturas);
	}

	// FACTURAS ELIMINACIÓN
	@GetMapping("/eliminaciones")
	public ResponseEntity<List<Facturas>> getFacturasEliminadas(@RequestParam Long limit) {
		List<Facturas> facturas = facServicio.fingAllFacturasEliminadas(limit);
		return ResponseEntity.ok(facturas);
	}

	@GetMapping("/eliminaciones/fechas")
	public ResponseEntity<List<Facturas>> getFacturasByFecEliminacion(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
		List<Facturas> facturas = facServicio.findByFecEliminacion(d, h);
		return ResponseEntity.ok(facturas);
	}

	/* Transferencias cobradas */
	@GetMapping("/transferencias")
	public ResponseEntity<List<R_transferencias>> transferenciasCobradas(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_dfecha,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date v_hfecha) {
		return ResponseEntity.ok(facServicio.transferenciasCobradas(v_dfecha, v_hfecha));
	}

	@GetMapping("/rangofeccobro")
	public ResponseEntity<List<Facturas>> getFechaCobro(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h) {
		return ResponseEntity.ok(facServicio.findFechaCobro(d, h));
	}

	// Cartera de un cliente a una fecha (Facturas)
	@GetMapping("/carteraCliente")
	public List<Facturas> carteraCliente(@Param("idcliente") Long idcliente,
			@Param("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta) {
		return facServicio.carteraCliente(idcliente, hasta);
	}

	// Cartera de un cliente a una fecha (Total, ya suma 1 a los del módulo 3 )
	@GetMapping("/totCarteraCliente")
	public Double totCarteraCliente(@Param("idcliente") Long idcliente,
			@Param("hasta") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hasta) {
		return facServicio.totCarteraCliente(idcliente, hasta);
	}

	/* REPORTE DE FACTURAS ELIMINADAS POR RANGO DE FECHA */
	@GetMapping("/reportes/facturasEliminadas")
	public ResponseEntity<List<RepFacEliminadas>> getEliminadasXfecha(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate h) {
		return ResponseEntity.ok(facServicio.findEliminadasXfecha(d, h));
	}

	/* REPORTE DE FACTURAS anuladas POR RANGO DE FECHA */
	@GetMapping("/reportes/facturasanuladas")
	public ResponseEntity<List<RepFacEliminadas>> findAnuladasXfecha(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate h) {
		return ResponseEntity.ok(facServicio.findAnuladasXfecha(d, h));
	}

	/*
	 * REPORTE DE FACTURAS TRANSFERENCIAS
	 */
	@GetMapping("/reportes/alltransferencias")
	public ResponseEntity<List<FacTransferencias>> getFacAllTransferidas(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h) {
		return ResponseEntity.ok(facServicio.getFacAllTransferidas(d, h));
	}

	@GetMapping("/reportes/pagadastransferencias")
	public ResponseEntity<List<FacTransferencias>> getFacPagadasTransferidas(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h) {
		return ResponseEntity.ok(facServicio.getFacPagadasTransferidas(d, h));
	}

	@GetMapping("/reportes/nopagadastransferencias")
	public ResponseEntity<List<FacTransferencias>> getFacNoPagadasTransferidas(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate d,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate h) {
		return ResponseEntity.ok(facServicio.getFacNoPagadasTransferidas(d, h));
	}

	/* CARTERA VENCIDA POR FACUTAS */
	@GetMapping("/reportes/CV_consumo")
	public ResponseEntity<List<CarteraVencidaFacturas>> findCVByFacturas(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
		return ResponseEntity.ok(facServicio.getCVByFacturasConsumo(fecha));
	}

	@GetMapping("/reportes/CV_noconsumo")
	public ResponseEntity<List<CVFacturasNoConsumo>> getCVByFacturasNoConsumo(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
		return ResponseEntity.ok(facServicio.getCVByFacturasNoConsumo(fecha));
	}

	@GetMapping("/CV_consumo")
	public ResponseEntity<Page<CarteraVencidaFacturas>> findCVByFacturas(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam int size) {
		return ResponseEntity.ok(facServicio.getCVByConsumo(fecha, page, size));
	}

	@GetMapping("/CV_noconsumo")
	public ResponseEntity<Page<CVFacturasNoConsumo>> getCVByFacturasNoConsumo(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
			@RequestParam(defaultValue = "20") int page,
			@RequestParam int size) {
		return ResponseEntity.ok(facServicio.getCVByNoConsumo(fecha, page, size));
	}

	@GetMapping("/remisiones")
	public ResponseEntity<List<RemiDTO>> getFacForRemisiones(@RequestParam Long idcliente,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechatope) {
		return ResponseEntity.ok(facServicio.getFacForRemisiones(idcliente, fechatope));
	}

	@GetMapping("/remisiones/cuenta")
	public ResponseEntity<List<RemiDTO>> getFacForRemisionesAbonados(@RequestParam Long idcliente,
			@RequestParam Long cuenta,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechatope) {
		return ResponseEntity.ok(facServicio.getFacForRemisionesAbonados(idcliente, cuenta, fechatope));
	}

	@PutMapping("/remisionfactura")
	public ResponseEntity<Facturas> updateFactura(@RequestParam Long idfactura, @RequestBody Facturas factura) {
		return ResponseEntity.ok(facServicio.updateFactura(idfactura, factura));
	}

	@GetMapping("/reportes/cv_facxrubro")
	public ResponseEntity<List<CVFacturasNoConsumo>> getCvFacturasByRubro(@RequestParam Long idrubro,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
		return ResponseEntity.ok(facServicio.getCvFacturasByRubro(idrubro, fecha));
	}

	@GetMapping("/sincobrar/cuentas")
	public ResponseEntity<List<ValorFactDTO>> getFacturasSinCobro(@RequestParam Long cuenta) {
		return ResponseEntity.ok(facServicio.findFacturasSinCobro(cuenta));
	}

	@GetMapping("/sincobrar/total")
	public ResponseEntity<ValorFactDTO> totalByCuenta(@RequestParam Long cuenta) {
		return ResponseEntity.ok(facServicio.getTotalesByAbonado(cuenta));
	}

	@GetMapping("/sincobrar/datos")
	public List<ValorFactDTO> findSincobroDatos(@RequestParam Long cuenta) {
		return facServicio.findSincobroDatos(cuenta);
	}

	@GetMapping("/sincobrar/total/datos")
	public ValorFactDTO getTotalesByAbonadoDatos(@RequestParam Long cuenta) {
		return facServicio.getTotalesByAbonadoDatos(cuenta);
	}

}
