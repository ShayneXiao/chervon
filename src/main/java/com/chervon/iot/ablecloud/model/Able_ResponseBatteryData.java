package com.chervon.iot.ablecloud.model;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by 喷水君 on 2017/7/30.
 */
public class Able_ResponseBatteryData {
    private String type;
    private  String id;
    private Map<String,String> attribute;
    private Map<String,Object> relationships;
    private  Map<String,String> links;
    public  Able_ResponseBatteryData(){}
    public Able_ResponseBatteryData(String type, String id, Map<String, String> attribute,
                                    Map<String, Object> relationships, Map<String, String> links) {
        this.type = type;
        this.id = id;
        this.attribute = attribute;
        this.relationships = relationships;
        this.links = links;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getAttribute() {
        return attribute;
    }

    public void setAttribute(Map<String, String> attribute) {
        this.attribute = attribute;
    }

    public Map<String, Object> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, Object> relationships) {
        this.relationships = relationships;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
