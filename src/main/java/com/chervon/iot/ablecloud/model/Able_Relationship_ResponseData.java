package com.chervon.iot.ablecloud.model;

import java.util.Map;

/**
 * Created by Admin on 2017/8/1.
 */
public class Able_Relationship_ResponseData extends Able_ResponseData {
    private Map<String,Object> relationships;

    public Able_Relationship_ResponseData() {
    }

    public Able_Relationship_ResponseData(Map<String, Object> relationships) {
        this.relationships = relationships;
    }

    public Able_Relationship_ResponseData(String type, String id, Map<String, Object> attributes, Map<String, String> links, Map<String, Object> relationships) {
        super(type, id, attributes, links);
        this.relationships = relationships;
    }

    public Map<String, Object> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, Object> relationships) {
        this.relationships = relationships;
    }
}
