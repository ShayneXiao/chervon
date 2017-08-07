package com.chervon.iot.ablecloud.service;

import com.chervon.iot.ablecloud.model.Able_Device;
import com.chervon.iot.ablecloud.model.Able_Relationship;
import com.chervon.iot.ablecloud.model.Able_ResponseBody;
import com.chervon.iot.mobile.model.Mobile_User;

import java.util.Map;

/**
 * Created by ZAC on 2017-7-31.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
public interface Able_Firmware_Service {
    Able_ResponseBody selectFirmwareByDeviceId(Able_Device device, Mobile_User mobileUser) throws Exception;

    Able_ResponseBody updateFirmware(Mobile_User user,Able_Device device,
                                     String targetVersion) throws Exception;

    Able_Relationship selectDeviceByFirmwareId(String firmware_id);
}
