package com.chervon.iot.ablecloud.model;

import java.util.Map;

/**
 * Created by Admin on 2017/8/1.
 */
public class Able_Relationship {
    private Object links;
    private Object data;

    public Able_Relationship() {
    }

    public Able_Relationship(Map<String, String> links, Object data) {
        this.links = links;
        this.data = data;
    }

    public Object getLinks() {
        return links;
    }

    public void setLinks(Object links) {
        this.links = links;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
