package com.chervon.iot.mobile.model.entity;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Admin on 2017/7/12.
 */
@Component
public class Relationship {

    private Map<String,String> links;
    private Map<String,String> data;
    public  Relationship(){
    }
    public Relationship(Map<String, String> links, Map<String, String> data) {
        this.links = links;
        this.data = data;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }



}
