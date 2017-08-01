package com.chervon.iot.ablecloud.service.imp;

import com.chervon.iot.ablecloud.mapper.AbleDeviceErrorsMapper;
import com.chervon.iot.ablecloud.model.AbleDeviceErrors;
import com.chervon.iot.ablecloud.model.Able_DevicePojo;
import com.chervon.iot.ablecloud.model.Able_ResponseDeviceError;
import com.chervon.iot.ablecloud.model.Able_ResponseListBody;
import com.chervon.iot.ablecloud.service.Able_DeviceErrorsService;
import com.chervon.iot.ablecloud.util.DeviceUtils;
import com.chervon.iot.ablecloud.util.HttpUtils;
import com.chervon.iot.common.util.GetUTCTime;
import com.chervon.iot.common.util.HttpClientUtil;
import com.chervon.iot.mobile.util.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


/**
 * Created by Shayne on 2017/8/1.
 */
@Service
public class Able_DeviceErrorsServiceImpl implements Able_DeviceErrorsService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Resource
    private AbleDeviceErrorsMapper ableDeviceErrorsMapper;

    @Value("${ablecloud.url}")
    private String ableUrl;

    @Override
    @Transactional
    public ResponseEntity<?> createDeviceError(AbleDeviceErrors ableDeviceErrors) {
        ableDeviceErrorsMapper.insert(ableDeviceErrors);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/vnd.api+json");
        return new ResponseEntity<Object>("导入错误信息成功", headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getDeviceErrors(String device_id, int pageNumber, int pageSize) throws Exception {
        Able_ResponseListBody able_responseBattery = new Able_ResponseListBody();

        List able_ResponseDeviceErrors = new ArrayList();
        PageHelper.startPage(pageNumber, pageSize);
        List<Able_ResponseDeviceError> able_responseDeviceErrors = ableDeviceErrorsMapper.getDeviceErrorByID(device_id);
        PageInfo<Able_ResponseDeviceError> pageInfos = new PageInfo<Able_ResponseDeviceError>(able_responseDeviceErrors);
        Map map; Map attributes; Map relationships; Map device; Map links; Map data;
        for (int i = 0; i < able_responseDeviceErrors.size(); i++) {
            map = new HashMap(); attributes = new HashMap(); relationships = new HashMap();
            device = new HashMap(); links = new HashMap(); data = new HashMap();
            Able_ResponseDeviceError able_responseDeviceError = able_responseDeviceErrors.get(i);
            map.put("type", "device_errors");
            map.put("id", device_id);

            attributes.put("code", able_responseDeviceError.getCode());
            attributes.put("description", able_responseDeviceError.getDesc());
            map.put("attributes", attributes);

            links.put("self", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/device_errors/" + device_id + "/relationships/device");
            links.put("related", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/device_errors/" + device_id + "/device");
            device.put("links", links);

            data.put("type", "devices");
            data.put("id", device_id);
            device.put("data", data);

            relationships.put("device", device);
            map.put("relationships", relationships);

            links = new HashMap();
            links.put("self", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/device_errors/" + device_id);
            map.put("links", links);
            able_ResponseDeviceErrors.add(map);
        }
        able_responseBattery.setData(able_ResponseDeviceErrors);

        map = new HashMap(); attributes = new HashMap(); links = new HashMap();

        String method = "getData";
        GetUTCTime getUTCTime = new GetUTCTime();
        long timesStamp = getUTCTime.getCurrentUTCTimeStr(new Date());
        Map<String,String> signiture=  HttpUtils.getHeadMaps(timesStamp,method);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("sn", device_id);
        requestBody.put("type", "psStatus");
        String jsonData = JsonUtils.objectToJson(requestBody);
        String responseJson = HttpClientUtil.doPostJson(ableUrl + "getData", jsonData, signiture);
        Able_DevicePojo able_devicePojo = JsonUtils.jsonToPojo(responseJson, Able_DevicePojo.class);
        List included = new ArrayList();
        map.put("type", "devices");
        map.put("id", device_id);
        attributes = new HashMap();
        attributes.put("name", "T-800");
        attributes.put("status", DeviceUtils.getDeviceStatus(able_devicePojo.getChargeState()));
        JsonNode jsonNode = MAPPER.readTree(jsonData);
        attributes.put("output_watts_hours", 10);  //DeviceUtils.getDumpEnergy(jsonNode)
        attributes.put("output_watts", 100); //
        attributes.put("capacity_percentage", 83);   //
        attributes.put("charge_time_seconds", 1000);  //jsonNode.get("totalRemainingTime")
        //待定
        attributes.put("charge_time_seconds", 0);
        attributes.put("discharge_time_seconds", 0);
        //待定
        attributes.put("is_low_power", true);
        map.put("attributes", attributes);
        links.put("self", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/devices/" + device_id);
        map.put("links", links);
        included.add(map);

        able_responseBattery.setIncluded(included);
        map = new HashMap();
        map.put("count", pageInfos.getTotal());
        able_responseBattery.setMeta(map);

        links = new HashMap();

        links.put("self", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/devices/" + device_id + "/device_errors");
        links.put("first", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/devices/"+ device_id +"/device_errors?page[number]=1&page[size]=" + pageSize);
        if(pageInfos.isHasPreviousPage()){
            links.put("prev", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/devices/"+ device_id +"/device_errors?page[number]="+ (pageNumber - 1)+"&page[size]=" + pageSize);
        }else{
            links.put("prev", "null");
        }
        if(pageInfos.isHasNextPage()){
            links.put("next", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/devices/"+ device_id +"/device_errors?page[number]="+ (pageNumber + 1)+"&page[size]=" + pageSize);
        }else{
            links.put("next", "null");
        }
        if(pageInfos.getLastPage() > pageNumber){
            links.put("last", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/devices/"+ device_id +"/device_errors?page[number]="+ pageInfos.getLastPage()+"&page[size]=" + pageSize);
        }else{
            links.put("last","null");
        }
        able_responseBattery.setLinks(links);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/vnd.api+json");
        return new ResponseEntity<Object>(able_responseBattery,headers, HttpStatus.OK);
    }
}
