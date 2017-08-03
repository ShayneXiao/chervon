package com.chervon.iot.ablecloud.controller;

import com.chervon.iot.ablecloud.model.AbleDeviceErrors;
import com.chervon.iot.ablecloud.service.Able_DeviceErrorsService;
import com.chervon.iot.mobile.util.JavaMailUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

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

    @PostMapping("/devices/createDeviceError")
    public Map createDeviceError(String sn, long timestamp, boolean recoverable, String device, String fault) {
        AbleDeviceErrors ableDeviceErrors = new AbleDeviceErrors(sn, recoverable, device, fault);
        Date date = new Date(timestamp);
        ableDeviceErrors.setTimestamp(date);
        ableDeviceErrors.setStatus("pending");
        return able_deviceErrorsService.createDeviceError(ableDeviceErrors);
    }

    @GetMapping("/devices/{device_id}/device_errors")
    public ResponseEntity<?> getDeviceErrors(@PathVariable("device_id") String device_id,
                                             @RequestParam("page[number]") Integer pageNumber, @RequestParam("page[size]") Integer pageSize) throws Exception {
        return able_deviceErrorsService.getDeviceErrors(device_id, pageNumber, pageSize);
    }

    @GetMapping("/device_errors/{device_error_id}")
    public ResponseEntity<?> getDeviceErrorByDeviceErrorID(@PathVariable("device_error_id") Integer device_error_id) throws Exception {
        return able_deviceErrorsService.getDeviceErrorByDeviceErrorID(device_error_id);
    }

    @PostMapping("/devices/endedDeviceError")
    public Map endedDeviceError(String sn, boolean recoverable, String device, String fault, String status){
        return able_deviceErrorsService.endedDeviceError(sn, recoverable, device, fault, status);
    }
}
