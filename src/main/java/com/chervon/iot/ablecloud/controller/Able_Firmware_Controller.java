package com.chervon.iot.ablecloud.controller;

import com.chervon.iot.ablecloud.mapper.Able_DeviceMapper;
import com.chervon.iot.ablecloud.model.Able_Device;
import com.chervon.iot.ablecloud.model.Able_Relationship;
import com.chervon.iot.ablecloud.model.Able_ResponseBody;
import com.chervon.iot.ablecloud.service.Able_Firmware_Service;
import com.chervon.iot.ablecloud.util.DeviceUtils;
import com.chervon.iot.common.common_util.HttpHeader;
import com.chervon.iot.mobile.mapper.Mobile_UserMapper;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZAC on 2017-7-31.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
@RestController
@RequestMapping("/api/v1/")
public class Able_Firmware_Controller {
    @Autowired
    private Able_Firmware_Service firmwareService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private Mobile_UserMapper userMapper;
    @Autowired
    private Able_DeviceMapper deviceMapper;

    /**
     * 查询firmware是否有更新
     * @param Authorization
     * @param device_id
     * @return
     */
    @RequestMapping(value = "devices/{device_id}/firmware",method = RequestMethod.GET)
    public ResponseEntity readFirmware(@RequestHeader String Authorization, @PathVariable String device_id) throws Exception {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);

        /**用户是否操作的是自己的device，用户是否已验证*/
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);
        ResponseEntity<?> checkResponse = DeviceUtils.check(mobileUser, device, Authorization);
        if (checkResponse != null) {
            return checkResponse;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("email", email);
        paramMap.put("device_id", device_id);

        //获得responseBody
        Able_ResponseBody responseBody = firmwareService.selectFirmwareByDeviceId(device,mobileUser);

        //获得httpHeader
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization","Bearer " + Authorization);

        return new ResponseEntity(responseBody,headers, HttpStatus.OK);
    }

    /**
     * 更新firmware
     * @param Authorization
     * @param device_id
     * @return
     */
    @RequestMapping(value = "devices/{device_id}/firmware",method = RequestMethod.POST)
    public ResponseEntity updateFirmware(@RequestHeader String Authorization,
                                         @PathVariable String device_id,
                                         @RequestBody String requestJson) throws Exception {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);

        /**用户是否操作的是自己的device，用户是否已验证*/
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);
        ResponseEntity<?> checkResponse = DeviceUtils.check(mobileUser, device, Authorization);
        if (checkResponse != null) {
            return checkResponse;
        }

        JsonNode requestJsonNode = jsonMapper.readTree(requestJson);
        String version = requestJsonNode.get("data").get("version").asText();

        //获得responseBody
        Able_ResponseBody responseBody = firmwareService.updateFirmware(mobileUser,device,version);

        //获得httpHeader
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization","Bearer " + Authorization);

        return new ResponseEntity(responseBody,headers, HttpStatus.OK);
    }

    /**
     * 根据firmware_id查询与之关联的device
     * @param Authorization
     * @param firmware_id
     * @return
     */
    @RequestMapping(value = "firmwares/{firmware_id}/relationships/device",method = RequestMethod.GET)
    public ResponseEntity relationShipDevice(@RequestHeader String Authorization, @PathVariable String firmware_id) {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);

        /**判断用户与device是否为空，用户是否操作的是自己的device，用户是否已验证*/
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        String device_id = firmware_id.replace("firmwares_", "").trim();
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);
        ResponseEntity<?> checkResponse = DeviceUtils.check(mobileUser, device, Authorization);
        if (checkResponse != null) {
            return checkResponse;
        }

        //获得responseBody
        Able_Relationship responseBody = firmwareService.selectDeviceByFirmwareId(firmware_id);

        //获得httpHeader
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization","Bearer " + Authorization);

        return new ResponseEntity(responseBody,headers,HttpStatus.OK);
    }
}
