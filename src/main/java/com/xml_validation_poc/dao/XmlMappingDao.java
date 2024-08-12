package com.xml_validation_poc.dao;

import com.xml_validation_poc.entity.XmlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XmlMappingDao extends JpaRepository<XmlMapping,Integer> {

    List<XmlMapping> getAllByCountryAndKindCode(String country,String kindCode);

}
