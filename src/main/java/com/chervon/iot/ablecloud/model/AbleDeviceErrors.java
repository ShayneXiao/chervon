package com.chervon.iot.ablecloud.model;

import java.util.Date;

public class AbleDeviceErrors {
    private Integer id;

    private String sn;

    private Boolean recoverable;

    private String device;

    private String fault;

    private Boolean isfixed;

    private Date timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn == null ? null : sn.trim();
    }

    public Boolean getRecoverable() {
        return recoverable;
    }

    public void setRecoverable(Boolean recoverable) {
        this.recoverable = recoverable;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device == null ? null : device.trim();
    }

    public String getFault() {
        return fault;
    }

    public void setFault(String fault) {
        this.fault = fault == null ? null : fault.trim();
    }

    public Boolean getIsfixed() {
        return isfixed;
    }

    public void setIsfixed(Boolean isfixed) {
        this.isfixed = isfixed;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public AbleDeviceErrors(String sn, Boolean recoverable, String device, String fault) {
        this.sn = sn;
        this.recoverable = recoverable;
        this.device = device;
        this.fault = fault;
    }

    public AbleDeviceErrors(){}
}