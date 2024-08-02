package com.xml_validation_poc.dto;

import lombok.Data;

import java.util.List;
@Data
public class RawNcxmlXpaths {
    private List<String> rawXpathList;
    private List<String> cxmlXpathList;

    public RawNcxmlXpaths(List<String> rawXpathList, List<String> cxmlPathList) {
        this.rawXpathList = rawXpathList;
        this.cxmlXpathList = cxmlPathList;
    }
}
