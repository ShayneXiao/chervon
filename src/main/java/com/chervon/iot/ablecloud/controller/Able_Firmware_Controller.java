package com.chervon.iot.ablecloud.controller;

import com.chervon.iot.ablecloud.model.Able_Relationship;
import com.chervon.iot.ablecloud.model.Able_ResponseBody;
import com.chervon.iot.ablecloud.service.Able_Firmware_Service;
import com.chervon.iot.common.common_util.HttpHeader;
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

    /**
     * 查询firmware是否有更新
     * @param Authorization
     * @param device_id
     * @return
     */
    @RequestMapping(value = "devices/{device_id}/firmware",method = RequestMethod.GET)
    public ResponseEntity readFirmware(@RequestHeader String Authorization, @PathVariable String device_id) throws Exception {
        Map<String, String> paramMap = new HashMap<>();
        String email = jwtTokenUtil.getEmailFromToken(Authorization);
        paramMap.put("email", email);
        paramMap.put("device_id", device_id);

        //获得responseBody
        Able_ResponseBody responseBody = firmwareService.selectFirmwareByDeviceId(paramMap);

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
        JsonNode requestJsonNode = jsonMapper.readTree(requestJson);
        String version = requestJsonNode.get("data").get("version").asText();

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("version", version);
        paramMap.put("device_id", device_id);

        //获得responseBody
        Able_ResponseBody responseBody = firmwareService.updateFirmware(paramMap);

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
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("firmware_id", firmware_id);

        //获得responseBody
        Able_Relationship responseBody = firmwareService.selectDeviceByFirmwareId(paramMap);

        //获得httpHeader
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization","Bearer " + Authorization);

        return new ResponseEntity(responseBody,headers,HttpStatus.OK);
    }
}
