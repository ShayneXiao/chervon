package com.chervon.iot.ablecloud.util;

import com.chervon.iot.ablecloud.model.Able_Device;
import com.chervon.iot.ablecloud.model.Able_Relationship;
import com.chervon.iot.ablecloud.model.Able_Relationship_ResponseData;
import com.chervon.iot.ablecloud.model.Able_ResponseData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zack on 2017/8/17.
 * Description:
 * Modified by:
 * Modified date:
 */
@Component
public class ResponseDataUtils {
    @Value("${relation_BaseLink}")
    private String baseLink;

    /**
     * 此方法专用于获得outlet相关接口响应的返回体中的responseData
     * @param outletName
     * @param device_id
     * @return
     * @throws Exception
     */
    public Able_Relationship_ResponseData getOutletResponseData(JsonNode statusJsonNode ,String outletName,String device_id) throws Exception {
        /*所需数据*/
        //outlet的状态
        String outletStatus = DeviceUtils.getOutletStatus(statusJsonNode,outletName);
        //输出功率
        Integer outputWatts = DeviceUtils.getOutputWatts(statusJsonNode);

        /**封装第一个responseData*/
        Able_Relationship_ResponseData responseData = new Able_Relationship_ResponseData();

        /**封装responseData字段*/
        responseData.setType("outlets");
        responseData.setId(outletName + "_" + device_id);

        /**封装responseData属性*/
        Map<String, Object> responseDataAttributes = new HashMap<>();
        responseDataAttributes.put("name", outletName);
        responseDataAttributes.put("outlet_type", "usb");
        responseDataAttributes.put("status", outletStatus);
        responseDataAttributes.put("output_watts", outputWatts);
        responseData.setAttributes(responseDataAttributes);

        /**封装responseData中的relationships*/
        /**创建responseDataRelationships对象*/
        Map<String, Object> responseDataRelationships = new HashMap<>();
        /**封装deviceRelationship*/
        //创建deviceRelationship对象
        Able_Relationship deviceRelationship = new Able_Relationship();
        //封装deviceRelationship中的links
        Map<String, String> deviceRelationshipLinks = new HashMap<>();
        deviceRelationshipLinks.put("self", baseLink + "outlets/" + outletName + "_" + device_id + "/relationships/device");
        deviceRelationshipLinks.put("related", baseLink + "outlets/" + outletName + "_" + device_id + "/device");
        deviceRelationship.setLinks(deviceRelationshipLinks);
        //封装deviceRelationship中的data
        Map<String, String> deviceRelationshipData = new HashMap<>();
        deviceRelationshipData.put("type", "devices");
        deviceRelationshipData.put("id", device_id);
        deviceRelationship.setData(deviceRelationshipData);
        //将deviceRelationship加入responseDataRelationships中
        responseDataRelationships.put("device", deviceRelationship);
        /****将responseDataRelationships加入responseData中****/
        responseData.setRelationships(responseDataRelationships);

        /****封装responseData中的links****/
        Map<String, String> responseDataLinks = new HashMap<>();
        responseDataLinks.put("self", baseLink + "outlets/" + outletName + "_" + device_id);
        /****将responseDataLinks加入responseData中****/
        responseData.setLinks(responseDataLinks);
        return responseData;
    }

    /**
     * 此方法仅用于获取device的included
     * @param statusJsonNode
     * @param device
     * @return
     */
    public Able_ResponseData getOutletIncluded(JsonNode statusJsonNode, Able_Device device) {
        /*所需数据*/
        String device_id = device.getDeviceId();
        Integer outputWatts = DeviceUtils.getOutputWatts(statusJsonNode);
        String chargeState =
                DeviceUtils.getDeviceStatus(statusJsonNode.get("chargeState").asInt());
        Integer outputWattHours = DeviceUtils.getOutputWattHours(statusJsonNode);
        Double capacityPercentage = DeviceUtils.getCapacityPercentage(statusJsonNode);

        //创建deviceIncluded对象
        Able_ResponseData deviceIncluded = new Able_ResponseData();

        //封装deviceIncluded的字段
        deviceIncluded.setType("devices");
        deviceIncluded.setId(device_id);

        //封装deviceIncluded的属性
        Map<String, Object> deviceIncludedAttributes = new HashMap<>();
        //name从哪里获取？？？ ---------》device中的product_code
        deviceIncludedAttributes.put("name", device.getProductCode());
        deviceIncludedAttributes.put("status", chargeState);
        deviceIncludedAttributes.put("serial_number", device_id);
        deviceIncludedAttributes.put("output_watts_hours", outputWattHours);
        deviceIncludedAttributes.put("output_watts", outputWatts);
        deviceIncludedAttributes.put("capacity_percentage", capacityPercentage);
        //充电时间、放电时间，无返回数据，待定。。。。
        Integer daiDing = 1111111;
        deviceIncludedAttributes.put("charge_time_seconds", daiDing);
        Integer daiDing2 = 2222222;
        deviceIncludedAttributes.put("discharge_time_seconds", daiDing2);
        //是否低功率，待定。。。
        deviceIncludedAttributes.put("is_low_power", true);
        //用户能否控制，待定。。。
        deviceIncludedAttributes.put("user_can_control", device.getUserCanControl());
        deviceIncluded.setAttributes(deviceIncludedAttributes);

        //封装deviceIncluded中的links
        Map<String, String> deviceIncludedLinks = new HashMap<>();
        deviceIncludedLinks.put("self", baseLink + "devices/" + device_id);
        deviceIncluded.setLinks(deviceIncludedLinks);

        return deviceIncluded;
    }
}
