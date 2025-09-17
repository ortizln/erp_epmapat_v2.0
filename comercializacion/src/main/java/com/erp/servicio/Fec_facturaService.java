package com.erp.servicio;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.erp.interfaces.DefinirProjection;
import com.erp.interfaces.FacIntereses;
import com.erp.interfaces.Fecfactura;
import com.erp.modelo.*;
import com.erp.repositorio.*;
import com.erp.repositorio.administracion.DefinirR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.sri.interfaces.fecFacturaDatos;

@Service
public class Fec_facturaService {

   @Autowired
   private Fec_facturaR dao;
   @Autowired
   private FacturasR facturasR;
   @Autowired
   private RubroxfacR rubroxfacR;
   @Autowired
   private DefinirR definirR;
   @Autowired
   private LecturasR lecturasR;
   @Autowired
   private Fec_factura_detallesR fecFacturaDetallesR;
   @Autowired
   private Fec_factura_detalles_impuestosR fecFacturaDetallesImpuestosR;
   @Autowired
   private Fec_factura_pagosR fecFacturaPagosR;

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

   public fecFacturaDatos getNroFactura(Long idfactura) {
      return dao.getNroFactura(idfactura);
   }



    // CREAR LAS FACTURAS
    public Map<String, Object> generarFecFactura(Long idfactura) {
        Map<String, Object> response = new HashMap<>();
        Fecfactura factura = facturasR.forFecfactura(idfactura);
        Fec_factura fecFactura = new Fec_factura();
        DefinirProjection definir = definirR.findDefinirWithoutFirma(1L);
        String concepto = "OTROS SERVICIOS";
        String[] partes = separarCodigo(factura.getNrofactura());
        String establecimiento = partes[0]; // 001
        String puntoEmision = partes[1]; // 013
        String secuencial = partes[2]; // 000022233

        Lecturas lectura = lecturasR.findOnefactura(idfactura);
        float m3 = 0;
        if (lectura != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
            Date fecemision = lecturasR.findDateByIdfactura(idfactura);
            String fechaFormateada = sdf.format(fecemision);
            m3 = lectura.getLecturaactual() - lectura.getLecturaanterior();
            concepto = "M3: " + m3 + " Emision: " + fechaFormateada + " Nro medidor: " + lectura.getIdabonado_abonados().getNromedidor();

        }
        fecFactura.setConcepto(concepto);
        // BUILD FECFACTURA
        fecFactura.setIdfactura(factura.getIdfactura());
        fecFactura.setDireccionestablecimiento(definir.getDirmatriz());
        fecFactura.setEmailcomprador(factura.getEmail());
        fecFactura.setRecaudador(factura.getNomusu());
        fecFactura.setFechaemision(factura.getFechacobro().atStartOfDay());
        fecFactura.setEstablecimiento(establecimiento);
        fecFactura.setPuntoemision(puntoEmision);
        fecFactura.setSecuencial(secuencial);
        fecFactura.setRazonsocialcomprador(factura.getNombre());
        fecFactura.setIdentificacioncomprador(factura.getCedula());
        fecFactura.setTelefonocomprador(factura.getTelefono());
        fecFactura.setTipoidentificacioncomprador(factura.getCodigo());
        fecFactura.setReferencia(factura.getIdabonado());
        fecFactura.setDireccioncomprador(factura.getDireccion());
        fecFactura.setClaveacceso(generarClaveAcceso(fecFactura, definir));
        fecFactura.setEstado("I");
        dao.save(fecFactura);
        generarFecFacturaDetalles(idfactura);
        generarFecFacturaPagos(idfactura, (long) m3);
        return response;
    }
    //CREAR FACTURA DETALLE
    public void generarFecFacturaDetalles(Long idfactura) {
        Fec_factura_detalles fecFacturaDetalles = new Fec_factura_detalles();
        List<Rubroxfac> rxf = rubroxfacR.findByIdfactura(idfactura);
        fecFacturaDetalles.setIdfactura(idfactura);
        fecFacturaDetalles.setDescuento(BigDecimal.valueOf(0));
        rxf.forEach(item->{
            String idfacdetalle = String.valueOf(idfactura+""+item.getIdrubro_rubros().getIdrubro());
            fecFacturaDetalles.setIdfacturadetalle(Long.valueOf(idfacdetalle));
            fecFacturaDetalles.setCodigoprincipal(String.valueOf(item.getIdrubro_rubros().getIdrubro()));
            fecFacturaDetalles.setCantidad(BigDecimal.valueOf(item.getCantidad()));
            fecFacturaDetalles.setPreciounitario(item.getValorunitario());
            fecFacturaDetalles.setDescripcion(item.getIdrubro_rubros().getDescripcion());
            fecFacturaDetallesR.save(fecFacturaDetalles);
            generarFecFacturaDetallesImpuestos(item, Long.valueOf(idfacdetalle));
        });
    }
    public void generarFecFacturaDetallesImpuestos(Rubroxfac rxf, Long idfecfacturadetalle) {
       Fec_factura_detalles_impuestos fecFacturaDetallesImpuestos = new Fec_factura_detalles_impuestos();
        String idfacdetalleimpuestos = String.valueOf(rxf.getIdrubro_rubros().getIdrubro()+""+idfecfacturadetalle);
        fecFacturaDetallesImpuestos.setIdfacturadetalleimpuestos(Long.valueOf(idfacdetalleimpuestos));
       fecFacturaDetallesImpuestos.setIdfacturadetalle(idfecfacturadetalle);
       fecFacturaDetallesImpuestos.setCodigoimpuesto("2");
       fecFacturaDetallesImpuestos.setCodigoporcentaje("0");
       fecFacturaDetallesImpuestos.setBaseimponible(rxf.getValorunitario());
        fecFacturaDetallesImpuestosR.save(fecFacturaDetallesImpuestos);

    }

    public void generarFecFacturaPagos(Long idfactura, Long m3) {
        List<FacIntereses> intereses = facturasR.getForIntereses(idfactura);
        for (FacIntereses item : intereses) {
            Fec_factura_pagos fecFacturaPagos = new Fec_factura_pagos();
            fecFacturaPagos.setTotal(BigDecimal.valueOf(item.getSuma()));
            Long formapago = item.getFormapago();
            if (formapago == 1 || formapago == 3 || formapago == 6) {
                fecFacturaPagos.setFormapago("01"); // Efectivo
            } else if (formapago == 4 || formapago == 7) {
                fecFacturaPagos.setFormapago("20"); // Tarjeta
            } else if (formapago == 5) {
                fecFacturaPagos.setFormapago("19"); // Otro medio
            } else {
                fecFacturaPagos.setFormapago("99"); // Desconocido
            }
            String idfacturapagos = String.valueOf(idfactura+""+m3);
            fecFacturaPagos.setIdfacturapagos(Long.valueOf(idfacturapagos));
            fecFacturaPagos.setUnidadtiempo("dias");
            fecFacturaPagos.setPlazo(0);
            fecFacturaPagos.setIdfactura(idfactura);
            fecFacturaPagosR.save(fecFacturaPagos);

        }
    }

        // COMPLEMENTOS PARA GENERAR LA FECFACTURA
    public static String[] separarCodigo(String codigo) {
        if (codigo == null || !codigo.contains("-")) {
            throw new IllegalArgumentException("El código no tiene el formato esperado");
        }

        // Separa por guiones
        String[] partes = codigo.split("-");

        if (partes.length != 3) {
            throw new IllegalArgumentException("El código debe tener 3 partes (ej: 001-013-000022233)");
        }

        return partes;
    }

    public String generarClaveAcceso(Fec_factura factura, DefinirProjection definir) {

        // 1. Fecha de emisión (DDMMYYYY)
        String fechaEmision = formatToDDMMYYYY(factura.getFechaemision());
        // 2. Tipo de comprobante (2 dígitos)
        String tipoComprobante = String.format("%02d", 1);
        // 3. RUC del emisor (13 dígitos)
        String ruc = definir.getRuc();
        // 4. Ambiente (1 dígito: 1=Pruebas, 2=Producción)
        Byte ambiente = definir.getTipoambiente(); // "1" o "2"
        // 5. Serie (4 dígitos establecimiento + 3 dígitos punto emisión)
        String serie = factura.getEstablecimiento() + factura.getPuntoemision();
        // 6. Secuencial (9 dígitos)
        String secuencial = String.format("%09d", Long.parseLong(factura.getSecuencial()));
        // 7. Código numérico (8 dígitos aleatorios)
        String codigoNumerico = generarCodigoNumerico();
        // 8. Tipo de emisión (1 dígito, normal=1)
        byte tipoEmision = definir.getTipoambiente(); // Normal
        // Concatenar todos los componentes
        String claveAcceso = fechaEmision + tipoComprobante + ruc + ambiente +
                serie + secuencial + codigoNumerico + tipoEmision;
        // 9. Calcular dígito verificador
        /*
         * String digitoVerificador = calcularDigitoVerificador(claveAcceso);
         */

        // Retornar clave de acceso completa (49 dígitos)
        return claveAcceso;
        /* return claveAcceso + digitoVerificador; */
    }

    public static String formatToDDMMYYYY(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        return dateTime.format(formatter);
    }

    private static String generarCodigoNumerico() {
        // Generar 8 dígitos aleatorios
        long randomNum = ThreadLocalRandom.current().nextLong(10000000L, 99999999L);
        return String.valueOf(randomNum);
    }

    private static String calcularDigitoVerificador(String claveAcceso43) {
        if (claveAcceso43 == null || claveAcceso43.length() != 43) {
            throw new IllegalArgumentException("La clave de acceso debe tener exactamente 43 dígitos");
        }

        int[] patrones = { 2, 3, 4, 5, 6, 7 };
        int suma = 0;
        int j = 0;

        // Recorrer desde el final hacia el inicio
        for (int i = claveAcceso43.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(claveAcceso43.charAt(i));
            suma += digito * patrones[j];
            j = (j + 1) % patrones.length; // repetir patrón
        }

        int modulo = suma % 11;
        int digitoVerificador = (modulo == 0 || modulo == 1) ? 0 : 11 - modulo;

        return String.valueOf(digitoVerificador);
    }




}
