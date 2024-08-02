package com.xml_validation_poc.controller;

import com.xml_validation_poc.dto.RawNcxmlXpaths;
import com.xml_validation_poc.dto.RequestFilePaths;
import com.xml_validation_poc.entity.XmlMapping;
import com.xml_validation_poc.service.XmlParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/poc")
public class xmlParserController {

    @Autowired
    private XmlParserService xmlParserService;

    @PostMapping("/parse")
    public ResponseEntity<RawNcxmlXpaths> parseXml(@RequestBody RequestFilePaths requestFilePaths){
        return ResponseEntity.status(HttpStatus.OK).body(xmlParserService.parseXmls(requestFilePaths));
    }

    @GetMapping("/getAllRecords")
    public ResponseEntity<List<XmlMapping>> getData(){
        return ResponseEntity.status(HttpStatus.OK).body(xmlParserService.getAll());
    }


}
