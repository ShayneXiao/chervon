package com.chervon.iot.ablecloud.service;

import com.chervon.iot.ablecloud.model.AbleDeviceErrors;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Created by Shayne on 2017/8/1.
 */
public interface Able_DeviceErrorsService {
    Map createDeviceError(AbleDeviceErrors ableDeviceErrors);

    ResponseEntity<?> getDeviceErrors(String device_id, int pageNumber, int PageSize) throws Exception;

    ResponseEntity<?> getDeviceErrorByDeviceErrorID(String authorization, Integer device_error_id) throws Exception;

    Map endedDeviceError(String sn, boolean recoverable, String device, String fault, String status);
}
