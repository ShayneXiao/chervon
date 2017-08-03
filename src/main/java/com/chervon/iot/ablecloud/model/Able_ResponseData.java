package com.chervon.iot.ablecloud.model;

import java.util.Map;

/**
 * Created by Admin on 2017/8/1.
 */
public class Able_ResponseData {
    private String type;
    private String id;
    private Map<String,Object> attributes;
    private Map<String,String> links;

    public Able_ResponseData() {
    }

    public Able_ResponseData(String type, String id, Map<String, Object> attributes, Map<String, String> links) {
        this.type = type;
        this.id = id;
        this.attributes = attributes;
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

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
