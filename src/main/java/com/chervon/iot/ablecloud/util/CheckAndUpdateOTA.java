package com.chervon.iot.ablecloud.util;

import com.chervon.iot.common.util.GetUTCTime;
import com.chervon.iot.common.util.HttpClientUtil;
import com.chervon.iot.mobile.util.JsonUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 2017/7/27.
 * Modified by Zack on 2017/8/9,delte the "X-Zc-User-Id"
 */
@Component
public class CheckAndUpdateOTA {
    private final String ablecloudUrl = "http://test.ablecloud.cn:5000/chervonService/v1/";

    public String getCheckUpdateResult(Map<String, String> paramMap) throws SQLException, Exception{
        //获取当前时间戳
        GetUTCTime getUTCTime = new GetUTCTime();
        long timeStamp = getUTCTime.getCurrentUTCTimeStr();

        //封装请求头
        Map<String,String> headMaps = HttpUtils.getHeadMaps(timeStamp,"checkUpdate");
//        headMaps.put("X-Zc-User-Id",paramMap.get("user_sfid"));

        //封装请求体
        Map<String,String> requsetBody = new HashMap<>();
        requsetBody.put("sn",paramMap.get("sn"));
        String requestJson = JsonUtils.objectToJson(requsetBody);

        //发送请求，并获得结果
        String  jsonData= HttpClientUtil.doPostJson(ablecloudUrl+"checkUpdate",requestJson,headMaps);
        return jsonData;
    }

    public String getConfirmUpdateResult(Map<String, String> paramMap) throws SQLException, Exception{
        //获取当前时间戳
        GetUTCTime getUTCTime = new GetUTCTime();
        long timeStamp = getUTCTime.getCurrentUTCTimeStr();

        //封装请求头
        Map<String,String> headMaps = HttpUtils.getHeadMaps(timeStamp,"confirmUpdate");
//        headMaps.put("X-Zc-User-Id",paramMap.get("user_sfid"));

        //封装请求体
        Map<String,String> requsetBody = new HashMap<>();
        requsetBody.put("sn",paramMap.get("sn"));
        requsetBody.put("targetVersion",paramMap.get("targetVersion"));
        String requestJson = JsonUtils.objectToJson(requsetBody);

        //发送请求，并获得结果
        String  jsonData= HttpClientUtil.doPostJson(ablecloudUrl+"confirmUpdate",requestJson,headMaps);
        return jsonData;
    }
}
