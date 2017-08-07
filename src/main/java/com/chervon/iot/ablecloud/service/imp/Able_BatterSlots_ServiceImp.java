package com.chervon.iot.ablecloud.service.imp;

import com.chervon.iot.ablecloud.mapper.Able_BatteryMapper;
import com.chervon.iot.ablecloud.mapper.Able_DeviceMapper;
import com.chervon.iot.ablecloud.model.*;
import com.chervon.iot.ablecloud.service.Able_BatterySlots_Service;
import com.chervon.iot.ablecloud.util.DeviceUtils;
import com.chervon.iot.ablecloud.util.HttpUtils;
import com.chervon.iot.common.common_util.HttpHeader;
import com.chervon.iot.common.exception.Bad_RequestException;
import com.chervon.iot.common.exception.ResultMsg;
import com.chervon.iot.common.exception.ResultStatusCode;
import com.chervon.iot.common.util.GetUTCTime;
import com.chervon.iot.common.util.HttpClientUtil;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.chervon.iot.mobile.service.imp.Mobile_UserLoginServiceImp;
import com.chervon.iot.mobile.util.ErrorResponseUtil;
import com.chervon.iot.mobile.util.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

/**
 * Created by 喷水君 on 2017/7/26.
 */
@Service
public class Able_BatterSlots_ServiceImp implements Able_BatterySlots_Service{
    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private Able_BatteryMapper able_batteryMapper;
    @Autowired
    private Able_DeviceMapper able_deviceMapper;
    @Value("${ablecloud.url}")
    private String ableUrl;
    @Value("${relation_BaseLink}")
    private String  egoBaseLink;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private Mobile_UserLoginServiceImp mobile_userLoginServiceImp;
    //一个设备下所有的电池包
    @Transactional
    @Override
    public ResponseEntity<?> batterySlots(String Authorization,String device_id,int pageNumber,int pageSize)throws IOException,Exception {
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization",Authorization);
        Able_Device able_device = able_deviceMapper.selectByPrimaryKey(device_id);
        if (able_device == null) {
            ResultMsg resultMsg = ErrorResponseUtil.errorFiled();
            return new ResponseEntity(resultMsg, headers, HttpStatus.valueOf(ResultStatusCode.SC_BAD_REQUEST.getErrcode()));
        }
        String email =jwtTokenUtil.getEmailFromToken(Authorization.substring(7));
       Mobile_User mobile_user= mobile_userLoginServiceImp.getUserByEmail(email);

        if(!mobile_user.getSfdcId().equals(able_device.getUsersfid())){
            ResultMsg resultMsg = ErrorResponseUtil.forbidend();
            return new ResponseEntity(resultMsg, headers, HttpStatus.valueOf(ResultStatusCode.SC_FORBIDDEN.getErrcode()));

        }
        PageHelper.startPage(pageNumber, pageSize);
        List<Able_Battery> batteryList = able_batteryMapper.selectListBattery(device_id);
        PageInfo<Able_Battery> pageInfo = new PageInfo<Able_Battery>(batteryList);
        Map<String,String> resPagationLink = new HashMap<>();
        resPagationLink.put("self",egoBaseLink+"devices/"+device_id+"/battery_slots?");
        resPagationLink.put("first",egoBaseLink+"devices/"+device_id+"/battery_slots?page[number]="+pageInfo.getFirstPage()+"&page[size]="+pageSize);
        if(!pageInfo.isHasPreviousPage()){
            resPagationLink.put("prev",null);
        }
        else {
            resPagationLink.put("prev",egoBaseLink+"devices/"+device_id+"/battery_slots?page[number]="+(pageNumber-1)+"&page[size]="+pageSize);
        }
        if(!pageInfo.isHasNextPage()) {
            resPagationLink.put("next", null);
        } else {
            resPagationLink.put("next", egoBaseLink+"devices/" + device_id + "/battery_slots?page[number]=" + (pageNumber + 1) + "&page[size]=" + pageSize);
        }
        if(pageInfo.getLastPage() <= pageNumber){
            resPagationLink.put("last",null);
        } else {
            resPagationLink.put("last", egoBaseLink+"devices/" + device_id + "/battery_slots?page[number]=" + pageInfo.getLastPage() + "&page[size]=" + pageSize);
        }
        String method ="getData";
        GetUTCTime getUTCTime = new GetUTCTime();
        long timesStamp = getUTCTime.getCurrentUTCTimeStr(new Date());
        Map<String,String> signiture=  HttpUtils.getHeadMaps(timesStamp,method);
        Map<String,String> requestBody = new HashMap<>();
        requestBody.put("sn",device_id);
        requestBody.put("type","psStatus");
        String jsonData = JsonUtils.objectToJson(requestBody);
        String responseJson= HttpClientUtil.doPostJson(ableUrl+"getData",jsonData,signiture);
        Able_DevicePojo able_devicePojo= JsonUtils.jsonToPojo(responseJson,Able_DevicePojo.class);
        List<Able_BatteryPojo> able_batteryPojoList =new ArrayList<>();
        able_batteryPojoList.add(able_devicePojo.getBat1());
        able_batteryPojoList.add(able_devicePojo.getBat2());
        able_batteryPojoList.add(able_devicePojo.getBat3());
        able_batteryPojoList.add(able_devicePojo.getBat4());
        List<Able_ResponseBatteryData> able_responseBatteryDataList = new ArrayList<>();
        Able_ResponseBatteryData able_responseBatteryData = new Able_ResponseBatteryData();
        Map<String,String> attributes = new HashMap<>();
        Map<String,Object> relationships = new HashMap<>();
        Map<String,String> links = new HashMap<>();
        Map<String ,Object> device = new HashMap<>();
        Map<String,String> devicelinks = new HashMap<>();
        Map<String,String> data = new HashMap<>();

        for(Able_Battery battery:batteryList){
            for(Able_BatteryPojo batteryPojo:able_batteryPojoList){
                if(battery.getBattery_id().equals(batteryPojo.getBatId())){
                    able_responseBatteryData.setType("battery_slots");
                    able_responseBatteryData.setId(battery.getBattery_id());
                    attributes.put("name",battery.getBattery_name());
                    if(batteryPojo.getChargeState()==0) {
                        attributes.put("battery_status", "loaded");
                    }
                    else if(batteryPojo.getChargeState()==1){
                        attributes.put("battery_status", "loadeding");
                    }
                    else{
                        attributes.put("battery_status", "unloaded");
                    }
                    attributes.put("capacity_amp_hours",String.valueOf(batteryPojo.getDumpEnergy()));
                    attributes.put("charge_level_percent",String.valueOf(batteryPojo.getDumpEnergyPercent()));
                    if(batteryPojo.getDumpEnergyPercent()<20){
                        attributes.put("is_low_power","true");
                    }
                    else {
                        attributes.put("is_low_power", "false");
                    }
                    able_responseBatteryData.setAttribute(attributes);
                    devicelinks.put("self", egoBaseLink+"battery_slots/"+battery.getBattery_id()+"/relationships/device");
                    devicelinks.put( "related", egoBaseLink+"battery_slots/"+device_id+"/device");
                    data.put("type","device");
                    data.put("id",device_id);
                    device.put("links",devicelinks);
                    device.put("data",data);
                    relationships.put("device",device);
                    able_responseBatteryData.setRelationships(relationships);
                    links.put("self",egoBaseLink+"battery_slots/"+battery.getBattery_id());
                    able_responseBatteryData.setLinks(links);
                    able_responseBatteryDataList.add(able_responseBatteryData);

                }
            }
        }

        Map<String,String> attributes1 = new HashMap<>();
        //
        attributes1.put("name","T-800");
        attributes1.put("status", DeviceUtils.getDeviceStatus(able_devicePojo.getChargeState()));
        JsonNode jsonNode = mapper.readTree(responseJson);
        attributes1.put("output_watts_hours",String.valueOf(DeviceUtils.getDumpEnergy(jsonNode)));
        attributes1.put("output_watts",String.valueOf(able_devicePojo.getAc1Power()+able_devicePojo.getAc2Power()+able_devicePojo.getAc3Power()));
        attributes1.put( "charge_time_seconds",String.valueOf(able_devicePojo.getTotalRemainingTime()));
        //
        attributes1.put("discharge_time_seconds","0");
        //
        attributes1.put("is_low_power", "true");
        Map<String,String>  includLink = new HashMap<>();
        includLink.put("self",egoBaseLink+"devices/"+device_id);
        Able_ResponseBatteryIncluded able_responseBatteryIncluded= new Able_ResponseBatteryIncluded("device",device_id,attributes1,includLink);
        List<Able_ResponseBatteryIncluded> able_responseBatteryIncludedList =new ArrayList<>();
        able_responseBatteryIncludedList.add(able_responseBatteryIncluded);
        Able_ResponseListBody able_responseBattery = new Able_ResponseListBody(able_responseBatteryDataList,able_responseBatteryIncludedList,new HashMap<>(),resPagationLink);
        return new ResponseEntity<Object>(able_responseBattery,headers, HttpStatus.OK);
    }
    //关联
    @Transactional
    @Override
    public String selectDeviceId(String battery_slot_id)throws Exception {
        Able_Battery able_battery = able_batteryMapper.selectDeviceId(battery_slot_id);
        if (able_battery == null) {
           throw new  Bad_RequestException();
        }
        return able_battery.getDevice_id();
    }
    //拿指定的电池包
    @Transactional
    @Override
    public ResponseEntity<?> batterySlot(String Authorization,String battery_slot_id)throws Exception {
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization",Authorization);
        Able_Battery able_battery =able_batteryMapper.selectDeviceId(battery_slot_id);
        if(able_battery==null){
            ResultMsg resultMsg = ErrorResponseUtil.errorFiled();
            return new ResponseEntity(resultMsg, headers, HttpStatus.valueOf(ResultStatusCode.SC_BAD_REQUEST.getErrcode()));
        }
            Able_Device able_device = able_deviceMapper.selectByPrimaryKey(able_battery.getDevice_id());
            String email =jwtTokenUtil.getEmailFromToken(Authorization.substring(7));
            Mobile_User mobile_user= mobile_userLoginServiceImp.getUserByEmail(email);
            if(!mobile_user.getSfdcId().equals(able_device.getUsersfid())){
                ResultMsg resultMsg = ErrorResponseUtil.forbidend();
                return new ResponseEntity(resultMsg, headers, HttpStatus.valueOf(ResultStatusCode.SC_FORBIDDEN.getErrcode()));

            }
        String method ="getData";
        GetUTCTime getUTCTime = new GetUTCTime();
        long timesStamp = getUTCTime.getCurrentUTCTimeStr(new Date());
        Map<String,String> signiture=  HttpUtils.getHeadMaps(timesStamp,method);
        Map<String,String> requestBody = new HashMap<>();
        requestBody.put("sn",able_battery.getDevice_id());
        requestBody.put("type","psStatus");
        String jsonData = JsonUtils.objectToJson(requestBody);
        String responseJson= HttpClientUtil.doPostJson(ableUrl+"getData",jsonData,signiture);
        Able_DevicePojo able_devicePojo= JsonUtils.jsonToPojo(responseJson,Able_DevicePojo.class);
        List<Able_BatteryPojo> able_batteryPojoList =new ArrayList<>();
        able_batteryPojoList.add(able_devicePojo.getBat1());
        able_batteryPojoList.add(able_devicePojo.getBat2());
        able_batteryPojoList.add(able_devicePojo.getBat3());
        able_batteryPojoList.add(able_devicePojo.getBat4());
        Able_BatteryPojo able_batteryBat=null;
        for(Able_BatteryPojo able_batterySize: able_batteryPojoList){
            if(able_batterySize.getBatId().equals(battery_slot_id)){
                able_batteryBat  =able_batterySize;
                break;
            }
        }
        Able_ResponseBatteryData able_responseBatteryData = new Able_ResponseBatteryData();
        Map<String,String> attributes = new HashMap<>();
        Map<String,Object> relationships = new HashMap<>();
        Map<String,String> links = new HashMap<>();
        Map<String ,Object> device = new HashMap<>();
        Map<String,String> devicelinks = new HashMap<>();
        Map<String,String> data = new HashMap<>();
        able_responseBatteryData.setType("battery_slots");
        able_responseBatteryData.setId(able_battery.getBattery_id());
        attributes.put("name",able_battery.getBattery_name());
        if(able_batteryBat.getChargeState()==0) {
            attributes.put("battery_status", "loaded");
        }
        else if(able_batteryBat.getChargeState()==1){
            attributes.put("battery_status", "loading");
        }
        else{
            attributes.put("battery_status", "unload");
        }
        attributes.put("capacity_amp_hours",String.valueOf(able_batteryBat.getDumpEnergy()));
        attributes.put("charge_level_percent",String.valueOf(able_batteryBat.getDumpEnergyPercent()));
        if(able_batteryBat.getDumpEnergyPercent()<20){
            attributes.put("is_low_power","true");
        }
        else {
            attributes.put("is_low_power", "false");
        }
        able_responseBatteryData.setAttribute(attributes);
        devicelinks.put("self", egoBaseLink+"battery_slots/"+able_battery.getBattery_id()+"/relationships/device");
        devicelinks.put( "related", egoBaseLink+"battery_slots/"+able_battery+"/device");
        data.put("type","device");
        data.put("id",able_battery.getDevice_id());
        device.put("links",devicelinks);
        device.put("data",data);
        relationships.put("device",device);
        able_responseBatteryData.setRelationships(relationships);
        links.put("self",egoBaseLink+"battery_slots/"+able_battery.getBattery_id());
        able_responseBatteryData.setLinks(links);
        Able_ResponseBody able_responseBattery = new Able_ResponseBody(able_responseBatteryData,new ArrayList<>(),new HashMap<>());
        return new ResponseEntity<Object>(able_responseBattery,headers, HttpStatus.OK);
    }
}
