package com.chervon.iot.ablecloud.controller;
import com.chervon.iot.ablecloud.mapper.Able_DeviceMapper;
import com.chervon.iot.ablecloud.model.Able_Device;
import com.chervon.iot.ablecloud.service.Able_BatterySlots_Service;
import com.chervon.iot.common.common_util.HttpHeader;
import com.chervon.iot.common.exception.ResultMsg;
import com.chervon.iot.common.exception.ResultStatusCode;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.chervon.iot.mobile.service.imp.Mobile_UserLoginServiceImp;
import com.chervon.iot.mobile.util.ErrorResponseUtil;
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
    @Autowired
    private Able_DeviceMapper able_deviceMapper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private Mobile_UserLoginServiceImp mobile_userLoginServiceImp;
    /**
     * 单个设备下所有得电池#@RequestParam(value = "page[number]",required = false)Integer pageNumber,@RequestParam(value = "page[size]",required = false)Integer pageSize
     **/
    @GetMapping("/devices/{device_id}/battery_slots")
    public ResponseEntity<?> batterySlots(@RequestHeader String Authorization,@PathVariable String device_id,@RequestParam(value = "page[number]",required = false)
            Integer pageNumber,@RequestParam(value = "page[size]",required = false)Integer pageSize )throws Exception{
        return   able_batterySlots_service.batterySlots(Authorization,device_id,pageNumber,pageSize);

    }
    //指定电池包数据
    @GetMapping("/battery_slots/{battery_slot_id}")
    public  ResponseEntity<?> batterySlot(@RequestHeader String Authorization,@PathVariable String battery_slot_id)throws Exception {
        return able_batterySlots_service.batterySlot(Authorization,battery_slot_id);

    }
    //关联
    @RequestMapping(value = "/battery_slots/{battery_slot_id}/relationships/device")
    public  ResponseEntity<?> batterySlotRelationship(@RequestHeader String Authorization, @PathVariable String battery_slot_id)throws Exception {
        String device_id=able_batterySlots_service.selectDeviceId(battery_slot_id);
        HttpHeaders httpHeaders=HttpHeader.HttpHeader();
        httpHeaders.add("Authorization",Authorization);
            Able_Device able_device = able_deviceMapper.selectByPrimaryKey(device_id);
            String email = jwtTokenUtil.getEmailFromToken(Authorization.substring(7));
            Mobile_User mobile_user = mobile_userLoginServiceImp.getUserByEmail(email);
            if (!mobile_user.getSfdcId().equals(able_device.getUsersfid())) {
                ResultMsg resultMsg = ErrorResponseUtil.forbidend();
                return new ResponseEntity(resultMsg, httpHeaders, HttpStatus.valueOf(ResultStatusCode.SC_FORBIDDEN.getErrcode()));
            }
        Map<String,Object>  respMap = new HashMap<>();
        Map<String,String> links = new HashMap<>();
        Map<String,String> data = new HashMap<>();
        links.put("self",Base_Url+"battery_slots/"+battery_slot_id+"/relationships");
        links.put("related",Base_Url+"battery_slots"+battery_slot_id+"/device");
        data.put("type","device");

        data.put("id",device_id);

        respMap.put("links",links);
        respMap.put("data",data);
        return new ResponseEntity<Object>(respMap,httpHeaders,HttpStatus.OK);
    }
}
