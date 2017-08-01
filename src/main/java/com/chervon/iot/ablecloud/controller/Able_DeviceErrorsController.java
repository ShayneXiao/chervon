package com.chervon.iot.ablecloud.controller;

import com.chervon.iot.ablecloud.model.AbleDeviceErrors;
import com.chervon.iot.ablecloud.service.Able_DeviceErrorsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by Shayne on 2017/8/1.
 */
@RestController
@RequestMapping("/api/v1")
public class Able_DeviceErrorsController {
    @Resource
    private Able_DeviceErrorsService able_deviceErrorsService;

    @RequestMapping("/devices/createDeviceError")
    public ResponseEntity<?> createDeviceError(AbleDeviceErrors ableDeviceErrors){
        return able_deviceErrorsService.createDeviceError(ableDeviceErrors);
    }

    @RequestMapping("/devices/{device_id}/device_errors")
    public ResponseEntity<?> getDeviceErrors(@PathVariable("device_id")String device_id,
                                             @RequestParam("page[number]")Integer pageNumber, @RequestParam("page[size]")Integer pageSize) throws Exception {
        return able_deviceErrorsService.getDeviceErrors(device_id, pageNumber, pageSize);
    }
}
