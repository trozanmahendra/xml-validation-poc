package com.xml_validation_poc.service;

import com.xml_validation_poc.dao.XmlMappingDao;
import com.xml_validation_poc.dto.RawNcxmlXpaths;
import com.xml_validation_poc.dto.RequestFilePaths;
import com.xml_validation_poc.entity.XmlMapping;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

@Service
public class XmlParserService {
    @Autowired
    private XmlMappingDao xmlMappingDao;
    public List<XmlMapping> getAll(){
        return xmlMappingDao.findAll();
    }

    public RawNcxmlXpaths parseXmls(RequestFilePaths requestFilePaths){
//    List<String> rawXpathList = new TreeSet<>(xmlParser(requestFilePaths.getRawFilePath())).stream().toList();
    List<String> rawXpathList = xmlParser(requestFilePaths.getRawFilePath());
//    List<String> cxmlPathList = new TreeSet<>(xmlParser(requestFilePaths.getCxmlFilePath())).stream().toList();
    List<String> cxmlPathList = xmlParser(requestFilePaths.getCxmlFilePath());
        return new RawNcxmlXpaths(rawXpathList,cxmlPathList);
    }

//    public RawNCxmlNodes parseXmlsForApplicationsLu(RequestFilePaths requestFilePaths){
//        List<XmlNode> rawXmlNodeList = rawXmlParserForApplicationsLu(requestFilePaths.getRawFilePath());
//        List<XmlNode> cxmlNodeList = cxmlParserForApplicationsLu(requestFilePaths.getCxmlFilePath());
//        return new RawNCxmlNodes(rawXmlNodeList,cxmlNodeList);
//    }
//
//    public List<XmlMapping> getApplicationsLuXmlMappings(){
//        return getAll().stream()
//                .filter(xmlMapping -> "applications".equals(xmlMapping.getLogicalUnit())).toList();
//    }
//
//    public List<XmlMapping> setValuesToApplicationsLu(RequestFilePaths requestFilePaths){
////        Applications Lu mappings from  db
//        List<XmlMapping> xmlMappings = getApplicationsLuXmlMappings();
////        xmlMappings.forEach(xmlMapping -> System.out.println(xmlMapping.getRawTag()));
////        xmlMappings.forEach(xmlMapping -> System.out.println(xmlMapping.getCxmlTag()));
////       applications Lu from raw and cxml files
//        RawNCxmlNodes rawNCxmlNodes = parseXmlsForApplicationsLu(requestFilePaths);
//        List<XmlNode> raw = rawNCxmlNodes.getRawXmlNodeList();
//        List<XmlNode> cxml = rawNCxmlNodes.getCxmlNodeList();
//
//        return xmlMappings.stream().peek(xmlMapping -> {
//            for (XmlNode xmlNode : raw) {
//
//                if (xmlMapping.getRawTag().equals(xmlNode.getXpath())) {
//                    xmlMapping.setRawTagValue(xmlNode.getNodeValue()+" , "+xmlMapping.getRawTagValue());
//                }
//            }
//            for (XmlNode xmlNode : cxml) {
//                if (xmlMapping.getCxmlTag().equals(xmlNode.getXpath())) {
//                    xmlMapping.setCxmlTagValue(xmlNode.getNodeValue()+" , "+xmlMapping.getRawTagValue());
//                }
//            }
//        }).toList();
//    }
    @Data
    public static class Xpath {
        public Xpath(String xPath, Node element) {
            this.xPath = xPath;
            this.element = element;
        }

        String xPath;
        Node element;

    public String getxPath() {
        return xPath;
    }
}

    public static Xpath getParentsRecursively(Node element, String xPath) {
        if (xPath == null) xPath = "";
        if (element.getNodeName().equals("#document")) {
            return new Xpath(xPath, element);
        } else {
            LinkedHashSet<String> strings = new LinkedHashSet<>(getAttributesAsMap(element.getAttributes()).keySet());
            String attributesString = "";
            for(String s: strings){
                attributesString = attributesString.concat("@").concat(s);
            }
            xPath = element.getNodeName()
//                    .concat(attributesString)
                    .concat("/").concat(xPath);
            return getParentsRecursively(element.getParentNode(), xPath);
        }
    }

    static Map<String, String> getAttributesAsMap(NamedNodeMap attributes) {
        Map<String, String> stringStringMap = new LinkedHashMap<>();
        for (int i = 0; i < attributes.getLength(); i++) {
            stringStringMap.put(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
        }
        return stringStringMap;
    }

    public Document getNodeFromXml(String xmlFilePath){
        Document document = null;
        try{
            File inputFile = new File(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // Disable external DTDs
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            document = documentBuilder.parse(inputFile);
            document.getDocumentElement().normalize();

        }catch (Exception exception){
            exception.fillInStackTrace();
        }
        return document;
    }

    public List<String> xmlParser(String filePath){
        List<String> xmlPathList = new ArrayList<>();
        try{
            NodeList nodeList = getNodeFromXml(filePath).getElementsByTagName("*");
            for (int i=0; i<nodeList.getLength(); i++) {
                // Get element
                Node element = nodeList.item(i);
                System.out.println("NodeName : "+element.getNodeName());
                System.out.println("Xpath : "+getParentsRecursively(element, null).getxPath());
                xmlPathList.add(getParentsRecursively(element, null).getxPath());
                System.out.println("Attributes : "+getAttributesAsMap(element.getAttributes()));
                System.out.println();
            }
        }catch (Exception exception){
            exception.fillInStackTrace();
        }
        return xmlPathList;
    }
}
