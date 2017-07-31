package com.chervon.iot.ablecloud.controller;

import com.chervon.iot.ablecloud.service.Able_DeviceService;
import com.chervon.iot.mobile.model.entity.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
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
public class Able_DeviceController {
    @Autowired
    private Able_DeviceService deviceService;
    @Autowired
    private ResponseBody responseBody;

    /**
     * 分页查询device
     * @param number
     * @param size
     * @return
     */
    @RequestMapping(value = "/devices", method = RequestMethod.GET)
    public ResponseEntity<?> listDevice(@RequestHeader String Authorization, @RequestParam String number, @RequestParam String size) {


        return null;
    }

    /**
     * 根据device_id查询具体某一个device
     * @param device_id
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}", method = RequestMethod.GET)
    public ResponseEntity<?> readDevice(@PathVariable String device_id) {
//        deviceService.selectByDeviceId(device_id);

        return null;
    }

    /**
     * 根据device_id更新具体某一个device
     * @param device_id
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateDevice(@PathVariable String device_id) {

        return null;
    }

    /**
     * 根据device_id删除具体某一个device
     * @param device_id
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDevice(@PathVariable String device_id) {

        return null;
    }

    /**
     * 根据device_id关联查询user
     * @param device_id
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/creator", method = RequestMethod.GET)
    public ResponseEntity<?> relationshipCreator(@PathVariable String device_id) {

        return null;
    }

    /**
     * 根据device_id关联查询outlets
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/outlets", method = RequestMethod.GET)
    public ResponseEntity<?> relationshipOutlets() {

        return null;
    }

    /**
     * 根据device_id关联查询event
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/events", method = RequestMethod.GET)
    public ResponseEntity<?> relationshipEvents() {

        return null;
    }

    /**
     * 根据device_id关联查询device errors
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/device_errors", method = RequestMethod.GET)
    public ResponseEntity<?> relationshipDeviceErrors() {

        return null;
    }

    /**
     * 根据device_id关联查询firmware
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/firmware", method = RequestMethod.GET)
    public ResponseEntity<?> relationshipFirmware() {

        return null;
    }

    /**
     * 根据device_id关联查询heartbeat
     * @return
     */
    @RequestMapping(value = "/devices/{device_id}/relationships/heartbeat", method = RequestMethod.GET)
    public ResponseEntity<?> relationshipHeartbeat() {

        return null;
    }
}
