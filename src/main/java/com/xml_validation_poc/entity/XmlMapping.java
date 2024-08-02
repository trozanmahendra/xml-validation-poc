package com.xml_validation_poc.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

@Entity
@Table(name = "xml_mapping")
@Data
public class XmlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String country;

    private String rawTagName;
    private String rawTag;

    private String cxmlTagName;
    private String cxmlTag;

    private String cxmlTagRegx;
    private Boolean  isValueChangeNeeded;
    private String rawTagValue;
    private String cxmlTagValue;
    private String logicalUnit;
}
