package com.xml_validation_poc.service;

import com.xml_validation_poc.dto.RawNCxmlNodes;
import com.xml_validation_poc.dto.RequestFilePaths;
import com.xml_validation_poc.dto.XmlNode;
import com.xml_validation_poc.entity.XmlMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import static com.xml_validation_poc.service.XmlParserService.getAttributesAsMap;
import static com.xml_validation_poc.service.XmlParserService.getParentsRecursively;

@Service
public class GenericLuProcessorService {

    @Autowired
    private XmlParserService xmlParserService;

    public List<XmlMapping> getGenericLuXmlMappingsPerCountryAndKindCode(String country, String kindCode,String logicalUnit){
        assert xmlParserService != null;
        return xmlParserService.getAllByCountryAndKindCode(country,kindCode).stream()
                .filter(xmlMapping -> logicalUnit.equals(xmlMapping.getLogicalUnit())).toList();
    }

    public List<XmlNode> cxmlParserForGenericLu(String filePath,String logicalUnit){
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
                if(getParentsRecursively(element,null).getxPath().contains(logicalUnit))
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

    public List<XmlNode> rawXmlParserForGenericLu(String filePath,String country,String kindCode,String logicalUnit){
        List<XmlNode> xmlNodeList = new ArrayList<>();
        try{
            NodeList nodeList = xmlParserService.getNodeFromXml(filePath).getElementsByTagName("*");
            String rawLu = getGenericLuXmlMappingsPerCountryAndKindCode(country,kindCode,logicalUnit).get(0).getRawLu();
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

    public RawNCxmlNodes parseXmlsForGenericLu(RequestFilePaths requestFilePaths){
        List<XmlNode> rawXmlNodeList = rawXmlParserForGenericLu(requestFilePaths.getRawFilePath(),requestFilePaths.getCountry(), requestFilePaths.getKindCode(),requestFilePaths.getLogicalUnit());
        List<XmlNode> cxmlNodeList = cxmlParserForGenericLu(requestFilePaths.getCxmlFilePath(),requestFilePaths.getLogicalUnit());
        return new RawNCxmlNodes(rawXmlNodeList,cxmlNodeList);
    }
    public List<XmlMapping> setValuesToGenericLu(RequestFilePaths requestFilePaths){
        RawNCxmlNodes rawNCxmlNodes = parseXmlsForGenericLu(requestFilePaths);
        List<XmlNode> raw = rawNCxmlNodes.getRawXmlNodeList();
        List<XmlNode> cxml = rawNCxmlNodes.getCxmlNodeList();

        return getGenericLuXmlMappingsPerCountryAndKindCode(requestFilePaths.getCountry(), requestFilePaths.getKindCode(),requestFilePaths.getLogicalUnit()).stream().peek(xmlMapping -> {
            for (XmlNode xmlNode : raw) {

                if (xmlMapping.getRawTag().equals(xmlNode.getXpath())) {
                    System.out.println(xmlNode.getNodeValue());
                    xmlMapping.setRawTagValue((xmlNode.getNodeValue().replace("\n","").replace(" ","")+" , "+xmlMapping.getRawTagValue()));
                }
            }
            for (XmlNode xmlNode : cxml) {
                if (xmlMapping.getCxmlTag().equals(xmlNode.getXpath())) {
                    xmlMapping.setCxmlTagValue((xmlNode.getNodeValue()
                            .replace("\n","")
                            .replace(" ","")+" , "+xmlMapping.getCxmlTagValue()));
                }
            }
        }).toList();
    }
}
