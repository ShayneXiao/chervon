package com.chervon.iot.ablecloud.controller;

import com.chervon.iot.ablecloud.model.Able_ResponseBody;
import com.chervon.iot.ablecloud.service.Able_UploadData_Service;
import com.chervon.iot.common.common_util.HttpHeader;
import com.chervon.iot.common.exception.ResultMsg;
import com.chervon.iot.mobile.util.ErrorResponseUtil;
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
 * Created by Admin on 2017/7/31.
 */
@RestController
@RequestMapping(value = "/api/v1")
public class Able_UploadData_Controller {
    @Autowired
    private Able_UploadData_Service ableUpdataData;

    /**
     *Status/Statistics Payloads Endpoints
     */
    @RequestMapping(value = {"/status_payloads","/statistic_payloads"},method= RequestMethod.POST)
    public ResponseEntity<?> statusPayloads(@RequestBody String jsonData) throws Exception {
        //返回头
        HttpHeaders headers = HttpHeader.HttpHeader();

        //解析请求体
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonData);

        Able_ResponseBody responseBody = null;
        //封装uploadData参数
        Map<String, String> paramMap = null;
        try {
            paramMap = parseRequestBody(jsonNode);
        } catch (Exception e) {
            ResultMsg resultMsg = ErrorResponseUtil.errorFiled();
            return new ResponseEntity(resultMsg,headers,HttpStatus.UNPROCESSABLE_ENTITY);
        }

        //返回体
        responseBody = ableUpdataData.uploadData(paramMap);
        return new ResponseEntity(responseBody, headers, HttpStatus.OK);
    }

    private Map<String, String> parseRequestBody(JsonNode jsonNode) throws Exception {
        Map<String,String> paramMaps = new HashMap<>();
        try {
            paramMaps.put("payload", jsonNode.get("data").get("attributes").get("payload").asText());
            paramMaps.put("timestamp", jsonNode.get("data").get("attributes").get("timestamp").asText());
            paramMaps.put("sn", jsonNode.get("data").get("attributes").get("sn").asText());
        } catch (Exception e) {
            throw new Exception(e);
        }

        return paramMaps;
    }
}
