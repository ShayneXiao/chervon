package com.chervon.iot.ablecloud.service;

import com.chervon.iot.ablecloud.model.Able_Relationship;
import com.chervon.iot.ablecloud.model.Able_ResponseListBody;

/**
 * Created by ZAC on 2017-7-27.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
public interface Able_Device_Service {
    Able_ResponseListBody selectDeviceList(String email, Integer number, Integer size) throws Exception;

    Object selectDeviceByDeviceId(String deviceId) throws Exception;

    void deleteByDeviceId(String device_id);

    Object selectCreatorByDeviceId(String device_id);

    Able_Relationship selectOutletsByDeviceId(String device_id) throws Exception;

    Able_Relationship selectEventByDeviceId(String device_id);

    Able_Relationship selectDeviceErrorsByDeviceId(String device_id);

    Able_Relationship selectFirmwareByDeviceId(String device_id);

    Able_Relationship selectHeartbeatByDeviceId(String device_id);

    Object updateDevice(String device_id, String updateStatus) throws Exception;
}
