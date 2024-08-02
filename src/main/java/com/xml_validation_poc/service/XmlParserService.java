package com.xml_validation_poc.service;

import com.xml_validation_poc.dao.XmlMappingDao;
import com.xml_validation_poc.dto.RawNcxmlXpaths;
import com.xml_validation_poc.dto.RequestFilePaths;
import com.xml_validation_poc.entity.XmlMapping;
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

    List<String> rawXpathList = new TreeSet<>(xmlParser(requestFilePaths.getRawFilePath(),null)).stream().toList();
    List<String> cxmlPathList = new TreeSet<>(xmlParser(requestFilePaths.getCxmlFilePath(),null)).stream().toList();
        return new RawNcxmlXpaths(rawXpathList,cxmlPathList);
    }

//    public Object validateApplicationsLu(){
//
//    }
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
                    .concat(attributesString)
                    .concat("/").concat(xPath);
            return getParentsRecursively(element.getParentNode(), xPath);
        }
    }

    private static Map<String, String> getAttributesAsMap(NamedNodeMap attributes) {
        Map<String, String> stringStringMap = new LinkedHashMap<>();
        for (int i = 0; i < attributes.getLength(); i++) {
            stringStringMap.put(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
        }
        return stringStringMap;
    }
    
    public List<String> xmlParser(String filePath,String Lu){
        List<String> xmlPathList = new ArrayList<>();
        try{
            File inputFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // Disable external DTDs
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document1 = documentBuilder.parse(inputFile);
            document1.getDocumentElement().normalize();

            NodeList nodeList = document1.getElementsByTagName("*");
            for (int i=0; i<nodeList.getLength(); i++) {
                // Get element
                Node element = nodeList.item(i);
                System.out.println("NodeName : "+element.getNodeName());
                System.out.println("Xpath : "+getParentsRecursively(element, null).getxPath());
                xmlPathList.add(getParentsRecursively(element, null).getxPath());
                System.out.println("Attributes : "+getAttributesAsMap(element.getAttributes()));
//            System.out.println("ParentElement : "+element.getParentNode().getNodeName());
//        System.out.println("Data : "+element.getTextContent());
                System.out.println();

            }
        }catch (Exception exception){
            exception.fillInStackTrace();
        }
        return xmlPathList;
    }
}
