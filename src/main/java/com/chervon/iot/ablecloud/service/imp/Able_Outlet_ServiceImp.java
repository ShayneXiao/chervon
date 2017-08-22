package com.chervon.iot.ablecloud.service.imp;

import com.chervon.iot.ablecloud.mapper.Able_DeviceMapper;
import com.chervon.iot.ablecloud.model.*;
import com.chervon.iot.ablecloud.service.Able_Outlet_Service;
import com.chervon.iot.ablecloud.util.ControlDevice;
import com.chervon.iot.ablecloud.util.DeviceUtils;
import com.chervon.iot.ablecloud.util.LoadAndGetData;
import com.chervon.iot.ablecloud.util.ResponseDataUtils;
import com.chervon.iot.mobile.mapper.Mobile_UserMapper;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 喷水君 on 2017/7/26.
 * Modified by:Zack
 * Modified date:2017/8/17
 * Modified description:添加具体实现
 */
@Service
public class Able_Outlet_ServiceImp implements Able_Outlet_Service {
    @Autowired
    private LoadAndGetData loadAndGetData;
    @Autowired
    private ObjectMapper jsonMapper;
    @Value("${relation_BaseLink}")
    private String baseLink;
    @Autowired
    private ResponseDataUtils responseDataUtils;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private Mobile_UserMapper userMapper;
    @Autowired
    private Able_DeviceMapper deviceMapper;
    @Autowired
    private ControlDevice controlDevice;

    /**
     * 根据device_id分页查询outlet集合
     * @param Authorization
     * @param device_id
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Object selectOutletList(String Authorization, String device_id, Integer pageNum, Integer pageSize) throws Exception {
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);

        /**判断用户与device是否为空，用户是否操作的是自己的device，用户是否已验证*/
        ResponseEntity<?> checkResult = DeviceUtils.check(mobileUser, device, Authorization);
        if (checkResult != null) {
            return checkResult;
        }

        /****************封装responseBody****************/
        /*所需数据*/
        Map<String, String> getDataParam = new HashMap<>();
        getDataParam.put("sn", device_id);
        getDataParam.put("type", "psStatus");
        String dataResultJson = loadAndGetData.getDataResult(getDataParam);
        JsonNode dataResultJsonNode = jsonMapper.readTree(dataResultJson);

        /********创建responseBody对象********/
        Able_ResponseListBody responseBody = new Able_ResponseListBody();

        /********封装responseData*********/
        /****创建responseData集合****/
        List<Able_Relationship_ResponseData> responseDataList = new ArrayList<>();

        Integer start = (pageNum - 1) * pageSize;
        if (start < 0) {
            start = 0;
        }
        Integer end = (pageNum - 1) * pageSize + pageSize;
        if (end >= 4) {
            end = 4;
        }

        String outletName;
        Able_Relationship_ResponseData responseData;
        for (int i = start + 1; i <= end; i++) {
            outletName = "ac" + (i - 1);
            if (i == 1) {
                outletName = "dc";
            }
            /****获得responseData对象*/
            responseData = responseDataUtils.getOutletResponseData(dataResultJsonNode,outletName, device_id);
            /****将responseData加入responseDataList*/
            responseDataList.add(responseData);
        }

        /********将responseData加入responseBody********/
        responseBody.setData(responseDataList);

        /********封装responseIncluded********/
        /****创建responseIncluded对象****/
        List<Able_ResponseData> responseIncluded = new ArrayList<>();

        /****获得deviceIncluded****/
        Able_ResponseData deviceIncluded =
                responseDataUtils.getOutletIncluded(dataResultJsonNode, device);

        /****将deviceIncluded加入responseIncluded*/
        responseIncluded.add(deviceIncluded);

        /********将responseIncluded加入responseBody********/
        responseBody.setIncluded(responseIncluded);

        /********封装responseBody中的meta*/
        Map<String, String> responseMeta = new HashMap<>();
        //count指的是什么？？？
        responseMeta.put("count", "1");
        responseBody.setMeta(responseMeta);

        /********封装responseBody中的links********/
        /**封装responseLinks*/
        Map<String, String> responseLinks = new HashMap<>();
        String first = "?page[number]=1&page[size]=" + pageSize;
        String last = "?page[number]=" + ((3 / pageSize) + 1) + "&page[size]=" + pageSize;
        String prev;
        String next;
        if (pageNum == 1) {
            if (pageSize < 4) {
                prev = "";
                next = "?page[number]=" + (pageNum + 1) + "&page[size]=" + pageSize;
            } else {
                prev = "";
                next = "";
            }
        } else if (pageNum * pageSize >= 4) {
            if (pageNum > 1) {
                Integer prevPageNum =
                        (pageNum - 1) > ((3 / pageSize) + 1) ? ((3 / pageSize) + 1) : (pageNum - 1);
                prev = "?page[number]=" + prevPageNum + "&page[size]=" + pageSize;
                next = "";
            } else {
                prev = "";
                next = "";
            }
        } else {
            prev = "?page[number]=" + (pageNum - 1) + "&page[size]=" + pageSize;
            next = "?page[number]=" + (pageNum + 1) + "&page[size]=" + pageSize;
        }
        responseLinks.put("self", baseLink + "devices/" + device_id + "/outlets");
        responseLinks.put("first", baseLink + "devices/" + device_id + "/outlets" + first);
        responseLinks.put("prev", baseLink + "devices/" + device_id + "/outlets" + prev);
        responseLinks.put("next", baseLink + "devices/" + device_id + "/outlets" + next);
        responseLinks.put("last", baseLink + "devices/" + device_id + "/outlets" + last);
        /*********将responseLinks加入responseBody*/
        responseBody.setLinks(responseLinks);

        return responseBody;
    }

    /**
     * 根据outlet_id查询具体的outlet
     * @param Authorization
     * @param outlet_id
     * @return
     * @throws Exception
     */
    @Override
    public Object selectOutletByOutletId(String Authorization, String outlet_id) throws Exception {
        /*解析数据*/
        String device_id = outlet_id.split("_")[1];
        String outletName = outlet_id.split("_")[0];
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);

        /**判断用户与device是否为空，用户是否操作的是自己的device，用户是否已验证*/
        ResponseEntity<?> checkResult = DeviceUtils.check(mobileUser, device, Authorization);
        if (checkResult != null) {
            return checkResult;
        }

        /****************封装responseBody****************/
        /*所需数据*/
        Map<String, String> getDataParam = new HashMap<>();
        getDataParam.put("sn", device_id);
        getDataParam.put("type", "psStatus");
        String dataResultJson = loadAndGetData.getDataResult(getDataParam);
        JsonNode dataResultJsonNode = jsonMapper.readTree(dataResultJson);

        /********创建responseBody对象********/
        Able_ResponseBody responseBody = new Able_ResponseBody();

        /********封装responseData*********/
        /****获得responseData对象*/
        Able_Relationship_ResponseData responseData =
                responseDataUtils.getOutletResponseData(dataResultJsonNode,outletName, device_id);
            /****将responseData加入responseDataList*/

        /********将responseData加入responseBody********/
        responseBody.setData(responseData);

        /********封装responseIncluded********/
        /****创建responseIncluded对象****/
        List<Able_ResponseData> responseIncluded = new ArrayList<>();

        /****获得deviceIncluded****/
        Able_ResponseData deviceIncluded =
                responseDataUtils.getOutletIncluded(dataResultJsonNode, device);

        /****将deviceIncluded加入responseIncluded*/
        responseIncluded.add(deviceIncluded);

        /********将responseIncluded加入responseBody********/
        responseBody.setIncluded(responseIncluded);

        /********封装responseBody中的meta*/
        Map<String, String> responseMeta = new HashMap<>();
        responseBody.setMeta(responseMeta);

        return responseBody;
    }

    /**
     * 根据outlet_id更新具体的outlet
     * @param Authorization
     * @param outlet_id
     * @param status
     * @return
     */
    @Override
    public Object updateOutletByOutletId(String Authorization, String outlet_id, String status) throws Exception {
        /*解析数据*/
        String device_id = outlet_id.split("_")[0];
        String outletName = outlet_id.split("_")[1];
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);

        Map<String, String> controlDeviceParam = new HashMap<>();
        controlDeviceParam.put("sn", device_id);
        controlDeviceParam.put("cmd", "");
        controlDevice.getControlDeviceResult(controlDeviceParam);

        return null;
    }

    /**
     * 根据outlet_id查询对应的device
     * @param Authorization
     * @param outlet_id
     * @return
     */
    @Override
    public Object selectDeviceByOutletId(String Authorization, String outlet_id) {
        /*解析数据*/
        String device_id = outlet_id.split("_")[1];
        String authorization = Authorization.replace("Bearer", "").trim();
        String email = jwtTokenUtil.getEmailFromToken(authorization);
        Mobile_User mobileUser = userMapper.getUserByEmail(email);
        Able_Device device = deviceMapper.selectByPrimaryKey(device_id);

        /**判断用户与device是否为空，用户是否操作的是自己的device，用户是否已验证*/
        ResponseEntity<?> checkResult = DeviceUtils.check(mobileUser, device, Authorization);
        if (checkResult != null) {
            return checkResult;
        }

        /**封装responseBody中的links*/
        Map<String, String> contentLinks = new HashMap<>();
        contentLinks.put("self", baseLink + "outlets/" + outlet_id + "/relationships/device");
        contentLinks.put("related", baseLink + "outlets/" + outlet_id + "/device");

        /**封装responseBody中的data*/
        Map<String, String> contentData = new HashMap<>();
        contentData.put("type", "devices");
        contentData.put("id", device_id);

        /**封装responseBody*/
        Able_Relationship responseBody = new Able_Relationship();
        responseBody.setData(contentData);
        responseBody.setLinks(contentLinks);
        return responseBody;
    }
}
