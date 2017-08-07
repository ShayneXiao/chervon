package com.chervon.iot.ablecloud.service.imp;

import com.chervon.iot.ablecloud.mapper.Able_DeviceMapper;
import com.chervon.iot.ablecloud.model.*;
import com.chervon.iot.ablecloud.service.Able_Firmware_Service;
import com.chervon.iot.ablecloud.util.CheckAndUpdateOTA;
import com.chervon.iot.ablecloud.util.DeviceUtils;
import com.chervon.iot.ablecloud.util.LoadAndGetData;
import com.chervon.iot.mobile.mapper.Mobile_UserMapper;
import com.chervon.iot.mobile.model.Mobile_User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZAC on 2017-7-31.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
@Service
public class Able_Firmware_ServiceImpl implements Able_Firmware_Service {
    @Autowired
    private LoadAndGetData loadAndGetData;
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private CheckAndUpdateOTA checkAndUpdateOTA;
    @Value("${relation_BaseLink}")
    private String baseLink;

    /**
     * 查询firmware是否有更新
     *
     * @param device,mobileUser
     * @return
     */
    @Override
    public Able_ResponseBody selectFirmwareByDeviceId(Able_Device device,Mobile_User mobileUser) throws Exception {
        String device_id = device.getDeviceId();

        //封装请求参数
        Map<String, String> loadAndGetDataParam = new HashMap<>();
        loadAndGetDataParam.put("sn", device_id);
        //type如何确定？？？
        loadAndGetDataParam.put("type", "psStatus");

        //从able获取数据
        String deviceJsonData = loadAndGetData.getDataResult(loadAndGetDataParam);
        JsonNode deviceJsonNode = jsonMapper.readTree(deviceJsonData);

        //从able获取更新信息（checkUpdate）
        Map<String, String> checkUpdateParam = new HashMap<>();
        checkUpdateParam.put("user_sfid", mobileUser.getSfdcId());
        checkUpdateParam.put("sn", device_id);
        String checkUpdateJsonResult = checkAndUpdateOTA.getCheckUpdateResult(checkUpdateParam);
        JsonNode checkUpdateJsonNode = jsonMapper.readTree(checkUpdateJsonResult);

        /****************封装responseBody中的data****************/
        /**创建responseData对象*/
        Able_MetaRelate_ResponseData responseData = new Able_MetaRelate_ResponseData();
        /**封装responseData字段*/
        responseData.setType("firmwares");
        responseData.setId("firmwares_" + device_id);
        /**封装responseData中的attribute*/
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("version", checkUpdateJsonNode.get("currentVersion").asInt());
        responseData.setAttributes(attributes);
        /**封装responseData中的relationships*/
        Map<String, Able_Relationship> relationships = new HashMap<>();
        Able_Relationship relationship = new Able_Relationship();
        //封装relationships中device的links
        Map<String, String> links = new HashMap<>();
        links.put("self", baseLink + "firmwares/firmwares_" + device_id + "/relationships/device");
        links.put("related", baseLink + "firmwares/firmwares_" + device_id + "/device");
        relationship.setLinks(links);
        //封装relationships中device的data
        Map<String, String> data = new HashMap<>();
        data.put("type", "devices");
        data.put("id", device_id);
        relationship.setData(data);
        //将relationships封装进responseDta中
        relationships.put("device",relationship);
        responseData.setRelationships(relationships);
        /**封装responseData中的links*/
        Map<String, String> resLinks = new HashMap<>();
        resLinks.put("self", baseLink + "firmwares/firmwares_" + device_id + "/relationships/device");
        responseData.setLinks(resLinks);
        /**封装responseData中的meta*/
        Map<String, Object> meta = new HashMap<>();
        meta.put("status", "waiting");
        meta.put("latest_version", checkUpdateJsonNode.get("targetVersion").asDouble());
        responseData.setMeta(meta);

        /****************封装responseBody中的included****************/
        //设备状态
        String deviceStatus = DeviceUtils.getDeviceStatus(deviceJsonNode.get("chargeState").asInt());

        /**创建included对象*/
        Able_ResponseData included = new Able_ResponseData();
        /**封装included字段*/
        included.setType("devices");
        included.setId(device_id);
        /**封装included中的attribute*/
        Map<String, Object> includedAttributes = new HashMap<>();
        //name从哪里获取？？？ ---------》device中的product_code
        includedAttributes.put("name", "T-800");
        includedAttributes.put("status", deviceStatus);
        Integer dumpEnergy = DeviceUtils.getDumpEnergy(deviceJsonNode);
        includedAttributes.put("output_watts_hours", dumpEnergy);
        Integer outputWatts = DeviceUtils.getOutputWatts(deviceJsonNode);
        includedAttributes.put("output_watts", outputWatts);
        Double dumpEnergyPercent = DeviceUtils.getDumpEnergyPercent(deviceJsonNode);
        includedAttributes.put("capacity_percentage", dumpEnergyPercent);
        //充电时间、放电时间，无返回数据，待定。。。。
        Integer daiDing = 1111111;
        includedAttributes.put("charge_time_seconds", daiDing);
        Integer daiDing2 = 2222222;
        includedAttributes.put("discharge_time_seconds", daiDing2);
        //是否低功率，待定。。。
        includedAttributes.put("is_low_power", true);
        included.setAttributes(includedAttributes);
        /**封装included中的links*/
        Map<String, String> includedLinks = new HashMap<>();
        includedLinks.put("self", baseLink + "devices/" + device_id);
        included.setLinks(includedLinks);

        /****************封装responseBody中的meta****************/
        /**创建responseBody的meta对象*/
        Map<String, Object> responseBodyMeta = new HashMap<>();

        /****************封装responseBody****************/
        /**创建ResponseBody，设置其值*/
        Able_ResponseBody responseBody = new Able_ResponseBody();
        responseBody.setData(responseData);
        List<Able_ResponseData> includeds = new ArrayList<>();
        includeds.add(included);
        responseBody.setIncluded(includeds);
        responseBody.setMeta(responseBodyMeta);

        return responseBody;
    }

    /**
     * 更新firmware
     * @param user,device,targetVersion
     * @return
     */
    @Override
    public Able_ResponseBody updateFirmware(Mobile_User user,Able_Device device,
                                            String targetVersion) throws Exception {
        String sn = device.getDeviceId();

        //获取user的sfid
        String user_sfid = user.getSfdcId();

        //拼装getConfirmUpdateResult参数
        Map<String, String> updateParam = new HashMap<>();
        updateParam.put("user_sfid",user_sfid);
        updateParam.put("sn", sn);
        updateParam.put("targetVersion", targetVersion);
        //拼装getDataResult参数
        Map<String, String> getDataParam = new HashMap<>();
        getDataParam.put("sn", sn);
        //type如何确定？？？
        getDataParam.put("type", "psStatus");

        //从able获取数据
        String updateResultJson = checkAndUpdateOTA.getConfirmUpdateResult(updateParam);
        JsonNode updateResultJsonNode = jsonMapper.readTree(updateResultJson);

        String deviceJsonData = loadAndGetData.getDataResult(getDataParam);
        JsonNode deviceJsonNode = jsonMapper.readTree(deviceJsonData);

        /****************封装responseBody中的data****************/
        /**创建responseData对象*/
        Able_MetaRelate_ResponseData responseData = new Able_MetaRelate_ResponseData();
        /**封装responseData字段*/
        responseData.setType("firmwares");
        responseData.setId("firmwares_" + sn);
        /**封装responseData中的attribute*/
        Map<String, Object> responseDataAttributes = new HashMap<>();
        //version值得是？？？-------》目标版本的值
        responseDataAttributes.put("version", Integer.valueOf(targetVersion));
        responseData.setAttributes(responseDataAttributes);
        /**封装responseData中的relationships*/
        Map<String, Able_Relationship> relationships = new HashMap<>();
        Able_Relationship relationship = new Able_Relationship();
        //封装relationships中device的data
        Map<String, String> relationshipData = new HashMap<>();
        relationshipData.put("type", "devices");
        relationshipData.put("id", sn);
        relationship.setData(relationshipData);
        //封装relationships中device的links
        Map<String, String> relationshipLinks = new HashMap<>();
        relationshipLinks.put("self", baseLink + "firmwares/firmwares_" + sn + "/relationships/device");
        relationshipLinks.put("related", baseLink + "firmwares/firmwares_" + sn + "/device");
        relationship.setLinks(relationshipLinks);
        //将relationships封装进responseData中
        responseData.setRelationships(relationships);
        /**封装responseData中的links*/
        Map<String, String> responseDataLinks = new HashMap<>();
        responseDataLinks.put("self", baseLink + "firmwares/firmwares_" + sn);
        responseData.setLinks(responseDataLinks);
        /**封装responseData中的meta*/
        Map<String, String> responseDataMeta = new HashMap<>();
        responseDataMeta.put("status", "installing");
        responseDataMeta.put("latest_version", targetVersion);
        responseData.setMeta(responseDataMeta);

        /****************封装responseBody中的included****************/
        //设备状态
        String chargeState =
                DeviceUtils.getDeviceStatus(deviceJsonNode.get("chargeState").asInt());

        /**创建included对象*/
        Able_ResponseData deviceIncluded = new Able_ResponseData();
        /**封装included字段*/
        deviceIncluded.setType("devices");
        deviceIncluded.setId(sn);
        /**封装included中的attribute*/
        Map<String, Object> includedAttributes = new HashMap<>();
        //name从哪里获取？？？ ---------》device中的product_code
        includedAttributes.put("name", "T-800");
        includedAttributes.put("status", chargeState);
        Integer dumpEnergy = DeviceUtils.getDumpEnergy(deviceJsonNode);
        includedAttributes.put("output_watts_hours", dumpEnergy);
        Integer outputWatts = DeviceUtils.getOutputWatts(deviceJsonNode);
        includedAttributes.put("output_watts", outputWatts);
        Double dumpEnergyPercent = DeviceUtils.getDumpEnergyPercent(deviceJsonNode);
        includedAttributes.put("capacity_percentage", dumpEnergyPercent);
        //充电时间、放电时间，无返回数据，待定。。。。
        Integer daiDing = 1111111;
        includedAttributes.put("charge_time_seconds", daiDing);
        Integer daiDing2 = 2222222;
        includedAttributes.put("discharge_time_seconds", daiDing2);
        //是否低功率，待定。。。
        includedAttributes.put("is_low_power", true);
        deviceIncluded.setAttributes(includedAttributes);
        /**封装included中的links*/
        Map<String, String> includedLinks = new HashMap<>();
        includedLinks.put("self", baseLink + "devices/" + sn);
        deviceIncluded.setLinks(includedLinks);


        /****************封装responseBody中的meta****************/
        /**创建responseBody的meta对象*/
        Map<String, Object> responseBodyMeta = new HashMap<>();
        //object是什么？？？
        responseBodyMeta.put("type", "object");
        responseBodyMeta.put("id", "object_" + sn);
        Map<String, String> linksInMeta = new HashMap<>();
        linksInMeta.put("self", baseLink + "object/object_" + sn);
        responseBodyMeta.put("links", linksInMeta);
        responseBodyMeta.put("message", "Firmware update requested");

        /****************封装responseBody****************/
        /**创建ResponseBody，设置其值*/
        Able_ResponseBody responseBody = new Able_ResponseBody();
        responseBody.setData(responseData);
        List<Able_ResponseData> includeds = new ArrayList<>();
        includeds.add(deviceIncluded);
        responseBody.setIncluded(includeds);
        responseBody.setIncluded(includeds);
        responseBody.setMeta(responseBodyMeta);

        return responseBody;
    }

    /**
     * 根据firmware_id查询与之关联的device
     * @param firmware_id
     * @return
     */
    @Override
    public Able_Relationship selectDeviceByFirmwareId(String firmware_id) {
        //截取设备Id：firmwareId = "firmwares_"+设备Id
        String deviceId = firmware_id.replace("firmwares_", "");

        /**
         * 封装responseBody中的links
         * */
        Map<String, String> links = new HashMap<>();
        links.put("self", baseLink + "firmwares/" + firmware_id + "/relationships/device");
        links.put("related", baseLink + "firmwares/" + firmware_id + "/device");

        /**
         * 封装responseBody中的data
         * */
        Map<String,String> data = new HashMap<>();
        data.put("type","devices");
        data.put("id",deviceId);

        /**
         * 封装responseBody,并return结果
         * */
        Able_Relationship responseBody = new Able_Relationship();
        responseBody.setLinks(links);
        responseBody.setData(data);
        return responseBody;
    }
}
