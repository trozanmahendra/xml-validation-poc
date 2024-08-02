package com.xml_validation_poc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RawNCxmlNodes {
    private List<XmlNode> rawXmlNodeList;
    private List<XmlNode> cxmlNodeList;
}
