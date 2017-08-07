package com.chervon.iot.ablecloud.service.imp;

import com.chervon.iot.ablecloud.model.Able_ResponseBody;
import com.chervon.iot.ablecloud.service.Able_UploadData_Service;
import com.chervon.iot.ablecloud.util.LoadAndGetData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 2017/7/31.
 */
@Service
public class Able_UploadData_ServiceImp implements Able_UploadData_Service {
    @Autowired
    private LoadAndGetData loadAndGetData;

    public Able_ResponseBody uploadData(Map<String, String> paramMap) throws Exception {
        Able_ResponseBody responseBody = new Able_ResponseBody();

        //调用able端接口，上传数据
        String resultJson = loadAndGetData.getUploadDataResult(paramMap);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(resultJson);

        //able端返回结果，1表示上传成功，0表示上传失败
        Map<String, String> metaMap = new HashMap<>();
        if(jsonNode.get("result").asInt() == 1){
            metaMap.put("message","Upload received");
        }else if (jsonNode.get("result").asInt() == 0){
            metaMap.put("message","Upload unreceived");
        }

        responseBody.setMeta(metaMap);

        List<Object> includes = new ArrayList<>();
        responseBody.setIncluded(includes);
        return responseBody;
    }
}
