package com.chervon.iot.ablecloud.model;

/**
 * Created by 喷水君 on 2017/7/28.
 */
public class Able_Battery {
    private String battery_id;
    private String battery_name;
    private String device_id;

    public String getBattery_id() {
        return battery_id;
    }

    public void setBattery_id(String battery_id) {
        this.battery_id = battery_id;
    }

    public String getBattery_name() {
        return battery_name;
    }

    public void setBattery_name(String battery_name) {
        this.battery_name = battery_name;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
