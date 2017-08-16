package com.chervon.iot.ablecloud.service.imp;

import com.alibaba.fastjson.support.odps.udf.CodecCheck;
import com.chervon.iot.ablecloud.mapper.Able_DeviceMapper;
import com.chervon.iot.ablecloud.model.*;
import com.chervon.iot.ablecloud.service.Able_Device_Service;
import com.chervon.iot.ablecloud.util.*;
import com.chervon.iot.common.util.GetUTCTime;
import com.chervon.iot.mobile.mapper.Mobile_UserMapper;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZAC on 2017-7-27.
 * Dexcription：
 * Modified by:
 * Modified Date:
 */
@Service
public class Able_Device_ServiceImpl implements Able_Device_Service {
    @Autowired
    private Able_DeviceMapper deviceMapper;
    @Autowired
    private LoadAndGetData loadAndGetData;
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private ControlDevice controlDevice;
    @Autowired
    private CheckAndUpdateOTA checkAndUpdateOTA;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private Mobile_UserMapper userMapper;
    @Value("${relation_BaseLink}")
    private String baseLink;

    /**
     * 查询device的集合并返回
     * @param Authorization,pageNum,pageSize
     * @param Authorization
     * @param pageNum
     * @param pageSize   @return
     * @throws Exception
     */
    @Override
    public Object selectDeviceList(String Authorization, Integer pageNum, Integer pageSize) throws Exception {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);

        /**检查用户是否验证*/
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        if (!"verified".equals(mobileUser.getStatus())) {
            return DeviceUtils.getCannotPerformResponse(Authorization);
        }
        /*获取用户的salesforce id*/
        String userSfdcId = mobileUser.getSfdcId();

        if (pageNum != null && pageSize != null) {
            PageHelper.startPage(pageNum, pageSize);
        }
        //获取device集合
        List<Able_Device> devices = deviceMapper.selectDevicesByUserSfid(userSfdcId);

        Able_ResponseListBody responseBody = null;

        //如果devices不为空，并且devices的长度大于0，则继续执行
        if (devices != null && devices.size() > 0) {
            List<String> deviceSNList = new ArrayList<>();
            for (Able_Device device : devices) {
                deviceSNList.add(device.getDeviceId());
            }
            String dataList = loadAndGetData.getDataList(deviceSNList);
            JsonNode deviceJsonNodeList = jsonMapper.readTree(dataList);

            List<Able_ResponseData> responseDataList = new ArrayList<>();
            List<Able_ResponseData> includeds = new ArrayList<>();
            Map<String, String> metaMap = new HashMap<>();
            Map<String, String> linksRes = new HashMap<>();

            //遍历deviceJsonNodeList
            for (int i = 0; i < deviceJsonNodeList.get("list").size(); i++) {
                //每一个deviceJsonNode
                JsonNode deviceJsonNode = deviceJsonNodeList.get("list").get(i);
                //获得device_id
                String device_id = devices.get(i).getDeviceId();
                //设备状态
                String chargeState =
                        DeviceUtils.getDeviceStatus(deviceJsonNode.get("chargeState").asInt());

                /************************封装responseBody中的data************************/
                //所用数据
                Integer dumpEnergy = DeviceUtils.getDumpEnergy(deviceJsonNode);
                Integer output_watts = DeviceUtils.getOutputWatts(deviceJsonNode);

                /**创建responseData对象*/
                Able_Relationship_ResponseData responseData = new Able_Relationship_ResponseData();

                /**封装responseData字段*/
                responseData.setType("devices");
                responseData.setId(device_id);

                /**封装responseData中的attribute*/
                Map<String, Object> responseDataAttribute = new HashMap<>();
                //name从哪里获取？？？ ---------》device中的product_code
                responseDataAttribute.put("name", devices.get(i).getProductCode());
                responseDataAttribute.put("status", chargeState);
                //设备新加字段，。。。
                responseDataAttribute.put("serial_number", device_id);
                responseDataAttribute.put("output_watts_hours", dumpEnergy);
                responseDataAttribute.put("output_watts", output_watts);
                Double dumpEnergyPercent = DeviceUtils.getDumpEnergyPercent(deviceJsonNode);
                responseDataAttribute.put("capacity_percentage", dumpEnergyPercent);
                //充电时间、放电时间，无返回数据，待定。。。。
                Integer daiDing = 1111111;
                responseDataAttribute.put("charge_time_seconds", daiDing);
                Integer daiDing2 = 2222222;
                responseDataAttribute.put("discharge_time_seconds", daiDing2);
                //是否低功率，待定。。。
                responseDataAttribute.put("is_low_power", true);
                //用户能否控制，待定。。。
                responseDataAttribute.put("user_can_control", true);
                responseData.setAttributes(responseDataAttribute);


                /**封装responseData中的relationships*/
                Map<String, Object> responseDataRelationships = new HashMap<>();

                /**封装responseData中的relationships中的productRelationship*/
                Able_Relationship dataProductRelationship = new Able_Relationship();
                //封装dataProductRelationship中的links
                Map<String, String> dataProductRelationshipLinks = new HashMap<>();
                dataProductRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/product");
                dataProductRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/product");
                dataProductRelationship.setLinks(dataProductRelationshipLinks);
                //封装dataProductRelationship中的data
                Map<String, String> dataProductRelationshipData = new HashMap<>();
                dataProductRelationshipData.put("type", "products");
                //product应从数据库中查询，由于现在没有清楚product与其他表的关系，暂不查询？？？
                dataProductRelationshipData.put("id", "products_" + device_id);
                dataProductRelationship.setData(dataProductRelationshipData);
                //将dataProductRelationship添加入responseDataRelationships
                responseDataRelationships.put("product", dataProductRelationship);

                /**封装responseData中的relationships中的creatorRelationship*/
                Able_Relationship dataCreatorRelationship = new Able_Relationship();
                //封装dataCreatorRelationship中的links
                Map<String, String> dataCreatorRelationshipLinks = new HashMap<>();
                dataCreatorRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/creator");
                dataCreatorRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/creator");
                dataCreatorRelationship.setLinks(dataCreatorRelationshipLinks);
                //封装dataCreatorRelationship中的data
                Map<String, String> dataCreatorRelationshipData = new HashMap<>();
                dataCreatorRelationshipData.put("type", "users");
                dataCreatorRelationshipData.put("id", userSfdcId);
                dataCreatorRelationship.setData(dataCreatorRelationshipData);
                //将dataCreatorRelationship添加入responseDataRelationships
                responseDataRelationships.put("creator", dataCreatorRelationship);

                /**封装responseData中的relationships中的outletsRelationship*/
                Able_Relationship dataOutletsRelationship = new Able_Relationship();
                //封装dataOutletsRelationship中的links
                Map<String, Object> dataOutletsRelationshipLinks = new HashMap<>();
                dataOutletsRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/outlets");
                dataOutletsRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/outlets");
                dataOutletsRelationship.setLinks(dataOutletsRelationshipLinks);
                //封装dataOutletsRelationship中的data
                Map<String, Object> dataOutletsRelationshipData = new HashMap<>();
                dataOutletsRelationshipData.put("type", "outlets");
                dataOutletsRelationshipData.put("id", "outlets_" + device_id);
                List<Map<String, Object>> dataOutletsRelationshipDataList = new ArrayList<>();
                dataOutletsRelationshipDataList.add(dataOutletsRelationshipData);
                dataOutletsRelationship.setData(dataOutletsRelationshipDataList);
                //将dataOutletsRelationship添加入responseDataRelationships
                responseDataRelationships.put("outlets", dataOutletsRelationship);

                /**封装responseData中的relationships中的eventsRelationship*/
                Able_Relationship dataEventsRelationship = new Able_Relationship();
                //封装dataEventsRelationship中的links
                Map<String, Object> dataEventsRelationshipLinks = new HashMap<>();
                dataEventsRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/events");
                dataEventsRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/events");
                dataEventsRelationship.setLinks(dataEventsRelationshipLinks);
                //封装dataEventsRelationship中的data
                Map<String, String> dataEventsRelationshipData = new HashMap<>();
                dataEventsRelationshipData.put("type", "events");
                dataEventsRelationshipData.put("id", "events_" + device_id);
                List<Map<String, Object>> dataEventsRelationshipDataList = new ArrayList<>();
                dataEventsRelationshipDataList.add(dataOutletsRelationshipData);
                dataEventsRelationship.setData(dataEventsRelationshipDataList);
                //将dataEventsRelationship添加入responseDataRelationships
                responseDataRelationships.put("events", dataEventsRelationship);


                /**封装responseData中的relationships中的deviceErrorsRelationship*/
                Able_Relationship dataDeviceErrorsRelationship = new Able_Relationship();
                //封装deviceErrorsRelationship中的links
                Map<String, Object> deviceErrorsRelationshipLinks = new HashMap<>();
                deviceErrorsRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/device_errors");
                deviceErrorsRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/device_errors");
                dataDeviceErrorsRelationship.setLinks(deviceErrorsRelationshipLinks);
                //封装deviceErrorsRelationship中的data
                List<Map<String, Object>> deviceErrorsRelationshipDataList = new ArrayList<>();
                Map<String, Object> deviceErrorsRelationshipData = new HashMap<>();
                deviceErrorsRelationshipData.put("type", "device_errors");
                deviceErrorsRelationshipData.put("id", "device_errors_" + device_id);
                deviceErrorsRelationshipDataList.add(deviceErrorsRelationshipData);
                dataDeviceErrorsRelationship.setData(deviceErrorsRelationshipDataList);
                //将deviceErrorsRelationship添加入responseDataRelationships
                responseDataRelationships.put("device_errors", dataDeviceErrorsRelationship);


                /**封装responseData中的relationships中的firmwareRelationship*/
                Able_Relationship dataFirmwareRelationship = new Able_Relationship();
                //封装dataFirmwareRelationship中的links
                Map<String, String> firmwareRelationshipLinks = new HashMap<>();
                firmwareRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/firmware");
                firmwareRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/firmware");
                dataFirmwareRelationship.setLinks(firmwareRelationshipLinks);
                //封装dataFirmwareRelationship中的data
                Map<String, String> firmwareRelationshipData = new HashMap<>();
                firmwareRelationshipData.put("type", "firmwares");
                firmwareRelationshipData.put("id", "firmwares_" + device_id);
                dataFirmwareRelationship.setData(firmwareRelationshipData);
                //将dataFirmwareRelationship添加入responseDataRelationships
                responseDataRelationships.put("firmware", dataFirmwareRelationship);

                /**将responseDataRelationships封装入responseData*/
                responseData.setRelationships(responseDataRelationships);

                /**封装responseData中的links*/
                Map<String, String> responseDataLinks = new HashMap<>();
                responseDataLinks.put("self", baseLink + "devices/" + device_id);
                responseData.setLinks(responseDataLinks);

                /**将responseData封装入responseData集合*/
                responseDataList.add(responseData);

                /**-----------------------------------------------------------------------*/

                /****************封装responseBody中的included****************/
                //所需数据
                //当前时间戳
                GetUTCTime utcTime = new GetUTCTime();
                long currentUTCTimeStr = utcTime.getCurrentUTCTimeStr();
                String currentUtcTime = utcTime.getUTCTime(currentUTCTimeStr, "yyyy-MM-dd'T'HH:mm:ss'Z'");
                //从able获取数据checkUpdate
                Map<String, String> checkParam = new HashMap<>();
                checkParam.put("user_sfid", userSfdcId);
                checkParam.put("sn", device_id);
                String checkUpdateJsonResult = checkAndUpdateOTA.getCheckUpdateResult(checkParam);
                JsonNode checkUpdateJsonNode = jsonMapper.readTree(checkUpdateJsonResult);

                /**创建included对象*/
                List<Able_ResponseData> includedList = new ArrayList<>();

                /**封装included中的productsIncluded*/
                /**创建productsIncluded对象*/
                Able_Relationship_ResponseData productsIncluded = new Able_Relationship_ResponseData();
                /**封装productsIncluded字段*/
                //product应从数据库中查询，由于现在没有清楚product与其他表的关系，暂不查询？？？
                productsIncluded.setType("products");
                productsIncluded.setId("products_" + device_id);
                /**封装productsIncluded中的relationships*/
                Map<String, Object> productsIncludedRelationships = new HashMap<>();
                //封装photo中的links
                Map<String, String> photoLinks = new HashMap<>();
                photoLinks.put("self", baseLink + "products/" + device_id + "/relationships/photos");
                photoLinks.put("related", baseLink + "products/" + device_id + "/photos");
                //封装photo中的data
                Map<String, String> photoData = new HashMap<>();
                photoData.put("type", "photos");
                photoData.put("id", "photos_" + device_id);
                //封装photo
                Map<String, Object> photoMap = new HashMap<>();
                photoMap.put("links", photoLinks);
                photoMap.put("data", photoData);
                //封装productsIncludedRelationships
                productsIncludedRelationships.put("photos", photoMap);
                productsIncluded.setRelationships(productsIncludedRelationships);
                /**封装productsIncluded中的links*/
                Map<String, String> productsIncludedLinks = new HashMap<>();
                productsIncludedLinks.put("self", baseLink + "products/products_" + device_id);
                productsIncluded.setLinks(productsIncludedLinks);
                /**封装productsIncluded中的attribute*/
                Map<String, Object> productsIncludedAttribute = new HashMap<>();
                //name从哪里获取？？？ ---------》device中的product_code
                productsIncludedAttribute.put("name", devices.get(i).getProductCode());
                productsIncluded.setAttributes(productsIncludedAttribute);
                /**将productsIncluded添加入includedList*/
                includedList.add(productsIncluded);

                /**封装included中的usersIncluded*/
                /**创建usersIncluded对象*/
                Able_ResponseData usersIncluded = new Able_ResponseData();
                /**封装usersIncluded字段*/
                usersIncluded.setType("users");
                usersIncluded.setId(userSfdcId);
                /**封装usersIncluded中的attribute*/
                Map<String, Object> usersIncludedAttributes = new HashMap<>();
                usersIncludedAttributes.put("name", mobileUser.getName());
                usersIncludedAttributes.put("email", mobileUser.getEmail());
                usersIncludedAttributes.put("status", mobileUser.getStatus());
                usersIncluded.setAttributes(usersIncludedAttributes);
                /**封装usersIncluded中的links*/
                Map<String, String> usersIncludedLinks = new HashMap<>();
                usersIncludedLinks.put("self", baseLink + "users/" + mobileUser.getSfdcId());
                usersIncluded.setLinks(usersIncludedLinks);
                /**将usersIncluded添加入includedList*/
                includedList.add(usersIncluded);


                /**封装included中的 outletsIncluded*/
                /**创建 outletsIncluded 对象*/
                Able_ResponseData outletsIncluded = new Able_ResponseData();
                /**封装 outletsIncluded 字段*/
                outletsIncluded.setType("outlets");
                outletsIncluded.setId("outlets_" + device_id);
                /**封装 outletsIncluded 中的attribute*/
                Map<String, Object> outletsIncludedAttributes = new HashMap<>();
                //outlet中的数据？？？
                outletsIncludedAttributes.put("name", "outlet_1");
                outletsIncludedAttributes.put("outlet_type", "usb");
                outletsIncludedAttributes.put("status", "on");
                outletsIncludedAttributes.put("output_watts", output_watts);
                outletsIncluded.setAttributes(outletsIncludedAttributes);
                /**封装 outletsIncluded 中的links*/
                Map<String, String> outletsIncludedLinks = new HashMap<>();
                outletsIncludedLinks.put("self", baseLink + "outlets/outlets_" + device_id);
                outletsIncluded.setLinks(outletsIncludedLinks);
                /**将 outletsIncluded 添加入includedList*/
                includedList.add(outletsIncluded);

                /**封装included中的 eventsIncluded*/
                /**创建 eventsIncluded 对象*/
                Able_ResponseData eventsIncluded = new Able_ResponseData();
                /**封装 eventsIncluded 字段*/
                eventsIncluded.setType("events");
                eventsIncluded.setId("events_" + device_id);
                /**封装 eventsIncluded 中的attribute*/
                Map<String, Object> eventsIncludedAttributes = new HashMap<>();
                //able没有提供相关接口？？？
                eventsIncludedAttributes.put("action", "on");
                eventsIncludedAttributes.put("date", currentUtcTime);
                eventsIncluded.setAttributes(eventsIncludedAttributes);
                /**封装 eventsIncluded 中的links*/
                Map<String, String> eventsIncludedLinks = new HashMap<>();
                eventsIncludedLinks.put("self", baseLink + "events/events_" + device_id);
                eventsIncluded.setLinks(eventsIncludedLinks);
                /**将 eventsIncluded 添加入includedList*/
                includedList.add(eventsIncluded);

                /**封装included中的 deviceErrorsIncluded*/
                /**创建 deviceErrorsIncluded 对象*/
                Able_ResponseData deviceErrorsIncluded = new Able_ResponseData();
                /**封装 deviceErrorsIncluded 字段*/
                deviceErrorsIncluded.setType("device_errors");
                deviceErrorsIncluded.setId("device_errors_" + device_id);
                /**封装 deviceErrorsIncluded 中的attribute*/
                Map<String, Object> deviceErrorsIncludedAttributes = new HashMap<>();
                //code具体指的是？？？mapping表中对应的字段找不到对应数据，controlDevice方法返回的值是否是该值
                deviceErrorsIncludedAttributes.put("code", 1);
                deviceErrorsIncludedAttributes.put("description", "Something went wrong");
                deviceErrorsIncluded.setAttributes(deviceErrorsIncludedAttributes);
                /**封装 deviceErrorsIncluded 中的links*/
                Map<String, String> deviceErrorsIncludedLinks = new HashMap<>();
                deviceErrorsIncludedLinks.put("self", baseLink + "device_errors/device_errors_" + device_id);
                deviceErrorsIncluded.setLinks(deviceErrorsIncludedLinks);
                /**将 deviceErrorsIncluded 添加入includedList*/
                includedList.add(deviceErrorsIncluded);

                /**封装included中的 firmwaresIncluded*/
                /**创建 firmwaresIncluded 对象*/
                Able_Meta_ResponseData firmwaresIncluded = new Able_Meta_ResponseData();
                /**封装 firmwaresIncluded 字段*/
                firmwaresIncluded.setType("firmwares");
                firmwaresIncluded.setId("firmwares_" + device_id);
                /**封装 firmwaresIncluded 中的attribute*/
                Map<String, Object> firmwaresIncludedAttributes = new HashMap<>();
                firmwaresIncludedAttributes.put("version", checkUpdateJsonNode.get("currentVersion").asInt());
                firmwaresIncluded.setAttributes(firmwaresIncludedAttributes);
                /**封装 firmwaresIncluded 中的links*/
                Map<String, String> firmwaresIncludedLinks = new HashMap<>();
                firmwaresIncludedLinks.put("self", baseLink + "firmwares/firmwares_" + device_id);
                firmwaresIncluded.setLinks(firmwaresIncludedLinks);
                /**封装 firmwaresIncluded 中的meta*/
                Map<String, Object> firmwaresIncludedMeta = new HashMap<>();
                //status对应的是哪一个字段？？？
                firmwaresIncludedMeta.put("status", "waiting");
                firmwaresIncludedMeta.put("latest_version",checkUpdateJsonNode.get("targetVersion").asDouble());
                firmwaresIncluded.setMeta(firmwaresIncludedMeta);
                /**将 firmwaresIncluded 添加入includedList*/
                includedList.add(firmwaresIncluded);

                /**封装included中的 photosIncluded*/
                /**创建 photosIncluded 对象*/
                Able_ResponseData photosIncluded = new Able_ResponseData();
                /**封装 photosIncluded 字段*/
                photosIncluded.setType("photos");
                photosIncluded.setId("photos_" + device_id);
                /**封装 photosIncluded 中的attribute*/
                Map<String, Object> photosIncludedAttributes = new HashMap<>();
                //src是从哪里来的？？？
                photosIncludedAttributes.put("src", "https://example.com/image.jpg");
                photosIncluded.setAttributes(photosIncludedAttributes);
                /**封装 photosIncluded 中的links*/
                Map<String, String> photosIncludedLinks = new HashMap<>();
                photosIncludedLinks.put("self", baseLink + "photos/photos_" + device_id);
                photosIncluded.setLinks(photosIncludedLinks);
                /**将 photosIncluded 添加入includedList*/
                includedList.add(photosIncluded);

                //count指的是什么？？？
                metaMap.put("count", ""+i+"");

                linksRes.put("self", baseLink + "devices");
                linksRes.put("first", baseLink + "devices?page[number]=" + pageNum + "&page[size]=" + pageSize);
                linksRes.put("prev", baseLink + "devices?page[number]=" + pageNum + "&page[size]=" + pageSize);
                linksRes.put("next", baseLink + "devices?page[number]=" + pageNum + "&page[size]=" + pageSize);
                linksRes.put("last", baseLink + "devices?page[number]=" + pageNum + "&page[size]=" + pageSize);
            }
            /**********封装reponseBody对象**********/
            responseBody = new Able_ResponseListBody();
            responseBody.setData(responseDataList);
            responseBody.setIncluded(includeds);
            responseBody.setMeta(metaMap);
            responseBody.setLinks(linksRes);
        }

        return responseBody;
    }

    /**
     * 根据device_id创建一个新的device
     * @param Authorization
     * @param device_id
     * @param deviceParam
     * @return
     */
    @Override
    public Object createDevice(String Authorization, String device_id, Map<String, Object> deviceParam) throws Exception {
        /*会用到的数据*/
        //当前时间戳
        GetUTCTime utcTime = new GetUTCTime();
        long currentUTCTimeStr = utcTime.getCurrentUTCTimeStr();
        String currentUtcTime = utcTime.getUTCTime(currentUTCTimeStr, "yyyy-MM-dd'T'HH:mm:ss'Z'");


        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);

        /**判断用户是否已验证*/
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        ResponseEntity<?> responseEntity = DeviceUtils.checkVerified(mobileUser, Authorization);
        if (responseEntity != null) {
            return responseEntity;
        }
        String userSfdcId = mobileUser.getSfdcId();

        Able_Device device = new Able_Device();
        device.setDeviceId(device_id);
        device.setIsdelete(false);
        device.setProductCode("device");
        device.setUsersfid(userSfdcId);
        device.setUserCanControl((Boolean) deviceParam.get("user_can_control"));
        /**判断是否操作的是用户自己的数据*/
        Able_Device deviceCheck = deviceMapper.selectByPrimaryKey(device_id);
        if (deviceCheck != null) {
            if (!deviceCheck.getUsersfid().equals(userSfdcId)) {
                return DeviceUtils.getCannotPerformResponse(Authorization);
            }
            device.setLastModified(currentUtcTime);

            /**更新数据库*/
            deviceMapper.updateByPrimaryKey(device);
        } else {
            device.setLastModified(currentUtcTime);

            /**插入数据库*/
            deviceMapper.insertSelective(device);
        }

        /*封装请求参数*/
        Map<String, String> loadAndGetDataParam = new HashMap<>();
        loadAndGetDataParam.put("sn", device_id);
        //type如何确定？？？
        loadAndGetDataParam.put("type", "psStatus");
        //从able获取数据
        String deviceJsonData = loadAndGetData.getDataResult(loadAndGetDataParam);
        JsonNode deviceJsonNode = jsonMapper.readTree(deviceJsonData);
        //设备状态
        String chargeState =
                DeviceUtils.getDeviceStatus(deviceJsonNode.get("chargeState").asInt());

        /****************封装responseBody中的responseData****************/
        //所用数据
        Integer dumpEnergy = DeviceUtils.getDumpEnergy(deviceJsonNode);
        Integer output_watts = DeviceUtils.getOutputWatts(deviceJsonNode);

        /********创建responseData对象********/
        Able_Relationship_ResponseData responseData = new Able_Relationship_ResponseData();

        /********封装responseData属性********/
        responseData.setType("devices");
        responseData.setId(device_id);

        /********封装responseData中的links********/
        Map<String, String> responseDataLinks = new HashMap<>();
        responseDataLinks.put("self", baseLink + "devices/" + device_id);
        responseData.setLinks(responseDataLinks);

        /********封装responseData中的attributes********/
        Map<String, Object> responseDataAttributes = new HashMap<>();
        //name从哪里获取？？？ ---------》device中的product_code
        responseDataAttributes.put("name", device.getProductCode());
        responseDataAttributes.put("status", chargeState);
        //设备新加字段，。。。
        responseDataAttributes.put("serial_number", device_id);
        responseDataAttributes.put("output_watts_hours", dumpEnergy);
        responseDataAttributes.put("output_watts", output_watts);
        Double dumpEnergyPercent = DeviceUtils.getDumpEnergyPercent(deviceJsonNode);
        responseDataAttributes.put("capacity_percentage", dumpEnergyPercent);
        //充电时间、放电时间，无返回数据，待定。。。。
        Integer daiDing = 1111111;
        responseDataAttributes.put("charge_time_seconds", daiDing);
        Integer daiDing2 = 2222222;
        responseDataAttributes.put("discharge_time_seconds", daiDing2);
        //是否低功率，待定。。。
        responseDataAttributes.put("is_low_power", true);
        //用户能否控制，待定。。。
        responseDataAttributes.put("user_can_control", true);
        responseData.setAttributes(responseDataAttributes);

        /********封装responseData中的relationships********/
        /**创建relationships*/
        Map<String, Object> responseDataRelationships = new HashMap<>();

        /**封装responseData中的relationships中的productRelationship*/
        Able_Relationship dataProductRelationship = new Able_Relationship();
        //封装dataProductRelationship中的links
        Map<String, String> dataProductRelationshipLinks = new HashMap<>();
        dataProductRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/product");
        dataProductRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/product");
        dataProductRelationship.setLinks(dataProductRelationshipLinks);
        //封装dataProductRelationship中的data
        Map<String, String> dataProductRelationshipData = new HashMap<>();
        dataProductRelationshipData.put("type", "products");
        //product应从数据库中查询，由于现在没有清楚product与其他表的关系，暂不查询？？？
        dataProductRelationshipData.put("id", "products_" + device_id);
        dataProductRelationship.setData(dataProductRelationshipData);
        //将dataProductRelationship添加入responseDataRelationships
        responseDataRelationships.put("product", dataProductRelationship);

        /**封装responseData中的relationships中的creatorRelationship*/
        Able_Relationship dataCreatorRelationship = new Able_Relationship();
        //封装dataCreatorRelationship中的links
        Map<String, String> dataCreatorRelationshipLinks = new HashMap<>();
        dataCreatorRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/creator");
        dataCreatorRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/creator");
        dataCreatorRelationship.setLinks(dataCreatorRelationshipLinks);
        //封装dataCreatorRelationship中的data
        Map<String, String> dataCreatorRelationshipData = new HashMap<>();
        dataCreatorRelationshipData.put("type", "users");
        dataCreatorRelationshipData.put("id", userSfdcId);
        dataCreatorRelationship.setData(dataCreatorRelationshipData);
        //将dataCreatorRelationship添加入responseDataRelationships
        responseDataRelationships.put("creator", dataCreatorRelationship);

        /**封装responseData中的relationships中的outletsRelationship*/
        Able_Relationship dataOutletsRelationship = new Able_Relationship();
        //封装dataOutletsRelationship中的links
        Map<String, Object> dataOutletsRelationshipLinks = new HashMap<>();
        dataOutletsRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/outlets");
        dataOutletsRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/outlets");
        dataOutletsRelationship.setLinks(dataOutletsRelationshipLinks);
        //封装dataOutletsRelationship中的data
        Map<String, Object> dataOutletsRelationshipData = new HashMap<>();
        dataOutletsRelationshipData.put("type", "outlets");
        dataOutletsRelationshipData.put("id", "outlets_" + device_id);
        List<Map<String, Object>> dataOutletsRelationshipDataList = new ArrayList<>();
        dataOutletsRelationshipDataList.add(dataOutletsRelationshipData);
        dataOutletsRelationship.setData(dataOutletsRelationshipDataList);
        //将dataOutletsRelationship添加入responseDataRelationships
        responseDataRelationships.put("outlets", dataOutletsRelationship);

        /**封装responseData中的relationships中的eventsRelationship*/
        Able_Relationship dataEventsRelationship = new Able_Relationship();
        //封装dataEventsRelationship中的links
        Map<String, Object> dataEventsRelationshipLinks = new HashMap<>();
        dataEventsRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/events");
        dataEventsRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/events");
        dataEventsRelationship.setLinks(dataEventsRelationshipLinks);
        //封装dataEventsRelationship中的data
        Map<String, String> dataEventsRelationshipData = new HashMap<>();
        dataEventsRelationshipData.put("type", "events");
        dataEventsRelationshipData.put("id", "events_" + device_id);
        List<Map<String, Object>> dataEventsRelationshipDataList = new ArrayList<>();
        dataEventsRelationshipDataList.add(dataOutletsRelationshipData);
        dataEventsRelationship.setData(dataEventsRelationshipDataList);
        //将dataEventsRelationship添加入responseDataRelationships
        responseDataRelationships.put("events", dataEventsRelationship);

        /**封装responseData中的relationships中的deviceErrorsRelationship*/
        Able_Relationship dataDeviceErrorsRelationship = new Able_Relationship();
        //封装deviceErrorsRelationship中的links
        Map<String, Object> deviceErrorsRelationshipLinks = new HashMap<>();
        deviceErrorsRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/device_errors");
        deviceErrorsRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/device_errors");
        dataDeviceErrorsRelationship.setLinks(deviceErrorsRelationshipLinks);
        //封装deviceErrorsRelationship中的data
        List<Map<String, Object>> deviceErrorsRelationshipDataList = new ArrayList<>();
        Map<String, Object> deviceErrorsRelationshipData = new HashMap<>();
        deviceErrorsRelationshipData.put("type", "device_errors");
        deviceErrorsRelationshipData.put("id", "device_errors_" + device_id);
        deviceErrorsRelationshipDataList.add(deviceErrorsRelationshipData);
        dataDeviceErrorsRelationship.setData(deviceErrorsRelationshipDataList);
        //将deviceErrorsRelationship添加入responseDataRelationships
        responseDataRelationships.put("device_errors", dataDeviceErrorsRelationship);

        /**封装responseData中的relationships中的firmwareRelationship*/
        Able_Relationship dataFirmwareRelationship = new Able_Relationship();
        //封装dataFirmwareRelationship中的links
        Map<String, String> firmwareRelationshipLinks = new HashMap<>();
        firmwareRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/firmware");
        firmwareRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/firmware");
        dataFirmwareRelationship.setLinks(firmwareRelationshipLinks);
        //封装dataFirmwareRelationship中的data
        Map<String, String> firmwareRelationshipData = new HashMap<>();
        firmwareRelationshipData.put("type", "firmwares");
        firmwareRelationshipData.put("id", "firmwares_" + device_id);
        dataFirmwareRelationship.setData(firmwareRelationshipData);
        //将dataFirmwareRelationship添加入responseDataRelationships
        responseDataRelationships.put("firmware", dataFirmwareRelationship);

        /********将relationships封装进responseData********/
        responseData.setRelationships(responseDataRelationships);

        /****************封装responseBody中的included****************/
        /**所用数据*/
        //从able获取数据checkUpdate
        Map<String, String> checkParam = new HashMap<>();
        checkParam.put("user_sfid", userSfdcId);
        checkParam.put("sn", device_id);
        String checkUpdateJsonResult = checkAndUpdateOTA.getCheckUpdateResult(checkParam);
        JsonNode checkUpdateJsonNode = jsonMapper.readTree(checkUpdateJsonResult);

        /********创建include对象集合********/
        List<Able_ResponseData> responseIncludedList = new ArrayList<>();

        /********封装included中的usersIncluded********/
        //创建usersIncluded对象
        Able_ResponseData usersIncluded = new Able_ResponseData();
        //封装usersIncluded字段
        usersIncluded.setType("users");
        usersIncluded.setId(userSfdcId);
        //封装usersIncluded中的attribute
        Map<String, Object> usersIncludedAttributes = new HashMap<>();
        usersIncludedAttributes.put("name", mobileUser.getName());
        usersIncludedAttributes.put("email", mobileUser.getEmail());
        usersIncludedAttributes.put("status", mobileUser.getStatus());
        usersIncluded.setAttributes(usersIncludedAttributes);
        //封装usersIncluded中的links
        Map<String, String> usersIncludedLinks = new HashMap<>();
        usersIncludedLinks.put("self", baseLink + "users/" + userSfdcId);
        usersIncluded.setLinks(usersIncludedLinks);
        //将usersIncluded添加入includedList
        responseIncludedList.add(usersIncluded);

        /********封装included中的 outletsIncluded********/
        //创建 outletsIncluded 对象
        Able_ResponseData outletsIncluded = new Able_ResponseData();
        //封装 outletsIncluded 字段
        outletsIncluded.setType("outlets");
        outletsIncluded.setId("outlets_" + device_id);
        //封装 outletsIncluded 中的attribute
        Map<String, Object> outletsIncludedAttributes = new HashMap<>();
        //outlet中的数据？？？
        outletsIncludedAttributes.put("name", "outlet_1");
        outletsIncludedAttributes.put("outlet_type", "usb");
        outletsIncludedAttributes.put("status", "on");
        outletsIncludedAttributes.put("output_watts", output_watts);
        outletsIncluded.setAttributes(outletsIncludedAttributes);
        //封装 outletsIncluded 中的links
        Map<String, String> outletsIncludedLinks = new HashMap<>();
        outletsIncludedLinks.put("self", baseLink + "outlets/outlets_" + device_id);
        outletsIncluded.setLinks(outletsIncludedLinks);
        //将 outletsIncluded 添加入includedList
        responseIncludedList.add(outletsIncluded);

        /********封装included中的 eventsIncluded********/
        //创建 eventsIncluded 对象
        Able_ResponseData eventsIncluded = new Able_ResponseData();
        //封装 eventsIncluded 字段
        eventsIncluded.setType("events");
        eventsIncluded.setId("events_" + device_id);
        //封装 eventsIncluded 中的attribute
        Map<String, Object> eventsIncludedAttributes = new HashMap<>();
        //able没有提供相关接口？？？
        eventsIncludedAttributes.put("action", "on");
        eventsIncludedAttributes.put("date", currentUtcTime);
        eventsIncluded.setAttributes(eventsIncludedAttributes);
        //封装 eventsIncluded 中的links
        Map<String, String> eventsIncludedLinks = new HashMap<>();
        eventsIncludedLinks.put("self", baseLink + "events/events_" + device_id);
        eventsIncluded.setLinks(eventsIncludedLinks);
        //将 eventsIncluded 添加入includedList
        responseIncludedList.add(eventsIncluded);

        /********封装included中的 deviceErrorsIncluded********/
        //创建 deviceErrorsIncluded 对象
        Able_ResponseData deviceErrorsIncluded = new Able_ResponseData();
        //封装 deviceErrorsIncluded 字段
        deviceErrorsIncluded.setType("device_errors");
        deviceErrorsIncluded.setId("device_errors_" + device_id);
        //封装 deviceErrorsIncluded 中的attribute
        Map<String, Object> deviceErrorsIncludedAttributes = new HashMap<>();
        //code具体指的是？？？mapping表中对应的字段找不到对应数据，controlDevice方法返回的值是否是该值
        deviceErrorsIncludedAttributes.put("code", 1);
        deviceErrorsIncludedAttributes.put("description", "Something went wrong");
        deviceErrorsIncluded.setAttributes(deviceErrorsIncludedAttributes);
        //封装 deviceErrorsIncluded 中的links
        Map<String, String> deviceErrorsIncludedLinks = new HashMap<>();
        deviceErrorsIncludedLinks.put("self", baseLink + "device_errors/device_errors_" + device_id);
        deviceErrorsIncluded.setLinks(deviceErrorsIncludedLinks);
        //将 deviceErrorsIncluded 添加入includedList
        responseIncludedList.add(deviceErrorsIncluded);

        /********封装included中的 firmwaresIncluded********/
        //创建 firmwaresIncluded 对象
        Able_Meta_ResponseData firmwaresIncluded = new Able_Meta_ResponseData();
        //封装 firmwaresIncluded 字段
        firmwaresIncluded.setType("firmwares");
        firmwaresIncluded.setId("firmwares_" + device_id);
        //封装 firmwaresIncluded 中的attribute
        Map<String, Object> firmwaresIncludedAttributes = new HashMap<>();
        firmwaresIncludedAttributes.put("version", checkUpdateJsonNode.get("currentVersion").asInt());
        firmwaresIncluded.setAttributes(firmwaresIncludedAttributes);
        //封装 firmwaresIncluded 中的links
        Map<String, String> firmwaresIncludedLinks = new HashMap<>();
        firmwaresIncludedLinks.put("self", baseLink + "firmwares/firmwares_" + device_id);
        firmwaresIncluded.setLinks(firmwaresIncludedLinks);
        //封装 firmwaresIncluded 中的meta
        Map<String, Object> firmwaresIncludedMeta = new HashMap<>();
        //status对应的是哪一个字段？？？
        firmwaresIncludedMeta.put("status", "waiting");
        firmwaresIncludedMeta.put("latest_version",checkUpdateJsonNode.get("targetVersion").asDouble());
        firmwaresIncluded.setMeta(firmwaresIncludedMeta);
        //将 firmwaresIncluded 添加入includedList
        responseIncludedList.add(firmwaresIncluded);

        /*****************封装responseBody中的meta****************/
        Map<String, String> responseMeta = new HashMap<>();

        /*****************封装responseBody*****************/
        Able_ResponseBody responseBody = new Able_ResponseBody();
        responseBody.setData(responseData);
        responseBody.setIncluded(responseIncludedList);
        responseBody.setMeta(responseMeta);
        return responseBody;
    }

    /**
     * 查询特定的device并返回
     * @param Authorization
     * @param device_id @return
     */
    @Override
    public Object selectDeviceByDeviceId(String Authorization, String device_id) throws Exception {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);

        /**判断用户与device是否为空，用户是否操作的是自己的device，用户是否已验证*/
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);
        ResponseEntity<?> checkResponse = DeviceUtils.check(mobileUser, device, Authorization);
        if (checkResponse != null) {
            return checkResponse;
        }

        String userSfdcId = mobileUser.getSfdcId();

        Map<String, String> loadAndGetDataParam = new HashMap<>();
        loadAndGetDataParam.put("sn", device_id);
        //type如何确定？？？
        loadAndGetDataParam.put("type", "psStatus");

        //从able获取数据
        String deviceJsonData = loadAndGetData.getDataResult(loadAndGetDataParam);
        JsonNode deviceJsonNode = jsonMapper.readTree(deviceJsonData);
        //设备状态
        String chargeState =
                DeviceUtils.getDeviceStatus(deviceJsonNode.get("chargeState").asInt());

        /************************封装responseBody中的data************************/
        //所用数据
        Integer dumpEnergy = DeviceUtils.getDumpEnergy(deviceJsonNode);
        Integer output_watts = DeviceUtils.getOutputWatts(deviceJsonNode);

        /**创建resposeData对象*/
        Able_Relationship_ResponseData responseData = new Able_Relationship_ResponseData();

        /**封装responseData字段*/
        responseData.setType("devices");
        responseData.setId(device_id);

        /**封装responseData中的attribute*/
        Map<String, Object> responseDataAttributes = new HashMap<>();
        //name从哪里获取？？？ ---------》device中的product_code
        responseDataAttributes.put("name", device.getProductCode());
        responseDataAttributes.put("status", chargeState);
        //设备新加字段，。。。
        responseDataAttributes.put("serial_number", device_id);
        responseDataAttributes.put("output_watts_hours", dumpEnergy);
        responseDataAttributes.put("output_watts", output_watts);
        Double dumpEnergyPercent = DeviceUtils.getDumpEnergyPercent(deviceJsonNode);
        responseDataAttributes.put("capacity_percentage", dumpEnergyPercent);
        //充电时间、放电时间，无返回数据，待定。。。。
        Integer daiDing = 1111111;
        responseDataAttributes.put("charge_time_seconds", daiDing);
        Integer daiDing2 = 2222222;
        responseDataAttributes.put("discharge_time_seconds", daiDing2);
        //是否低功率，待定。。。
        responseDataAttributes.put("is_low_power", true);
        //用户能否控制，待定。。。
        responseDataAttributes.put("user_can_control", true);
        responseData.setAttributes(responseDataAttributes);

        /**********封装responseData中的relationships**********/
        /**创建relationships对象*/
        Map<String, Object> responseDataRelationships = new HashMap<>();

        /**封装responseData中的relationships中的productRelationship*/
        Able_Relationship dataProductRelationship = new Able_Relationship();
        //封装dataProductRelationship中的links
        Map<String, String> dataProductRelationshipLinks = new HashMap<>();
        dataProductRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/product");
        dataProductRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/product");
        dataProductRelationship.setLinks(dataProductRelationshipLinks);
        //封装dataProductRelationship中的data
        Map<String, String> dataProductRelationshipData = new HashMap<>();
        dataProductRelationshipData.put("type", "products");
        //product应从数据库中查询，由于现在没有清楚product与其他表的关系，暂不查询？？？
        dataProductRelationshipData.put("id", "products_" + device_id);
        dataProductRelationship.setData(dataProductRelationshipData);
        //将dataProductRelationship添加入responseDataRelationships
        responseDataRelationships.put("product", dataProductRelationship);

        /**封装responseData中的relationships中的creatorRelationship*/
        Able_Relationship dataCreatorRelationship = new Able_Relationship();
        //封装dataCreatorRelationship中的links
        Map<String, String> dataCreatorRelationshipLinks = new HashMap<>();
        dataCreatorRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/creator");
        dataCreatorRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/creator");
        dataCreatorRelationship.setLinks(dataCreatorRelationshipLinks);
        //封装dataCreatorRelationship中的data
        Map<String, String> dataCreatorRelationshipData = new HashMap<>();
        dataCreatorRelationshipData.put("type", "users");
        dataCreatorRelationshipData.put("id", userSfdcId);
        dataCreatorRelationship.setData(dataCreatorRelationshipData);
        //将dataCreatorRelationship添加入responseDataRelationships
        responseDataRelationships.put("creator", dataCreatorRelationship);

        /**封装responseData中的relationships中的outletsRelationship*/
        Able_Relationship dataOutletsRelationship = new Able_Relationship();
        //封装dataOutletsRelationship中的links
        Map<String, Object> dataOutletsRelationshipLinks = new HashMap<>();
        dataOutletsRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/outlets");
        dataOutletsRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/outlets");
        dataOutletsRelationship.setLinks(dataOutletsRelationshipLinks);
        //封装dataOutletsRelationship中的data
        Map<String, Object> dataOutletsRelationshipData = new HashMap<>();
        dataOutletsRelationshipData.put("type", "outlets");
        dataOutletsRelationshipData.put("id", "outlets_" + device_id);
        List<Map<String, Object>> dataOutletsRelationshipDataList = new ArrayList<>();
        dataOutletsRelationshipDataList.add(dataOutletsRelationshipData);
        dataOutletsRelationship.setData(dataOutletsRelationshipDataList);
        //将dataOutletsRelationship添加入responseDataRelationships
        responseDataRelationships.put("outlets", dataOutletsRelationship);

        /**封装responseData中的relationships中的eventsRelationship*/
        Able_Relationship dataEventsRelationship = new Able_Relationship();
        //封装dataEventsRelationship中的links
        Map<String, Object> dataEventsRelationshipLinks = new HashMap<>();
        dataEventsRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/events");
        dataEventsRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/events");
        dataEventsRelationship.setLinks(dataEventsRelationshipLinks);
        //封装dataEventsRelationship中的data
        Map<String, String> dataEventsRelationshipData = new HashMap<>();
        dataEventsRelationshipData.put("type", "events");
        dataEventsRelationshipData.put("id", "events_" + device_id);
        List<Map<String, Object>> dataEventsRelationshipDataList = new ArrayList<>();
        dataEventsRelationshipDataList.add(dataOutletsRelationshipData);
        dataEventsRelationship.setData(dataEventsRelationshipDataList);
        //将dataEventsRelationship添加入responseDataRelationships
        responseDataRelationships.put("events", dataEventsRelationship);

        /**封装responseData中的relationships中的deviceErrorsRelationship*/
        Able_Relationship dataDeviceErrorsRelationship = new Able_Relationship();
        //封装deviceErrorsRelationship中的links
        Map<String, Object> deviceErrorsRelationshipLinks = new HashMap<>();
        deviceErrorsRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/device_errors");
        deviceErrorsRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/device_errors");
        dataDeviceErrorsRelationship.setLinks(deviceErrorsRelationshipLinks);
        //封装deviceErrorsRelationship中的data
        List<Map<String, Object>> deviceErrorsRelationshipDataList = new ArrayList<>();
        Map<String, Object> deviceErrorsRelationshipData = new HashMap<>();
        deviceErrorsRelationshipData.put("type", "device_errors");
        deviceErrorsRelationshipData.put("id", "device_errors_" + device_id);
        deviceErrorsRelationshipDataList.add(deviceErrorsRelationshipData);
        dataDeviceErrorsRelationship.setData(deviceErrorsRelationshipDataList);
        //将deviceErrorsRelationship添加入responseDataRelationships
        responseDataRelationships.put("device_errors", dataDeviceErrorsRelationship);

        /**封装responseData中的relationships中的firmwareRelationship*/
        Able_Relationship dataFirmwareRelationship = new Able_Relationship();
        //封装dataFirmwareRelationship中的links
        Map<String, String> firmwareRelationshipLinks = new HashMap<>();
        firmwareRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/firmware");
        firmwareRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/firmware");
        dataFirmwareRelationship.setLinks(firmwareRelationshipLinks);
        //封装dataFirmwareRelationship中的data
        Map<String, String> firmwareRelationshipData = new HashMap<>();
        firmwareRelationshipData.put("type", "firmwares");
        firmwareRelationshipData.put("id", "firmwares_" + device_id);
        dataFirmwareRelationship.setData(firmwareRelationshipData);
        //将dataFirmwareRelationship添加入responseDataRelationships
        responseDataRelationships.put("firmware", dataFirmwareRelationship);

        /**封装responseData中的relationships*/
        responseData.setRelationships(responseDataRelationships);

        /**设置responseData的links*/
        Map<String, String> dataLinks = new HashMap<>();
        dataLinks.put("self", baseLink + "devices/" + device_id);
        responseData.setLinks(dataLinks);

        /***********封装responseBody中的included**********/
        /********所需数据********/
        //当前时间戳
        GetUTCTime utcTime = new GetUTCTime();
        long currentUTCTimeStr = utcTime.getCurrentUTCTimeStr();
        String currentUtcTime = utcTime.getUTCTime(currentUTCTimeStr, "yyyy-MM-dd'T'HH:mm:ss'Z'");

        //从able获取数据checkUpdate
        Map<String, String> checkParam = new HashMap<>();
        checkParam.put("user_sfid", userSfdcId);
        checkParam.put("sn", device_id);
        String checkUpdateJsonResult = checkAndUpdateOTA.getCheckUpdateResult(checkParam);
        JsonNode checkUpdateJsonNode = jsonMapper.readTree(checkUpdateJsonResult);

        /**创建include对象集合*/
        List<Able_ResponseData> includedList = new ArrayList<>();

        /********封装included中的usersIncluded********/
        /**创建usersIncluded对象*/
        Able_ResponseData usersIncluded = new Able_ResponseData();
        /**封装usersIncluded字段*/
        usersIncluded.setType("users");
        usersIncluded.setId(userSfdcId);
        /**封装usersIncluded中的attribute*/
        Map<String, Object> usersIncludedAttributes = new HashMap<>();
        usersIncludedAttributes.put("name", mobileUser.getName());
        usersIncludedAttributes.put("email", mobileUser.getEmail());
        usersIncludedAttributes.put("status", mobileUser.getStatus());
        usersIncluded.setAttributes(usersIncludedAttributes);
        /**封装usersIncluded中的links*/
        Map<String, String> usersIncludedLinks = new HashMap<>();
        usersIncludedLinks.put("self", baseLink + "users/" + userSfdcId);
        usersIncluded.setLinks(usersIncludedLinks);
        /**将usersIncluded添加入includedList*/
        includedList.add(usersIncluded);

        /********封装included中的 outletsIncluded********/
        /**创建 outletsIncluded 对象*/
        Able_ResponseData outletsIncluded = new Able_ResponseData();
        /**封装 outletsIncluded 字段*/
        outletsIncluded.setType("outlets");
        outletsIncluded.setId("outlets_" + device_id);
        /**封装 outletsIncluded 中的attribute*/
        Map<String, Object> outletsIncludedAttributes = new HashMap<>();
        //outlet中的数据？？？
        outletsIncludedAttributes.put("name", "outlet_1");
        outletsIncludedAttributes.put("outlet_type", "usb");
        outletsIncludedAttributes.put("status", "on");
        outletsIncludedAttributes.put("output_watts", output_watts);
        outletsIncluded.setAttributes(outletsIncludedAttributes);
        /**封装 outletsIncluded 中的links*/
        Map<String, String> outletsIncludedLinks = new HashMap<>();
        outletsIncludedLinks.put("self", baseLink + "outlets/outlets_" + device_id);
        outletsIncluded.setLinks(outletsIncludedLinks);
        /**将 outletsIncluded 添加入includedList*/
        includedList.add(outletsIncluded);

        /********封装included中的 eventsIncluded********/
        /**创建 eventsIncluded 对象*/
        Able_ResponseData eventsIncluded = new Able_ResponseData();
        /**封装 eventsIncluded 字段*/
        eventsIncluded.setType("events");
        eventsIncluded.setId("events_" + device_id);
        /**封装 eventsIncluded 中的attribute*/
        Map<String, Object> eventsIncludedAttributes = new HashMap<>();
        //able没有提供相关接口？？？
        eventsIncludedAttributes.put("action", "on");
        eventsIncludedAttributes.put("date", currentUtcTime);
        eventsIncluded.setAttributes(eventsIncludedAttributes);
        /**封装 eventsIncluded 中的links*/
        Map<String, String> eventsIncludedLinks = new HashMap<>();
        eventsIncludedLinks.put("self", baseLink + "events/events_" + device_id);
        eventsIncluded.setLinks(eventsIncludedLinks);
        /**将 eventsIncluded 添加入includedList*/
        includedList.add(eventsIncluded);

        /********封装included中的 deviceErrorsIncluded********/
        /**创建 deviceErrorsIncluded 对象*/
        Able_ResponseData deviceErrorsIncluded = new Able_ResponseData();
        /**封装 deviceErrorsIncluded 字段*/
        deviceErrorsIncluded.setType("device_errors");
        deviceErrorsIncluded.setId("device_errors_" + device_id);
        /**封装 deviceErrorsIncluded 中的attribute*/
        Map<String, Object> deviceErrorsIncludedAttributes = new HashMap<>();
        //code具体指的是？？？mapping表中对应的字段找不到对应数据，controlDevice方法返回的值是否是该值
        deviceErrorsIncludedAttributes.put("code", 1);
        deviceErrorsIncludedAttributes.put("description", "Something went wrong");
        deviceErrorsIncluded.setAttributes(deviceErrorsIncludedAttributes);
        /**封装 deviceErrorsIncluded 中的links*/
        Map<String, String> deviceErrorsIncludedLinks = new HashMap<>();
        deviceErrorsIncludedLinks.put("self", baseLink + "device_errors/device_errors_" + device_id);
        deviceErrorsIncluded.setLinks(deviceErrorsIncludedLinks);
        /**将 deviceErrorsIncluded 添加入includedList*/
        includedList.add(deviceErrorsIncluded);

        /********封装included中的 firmwaresIncluded********/
        /**创建 firmwaresIncluded 对象*/
        Able_Meta_ResponseData firmwaresIncluded = new Able_Meta_ResponseData();
        /**封装 firmwaresIncluded 字段*/
        firmwaresIncluded.setType("firmwares");
        firmwaresIncluded.setId("firmwares_" + device_id);
        /**封装 firmwaresIncluded 中的attribute*/
        Map<String, Object> firmwaresIncludedAttributes = new HashMap<>();
        firmwaresIncludedAttributes.put("version", checkUpdateJsonNode.get("currentVersion").asInt());
        firmwaresIncluded.setAttributes(firmwaresIncludedAttributes);
        /**封装 firmwaresIncluded 中的links*/
        Map<String, String> firmwaresIncludedLinks = new HashMap<>();
        firmwaresIncludedLinks.put("self", baseLink + "firmwares/firmwares_" + device_id);
        firmwaresIncluded.setLinks(firmwaresIncludedLinks);
        /**封装 firmwaresIncluded 中的meta*/
        Map<String, Object> firmwaresIncludedMeta = new HashMap<>();
        //status对应的是哪一个字段？？？
        firmwaresIncludedMeta.put("status", "waiting");
        firmwaresIncludedMeta.put("latest_version",checkUpdateJsonNode.get("targetVersion").asDouble());
        firmwaresIncluded.setMeta(firmwaresIncludedMeta);
        /**将 firmwaresIncluded 添加入includedList*/
        includedList.add(firmwaresIncluded);

        /****************封装responseBody****************/
        Able_ResponseBody responseBody = new Able_ResponseBody();
        responseBody.setData(responseData);
        responseBody.setIncluded(includedList);
        Map<String, String> meta = new HashMap<>();
        responseBody.setMeta(meta);
        return responseBody;
    }

    /**
     * 根据device_id,deviceStatus更新设备
     *
     * @param Authorization,device_id,updateStatus
     * @return
     */
    @Override
    public Object updateDevice(String Authorization, String device_id, String updateStatus) throws Exception {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);

        /**判断用户与device是否为空，用户是否操作的是自己的device，用户是否已验证*/
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);
        ResponseEntity<?> checkResponse = DeviceUtils.check(mobileUser, device, Authorization);
        if (checkResponse != null) {
            return checkResponse;
        }
        /**判断用户是否能够控制此设备*/
        if (!device.getUserCanControl()) {
            return DeviceUtils.getCannotPerformResponse(Authorization);
        }

        /**从able获取controlDevice数据*/
        //拼装参数
        Map<String, String> controlDeviceParam = new HashMap<>();
        controlDeviceParam.put("user_sfid", mobileUser.getSfdcId());
        controlDeviceParam.put("sn", device_id);
        controlDeviceParam.put("cmd", updateStatus);
        //获得数据
        String controlDeviceResult = controlDevice.getControlDeviceResult(controlDeviceParam);
        JsonNode controlDeviceJsonNode = jsonMapper.readTree(controlDeviceResult);

        /**从able获取device实时数据*/
        //拼装参数
        Map<String, String> loadAndGetDataParam = new HashMap<>();
        loadAndGetDataParam.put("sn", device_id);
        //type如何确定？？？
        loadAndGetDataParam.put("type", "psStatus");
        //获得数据
        String deviceJsonData = loadAndGetData.getDataResult(loadAndGetDataParam);
        JsonNode deviceJsonNode = jsonMapper.readTree(deviceJsonData);
        //设备状态
        String chargeState =
                DeviceUtils.getDeviceStatus(deviceJsonNode.get("chargeState").asInt());


        /****************封装responseBody中的data****************/
        //所用数据
        Integer dumpEnergy = DeviceUtils.getDumpEnergy(deviceJsonNode);
        Integer output_watts = DeviceUtils.getOutputWatts(deviceJsonNode);
        Double dumpEnergyPercent = DeviceUtils.getDumpEnergyPercent(deviceJsonNode);

        /**创建responseData对象*/
        Able_Relationship_ResponseData responseData = new Able_Relationship_ResponseData();
        /**封装responseData字段*/
        responseData.setType("devices");
        responseData.setId(device_id);
        /**封装responseData中的attribute*/
        //创建responseData中的attribute对象
        Map<String, Object> responseDataAttributes = new HashMap<>();
        //封装responseDataAttributes参数
        //name从哪里获取？？？ ---------》device中的product_code
        responseDataAttributes.put("name", "T-800");
        responseDataAttributes.put("status", chargeState);
        responseDataAttributes.put("output_watts_hours", dumpEnergy);
        responseDataAttributes.put("output_watts", output_watts);
        responseDataAttributes.put("capacity_percentage", dumpEnergyPercent);
        //充电时间、放电时间，无返回数据，待定。。。。
        Integer daiDing = 1111111;
        responseDataAttributes.put("charge_time_seconds", daiDing);
        Integer daiDing2 = 2222222;
        responseDataAttributes.put("discharge_time_seconds", daiDing2);
        //是否低功率，待定。。。
        responseDataAttributes.put("is_low_power", true);
        responseData.setAttributes(responseDataAttributes);

        /********封装responseData中的relationships********/
        Map<String, Object> responseDataRelationships = new HashMap<>();

        /**封装responseData中的relationships中的 productRelationship*/
        Able_Relationship dataProductRelationship = new Able_Relationship();
        //封装dataProductRelationship中的links
        Map<String, String> dataProductRelationshipLinks = new HashMap<>();
        dataProductRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/product");
        dataProductRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/product");
        dataProductRelationship.setLinks(dataProductRelationshipLinks);
        //封装dataProductRelationship中的data
        Map<String, String> dataProductRelationshipData = new HashMap<>();
        dataProductRelationshipData.put("type", "products");
        //product应从数据库中查询，由于现在没有清楚product与其他表的关系，暂不查询？？？
        dataProductRelationshipData.put("id", "products_" + device_id);
        dataProductRelationship.setData(dataProductRelationshipData);
        //将dataProductRelationship添加入responseDataRelationships
        responseDataRelationships.put("product", dataProductRelationship);

        /**封装responseData中的relationships中的 creatorRelationship*/
        Able_Relationship dataCreatorRelationship = new Able_Relationship();
        //封装dataCreatorRelationship中的links
        Map<String, String> dataCreatorRelationshipLinks = new HashMap<>();
        dataCreatorRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/creator");
        dataCreatorRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/creator");
        dataCreatorRelationship.setLinks(dataCreatorRelationshipLinks);
        //封装dataCreatorRelationship中的data
        Map<String, String> dataCreatorRelationshipData = new HashMap<>();
        dataCreatorRelationshipData.put("type", "users");
        dataCreatorRelationshipData.put("id", mobileUser.getSfdcId());
        dataCreatorRelationship.setData(dataCreatorRelationshipData);
        //将dataCreatorRelationship添加入responseDataRelationships
        responseDataRelationships.put("creator", dataCreatorRelationship);

        /**封装responseData中的relationships中的 outletsRelationship*/
        Able_Relationship dataOutletsRelationship = new Able_Relationship();
        //封装dataOutletsRelationship中的links
        Map<String, Object> dataOutletsRelationshipLinks = new HashMap<>();
        dataOutletsRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/outlets");
        dataOutletsRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/outlets");
        dataOutletsRelationship.setLinks(dataOutletsRelationshipLinks);
        //封装dataOutletsRelationship中的data
        Map<String, Object> dataOutletsRelationshipData = new HashMap<>();
        dataOutletsRelationshipData.put("type", "outlets");
        dataOutletsRelationshipData.put("id", "outlets_" + device_id);
        List<Map<String, Object>> dataOutletsRelationshipDataList = new ArrayList<>();
        dataOutletsRelationshipDataList.add(dataOutletsRelationshipData);
        dataOutletsRelationship.setData(dataOutletsRelationshipDataList);
        //将dataOutletsRelationship添加入responseDataRelationships
        responseDataRelationships.put("outlets", dataOutletsRelationship);

        /**封装responseData中的relationships中的 eventsRelationship*/
        Able_Relationship dataEventsRelationship = new Able_Relationship();
        //封装dataEventsRelationship中的links
        Map<String, Object> dataEventsRelationshipLinks = new HashMap<>();
        dataEventsRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/events");
        dataEventsRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/events");
        dataEventsRelationship.setLinks(dataEventsRelationshipLinks);
        //封装dataEventsRelationship中的data
        Map<String, String> dataEventsRelationshipData = new HashMap<>();
        dataEventsRelationshipData.put("type", "events");
        dataEventsRelationshipData.put("id", "events_" + device_id);
        List<Map<String, Object>> dataEventsRelationshipDataList = new ArrayList<>();
        dataEventsRelationshipDataList.add(dataOutletsRelationshipData);
        dataEventsRelationship.setData(dataEventsRelationshipDataList);
        //将dataEventsRelationship添加入responseDataRelationships
        responseDataRelationships.put("events", dataEventsRelationship);

        /**封装responseData中的relationships中的 deviceErrorsRelationship*/
        Able_Relationship dataDeviceErrorsRelationship = new Able_Relationship();
        //封装deviceErrorsRelationship中的links
        Map<String, Object> deviceErrorsRelationshipLinks = new HashMap<>();
        deviceErrorsRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/device_errors");
        deviceErrorsRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/device_errors");
        dataDeviceErrorsRelationship.setLinks(deviceErrorsRelationshipLinks);
        //封装deviceErrorsRelationship中的data
        List<Map<String, Object>> deviceErrorsRelationshipDataList = new ArrayList<>();
        Map<String, Object> deviceErrorsRelationshipData = new HashMap<>();
        deviceErrorsRelationshipData.put("type", "device_errors");
        deviceErrorsRelationshipData.put("id", "device_errors_" + device_id);
        deviceErrorsRelationshipDataList.add(deviceErrorsRelationshipData);
        dataDeviceErrorsRelationship.setData(deviceErrorsRelationshipDataList);
        //将deviceErrorsRelationship添加入responseDataRelationships
        responseDataRelationships.put("device_errors", dataDeviceErrorsRelationship);

        /**封装responseData中的relationships中的 frimwareRelationship*/
        Able_Relationship dataFirmwareRelationship = new Able_Relationship();
        //封装dataFirmwareRelationship中的links
        Map<String, String> firmwareRelationshipLinks = new HashMap<>();
        firmwareRelationshipLinks.put("self", baseLink + "devices/" + device_id + "/relationships/firmware");
        firmwareRelationshipLinks.put("related", baseLink + "devices/" + device_id + "/firmware");
        dataFirmwareRelationship.setLinks(firmwareRelationshipLinks);
        //封装dataFirmwareRelationship中的data
        Map<String, String> firmwareRelationshipData = new HashMap<>();
        firmwareRelationshipData.put("type", "firmwares");
        firmwareRelationshipData.put("id", "firmwares_" + device_id);
        dataFirmwareRelationship.setData(firmwareRelationshipData);
        //将dataFirmwareRelationship添加入responseDataRelationships
        responseDataRelationships.put("firmware", dataFirmwareRelationship);

        /********封装reponseBody中的links*******/
        Map<String, String> dataLinks = new HashMap<>();
        dataLinks.put("self", baseLink + "devices/" + device_id);

        /**封装responseData*/
        responseData.setAttributes(responseDataAttributes);
        responseData.setRelationships(responseDataRelationships);
        responseData.setLinks(dataLinks);

        /****************封装reponseBody中的included****************/
        /**所用数据*/
        //user的sfid
        String userSfdcId = mobileUser.getSfdcId();
        //当前时间戳
        GetUTCTime utcTime = new GetUTCTime();
        long currentUTCTimeStr = utcTime.getCurrentUTCTimeStr();
        String currentUtcTime = utcTime.getUTCTime(currentUTCTimeStr, "yyyy-MM-dd'T'HH:mm:ss'Z'");
        //从able获取数据checkUpdate
        Map<String, String> checkParam = new HashMap<>();
        checkParam.put("user_sfid", userSfdcId);
        checkParam.put("sn", device_id);
        String checkUpdateJsonResult = checkAndUpdateOTA.getCheckUpdateResult(checkParam);
        JsonNode checkUpdateJsonNode = jsonMapper.readTree(checkUpdateJsonResult);

        /**创建responseBody中的included对象集合*/
        List<Able_ResponseData> includedList = new ArrayList<>();

        /********封装included中的usersIncluded********/
        /**创建usersIncluded对象*/
        Able_ResponseData usersIncluded = new Able_ResponseData();
        /**封装usersIncluded字段*/
        usersIncluded.setType("users");
        usersIncluded.setId(userSfdcId);
        /**封装usersIncluded中的attribute*/
        Map<String, Object> usersIncludedAttributes = new HashMap<>();
        usersIncludedAttributes.put("name", mobileUser.getName());
        usersIncludedAttributes.put("email", mobileUser.getEmail());
        usersIncludedAttributes.put("status", mobileUser.getStatus());
        usersIncluded.setAttributes(usersIncludedAttributes);
        /**封装usersIncluded中的links*/
        Map<String, String> usersIncludedLinks = new HashMap<>();
        usersIncludedLinks.put("self", baseLink + "users/" + userSfdcId);
        usersIncluded.setLinks(usersIncludedLinks);
        /**将usersIncluded添加入includedList*/
        includedList.add(usersIncluded);

        /********封装included中的 outletsIncluded********/
        /**创建 outletsIncluded 对象*/
        Able_ResponseData outletsIncluded = new Able_ResponseData();
        /**封装 outletsIncluded 字段*/
        outletsIncluded.setType("outlets");
        outletsIncluded.setId("outlets_" + device_id);
        /**封装 outletsIncluded 中的attribute*/
        Map<String, Object> outletsIncludedAttributes = new HashMap<>();
        //outlet中的数据？？？
        outletsIncludedAttributes.put("name", "outlet_1");
        outletsIncludedAttributes.put("outlet_type", "usb");
        outletsIncludedAttributes.put("status", "on");
        outletsIncludedAttributes.put("output_watts", output_watts);
        outletsIncluded.setAttributes(outletsIncludedAttributes);
        /**封装 outletsIncluded 中的links*/
        Map<String, String> outletsIncludedLinks = new HashMap<>();
        outletsIncludedLinks.put("self", baseLink + "outlets/outlets_" + device_id);
        outletsIncluded.setLinks(outletsIncludedLinks);
        /**将 outletsIncluded 添加入includedList*/
        includedList.add(outletsIncluded);

        /********封装included中的 eventsIncluded********/
        /**创建 eventsIncluded 对象*/
        Able_ResponseData eventsIncluded = new Able_ResponseData();
        /**封装 eventsIncluded 字段*/
        eventsIncluded.setType("events");
        eventsIncluded.setId("events_" + device_id);
        /**封装 eventsIncluded 中的attribute*/
        Map<String, Object> eventsIncludedAttributes = new HashMap<>();
        //able没有提供相关接口？？？
        eventsIncludedAttributes.put("action", "on");
        eventsIncludedAttributes.put("date", currentUtcTime);
        eventsIncluded.setAttributes(eventsIncludedAttributes);
        /**封装 eventsIncluded 中的links*/
        Map<String, String> eventsIncludedLinks = new HashMap<>();
        eventsIncludedLinks.put("self", baseLink + "events/events_" + device_id);
        eventsIncluded.setLinks(eventsIncludedLinks);
        /**将 eventsIncluded 添加入includedList*/
        includedList.add(eventsIncluded);

        /********封装included中的 deviceErrorsIncluded********/
        /**创建 deviceErrorsIncluded 对象*/
        Able_ResponseData deviceErrorsIncluded = new Able_ResponseData();
        /**封装 deviceErrorsIncluded 字段*/
        deviceErrorsIncluded.setType("device_errors");
        deviceErrorsIncluded.setId("device_errors_" + device_id);
        /**封装 deviceErrorsIncluded 中的attribute*/
        Map<String, Object> deviceErrorsIncludedAttributes = new HashMap<>();
        //code具体指的是？？？mapping表中对应的字段找不到对应数据，controlDevice方法返回的值是否是该值
        deviceErrorsIncludedAttributes.put("code", 1);
        deviceErrorsIncludedAttributes.put("description", "Something went wrong");
        deviceErrorsIncluded.setAttributes(deviceErrorsIncludedAttributes);
        /**封装 deviceErrorsIncluded 中的links*/
        Map<String, String> deviceErrorsIncludedLinks = new HashMap<>();
        deviceErrorsIncludedLinks.put("self", baseLink + "device_errors/device_errors_" + device_id);
        deviceErrorsIncluded.setLinks(deviceErrorsIncludedLinks);
        /**将 deviceErrorsIncluded 添加入includedList*/
        includedList.add(deviceErrorsIncluded);

        /********封装included中的 firmwaresIncluded********/
        /**创建 firmwaresIncluded 对象*/
        Able_Meta_ResponseData firmwaresIncluded = new Able_Meta_ResponseData();
        /**封装 firmwaresIncluded 字段*/
        firmwaresIncluded.setType("firmwares");
        firmwaresIncluded.setId("firmwares_" + device_id);
        /**封装 firmwaresIncluded 中的attribute*/
        Map<String, Object> firmwaresIncludedAttributes = new HashMap<>();
        firmwaresIncludedAttributes.put("version", checkUpdateJsonNode.get("currentVersion").asInt());
        firmwaresIncluded.setAttributes(firmwaresIncludedAttributes);
        /**封装 firmwaresIncluded 中的links*/
        Map<String, String> firmwaresIncludedLinks = new HashMap<>();
        firmwaresIncludedLinks.put("self", baseLink + "firmwares/firmwares_" + device_id);
        firmwaresIncluded.setLinks(firmwaresIncludedLinks);
        /**封装 firmwaresIncluded 中的meta*/
        Map<String, Object> firmwaresIncludedMeta = new HashMap<>();
        //status对应的是哪一个字段？？？
        firmwaresIncludedMeta.put("status", "waiting");
        firmwaresIncludedMeta.put("latest_version",checkUpdateJsonNode.get("targetVersion").asDouble());
        firmwaresIncluded.setMeta(firmwaresIncludedMeta);
        /**将 firmwaresIncluded 添加入includedList*/
        includedList.add(firmwaresIncluded);

        /****************封装responseBody中的meta****************/
        Map<String, String> responseMeta = new HashMap<>();

        /********************************封装responseBody********************************/
        Able_ResponseBody responseBody = new Able_ResponseBody();
        responseBody.setData(responseData);
        responseBody.setIncluded(includedList);
        responseBody.setMeta(responseMeta);

        return responseBody;
    }

    /**
     * 根据device_id删除指定的device
     * @param Authorization
     * @param device_id
     */
    @Override
    public ResponseEntity deleteByDeviceId(String Authorization, String device_id) {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);

        /**判断用户是否操作的是自己的device，用户是否已验证*/
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);
        if (!"verified".equals(mobileUser.getStatus())) {
            return DeviceUtils.getCannotPerformResponse(Authorization);
        } else if (!mobileUser.getSfdcId().equals(device.getUsersfid())) {
            return DeviceUtils.getCannotPerformResponse(Authorization);
        }

        device.setIsdelete(true);
        deviceMapper.updateByPrimaryKeySelective(device);
        return null;
    }

    /**
     * 根据device_id查询user并返回
     *
     * @param Authorization,device_id
     * @return
     */
    @Override
    public Object selectCreatorByDeviceId(String Authorization, String device_id) {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);

        /**判断用户与device是否为空，用户是否操作的是自己的device，用户是否已验证*/
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);
        ResponseEntity<?> checkResponse = DeviceUtils.check(mobileUser, device, Authorization);
        if (checkResponse != null) {
            return checkResponse;
        }

        /**封装responseBody中的links*/
        Map<String, String> contentLinks = new HashMap<>();
        contentLinks.put("self", baseLink + "devices/" + device_id + "/relationships/creator");
        contentLinks.put("related", baseLink + "devices/" + device_id + "/creator");

        /**封装responseBody中的data*/
        Map<String, String> contentData = new HashMap<>();
        contentData.put("type", "users");
        contentData.put("id", mobileUser.getSfdcId());

        /**封装responseBody*/
        Able_Relationship responseBody = new Able_Relationship();
        responseBody.setData(contentData);
        responseBody.setLinks(contentLinks);
        return responseBody;
    }

    /**
     * 根据device_id查询outlet并返回
     *
     *
     * @param Authorization
     * @param device_id
     * @return
     */
    @Override
    public Object selectOutletsByDeviceId(String Authorization, String device_id) throws Exception {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);

        /**判断用户与device是否为空，用户是否操作的是自己的device，用户是否已验证*/
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);
        ResponseEntity<?> checkResponse = DeviceUtils.check(mobileUser, device, Authorization);
        if (checkResponse != null) {
            return checkResponse;
        }

        /**创建rsponseBody对象*/
        Able_Relationship responseBody = new Able_Relationship();
        /**封装rspoonseBody中的links*/
        Map<String, String> contentLinks = new HashMap<>();
        contentLinks.put("self", baseLink + "devices/" + device_id + "/relationships/outlets");
        contentLinks.put("related", baseLink + "devices/" + device_id + "/outlets");
        responseBody.setLinks(contentLinks);
        /**封装responseBody中的data*/
        Map<String, String> contentData = new HashMap<>();
        contentData.put("type", "outlets");
        contentData.put("id", "outlets_" + device_id);
        List<Map<String, String>> responseData = new ArrayList<>();
        responseData.add(contentData);
        responseBody.setData(responseData);
        return responseBody;
    }

    /**
     * 根据device_id查询events并返回
     *
     * @param Authorization
     * @param device_id
     * @return
     */
    @Override
    public Object selectEventByDeviceId(String Authorization, String device_id) {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);

        /**判断用户与device是否为空，用户是否操作的是自己的device，用户是否已验证*/
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);
        ResponseEntity<?> checkResponse = DeviceUtils.check(mobileUser, device, Authorization);
        if (checkResponse != null) {
            return checkResponse;
        }

        /**创建rsponseBody对象*/
        Able_Relationship responseBody = new Able_Relationship();
        /**封装rspoonseBody中的links*/
        Map<String, String> contentLinks = new HashMap<>();
        contentLinks.put("self", baseLink + "devices/" + device_id + "/relationships/events");
        contentLinks.put("related", baseLink + "devices/" + device_id + "/events");
        responseBody.setLinks(contentLinks);
        /**封装responseBody中的data*/
        Map<String, String> contentData = new HashMap<>();
        contentData.put("type", "events");
        contentData.put("id", "events_" + device_id);
        List<Map<String, String>> responseData = new ArrayList<>();
        responseData.add(contentData);
        responseBody.setData(responseData);
        return responseBody;
    }

    /**
     * 根据device_id查询device_errors并返回
     *
     * @param Authorization
     * @param device_id
     * @return
     */
    @Override
    public Object selectDeviceErrorsByDeviceId(String Authorization, String device_id) {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);

        /**判断用户与device是否为空，用户是否操作的是自己的device，用户是否已验证*/
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);
        ResponseEntity<?> checkResponse = DeviceUtils.check(mobileUser, device, Authorization);
        if (checkResponse != null) {
            return checkResponse;
        }


        /**创建rsponseBody对象*/
        Able_Relationship responseBody = new Able_Relationship();
        /**封装rspoonseBody中的links*/
        Map<String, String> contentLinks = new HashMap<>();
        contentLinks.put("self", baseLink + "devices/" + device_id + "/relationships/device_errors");
        contentLinks.put("related", baseLink + "devices/" + device_id + "/device_errors");
        responseBody.setLinks(contentLinks);
        /**封装responseBody中的data*/
        Map<String, String> contentData = new HashMap<>();
        contentData.put("type", "device_errors");
        contentData.put("id", "device_errors_" + device_id);
        List<Map<String, String>> responseData = new ArrayList<>();
        responseData.add(contentData);
        responseBody.setData(responseData);
        return responseBody;
    }

    /**
     * 根据device_id查询firmware并返回
     *
     * @param Authorization
     * @param device_id
     * @return
     */
    @Override
    public Object selectFirmwareByDeviceId(String Authorization, String device_id) {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);

        /**判断用户与device是否为空，用户是否操作的是自己的device，用户是否已验证*/
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);
        ResponseEntity<?> checkResponse = DeviceUtils.check(mobileUser, device, Authorization);
        if (checkResponse != null) {
            return checkResponse;
        }

        /**封装responseBody中的links*/
        Map<String, String> contentLinks = new HashMap<>();
        contentLinks.put("self", baseLink + "devices/" + device_id + "/relationships/firmware");
        contentLinks.put("related", baseLink + "devices/" + device_id + "/firmware");

        /**封装responseBody中的data*/
        Map<String, String> contentData = new HashMap<>();
        contentData.put("type", "firmwares");
        contentData.put("id", "firmwares_" + device_id);

        /**封装responseBody*/
        Able_Relationship responseBody = new Able_Relationship();
        responseBody.setData(contentData);
        responseBody.setLinks(contentLinks);
        return responseBody;
    }
}
