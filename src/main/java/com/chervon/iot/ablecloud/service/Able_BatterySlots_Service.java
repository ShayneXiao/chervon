package com.chervon.iot.ablecloud.service;

import org.springframework.http.ResponseEntity;

import java.io.IOException;

/**
 * Created by 喷水君 on 2017/7/26.
 */
public interface Able_BatterySlots_Service {
   ResponseEntity<?> batterySlots(String Authorization,String device_id,int pageNumber,int pageSize)throws IOException,Exception;
   String selectDeviceId(String battery_slot_id)throws Exception;
   ResponseEntity<?> batterySlot(String Authorization,String battery_slot_id)throws Exception;
}
