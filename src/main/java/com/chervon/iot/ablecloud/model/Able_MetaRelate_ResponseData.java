package com.chervon.iot.ablecloud.model;

import java.util.Map;

/**
 * Created by ZAC on 2017-8-1.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
public class Able_MetaRelate_ResponseData extends Able_ResponseData {
    private Map<String,?> meta;
    private Map<String,Able_Relationship> relationships;

    public Able_MetaRelate_ResponseData() {
    }

    public Able_MetaRelate_ResponseData(Map<String, ?> meta, Map<String, Able_Relationship> relationships) {
        this.meta = meta;
        this.relationships = relationships;
    }

    public Map<String, ?> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, ?> meta) {
        this.meta = meta;
    }

    public Map<String, Able_Relationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, Able_Relationship> relationships) {
        this.relationships = relationships;
    }
}
