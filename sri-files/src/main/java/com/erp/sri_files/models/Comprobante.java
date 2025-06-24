package com.erp.sri_files.models;

import jakarta.xml.bind.annotation.*;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@XmlRootElement(name = "factura")
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
    

}