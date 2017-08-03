package com.chervon.iot.ablecloud.model;

import java.util.Map;

/**
 * Created by Admin on 2017/8/1.
 */
public class Able_Meta_ResponseData extends Able_ResponseData {
    private Map<String,?> meta;

    public Able_Meta_ResponseData() {
    }

    public Able_Meta_ResponseData(Map<String, ?> meta) {
        this.meta = meta;
    }

    public Able_Meta_ResponseData(String type, String id, Map<String, Object> attributes, Map<String, String> links, Map<String, ?> meta) {
        super(type, id, attributes, links);
        this.meta = meta;
    }

    public Map<String, ?> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, ?> meta) {
        this.meta = meta;
    }
}
