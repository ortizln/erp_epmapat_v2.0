package com.erp.comercializacion.sri.services;

import java.io.IOException;

import com.erp.comercializacion.sri.models.YourDataModel;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
@Service
public class XmlParserService {
    public YourDataModel parseXmlToObject(String xmlString) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(xmlString, YourDataModel.class);
    }
}
