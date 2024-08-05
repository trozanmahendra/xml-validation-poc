package com.xml_validation_poc.service;

import com.xml_validation_poc.dto.RawNCxmlNodes;
import com.xml_validation_poc.dto.RequestFilePaths;
import com.xml_validation_poc.dto.XmlNode;
import com.xml_validation_poc.entity.XmlMapping;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import static com.xml_validation_poc.service.XmlParserService.getAttributesAsMap;
import static com.xml_validation_poc.service.XmlParserService.getParentsRecursively;

@Service
public class ApplicationsLuProcessorService {

    @Autowired
    private XmlParserService xmlParserService;
    private  List<XmlMapping> xmlMappings;

    @PostConstruct
    public void getApplicationsLuXmlMappings(){
        assert xmlParserService != null;
        xmlMappings = xmlParserService.getAll().stream()
                .filter(xmlMapping -> "applications".equals(xmlMapping.getLogicalUnit())).toList();
    }

    public List<XmlNode> cxmlParserForApplicationsLu(String filePath){
        List<XmlNode> xmlNodeList = new ArrayList<>();
        try{
            NodeList nodeList = xmlParserService.getNodeFromXml(filePath).getElementsByTagName("*");
            for (int i=0; i<nodeList.getLength(); i++) {
                // Get element
                Node element = nodeList.item(i);
                System.out.println("NodeName : "+element.getNodeName());
                System.out.println("Xpath : "+getParentsRecursively(element, null).getxPath());
                System.out.println("Attributes : "+getAttributesAsMap(element.getAttributes()));
                System.out.println("data :"+element.getTextContent());
                System.out.println();
                XmlNode xmlNode = XmlNode.builder()
                        .nodeName(element.getNodeName())
                        .nodeValue(element.getTextContent().trim())
                        .attributesMap(getAttributesAsMap(element.getAttributes()))
                        .xpath(getParentsRecursively(element,null).getxPath())
                        .build();
                if(getParentsRecursively(element,null).getxPath().contains("applications"))
                    xmlNodeList.add(xmlNode);
                /*System.out.println("NodeName : "+element.getNodeName());
                System.out.println("Xpath : "+getParentsRecursively(element, null).getxPath());
                System.out.println("Attributes : "+getAttributesAsMap(element.getAttributes()));
                System.out.println();*/
            }
        }catch (Exception exception){
            exception.fillInStackTrace();
        }
        return xmlNodeList;
    }

    public List<XmlNode> rawXmlParserForApplicationsLu(String filePath){
        List<XmlNode> xmlNodeList = new ArrayList<>();
        try{
            NodeList nodeList = xmlParserService.getNodeFromXml(filePath).getElementsByTagName("*");
            for (int i=0; i<nodeList.getLength(); i++) {
                // Get element
                Node element = nodeList.item(i);
//                System.out.println("NodeName : "+element.getNodeName());
//                System.out.println("Xpath : "+getParentsRecursively(element, null).getxPath());
//                System.out.println("Attributes : "+getAttributesAsMap(element.getAttributes()));
//                System.out.println("data :"+element.getTextContent());
//                System.out.println();
                XmlNode xmlNode = XmlNode.builder()
                        .nodeName(element.getNodeName())
                        .nodeValue(element.getTextContent().trim())
                        .attributesMap(getAttributesAsMap(element.getAttributes()))
                        .xpath(getParentsRecursively(element,null).getxPath())
                        .build();
                String rawLu = xmlMappings.get(0).getRawLu();
                if(getParentsRecursively(element,null).getxPath().contains(rawLu))
                    xmlNodeList.add(xmlNode);
                /*System.out.println("NodeName : "+element.getNodeName());
                System.out.println("Xpath : "+getParentsRecursively(element, null).getxPath());
                System.out.println("Attributes : "+getAttributesAsMap(element.getAttributes()));
                System.out.println();*/
            }
        }catch (Exception exception){
            exception.fillInStackTrace();
        }
        return xmlNodeList;
    }

    public RawNCxmlNodes parseXmlsForApplicationsLu(RequestFilePaths requestFilePaths){
        List<XmlNode> rawXmlNodeList = rawXmlParserForApplicationsLu(requestFilePaths.getRawFilePath());
        List<XmlNode> cxmlNodeList = cxmlParserForApplicationsLu(requestFilePaths.getCxmlFilePath());
        return new RawNCxmlNodes(rawXmlNodeList,cxmlNodeList);
    }


    public List<XmlMapping> setValuesToApplicationsLu(RequestFilePaths requestFilePaths){
//        Applications Lu mappings from  db
//        List<XmlMapping> xmlMappings = getApplicationsLuXmlMappings();
//        xmlMappings.forEach(xmlMapping -> System.out.println(xmlMapping.getRawTag()));
//        xmlMappings.forEach(xmlMapping -> System.out.println(xmlMapping.getCxmlTag()));
//       applications Lu from raw and cxml files
        RawNCxmlNodes rawNCxmlNodes = parseXmlsForApplicationsLu(requestFilePaths);
        List<XmlNode> raw = rawNCxmlNodes.getRawXmlNodeList();
        List<XmlNode> cxml = rawNCxmlNodes.getCxmlNodeList();

        return xmlMappings.stream().peek(xmlMapping -> {
            for (XmlNode xmlNode : raw) {

                if (xmlMapping.getRawTag().equals(xmlNode.getXpath())) {
                    xmlMapping.setRawTagValue((xmlNode.getNodeValue()+" , "+xmlMapping.getRawTagValue()).replace(" , null",""));
                }
            }
            for (XmlNode xmlNode : cxml) {
                if (xmlMapping.getCxmlTag().equals(xmlNode.getXpath())) {
                    xmlMapping.setCxmlTagValue((xmlNode.getNodeValue()+" , "+xmlMapping.getCxmlTagValue()).replace(" , null",""));
                }
            }
        }).toList();
    }
}
