package com.chervon.iot.ablecloud.model;

import java.util.Date;

/**
 * Created by Shayne on 2017/8/1.
 */
public class Able_ResponseDeviceError {
    private Integer id;

    private String sn;

    private Boolean recoverable;

    private Date timestamp;

    private String type_name;

    private String code;

    private String desc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Boolean getRecoverable() {
        return recoverable;
    }

    public void setRecoverable(Boolean recoverable) {
        this.recoverable = recoverable;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
