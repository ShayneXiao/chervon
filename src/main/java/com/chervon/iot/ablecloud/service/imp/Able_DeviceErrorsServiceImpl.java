package com.chervon.iot.ablecloud.service.imp;

import com.chervon.iot.ablecloud.mapper.AbleDeviceErrorsMapper;
import com.chervon.iot.ablecloud.model.*;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
    @Resource
    private RedisTemplate redisTemplate;

    @Value("${ablecloud.url}")
    private String ableUrl;

    @Override
    @Transactional
    public Map createDeviceError(AbleDeviceErrors ableDeviceErrors) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = ableDeviceErrors.getSn() + ableDeviceErrors.getRecoverable() + ableDeviceErrors.getDevice() + ableDeviceErrors.getFault();
        Object deviceErrors = valueOperations.get(key);
        Map map = new HashMap();
        map.put("code", "200");
        if(deviceErrors == null){
            ableDeviceErrorsMapper.insert(ableDeviceErrors);
            valueOperations.set(key, "1");
            map.put("msg", "This Device Error has existed ");
            return map;
        }
        map.put("msg", "success");
        return map;
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
            map.put("id", able_responseDeviceError.getId());

            attributes.put("code", able_responseDeviceError.getCode());
            attributes.put("description", able_responseDeviceError.getDesc());
            map.put("attributes", attributes);

            links.put("self", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/device_errors/" + device_id + "/relationships/device");
            links.put("related", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/device_errors/" + device_id + "/device");
            device.put("links", links);

            data.put("type", "devices");
            data.put("id", able_responseDeviceError.getId());
            device.put("data", data);

            relationships.put("device", device);
            map.put("relationships", relationships);

            links = new HashMap();
            links.put("self", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/device_errors/" + able_responseDeviceError.getId());
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
        //待定
        attributes.put("name", "T-800");
        attributes.put("status", DeviceUtils.getDeviceStatus(able_devicePojo.getChargeState()));
        JsonNode jsonNode = MAPPER.readTree(responseJson);
        attributes.put("output_watts_hours", DeviceUtils.getDumpEnergy(jsonNode));
        attributes.put("output_watts", able_devicePojo.getAc1Power() + able_devicePojo.getAc2Power() + able_devicePojo.getAc3Power());
        attributes.put("capacity_percentage", DeviceUtils.getDumpEnergyPercent(jsonNode));
        attributes.put("charge_time_seconds", jsonNode.get("totalRemainingTime"));
        //待定
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

    @Override
    public ResponseEntity<?> getDeviceErrorByDeviceErrorID(Integer device_error_id) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/vnd.api+json");

        Map responseData = new HashMap();
        Able_ResponseDeviceError ableResponseDeviceError = ableDeviceErrorsMapper.getDeviceErrorByDeviceErrorID(device_error_id);
        if(ableResponseDeviceError != null){
            Map data = new HashMap(); Map attributes = new HashMap(); Map relationships = new HashMap();
            Map device = new HashMap(); Map links = new HashMap(); Map dataChild = new HashMap();
            data.put("type", "device_errors");
            data.put("id", device_error_id);

            attributes.put("code", ableResponseDeviceError.getCode());
            attributes.put("description", ableResponseDeviceError.getDesc());
            data.put("attributes", attributes);

            links.put("self", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/device_errors/" + device_error_id + "/relationships/device");
            links.put("related", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/device_errors/" + device_error_id + "/device");
            dataChild.put("type", "devices");
            dataChild.put("id", device_error_id);

            device.put("links", links);
            device.put("data", dataChild);
            relationships.put("device", device);
            data.put("relationships", relationships);
            Map linksChild = new HashMap();
            linksChild.put("self", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/device_errors/" + device_error_id);
            data.put("links", linksChild);


            String method = "getData";
            GetUTCTime getUTCTime = new GetUTCTime();
            long timesStamp = getUTCTime.getCurrentUTCTimeStr(new Date());
            Map<String,String> signiture=  HttpUtils.getHeadMaps(timesStamp,method);
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("sn", ableResponseDeviceError.getSn());
            requestBody.put("type", "psStatus");
            String jsonData = JsonUtils.objectToJson(requestBody);
            String responseJson = HttpClientUtil.doPostJson(ableUrl + "getData", jsonData, signiture);
            Able_DevicePojo able_devicePojo = JsonUtils.jsonToPojo(responseJson, Able_DevicePojo.class);

            List included = new ArrayList();

            dataChild = new HashMap();
            dataChild.put("type", "devices");
            dataChild.put("id", ableResponseDeviceError.getSn());
            Map attributesChild = new HashMap();
            //待定
            attributesChild.put("name", "T-800");
            attributesChild.put("status", DeviceUtils.getDeviceStatus(able_devicePojo.getChargeState()));
            JsonNode jsonNode = MAPPER.readTree(responseJson);
            attributesChild.put("output_watts_hours", DeviceUtils.getDumpEnergy(jsonNode));
            attributesChild.put("output_watts", able_devicePojo.getAc1Power() + able_devicePojo.getAc2Power() + able_devicePojo.getAc3Power());
            attributesChild.put("capacity_percentage", DeviceUtils.getDumpEnergyPercent(jsonNode));
            attributesChild.put("charge_time_seconds", jsonNode.get("totalRemainingTime"));
            //待定
            attributesChild.put("discharge_time_seconds", 0);
            //待定
            attributesChild.put("is_low_power", true);
            dataChild.put("attributes", attributesChild);
            links = new HashMap();
            links.put("self", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/devices/" + device_error_id);
            dataChild.put("links", links);
            included.add(dataChild);
            Map meta = new HashMap();
            responseData.put("data", data);
            responseData.put("included", included);
            responseData.put("meta", meta);
        }
        return new ResponseEntity<Object>(responseData,headers, HttpStatus.OK);
    }

    @Override
    public Map endedDeviceError(String sn, boolean recoverable, String device, String fault) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = sn + recoverable + device + fault;
        Object deviceErrors = valueOperations.get(key);
        if(deviceErrors != null){
            redisTemplate.delete(key);
        }
        Map map = new HashMap();
        map.put("code", "200");
        map.put("msg", "Ended This Device Error");
        return map;
    }
}