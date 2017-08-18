package com.chervon.iot.ablecloud.controller;

import com.chervon.iot.ablecloud.service.Able_Device_Service;
import com.chervon.iot.common.common_util.HttpHeader;
import com.chervon.iot.mobile.sercuity.filter.ApiAuthentication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by ZAC on 2017-7-27.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
@RestController
@RequestMapping("/api/v1")
public class Able_Device_Controller {
    @Autowired
    private Able_Device_Service deviceService;
    @Autowired
    private ObjectMapper jsonMapper;

    /**
     * 分页查询device
     * @param number
     * @param size
     * @return
     */
    @RequestMapping(value = "/devices", method = RequestMethod.GET)
    @ApiAuthentication
    public ResponseEntity<?> listDevice(@RequestHeader String Authorization,
                                        @RequestParam(value = "page[number]") String number,
                                        @RequestParam(value = "page[size]",required = false)String size) throws Exception {
        int pageNum = 0;
        int pageSize = 0;
        try {
            pageNum = Integer.parseInt(number);
            pageSize = Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("类型匹配异常");
        }
        /**获得responseBody，或response*/
        Object responseBody =
                deviceService.selectDeviceList(Authorization, pageNum, pageSize);

        if (responseBody == null) {

        }
        if (responseBody instanceof ResponseEntity) {
            return (ResponseEntity<?>) responseBody;
        }

        /**设置响应头*/
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization",Authorization);
        return new ResponseEntity<Object>(responseBody,headers, HttpStatus.OK);
    }

    /**
     * 根据device_id查询具体某一个device
     * @param device_id
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}", method = RequestMethod.GET)
    @ApiAuthentication
    public ResponseEntity<?> readDevice(@RequestHeader String Authorization, @PathVariable String device_id) throws Exception {
        /**获得responseBody，或response*/
        Object responseBody = deviceService.selectDeviceByDeviceId(Authorization,device_id);
        if (responseBody instanceof ResponseEntity) {
            return (ResponseEntity<?>) responseBody;
        }

        /**设置响应头*/
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization",Authorization);

        return new ResponseEntity<Object>(responseBody,headers, HttpStatus.OK);
    }

    /**
     * 根据device_id更新具体某一个device
     * @param device_id
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}", method = RequestMethod.PATCH)
    @ApiAuthentication
    public ResponseEntity<?> updateDevice(@RequestHeader String Authorization,
                                          @PathVariable String device_id,
                                          @RequestBody String requestJson) throws Exception {
        JsonNode requestJsonNode = jsonMapper.readTree(requestJson);
        if (requestJsonNode.has("data")) {

        }
        String updateStatus = requestJsonNode.get("data").get("attributes").get("status").asText();

        /**获得responseBody，或response*/
        Object responseBody = deviceService.updateDevice(Authorization, device_id, updateStatus);
        if (responseBody instanceof ResponseEntity) {
            return (ResponseEntity<?>) responseBody;
        }

        /**获取httpHeader*/
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization",Authorization);

        return new ResponseEntity<Object>(responseBody,headers, HttpStatus.OK);
    }

    /**
     * 根据device_id删除具体某一个device
     * @param Authorization,device_id
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}", method = RequestMethod.DELETE)
    @ApiAuthentication
    public ResponseEntity<?> deleteDevice(@RequestHeader String Authorization, @PathVariable String device_id) {
        /**获得responseBody,或response*/
        ResponseEntity responseBody = deviceService.deleteByDeviceId(Authorization, device_id);
        if (responseBody != null) {
            return responseBody;
        }

        return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
    }

    /**
     * 根据device_id关联查询user
     * @param device_id
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/creator", method = RequestMethod.GET)
    @ApiAuthentication
    public ResponseEntity<?> relationshipCreator(@RequestHeader String Authorization, @PathVariable String device_id) {
        /**获得responseBody,或response*/
        Object responseBody = deviceService.selectCreatorByDeviceId(Authorization,device_id);
        if (responseBody instanceof ResponseEntity) {
            return (ResponseEntity<?>) responseBody;
        }

        /**获取httpHeader*/
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization",Authorization);

        return new ResponseEntity<Object>(responseBody,headers, HttpStatus.OK);
    }

    /**
     * 根据device_id关联查询outlets
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/outlets", method = RequestMethod.GET)
    @ApiAuthentication
    public ResponseEntity<?> relationshipOutlets(@RequestHeader String Authorization, @PathVariable String device_id) throws Exception {
        /**获得responseBody,或response*/
        Object responseBody = deviceService.selectOutletsByDeviceId(Authorization, device_id);
        if (responseBody instanceof ResponseEntity) {
            return (ResponseEntity<?>) responseBody;
        }

        /**获取httpHeader*/
        HttpHeaders header = HttpHeader.HttpHeader();
        header.add("Authorization",Authorization);

        return new ResponseEntity<Object>(responseBody,header, HttpStatus.OK);
    }

    /**
     * 根据device_id关联查询event
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/events", method = RequestMethod.GET)
    @ApiAuthentication
    public ResponseEntity<?> relationshipEvents(@RequestHeader String Authorization, @PathVariable String device_id) {
        /**获得responseBody,或response*/
        Object responseBody = deviceService.selectEventByDeviceId(Authorization, device_id);
        if (responseBody instanceof ResponseEntity) {
            return (ResponseEntity<?>) responseBody;
        }

        /**获取httpHeader*/
        HttpHeaders header = HttpHeader.HttpHeader();
        header.add("Authorization",Authorization);

        return new ResponseEntity<Object>(responseBody,header, HttpStatus.OK);
    }

    /**
     * 根据device_id关联查询device errors
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/device_errors", method = RequestMethod.GET)
    @ApiAuthentication
    public ResponseEntity<?> relationshipDeviceErrors(@RequestHeader String Authorization, @PathVariable String device_id) {
        /**获得responseBody,或response*/
        Object responseBody = deviceService.selectDeviceErrorsByDeviceId(Authorization, device_id);
        if (responseBody instanceof ResponseEntity) {
            return (ResponseEntity<?>) responseBody;
        }

        /**获取httpHeader*/
        HttpHeaders header = HttpHeader.HttpHeader();
        header.add("Authorization",Authorization);

        return new ResponseEntity<Object>(responseBody,header, HttpStatus.OK);
    }

    /**
     * 根据device_id关联查询firmware
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/firmware", method = RequestMethod.GET)
    @ApiAuthentication
    public ResponseEntity<?> relationshipFirmware(@RequestHeader String Authorization, @PathVariable String device_id) {
        /**获得responseBody,或response*/
        Object responseBody = deviceService.selectFirmwareByDeviceId(Authorization, device_id);
        if (responseBody instanceof ResponseEntity) {
            return (ResponseEntity<?>) responseBody;
        }

        /**获取httpHeader*/
        HttpHeaders header = HttpHeader.HttpHeader();
        header.add("Authorization",Authorization);

        return new ResponseEntity<Object>(responseBody,header, HttpStatus.OK);
    }
}
