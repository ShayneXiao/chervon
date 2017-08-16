package com.chervon.iot.ablecloud.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Created by ZAC on 2017-7-27.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
public interface Able_Device_Service {
    Object selectDeviceList(String Authorization, Integer number, Integer size) throws Exception;

    Object selectDeviceByDeviceId(String Authorization, String device_id) throws Exception;

    Object updateDevice(String Authorization, String device_id, String updateStatus) throws Exception;

    ResponseEntity deleteByDeviceId(String authorization, String device_id);

    Object selectCreatorByDeviceId(String Authorization, String device_id);

    Object selectOutletsByDeviceId(String Authorization, String device_id) throws Exception;

    Object selectEventByDeviceId(String Authorization, String device_id);

    Object selectDeviceErrorsByDeviceId(String Authorization, String device_id);

    Object selectFirmwareByDeviceId(String authorization, String device);

    Object createDevice(String Authorization, String device_id, Map<String, Object> deviceParam) throws Exception;
}
