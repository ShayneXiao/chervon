package com.chervon.iot.ablecloud.controller;

import com.chervon.iot.ablecloud.mapper.Able_DeviceMapper;
import com.chervon.iot.ablecloud.model.AbleDeviceErrors;
import com.chervon.iot.ablecloud.model.Able_Device;
import com.chervon.iot.ablecloud.service.Able_DeviceErrorsService;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.chervon.iot.mobile.service.Mobile_UserLoginService;
import com.chervon.iot.mobile.util.JavaMailUtil;
import com.chervon.iot.mobile.util.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Shayne on 2017/8/1.
 */
/**
 * Created by Shayne on 2017/8/1.
 */
@RestController
@RequestMapping("/api/v1")
public class Able_DeviceErrorsController {
    @Resource
    private Able_DeviceErrorsService able_deviceErrorsService;
    @Autowired
    private Mobile_UserLoginService mobile_userLoginService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private Able_DeviceMapper able_deviceMapper;

    @PostMapping("/devices/createDeviceError")
    public Map createDeviceError(String sn, long timestamp, boolean recoverable, String device, String fault) {
        AbleDeviceErrors ableDeviceErrors = new AbleDeviceErrors(sn, recoverable, device, fault);
        Date date = new Date(timestamp);
        ableDeviceErrors.setTimestamp(date);
        ableDeviceErrors.setStatus("pending");
        return able_deviceErrorsService.createDeviceError(ableDeviceErrors);
    }

    @GetMapping("/devices/{device_id}/device_errors")
    public ResponseEntity<?> getDeviceErrors(@RequestHeader("Authorization")String authorization, @PathVariable("device_id") String device_id,
                                             @RequestParam("page[number]") Integer pageNumber, @RequestParam("page[size]") Integer pageSize) throws Exception {
        authorization = authorization.substring(7);
        String email = jwtTokenUtil.getEmailFromToken(authorization);
        ValueOperations<String, Object> valueOperations =  redisTemplate.opsForValue();
        Mobile_User mobile_user = (Mobile_User)valueOperations.get(email);
        if(mobile_user != null){
            if("unverified".equals(mobile_user.getStatus())){
                return this.falierResult(authorization);
            }
            Able_Device able_device= able_deviceMapper.selectByDeviceUserSfId(mobile_user.getSfdcId(), device_id);
            if(able_device == null){
                return this.falierResult(authorization);
            }else{
                throw new Exception();
            }
        }
        return able_deviceErrorsService.getDeviceErrors(device_id, pageNumber, pageSize);
    }

    @GetMapping("/device_errors/{device_error_id}")
    public ResponseEntity<?> getDeviceErrorByDeviceErrorID(@RequestHeader("Authorization")String authorization, @PathVariable("device_error_id") Integer device_error_id) throws Exception {
        return able_deviceErrorsService.getDeviceErrorByDeviceErrorID(authorization, device_error_id);
    }

    @PostMapping("/devices/endedDeviceError")
    public Map endedDeviceError(String sn, boolean recoverable, String device, String fault, String status){
        return able_deviceErrorsService.endedDeviceError(sn, recoverable, device, fault, status);
    }

    private ResponseEntity<?> falierResult (String authorization){
        Map data = new HashMap(); List list = new ArrayList(); Map map = new HashMap();;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/vnd.api+json");
        map.put("status", "403");
        map.put("title", "You cannot perform this action.");
        map.put("message", null);
        Map source = new HashMap();
        source.put("pointer", "");
        map.put("source", source);
        list.add(map);
        data.put("errors", list);
        headers.add("Authorization", authorization);
        return new ResponseEntity<Object>(data,headers, HttpStatus.FORBIDDEN);
    }
}
