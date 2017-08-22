package com.chervon.iot.ablecloud.service;

/**
 * Created by 喷水君 on 2017/7/26.
 */
public interface Able_Outlet_Service {
    Object selectOutletList(String Authorization, String device_id, Integer pageNum, Integer pageSize) throws Exception;

    Object selectOutletByOutletId(String Authorization, String outlet_id) throws Exception;

    Object updateOutletByOutletId(String Authorization, String outlet_id, String status) throws Exception;

    Object selectDeviceByOutletId(String Authorization, String outlet_id);
}
