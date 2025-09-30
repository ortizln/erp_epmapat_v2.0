package com.erp.sri_files.utils;

import org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import xades4j.algorithms.GenericAlgorithm;
import xades4j.production.DataObjectReference;
import xades4j.properties.DataObjectFormatProperty;
import xades4j.production.Enveloped;
import xades4j.production.SignedDataObjects;
import xades4j.production.XadesBesSigningProfile;
import xades4j.production.XadesSigner;
import xades4j.providers.impl.DirectKeyingDataProvider;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.Security;
@Service
public class XadesBesService {

    public XadesBesService() {
        Security.addProvider(new XMLDSigRI());
        System.setProperty("org.apache.xml.security.ignoreLineBreaks", "true");
        try { org.apache.xml.security.Init.init(); } catch (Throwable ignored) {}
    }

    /** Firma ENVELOPED XAdES-BES el nodo #comprobante e inserta KeyInfo con el cert del P12. */
    public String signFacturaComprobanteXades(String xml, Pkcs12Loader.KeyMaterial km) throws Exception {
        if (xml == null || xml.isBlank()) throw new IllegalArgumentException("XML vacío");

        // Parser seguro
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        Element root = doc.getDocumentElement();

// Forzamos el atributo 'id' como tipo ID
        if (root.hasAttribute("id")) {
            root.setIdAttribute("id", true);
            System.out.println("Marcado atributo id como tipo ID: " + root.getAttribute("id"));
        } else {
            throw new IllegalStateException("El nodo <factura> no tiene atributo id");
        }


        // IMPORTANTE: declarar 'id' como atributo tipo ID para que #comprobante resuelva
        if (!root.hasAttribute("id")) {
            throw new IllegalArgumentException("El elemento raíz no tiene atributo id=\"comprobante\"");
        }
        root.setIdAttribute("id", true);

        // Proveedor directo (cert + key) desde el P12
        DirectKeyingDataProvider kdp = new DirectKeyingDataProvider(km.cert(), km.key());
        XadesSigner signer = (new XadesBesSigningProfile(kdp)).newSigner();

        // Referencia al nodo #comprobante con transforms: enveloped + exclusive-c14n
        // 1) Asegura el ID
        root.setIdAttribute("id", true); // y que root.getAttribute("id").equals("comprobante")

        // 2) Referencia con la URI directa
        DataObjectReference obj = (DataObjectReference) new DataObjectReference("#comprobante")
                .withTransform(new GenericAlgorithm("http://www.w3.org/2000/09/xmldsig#enveloped-signature"))
                .withTransform(new GenericAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#"));

        // 3) Usa el constructor de Enveloped que acepta los SDOs y luego firma el root
        SignedDataObjects sdos = new SignedDataObjects(obj);
        new Enveloped(signer).sign(root);
        // --- Verificación rápida: la Reference debe ser URI="#comprobante" ---
        XPath xp = XPathFactory.newInstance().newXPath();
        String refUri = (String) xp.evaluate(
                "/*[local-name()='factura']/*[local-name()='Signature']/*[local-name()='SignedInfo']/*[local-name()='Reference'][1]/@URI",
                doc,
                XPathConstants.STRING
        );

        System.out.println("Reference URI encontrado: " + refUri);


        if (refUri == null || !refUri.startsWith("#comprobante")) {
            throw new IllegalStateException("La firma resultante no referencia #comprobante (URI encontrado='" + refUri + "')");
        }

        // Serializa (sin &#13;)
        Transformer t = TransformerFactory.newInstance().newTransformer();
        StringWriter sw = new StringWriter();
        t.transform(new DOMSource(doc),
                new StreamResult(sw));
        return sw.toString().replace("&#13;", "");
    }
}
