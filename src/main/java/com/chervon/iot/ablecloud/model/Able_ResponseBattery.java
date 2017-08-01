package com.chervon.iot.ablecloud.model;

import com.chervon.iot.mobile.model.entity.Relationship;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Admin on 2017/7/12.
 */
public class Able_ResponseBattery {
   private Object data;
   private  Object included;
   private Object meta;
   private Object links;

    public Able_ResponseBattery(Object data, Object included, Object meta, Object links) {
        this.data = data;
        this.included = included;
        this.meta = meta;
        this.links = links;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getIncluded() {
        return included;
    }

    public void setIncluded(Object included) {
        this.included = included;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
    }

    public Object getLinks() {
        return links;
    }

    public void setLinks(Object links) {
        this.links = links;
    }

    public Able_ResponseBattery(){

    }
}
