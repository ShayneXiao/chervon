package com.chervon.iot.mobile.model.entity;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 2017/7/12.
 */
@Component
public class ResponseBody {
    private ResponseData data;
    private List<Included> included;
    private Map<String,String> meta;
    public ResponseBody(){

    }
    public ResponseBody(ResponseData data, List<Included> included, Map<String, String> meta) {
        this.data = data;
        this.included = included;
        this.meta = meta;
    }

    public ResponseData getData() {
        return data;
    }

    public void setData(ResponseData data) {
        this.data = data;
    }

    public List<Included> getIncluded() {
        return included;
    }

    public void setIncluded(List<Included> included) {
        this.included = included;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

  /*  public static class DetailResponseBody extends ResponseBody{
        private Map<String,String> links;

        public DetailResponseBody(ResponseData data, List<Included> included, Map<String, String> meta, Map<String, String> links) {
            super(data, included, meta);
            this.links = links;
        }

        public Map<String, String> getLinks() {
            return links;
        }

        public void setLinks(Map<String, String> links) {
            this.links = links;
        }
    }*/
}
