package com.chervon.iot.ablecloud.controller;
import com.chervon.iot.ablecloud.service.Able_BatterySlots_Service;
import com.chervon.iot.common.common_util.HttpHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 喷水君 on 2017/7/26.
 */
@RestController
@RequestMapping(value = "/api/v1")
public class Able_BatterySlots_Controller {

    @Value(value = "${relation_BaseLink}")
    private String Base_Url;
    @Autowired
    private Able_BatterySlots_Service able_batterySlots_service;
/**
 * 单个设备下所有得电池#@RequestParam(value = "page[number]",required = false)Integer pageNumber,@RequestParam(value = "page[size]",required = false)Integer pageSize
 **/
    @GetMapping("/devices/{device_id}/battery_slots")
    public ResponseEntity<?> batterySlots(@RequestHeader String Authorization,@PathVariable String device_id,@RequestParam(value = "page[number]",required = false)
            Integer pageNumber,@RequestParam(value = "page[size]",required = false)Integer pageSize )throws Exception{
         return   able_batterySlots_service.batterySlots(Authorization,device_id,pageNumber,pageSize);

    }

    @RequestMapping(value = "/battery_slots/{battery_slot_id}")
    public  ResponseEntity<?> batterySlot(@PathVariable String battery_slot_id) {
        return null;
    }
    @RequestMapping(value = "/battery_slots/{battery_slot_id}/relationships/device")
    public  ResponseEntity<?> batterySlotRelationship(@RequestHeader String Authorization, @PathVariable String battery_slot_id) {
        Map<String,String> links = new HashMap<>();
        Map<String,String> data = new HashMap<>();
        links.put("self",Base_Url+"battery_slots/"+battery_slot_id+"/relationships");
        links.put("related",Base_Url+"battery_slots"+battery_slot_id+"/device");
        data.put("type","device");
        able_batterySlots_service.selectDeviceId(battery_slot_id);
        data.put("id",battery_slot_id);
       return null;
    }
}
