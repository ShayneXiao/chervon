package com.chervon.iot.ablecloud.controller;

import com.chervon.iot.ablecloud.model.Able_Relationship;
import com.chervon.iot.ablecloud.model.Able_ResponseBody;
import com.chervon.iot.ablecloud.model.Able_ResponseListBody;
import com.chervon.iot.ablecloud.service.Able_Device_Service;
import com.chervon.iot.common.common_util.HttpHeader;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
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
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ObjectMapper jsonMapper;

    /**
     * 分页查询device
     * @param number
     * @param size
     * @return
     */
    @RequestMapping(value = "/devices", method = RequestMethod.GET)
    public ResponseEntity<?> listDevice(@RequestHeader String Authorization, @RequestParam String number, @RequestParam String size) throws Exception {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);
        int pageNum = 0;
        int pageSize = 0;
        try {
            pageNum = Integer.parseInt(number);
            pageSize = Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("类型匹配异常");
        }
        Able_ResponseListBody responseBody = deviceService.selectDeviceList(email, pageNum, pageSize);

        if (responseBody == null) {

        }

        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization","Bearer "+Authorization);
        return new ResponseEntity<Object>(responseBody,headers, HttpStatus.OK);
    }

    /**
     * 根据device_id查询具体某一个device
     * @param device_id
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}", method = RequestMethod.GET)
    public ResponseEntity<?> readDevice(@RequestHeader String Authorization, @PathVariable String device_id) throws Exception {
        System.out.println("----------------进入read device----------------");
        Object responseBody = deviceService.selectDeviceByDeviceId(device_id);

        if (responseBody == null) {

        }

        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization","Bearer " + Authorization);

        return new ResponseEntity<Object>(responseBody,headers,HttpStatus.OK);
    }

    /**
     * 根据device_id更新具体某一个device
     * @param device_id
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateDevice(@RequestHeader String Authorization,
                                          @PathVariable String device_id,
                                          @RequestBody String requestJson) throws Exception {
        JsonNode requestJsonNode = jsonMapper.readTree(requestJson);
        String updateStatus = requestJsonNode.get("data").get("attributes").get("status").asText();

        //获取responseBody
        Object responseBody = deviceService.updateDevice(device_id, updateStatus);

        //获取httpHeader
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization","Bearer " + Authorization);

        return new ResponseEntity<Object>(responseBody,headers,HttpStatus.OK);
    }

    /**
     * 根据device_id删除具体某一个device
     * @param device_id
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDevice(@PathVariable String device_id) {
        deviceService.deleteByDeviceId(device_id);

        return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
    }

    /**
     * 根据device_id关联查询user
     * @param device_id
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/creator", method = RequestMethod.GET)
    public ResponseEntity<Object> relationshipCreator(@RequestHeader String Authorization, @PathVariable String device_id) {
        Object responseBody = deviceService.selectCreatorByDeviceId(device_id);
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization","Bearer " + Authorization);
        return new ResponseEntity<Object>(responseBody,headers,HttpStatus.OK);
    }

    /**
     * 根据device_id关联查询outlets
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/outlets", method = RequestMethod.GET)
    public ResponseEntity<?> relationshipOutlets(@RequestHeader String Authorization, @PathVariable String device_id) throws Exception {
        Able_Relationship responseBody = deviceService.selectOutletsByDeviceId(device_id);

        HttpHeaders header = HttpHeader.HttpHeader();
        header.add("Authorization","Bearer " + Authorization);

        return new ResponseEntity<Object>(responseBody,header,HttpStatus.OK);
    }

    /**
     * 根据device_id关联查询event
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/events", method = RequestMethod.GET)
    public ResponseEntity<?> relationshipEvents(@RequestHeader String Authorization, @PathVariable String device_id) {
        Able_Relationship responseBody = deviceService.selectEventByDeviceId(device_id);

        HttpHeaders header = HttpHeader.HttpHeader();
        header.add("Authorization","Bearer " + Authorization);

        return new ResponseEntity<Object>(responseBody,header,HttpStatus.OK);
    }

    /**
     * 根据device_id关联查询device errors
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/device_errors", method = RequestMethod.GET)
    public ResponseEntity<?> relationshipDeviceErrors(@RequestHeader String Authorization, @PathVariable String device_id) {
        Able_Relationship responseBody = deviceService.selectDeviceErrorsByDeviceId(device_id);

        HttpHeaders header = HttpHeader.HttpHeader();
        header.add("Authorization","Bearer " + Authorization);

        return new ResponseEntity<Object>(responseBody,header,HttpStatus.OK);
    }

    /**
     * 根据device_id关联查询firmware
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/firmware", method = RequestMethod.GET)
    public ResponseEntity<?> relationshipFirmware(@RequestHeader String Authorization, @PathVariable String device_id) {
        Able_Relationship responseBody = deviceService.selectFirmwareByDeviceId(device_id);

        HttpHeaders header = HttpHeader.HttpHeader();
        header.add("Authorization","Bearer " + Authorization);

        return new ResponseEntity<Object>(responseBody,header,HttpStatus.OK);
    }

    /**
     * 根据device_id关联查询heartbeat
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/heartbeat", method = RequestMethod.GET)
    public ResponseEntity<?> relationshipHeartbeat(@RequestHeader String Authorization, @PathVariable String device_id) {
        Able_Relationship responseBody = deviceService.selectHeartbeatByDeviceId(device_id);

        HttpHeaders header = HttpHeader.HttpHeader();
        header.add("Authorization","Bearer " + Authorization);

        return new ResponseEntity<Object>(responseBody,header,HttpStatus.OK);
    }
}
