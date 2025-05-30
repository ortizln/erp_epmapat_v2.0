package com.erp.sri.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.xml.bind.annotation.*;

@Data
@Getter
@Setter
@XmlRootElement(name = "comprobante")
@XmlAccessorType(XmlAccessType.FIELD)
public class Comprobante {
    @XmlAttribute
    private String id;
    
    @XmlAttribute
    private String version;
    
    @XmlElement(name = "infoTributaria")
    private InfoTributaria infoTributaria;
    
    @XmlElement(name = "infoFactura")
    private InfoFactura infoFactura;
    
    @XmlElementWrapper(name = "detalles")
    @XmlElement(name = "detalle")
    private List<Detalle> detalles;
    
    @XmlElement(name = "totalConImpuestos")
    private TotalConImpuestos totalConImpuestos;
}