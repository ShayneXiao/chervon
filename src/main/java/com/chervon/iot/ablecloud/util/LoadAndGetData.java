package com.chervon.iot.ablecloud.util;

import com.chervon.iot.common.util.GetUTCTime;
import com.chervon.iot.common.util.HttpClientUtil;
import com.chervon.iot.mobile.util.JsonUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 2017/7/27.
 */
@Component
public class LoadAndGetData {
    private final String ablecloudUrl = "http://test.ablecloud.cn:5000/chervonService/v1/";

    public static void main(String[] args){
//        LoadAndGetData lg = new LoadAndGetData();
//        try {
//            String test = lg.getDataResult("NLM011107030201X", "batStatics4");
//            System.out.println(test);
//            System.out.println();
//        }catch (Exception e){
//
//        }
    }

    public String getUploadDataResult(Map<String, String> paramMap) throws SQLException, Exception{
        //获取当前时间戳
        GetUTCTime getUTCTime = new GetUTCTime();
        long timeStamp = getUTCTime.getCurrentUTCTimeStr();

        //封装请求头
        Map<String,String> headMaps = HttpUtils.getHeadMaps(timeStamp,"uploadData");

        //封装请求体
        Map<String,String> requsetBody = new HashMap<>();
        requsetBody.put("sn",paramMap.get("sn"));
        requsetBody.put("payLoad",paramMap.get("payload"));
        String requestJson = JsonUtils.objectToJson(requsetBody);

        //发送请求，并获取结果
        String  jsonData= HttpClientUtil.doPostJson(ablecloudUrl+"uploadData",requestJson,headMaps);
        return jsonData;
    }

    public String getDataResult(Map<String, String> paramMap) throws SQLException, Exception{
        //获取当前时间戳
        GetUTCTime getUTCTime = new GetUTCTime();
        long timeStamp = getUTCTime.getCurrentUTCTimeStr();

        //封装请求头
        Map<String,String> headMaps = HttpUtils.getHeadMaps(timeStamp,"getData");

        //封装请求体
        Map<String,String> requsetBody = new HashMap<>();
        requsetBody.put("sn",paramMap.get("sn"));
        requsetBody.put("type",paramMap.get("type"));
        String requestJson = JsonUtils.objectToJson(requsetBody);

        //发送请求，并获得结果
        String  jsonData= HttpClientUtil.doPostJson(ablecloudUrl+"getData",requestJson,headMaps);
        return jsonData;
    }

    /**
     * 获取多个device的信息
     * @param deviceSNList
     * @return
     */
    public String getDataList(List<String> deviceSNList) throws Exception {
        //获取当前时间戳
        GetUTCTime getUTCTime = new GetUTCTime();
        long timeStamp = getUTCTime.getCurrentUTCTimeStr();

        //封装请求头
        Map<String,String> headMaps = HttpUtils.getHeadMaps(timeStamp,"getStatusList");

        //封装请求体
        Map<String,List<String>> requsetBody = new HashMap<>();
        requsetBody.put("sn",deviceSNList);
        String requestJson = JsonUtils.objectToJson(requsetBody);

        //发送请求，并获得结果
        String  jsonData= HttpClientUtil.doPostJson(ablecloudUrl+"getStatusList",requestJson,headMaps);
        return jsonData;
    }
}
