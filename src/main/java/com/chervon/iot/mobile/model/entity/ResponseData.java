package com.chervon.iot.mobile.model.entity;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 2017/7/12.
 */
@Component
public class ResponseData {
    private String type;
    private String id;
    private Map<String,String> attributes;
    private Map<String,Relationship> relationships;
    private Map<String,String> links;

    public  ResponseData(){

    }
    public ResponseData(String type, String id, Map<String, String> attributes, Map<String,Relationship> relationships, Map<String, String> links) {
        this.type = type;
        this.id = id;
        this.attributes = attributes;
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

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Map<String,Relationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String,Relationship> relationships) {
        this.relationships = relationships;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
