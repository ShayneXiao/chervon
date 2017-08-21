package com.chervon.iot.ablecloud.service.imp;

import com.chervon.iot.ablecloud.mapper.AbleDeviceErrorsMapper;
import com.chervon.iot.ablecloud.mapper.Able_DeviceMapper;
import com.chervon.iot.ablecloud.model.*;
import com.chervon.iot.ablecloud.service.Able_DeviceErrorsService;
import com.chervon.iot.ablecloud.util.DeviceUtils;
import com.chervon.iot.ablecloud.util.HttpUtils;
import com.chervon.iot.common.util.GetUTCTime;
import com.chervon.iot.common.util.HttpClientUtil;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.chervon.iot.mobile.util.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private Able_DeviceMapper able_deviceMapper;

    @Value("${ablecloud.url}")
    private String ableUrl;
    private @Value("${relation_BaseLink}") String relation_BaseLink;

    @Override
    @Transactional
    public ResponseEntity<?> createDeviceError(AbleDeviceErrors ableDeviceErrors) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/vnd.api+json");
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = ableDeviceErrors.getSn() + ableDeviceErrors.getRecoverable() + ableDeviceErrors.getDevice() + ableDeviceErrors.getFault();
        Object deviceErrors = valueOperations.get(key);
        Map map = new HashMap();
        map.put("code", 200);
        if(deviceErrors == null){
            ableDeviceErrorsMapper.insert(ableDeviceErrors);
            valueOperations.set(key, JsonUtils.objectToJson(ableDeviceErrors));
            map.put("msg", "success");
            return new ResponseEntity<Object>(map, headers, HttpStatus.OK);
        }
        map.put("msg", "This Device Error has existed ");
        return new ResponseEntity<Object>(map, headers, HttpStatus.OK);
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

            links.put("self", relation_BaseLink + "device_errors/" + device_id + "/relationships/device");
            links.put("related", relation_BaseLink + "device_errors/" + device_id + "/device");
            device.put("links", links);

            data.put("type", "devices");
            data.put("id", able_responseDeviceError.getId());
            device.put("data", data);

            relationships.put("device", device);
            map.put("relationships", relationships);

            links = new HashMap();
            links.put("self", relation_BaseLink + "device_errors/" + able_responseDeviceError.getId());
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
        links.put("self", relation_BaseLink + "devices/" + device_id);
        map.put("links", links);
        included.add(map);

        able_responseBattery.setIncluded(included);
        map = new HashMap();
        map.put("count", pageInfos.getTotal());
        able_responseBattery.setMeta(map);

        links = new HashMap();

        links.put("self", relation_BaseLink + "devices/" + device_id + "/device_errors");
        links.put("first", relation_BaseLink + "devices/"+ device_id +"/device_errors?page[number]=1&page[size]=" + pageSize);
        if(pageInfos.isHasPreviousPage()){
            links.put("prev", relation_BaseLink + "devices/"+ device_id +"/device_errors?page[number]="+ (pageNumber - 1)+"&page[size]=" + pageSize);
        }else{
            links.put("prev", "null");
        }
        if(pageInfos.isHasNextPage()){
            links.put("next", relation_BaseLink + "devices/"+ device_id +"/device_errors?page[number]="+ (pageNumber + 1)+"&page[size]=" + pageSize);
        }else{
            links.put("next", "null");
        }
        if(pageInfos.getLastPage() > pageNumber){
            links.put("last", relation_BaseLink + "devices/"+ device_id +"/device_errors?page[number]="+ pageInfos.getLastPage()+"&page[size]=" + pageSize);
        }else{
            links.put("last","null");
        }
        able_responseBattery.setLinks(links);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/vnd.api+json");
        return new ResponseEntity<Object>(able_responseBattery,headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getDeviceErrorByDeviceErrorID(String authorization, Integer device_error_id) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/vnd.api+json");

        Map responseData = new HashMap();
        Able_ResponseDeviceError ableResponseDeviceError = ableDeviceErrorsMapper.getDeviceErrorByDeviceErrorID(device_error_id);
        if(ableResponseDeviceError != null){
            authorization = authorization.substring(7);
            String email = jwtTokenUtil.getEmailFromToken(authorization);
            ValueOperations<String, Object> valueOperations =  redisTemplate.opsForValue();
            Mobile_User mobileUser = (Mobile_User)valueOperations.get(email);
            if(mobileUser != null){
                if("unverified".equals(mobileUser.getStatus())){
                    return this.falierResult(authorization);
                }
                Able_Device able_device= able_deviceMapper.selectByDeviceUserSfId(mobileUser.getSfdcId(), ableResponseDeviceError.getSn());
                if(able_device == null){
                    return this.falierResult(authorization);
                }
            }
            Map data = new HashMap(); Map attributes = new HashMap(); Map relationships = new HashMap();
            Map device = new HashMap(); Map links = new HashMap(); Map dataChild = new HashMap();
            data.put("type", "device_errors");
            data.put("id", device_error_id);

            attributes.put("code", ableResponseDeviceError.getCode());
            attributes.put("description", ableResponseDeviceError.getDesc());
            data.put("attributes", attributes);

            links.put("self", relation_BaseLink + "device_errors/" + device_error_id + "/relationships/device");
            links.put("related", relation_BaseLink + "device_errors/" + device_error_id + "/device");
            dataChild.put("type", "devices");
            dataChild.put("id", device_error_id);

            device.put("links", links);
            device.put("data", dataChild);
            relationships.put("device", device);
            data.put("relationships", relationships);
            Map linksChild = new HashMap();
            linksChild.put("self", relation_BaseLink + "device_errors/" + device_error_id);
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
            links.put("self", relation_BaseLink + "device_errors/" + device_error_id);
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
    @Transactional
    public ResponseEntity<?> endedDeviceError(String sn, boolean recoverable, String device, String fault, String status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/vnd.api+json");
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = sn + recoverable + device + fault;
        String deviceErrors = valueOperations.get(key);
        Map map = new HashMap();
        map.put("code", 200);
        if(deviceErrors != null){
            if("closed".equals(status) || "pendding".equals(status)){
                redisTemplate.delete(key);
            }
            AbleDeviceErrors ableDeviceErrors = JsonUtils.jsonToPojo(deviceErrors.toString(), AbleDeviceErrors.class);
            ableDeviceErrors.setStatus(status);
            AbleDeviceErrorsExample ableDeviceErrorsExample = new AbleDeviceErrorsExample();
            AbleDeviceErrorsExample.Criteria criteria = ableDeviceErrorsExample.createCriteria();
            criteria.andDeviceEqualTo(device).andSnEqualTo(sn).andRecoverableEqualTo(recoverable).andFaultEqualTo(fault);
            ableDeviceErrorsMapper.updateByExampleSelective(ableDeviceErrors, ableDeviceErrorsExample);

            map.put("msg", status + " this Device Error");
            return new ResponseEntity<Object>(map, headers, HttpStatus.OK);
        }else{
            map.put("msg", "This Device Error doesn't existed !!!");
            return new ResponseEntity<Object>(map, headers, HttpStatus.OK);
        }
    }

    private ResponseEntity<?> falierResult (String authorization){
        Map data = new HashMap(); List list = new ArrayList(); Map map = new HashMap();;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/vnd.api+json");
        map.put("status", "403");
        map.put("title", "You cannot perform this action.");
        map.put("message", null);
        Map source = new HashMap();
        source.put("pointer", "");
        map.put("source", source);
        list.add(map);
        data.put("errors", list);
        headers.add("Authorization", authorization);
        return new ResponseEntity<Object>(data,headers, HttpStatus.FORBIDDEN);
    }
}

