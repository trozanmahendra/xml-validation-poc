package com.xml_validation_poc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestFilePaths {

    private String rawFilePath;
    private String cxmlFilePath;
    private String country;
    private String kindCode;
    private String logicalUnit;

    /* fr
    {
        "rawFilePath":"C:/Users/HP/Downloads/Raw xml 3140449-FULLTEXT_FR.xml",
            "cxmlFilePath":"C:/Users/HP/Downloads/DJcXML3140449.xml",
            "country":"fr"

    }
    */

    /* kr
    {
        "rawFilePath":"C:/Users/HP/Downloads/1020220114665_A_Raw.xml",
            "cxmlFilePath":"C:/Users/HP/Downloads/1020220114665_A_cXML.xml",
            "country":"kr",
            "kindCode":"A"

    }
    {
  "rawFilePath": "C:/Users/HP/Downloads/1020157033268_B1_raw.xml",
  "cxmlFilePath": "C:/Users/HP/Downloads/1020157033268_B1_cXML_Output.xml",
  "country": "kr",
  "kindCode": "B1",
  "logicalUnit": "applications"
}
    */
}
