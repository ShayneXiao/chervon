package com.chervon.iot.ablecloud.service;

import com.chervon.iot.ablecloud.model.AbleDeviceErrors;
import org.springframework.http.ResponseEntity;

/**
 * Created by Shayne on 2017/8/1.
 */
public interface Able_DeviceErrorsService {
    ResponseEntity<?> createDeviceError(AbleDeviceErrors ableDeviceErrors);

    ResponseEntity<?> getDeviceErrors(String device_id, int pageNumber, int PageSize) throws Exception;
}
