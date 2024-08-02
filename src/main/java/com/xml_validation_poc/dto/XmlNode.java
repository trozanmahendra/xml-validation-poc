package com.xml_validation_poc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XmlNode {
    private String nodeName;
    private String nodeValue;
    private String xpath;
    private Map<String,String> attributesMap;
}
