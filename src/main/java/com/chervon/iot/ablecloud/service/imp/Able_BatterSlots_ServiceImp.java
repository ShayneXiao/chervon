package com.chervon.iot.ablecloud.service.imp;

import com.chervon.iot.ablecloud.mapper.Able_BatteryMapper;
import com.chervon.iot.ablecloud.model.*;
import com.chervon.iot.ablecloud.service.Able_BatterySlots_Service;
import com.chervon.iot.ablecloud.util.HttpUtils;
import com.chervon.iot.common.util.HttpClientUtil;
import com.chervon.iot.mobile.util.JsonUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 喷水君 on 2017/7/26.
 */
@Service
public class Able_BatterSlots_ServiceImp implements Able_BatterySlots_Service{
   @Autowired
    private Able_BatteryMapper able_batteryMapper;
    @Autowired
    private HttpUtils httpUtils;
    @Value("${ablecloud.url}")
    private String ableUrl;
    @Override
    public ResponseEntity<?> batterySlots(String Authorization,String device_id,int pageNumber,int pageSize)throws IOException,Exception {
        List<Able_Battery> batteryList = able_batteryMapper.selectListBattery(device_id);
       //   PageInfo<Able_Battery> pageInfo = new PageInfo<Able_Battery>(batteryList);
        String method = "getData";
        Map<String,String> signiture=  httpUtils.getHeadMaps(method);
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
        List<Able_ResponseBatteryData> able_responseBatteryDataListPagination = new ArrayList<>();
        Able_ResponseBatteryData able_responseBatteryData = new Able_ResponseBatteryData();
        Map<String,String> attributes = new HashMap<>();
        Map<String,Object> relationships = new HashMap<>();
        Map<String,String> links = new HashMap<>();
        Map<String ,Object> device = new HashMap<>();
        Map<String,String> devicelinks = new HashMap<>();
        Map<String,String> data = new HashMap<>();
        double output_watts_hours =0.0;

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
                        attributes.put("battery_status", "loaded");
                    }
                    else{
                        attributes.put("battery_status", "loaded");
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
                    devicelinks.put("self", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/battery_slots/"+battery.getBattery_id()+"/relationships/device");
                    devicelinks.put( "related", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/battery_slots/"+device_id+"/device");
                    data.put("type","device");
                    data.put("id",device_id);
                    device.put("links",devicelinks);
                    device.put("data",data);
                    relationships.put("device",device);
                    able_responseBatteryData.setRelationships(relationships);
                    links.put("self","https://private-b1af72-egoapi.apiary-mock.com/api/v1/battery_slots/"+battery.getBattery_id());
                    able_responseBatteryData.setLinks(links);
                    able_responseBatteryDataList.add(able_responseBatteryData);
                    output_watts_hours+=batteryPojo.getDumpEnergy();
                }
            }
        }
        //分页
        int totalPage = able_responseBatteryDataList.size() / pageSize;
        if(totalPage < 1) totalPage = 1;
        int begin = (pageNumber - 1) *  totalPage;
        int pageSizeSum = begin + pageSize;
        if(pageSizeSum > able_responseBatteryDataList.size()) pageSizeSum = able_responseBatteryDataList.size();
        if(begin <= 0) begin = 0;
        if(pageNumber > totalPage) begin = able_responseBatteryDataList.size();
        for(int i = begin; i < pageSizeSum; i++ ){
            able_responseBatteryDataListPagination.add(able_responseBatteryDataList.get(i));
        }
        Map<String,String> resPagationLink = new HashMap<>();
        resPagationLink.put("self","https://private-b1af72-egoapi.apiary-mock.com/api/v1/devices/"+device_id+"/battery_slots?");
       // resPagationLink.put()
        Map<String,String> attributes1 = new HashMap<>();
        //
        attributes1.put("name","T-800");
        if(able_devicePojo.getChargeState()==0){
            attributes1.put("status","uncharge");
        }
        else if(able_devicePojo.getChargeState()==1){
            attributes1.put("status","charging");
        }
        else if(able_devicePojo.getChargeState()==2){
            attributes1.put("status","charged");
        }
        else if(able_devicePojo.getChargeState()==3){
            attributes1.put("status","discharge");
        }
        else{
            attributes1.put("status","discharged");
        }
        attributes1.put("output_watts_hours",String.valueOf(output_watts_hours));
        attributes1.put("output_watts",String.valueOf(able_devicePojo.getAc1Power()+able_devicePojo.getAc2Power()+able_devicePojo.getAc3Power()));
        attributes1.put( "charge_time_seconds",String.valueOf(able_devicePojo.getTotalRemainingTime()));
       //
        attributes1.put("discharge_time_seconds","0");
        //
        attributes1.put("is_low_power", "true");
        Map<String,String>  includLink = new HashMap<>();
        includLink.put("self","https://private-b1af72-egoapi.apiary-mock.com/api/v1/devices/"+device_id);
        Able_ResponseBatteryIncluded able_responseBatteryIncluded= new Able_ResponseBatteryIncluded("device",device_id,attributes1,includLink);
        List<Able_ResponseBatteryIncluded> able_responseBatteryIncludedList =new ArrayList<>();
        able_responseBatteryIncludedList.add(able_responseBatteryIncluded);

        Able_ResponseBattery able_responseBattery = new Able_ResponseBattery(able_responseBatteryDataListPagination,able_responseBatteryIncludedList,new HashMap<>(),resPagationLink);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/vnd.api+json");
        return new ResponseEntity<Object>(able_responseBattery,headers, HttpStatus.OK);
    }

    @Override
    public String selectDeviceId(String deviceId) {
        return null;
    }
}
