package com.chervon.iot.ablecloud.service;

/**
 * Created by ZAC on 2017-7-31.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
public interface Able_Firmware_Service {
    Object selectFirmwareByDeviceId(String Authorization, String device_id) throws Exception;

    Object updateFirmware(String Authorization, String device_id, String targetVersion) throws Exception;

    Object selectDeviceByFirmwareId(String Authorization, String firmware_id);
}
