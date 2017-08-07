package com.chervon.iot.ablecloud.service;

import com.chervon.iot.ablecloud.model.Able_ResponseBody;

import java.util.Map;

/**
 * Created by Admin on 2017/7/31.
 */
public interface Able_UploadData_Service {
    Able_ResponseBody uploadData(Map<String, String> paramMap) throws Exception;
}
