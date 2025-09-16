package com.erp.sri_files.services;

import com.erp.sri_files.config.SingleKeyStoreKeyingDataProvider;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import org.w3c.dom.*;
import xades4j.production.*;


import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import xades4j.providers.*;


@Service
public class XmlSignerService {

    /**
     * Firma un XML con XAdES-BES usando un certificado .p12 en memoria.
     *
     * @param xmlContent XML sin firmar
     * @param p12Bytes   Certificado .p12 en bytes (desde BD)
     * @param password   Contraseña del .p12
     * @return XML firmado (String)
     */
    public static String signXml(String xmlContent, byte[] p12Bytes, String password) throws Exception {
        if (p12Bytes == null || p12Bytes.length == 0)
            throw new IllegalArgumentException("El certificado no puede estar vacío");
        if (xmlContent == null || xmlContent.trim().isEmpty())
            throw new IllegalArgumentException("El XML no puede estar vacío");

        // ---- 1) Cargar el KeyStore en memoria ----
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new ByteArrayInputStream(p12Bytes), password.toCharArray());

        // Tomar el primer alias (normalmente hay uno solo en un .p12)
        String alias = keyStore.aliases().nextElement();

        // Obtener la clave privada + certificado
        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(
                alias,
                new KeyStore.PasswordProtection(password.toCharArray())
        );

        // ---- 2) Crear el KeyingDataProvider en memoria ----
        SingleKeyStoreKeyingDataProvider kp = new SingleKeyStoreKeyingDataProvider(pkEntry);

        // ---- 3) Parsear el XML ----
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xmlContent)));

        // ---- 4) Configurar el firmante XAdES ----
        XadesBesSigningProfile profile = new XadesBesSigningProfile(kp);
        XadesSigner signer = profile.newSigner();

        // ---- 5) Preparar objetos a firmar (enveloped) ----
        Element rootElement = doc.getDocumentElement();

        SignedDataObjects dataObjs = new SignedDataObjects(
                new EnvelopedXmlObject(rootElement)
        );
        // ---- 6) Ejecutar firma ----
        signer.sign(dataObjs, (Element) doc.getDocumentElement());

        // ---- 7) Serializar el XML firmado ----
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            tf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tf.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (IllegalArgumentException ignored) {
        }

        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "no");
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");

        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString().replace("&#13;", ""); // limpiar CRs
    }

        public String sSignXml(String xmlContent, byte[] certificado, String password) throws Exception {
                // ========== VALIDACIONES INICIALES ==========
                if (certificado == null || certificado.length == 0) {
                        throw new IllegalArgumentException("Certificado no proporcionado");
                }
                if (xmlContent == null || xmlContent.trim().isEmpty()) {
                        throw new IllegalArgumentException("Contenido XML vacío");
                }
                if (password == null || password.isBlank()) {
                        throw new IllegalArgumentException("Contraseña inválida");
                }

                // ========== CONFIGURACIÓN DE SEGURIDAD ==========
                Security.addProvider(new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
                System.setProperty("org.apache.xml.security.ignoreLineBreaks", "true");

                // ========== CARGA DEL CERTIFICADO ==========
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(new ByteArrayInputStream(certificado), password.toCharArray());

                String alias = keyStore.aliases().nextElement();
                PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
                Certificate[] certChain = keyStore.getCertificateChain(alias);
                X509Certificate cert = (X509Certificate) certChain[0];
                PublicKey publicKey = cert.getPublicKey();

                // ========== PARSEO DEL XML ==========
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

                Document document = factory.newDocumentBuilder()
                                .parse(new InputSource(new StringReader(xmlContent)));
                Element rootElement = document.getDocumentElement();

                // ========== CONFIGURACIÓN DE FIRMA DIGITAL ==========
                XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance("DOM");

                // Configuración de transformadas (IMPORTANTE PARA SAT)
                List<Transform> transforms = new ArrayList<>();
                transforms.add(sigFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
                transforms.add(sigFactory.newTransform("http://www.w3.org/2001/10/xml-exc-c14n#",
                                (TransformParameterSpec) null));

                // Configuración de referencia
                Reference reference = sigFactory.newReference(
                                "", // Referencia al documento completo
                                sigFactory.newDigestMethod(DigestMethod.SHA256, null),
                                transforms,
                                null,
                                null);

                // Configuración de SignedInfo
                SignedInfo signedInfo = sigFactory.newSignedInfo(
                                sigFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                                                (C14NMethodParameterSpec) null),
                                sigFactory.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
                                Collections.singletonList(reference));

                // ========== CONFIGURACIÓN DE KEYINFO (CERTIFICADO) ==========
                KeyInfoFactory keyInfoFactory = sigFactory.getKeyInfoFactory();
                KeyValue keyValue = keyInfoFactory.newKeyValue(publicKey); // Genera <KeyValue>...</KeyValue>

                // Incluir toda la cadena de certificados
                // Crear X509Data con la cadena de certificados
                List<X509Certificate> x509CertList = Arrays.stream(certChain)
                                .map(c -> (X509Certificate) c)
                                .collect(Collectors.toList());
                X509Data x509Data = keyInfoFactory.newX509Data(x509CertList);

                // Combinar ambos en KeyInfo
                List<XMLStructure> keyInfoContent = new ArrayList<>();
                keyInfoContent.add(x509Data); // Certificado(s)
                keyInfoContent.add(keyValue); // Clave pública

                KeyInfo keyInfo = keyInfoFactory.newKeyInfo(keyInfoContent);
                // ========== APLICACIÓN DE LA FIRMA ==========
                DOMSignContext signContext = new DOMSignContext(privateKey, rootElement);
                signContext.setDefaultNamespacePrefix("ds");

                XMLSignature signature = sigFactory.newXMLSignature(signedInfo, keyInfo);
                signature.sign(signContext);

                // ========== SERIALIZACIÓN DEL XML FIRMADO ==========
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                try {
                        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
                } catch (IllegalArgumentException e) {
                        // Algunas implementaciones no soportan estos atributos
                }

                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                transformer.setOutputProperty(OutputKeys.INDENT, "no");
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

                StringWriter writer = new StringWriter();
                transformer.transform(new DOMSource(document), new StreamResult(writer));

                String signedXml = writer.toString();
                return signedXml.replace("&#13;", "");
        }

        public String __signXml(String xmlContent, byte[] certificado, String password) throws Exception {
                if (certificado == null || certificado.length == 0) {
                        throw new IllegalArgumentException("El certificado proporcionado está vacío o es nulo");
                }

                if (xmlContent == null || xmlContent.trim().isEmpty()) {
                        throw new IllegalArgumentException("El contenido XML es nulo o vacío");
                }

                if (password == null || password.isBlank()) {
                        throw new IllegalArgumentException("La contraseña del certificado es nula o vacía");
                }

                // Cargar el certificado desde el almacén
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(new ByteArrayInputStream(certificado), password.toCharArray());

                String alias = keyStore.aliases().nextElement();
                PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());

                X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
                PublicKey publicKey = cert.getPublicKey();

                /*
                 * // Parsear el XML
                 * DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                 * dbf.setNamespaceAware(true);
                 * DocumentBuilder db = dbf.newDocumentBuilder();
                 * // Document document = db.parse(new
                 * ByteArrayInputStream(xmlContent.getBytes()));
                 * Document document = db.parse(new
                 * ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
                 */

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(xmlContent))); // <- ahora está
                                                                                                  // definido

                // Crear XML Signature Factory
                XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");

                // Referencia al documento XML
                Reference reference = signatureFactory
                                .newReference("", signatureFactory.newDigestMethod(DigestMethod.SHA256, null),
                                                Collections.singletonList(
                                                                signatureFactory.newTransform(Transform.ENVELOPED,
                                                                                (TransformParameterSpec) null)),
                                                null, null);

                // Crear SignedInfo
                SignedInfo signedInfo = signatureFactory.newSignedInfo(
                                signatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                                                (C14NMethodParameterSpec) null),
                                signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
                                Collections.singletonList(reference));

                // Crear KeyInfo con datos del certificado
                // 4. Crear KeyInfo con datos del certificado y metadatos
                KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
                KeyValue keyValue = keyInfoFactory.newKeyValue(publicKey); // Genera <KeyValue>...</KeyValue>

                // --- Datos del firmante (X509Data) ---
                X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(cert));

                // --- Metadatos personalizados (ej: nombre, RFC, fecha) ---
                List<XMLStructure> keyInfoContent = new ArrayList<>();
                keyInfoContent.add(x509Data);
                keyInfoContent.add(keyValue);
                // (Opcional) Agregar un "Object" con datos adicionales
                /*
                 * XMLObjectBuilder objectBuilder = new XMLObjectBuilder() {
                 * // Implementación personalizada para añadir datos
                 * };
                 */
                // Ejemplo: Añadir un nodo <ds:Object Id="customData">...</ds:Object>
                Element customData = document.createElement("customData");
                customData.setTextContent("RFC: ABC123456789 - Fecha: " + new Date());

                XMLObject xmlObject = signatureFactory.newXMLObject(
                                Collections.singletonList(new DOMStructure(customData)),
                                "customData", null, null);

                KeyInfo keyInfo = keyInfoFactory.newKeyInfo(keyInfoContent);

                // Firmar el documento
                DOMSignContext domSignContext = new DOMSignContext(privateKey, document.getDocumentElement());
                XMLSignature signature = signatureFactory.newXMLSignature(signedInfo, keyInfo);
                signature.sign(domSignContext);

                // Convertir el documento firmado a String
                StringWriter stringWriter = new StringWriter();
                // Transformer transformer = TransformerFactory.newInstance().newTransformer();
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setAttribute("indent-number", 0); // Evita indentación
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); // Opcional
                transformer.setOutputProperty(OutputKeys.INDENT, "no"); // Desactiva indentación

                // Convertir el documento firmado a String
                // StringWriter stringWriter = new StringWriter();
                transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
                String signedXml = stringWriter.toString();
                return signedXml.replace("&#13;", ""); // No es lo ideal, pero puede funcionar
        }

        public byte[] _signXml(Document xmlData, byte[] p12Data, String password) throws Exception {

                // Cargar el KeyStore desde los bytes del certificado
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(new ByteArrayInputStream(p12Data), password.toCharArray());

                // Obtener la clave privada y el certificado
                String alias = keyStore.aliases().nextElement();
                PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
                X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);

                // Preparar el XML para firmar
                /*
                 * DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                 * dbf.setNamespaceAware(true);
                 * DocumentBuilder builder = dbf.newDocumentBuilder();
                 * Document document = builder.parse(new ByteArrayInputStream(xmlData));
                 */
                Document document = xmlData;

                // Crear XML Signature Factory
                XMLSignatureFactory xmlSigFactory = XMLSignatureFactory.getInstance("DOM");

                // Crear referencia a todo el documento
                Reference reference = xmlSigFactory.newReference("",
                                xmlSigFactory.newDigestMethod(DigestMethod.SHA256, null));

                // Crear SignedInfo
                SignedInfo signedInfo = xmlSigFactory.newSignedInfo(
                                xmlSigFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                                                (C14NMethodParameterSpec) null),
                                xmlSigFactory.newSignatureMethod(SignatureMethod.RSA_SHA256,
                                                (SignatureMethodParameterSpec) null),
                                Collections.singletonList(reference));

                // Crear KeyInfo
                KeyInfoFactory keyInfoFactory = xmlSigFactory.getKeyInfoFactory();
                X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(certificate));
                KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

                // Firmar el documento
                DOMSignContext domSignContext = new DOMSignContext(privateKey, document.getDocumentElement());
                XMLSignature signature = xmlSigFactory.newXMLSignature(signedInfo, keyInfo);
                signature.sign(domSignContext);

                // Convertir el documento firmado a bytes
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                TransformerFactory.newInstance().newTransformer()
                                .transform(new DOMSource(document),
                                                new StreamResult(outputStream));

                return outputStream.toByteArray();
        }



}