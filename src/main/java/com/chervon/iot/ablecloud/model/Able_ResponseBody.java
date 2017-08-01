package com.chervon.iot.ablecloud.model;

/**
 * Created by 喷水君 on 2017/8/1.
 */
public class Able_ResponseBody {
    private Object data;
    private  Object included;
    private Object meta;

    public Able_ResponseBody(Object data, Object included, Object meta) {
        this.data = data;
        this.included = included;
        this.meta = meta;
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
}
