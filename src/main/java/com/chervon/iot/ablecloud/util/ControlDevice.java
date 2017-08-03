package com.chervon.iot.ablecloud.util;

import com.chervon.iot.common.util.GetUTCTime;
import com.chervon.iot.common.util.HttpClientUtil;
import com.chervon.iot.mobile.util.JsonUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 2017/7/26.
 */
@Component
public class ControlDevice {
    private final String ablecloudUrl = "http://test.ablecloud.cn:5000/chervonService/v1/";

    public String getControlDeviceResult(Map<String, String> paramMap) throws SQLException, Exception{
        //获取当前时间戳
        GetUTCTime getUTCTime = new GetUTCTime();
        long timeStamp = getUTCTime.getCurrentUTCTimeStr();

        //封装请求头
        Map<String,String> headMaps = HttpUtils.getHeadMaps(timeStamp,"controlDevice");
        headMaps.put("X-Zc-User-Id",paramMap.get("user_sfid"));

        //封装请求体
        Map<String,String> requsetBody = new HashMap<String,String>();
        requsetBody.put("sn",paramMap.get("sn"));
        requsetBody.put("cmd",paramMap.get("cmd"));
        String requestJson = JsonUtils.objectToJson(requsetBody);

        //发送请求，并获得结果
        String  jsonData= HttpClientUtil.doPostJson(ablecloudUrl+"/controlDevice",requestJson,headMaps);
        return jsonData;
    }
}
