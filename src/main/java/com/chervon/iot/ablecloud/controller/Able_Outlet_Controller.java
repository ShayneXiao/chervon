package com.chervon.iot.ablecloud.controller;

import com.chervon.iot.ablecloud.service.Able_Outlet_Service;
import com.chervon.iot.ablecloud.util.DeviceUtils;
import com.chervon.iot.common.common_util.HttpHeader;
import com.chervon.iot.mobile.sercuity.filter.ApiAuthentication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by 喷水君 on 2017/7/26.
 * Modified by:Zack
 * Modified date:2017/8/17
 * Modified description:添加具体实现
 */
@RestController
@RequestMapping(value = "/api/v1")
public class Able_Outlet_Controller {
    @Autowired
    private Able_Outlet_Service outletService;
    @Autowired
    private ObjectMapper jsonMapper;

    /**
     * 根据device_id分页查询outlet集合
     * @param Authorization
     * @param device_id
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/outlets",method = RequestMethod.GET)
    @ApiAuthentication
    public ResponseEntity<?> outletsList(@RequestHeader String Authorization, @PathVariable String device_id , Pageable pageable) throws Exception {
        /**获得响应体或响应*/
        Object responseBody = outletService.selectOutletList(Authorization,device_id,pageable);

        /**设置响应头*/
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization",Authorization);
        return new ResponseEntity<Object>(responseBody,headers, HttpStatus.OK);
    }

    /**
     * 根据outlet_id查询具体的outlet
     * @param Authorization
     * @param outlet_id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/outlets/{outlet_id}",method = RequestMethod.GET)
    @ApiAuthentication
    public ResponseEntity<?> outletRead(@RequestHeader String Authorization,@PathVariable String outlet_id) throws Exception {
        /**获得响应体或响应*/
        Object responseBody = outletService.selectOutletByOutletId(Authorization, outlet_id);

        /**设置响应头*/
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization",Authorization);
        return new ResponseEntity<Object>(responseBody,headers, HttpStatus.OK);
    }

    /**
     * 根据outlet_id更新具体的outlet
     * @param Authorization
     * @param outlet_id
     * @return
     */
    @RequestMapping(value = "/outlets/{outlet_id}",method = RequestMethod.PATCH)
    @ApiAuthentication
    public ResponseEntity<?> outletUpdate(@RequestHeader String Authorization,@PathVariable String outlet_id,
                                          @RequestBody String jsonParam) throws Exception {
        JsonNode paramJsonNode = jsonMapper.readTree(jsonParam);
        String status;
        try {
            status = paramJsonNode.get("data").get("attributes").get("status").asText();
        } catch (Exception e) {
            return DeviceUtils.getFieldIsWrong();
        }

        /**获得响应体或响应*/
        Object responseBody = outletService.updateOutletByOutletId(Authorization, outlet_id, status);

        /**设置响应头*/
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization",Authorization);
        return new ResponseEntity<Object>(responseBody,headers, HttpStatus.OK);
    }

    /**
     * 根据outlet_id查询对应的device
     * @param Authorization
     * @param outlet_id
     * @return
     */
    @RequestMapping(value = "outlets/{outlet_id}/relationships/device",method = RequestMethod.GET)
    @ApiAuthentication
    public ResponseEntity outletRelationshipDevice(@RequestHeader String Authorization,@PathVariable String outlet_id) {
        /**获得响应体或响应*/
        Object responseBody = outletService.selectDeviceByOutletId(Authorization, outlet_id);

        /**设置响应头*/
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization",Authorization);
        return new ResponseEntity<Object>(responseBody,headers, HttpStatus.OK);
    }
}
