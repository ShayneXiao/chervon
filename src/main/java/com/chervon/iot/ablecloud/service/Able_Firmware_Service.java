package com.chervon.iot.ablecloud.service;

import com.chervon.iot.ablecloud.model.Able_Relationship;
import com.chervon.iot.ablecloud.model.Able_ResponseBody;

import java.util.Map;

/**
 * Created by ZAC on 2017-7-31.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
public interface Able_Firmware_Service {
    Able_ResponseBody selectFirmwareByDeviceId(Map<String, String> paramMap) throws Exception;

    Able_Relationship selectDeviceByFirmwareId(Map<String, String> paramMap);

    Able_ResponseBody updateFirmware(Map<String, String> paramMap) throws Exception;
}
