package com.chervon.iot.ablecloud.service;

import org.springframework.data.domain.Pageable;

/**
 * Created by 喷水君 on 2017/7/26.
 */
public interface Able_Outlet_Service {
    Object selectOutletList(String Authorization, String device_id, Pageable pageable) throws Exception;

    Object selectOutletByOutletId(String Authorization, String outlet_id) throws Exception;

    Object updateOutletByOutletId(String Authorization, String outlet_id, String status) throws Exception;

    Object selectDeviceByOutletId(String Authorization, String outlet_id);
}
