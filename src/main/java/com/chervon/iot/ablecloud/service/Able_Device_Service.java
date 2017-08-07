package com.chervon.iot.ablecloud.service;

import com.chervon.iot.ablecloud.model.Able_Device;
import com.chervon.iot.ablecloud.model.Able_Relationship;
import com.chervon.iot.ablecloud.model.Able_ResponseListBody;
import com.chervon.iot.mobile.model.Mobile_User;

/**
 * Created by ZAC on 2017-7-27.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
public interface Able_Device_Service {
    Able_ResponseListBody selectDeviceList(Mobile_User mobileUser, Integer number, Integer size) throws Exception;

    Object selectDeviceByDeviceId(Able_Device device, Mobile_User mobileUser) throws Exception;

    Object updateDevice(Able_Device device,Mobile_User mobileUser, String updateStatus) throws Exception;

    void deleteByDeviceId(String device_id);

    Object selectCreatorByDeviceId(Able_Device device, Mobile_User mobileUser);

    Able_Relationship selectOutletsByDeviceId(Able_Device device) throws Exception;

    Able_Relationship selectEventByDeviceId(Able_Device device);

    Able_Relationship selectDeviceErrorsByDeviceId(Able_Device device);

    Able_Relationship selectFirmwareByDeviceId(Able_Device device);

    Able_Relationship selectHeartbeatByDeviceId(Able_Device device);
}
