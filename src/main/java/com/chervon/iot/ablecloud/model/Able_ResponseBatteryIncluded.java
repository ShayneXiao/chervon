package com.chervon.iot.ablecloud.model;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by 喷水君 on 2017/7/30.
 */
public class Able_ResponseBatteryIncluded {
    private String type;
    private String id;
    private Map<String,String> attrubutes;
    private Map<String,String> links;

    public Able_ResponseBatteryIncluded(String type, String id, Map<String,
            String> attrubutes, Map<String, String> links) {
        this.type = type;
        this.id = id;
        this.attrubutes = attrubutes;
        this.links = links;
    }

    public String getType() {
        return type;
    }

    public Able_ResponseBatteryIncluded() {
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

    public Map<String, String> getAttrubutes() {
        return attrubutes;
    }

    public void setAttrubutes(Map<String, String> attrubutes) {
        this.attrubutes = attrubutes;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
