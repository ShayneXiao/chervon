package com.chervon.iot.ablecloud.util;

import com.chervon.iot.ablecloud.model.Able_Device;
import com.chervon.iot.common.common_util.HttpHeader;
import com.chervon.iot.common.exception.ResultMsg;
import com.chervon.iot.mobile.model.Mobile_User;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 2017/7/31.
 */
public class DeviceUtils {
    /**
     * 获得一个outlet的状态
     * @param statusJsonNode
     * @param outletName
     * @return
     */
    public static String getOutletStatus(JsonNode statusJsonNode, String outletName) {
        String outletStatus = null;
        if (statusJsonNode == null || outletName == null || "".equals(outletName)) {
            return outletStatus;
        }

        Boolean outlet;
        if ("ac1".equals(outletName)) {
            outlet = statusJsonNode.get("ac1").asBoolean();
        } else if ("ac2".equals(outletName)) {
            outlet = statusJsonNode.get("ac2").asBoolean();
        } else if ("ac3".equals(outletName)) {
            outlet = statusJsonNode.get("ac3").asBoolean();
        } else if ("dc".equals(outletName)) {
            outlet = statusJsonNode.get("dc").asBoolean();
        } else {
            outlet = false;
        }

        if (outlet == true) {
            outletStatus = "on";
        } else {
            outletStatus = "off";
        }
        return outletStatus;
    }

    /**
     * 获得device的充放电状态
     * @param chargeState
     * @return
     */
    public static String getDeviceStatus(int chargeState){
        String deviceStatus = "";
        if(chargeState == 0){
            deviceStatus = "not charge";
        }else if(chargeState == 1){
            deviceStatus = "charging";
        }else if(chargeState == 2){
            deviceStatus = "end charge";
        }else if(chargeState == 3){
            deviceStatus = "discharging";
        }else if(chargeState == 4){
            deviceStatus = "end discharge";
        }
        return deviceStatus;
    }

    /**
     * 获得剩余电量（dumpEnergy）
     * @param jsonNode
     * @return
     */
    public static Integer getOutputWattHours(JsonNode jsonNode) {
        int bat1DumpEnergy = jsonNode.get("bat1").get("dumpEnergy").asInt();
        int bat2DumpEnergy = jsonNode.get("bat2").get("dumpEnergy").asInt();
        int bat3DumpEnergy = jsonNode.get("bat3").get("dumpEnergy").asInt();
        int bat4DumpEnergy = jsonNode.get("bat4").get("dumpEnergy").asInt();
        Integer dumpEnergy = bat1DumpEnergy + bat2DumpEnergy + bat3DumpEnergy + bat4DumpEnergy;
        return dumpEnergy;
    }

    /**
     * 获得输出瓦特（output_watts）
     * @param jsonNode
     * @return
     */
    public static Integer getOutputWatts(JsonNode jsonNode) {
        int ac1Power = jsonNode.get("ac1Power").asInt();
        int ac2Power = jsonNode.get("ac2Power").asInt();
        int ac3Power = jsonNode.get("ac3Power").asInt();
        Integer output_watts = ac1Power + ac2Power + ac3Power;
        return output_watts;
    }

    /**
     * 获得capacity_percentage
     * @param jsonNode
     * @return
     */
    public static Double getCapacityPercentage(JsonNode jsonNode) {
        double dumpEnergyPercent1 = jsonNode.get("bat1").get("dumpEnergyPercent").asDouble();
        double dumpEnergyPercent2 = jsonNode.get("bat2").get("dumpEnergyPercent").asDouble();
        double dumpEnergyPercent3 = jsonNode.get("bat3").get("dumpEnergyPercent").asDouble();
        double dumpEnergyPercent4 = jsonNode.get("bat4").get("dumpEnergyPercent").asDouble();
        Double dumpEnergyPercent = (dumpEnergyPercent1 + dumpEnergyPercent2 + dumpEnergyPercent3 + dumpEnergyPercent4) / 4.0;
        return dumpEnergyPercent;
    }

    /**
     * 获得错误信息ResultMsg对象（404）
     * @return
     */
    public static ResultMsg getNotFound() {
        ResultMsg.Error error = new ResultMsg.Error();
        error.setStatus(404);
        error.setTitle("Object not found.");
        error.setMessage(null);
        Map<String, String> source = new HashMap<>();
        source.put("pointer", "");
        error.setSource(source);

        List<ResultMsg.Error> errorList = new ArrayList<>();
        errorList.add(error);
        return new ResultMsg(errorList);
    }

    /**
     * 获得错误信息respones对象（404）
     * @return
     */
    public static ResponseEntity<Object> getNotFoundResponse(String Authorization) {
        ResultMsg.Error error = new ResultMsg.Error();
        error.setStatus(404);
        error.setTitle("Object not found.");
        error.setMessage(null);
        Map<String, String> source = new HashMap<>();
        source.put("pointer", "");
        error.setSource(source);

        List<ResultMsg.Error> errorList = new ArrayList<>();
        errorList.add(error);
        ResultMsg resultMsg = new ResultMsg(errorList);

        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization", Authorization);
        return new ResponseEntity<Object>(resultMsg, headers, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 获得错误信息response对象（403）
     * @return
     */
    public static ResponseEntity<?> getCannotPerformResponse(String Authorization) {
        ResultMsg.Error error = new ResultMsg.Error();
        error.setStatus(403);
        error.setTitle("You cannot perform this action.");
        error.setMessage(null);
        Map<String, String> source = new HashMap<>();
        source.put("pointer", "");
        error.setSource(source);

        List<ResultMsg.Error> errorList = new ArrayList<>();
        errorList.add(error);
        ResultMsg resultMsg = new ResultMsg(errorList);

        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization", Authorization);
        return new ResponseEntity<Object>(resultMsg, headers, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 判断用户是否已验证
     * @param mobileUser
     * @param Authorization
     * @return
     */
    public static ResponseEntity<?> checkVerified(Mobile_User mobileUser,String Authorization) {
        ResponseEntity<?> responseEntity = null;
        if (!"verified".equals(mobileUser.getStatus())) {
            responseEntity = DeviceUtils.getCannotPerformResponse(Authorization);
        }
        return responseEntity;
    }

    /**
     * 判断用户与device是否为空，用户是否操作的是自己的device，用户是否已验证
     * @param mobileUser
     * @param device
     * @param Authorization
     * @return
     */
    public static ResponseEntity<?> check(Mobile_User mobileUser, Able_Device device,String Authorization) {
        ResponseEntity<?> responseEntity = null;
        if (mobileUser == null | device == null) {
            responseEntity = DeviceUtils.getNotFoundResponse(Authorization);
        } else if (!"verified".equals(mobileUser.getStatus())) {
            responseEntity = DeviceUtils.getCannotPerformResponse(Authorization);
        } else if (mobileUser.getSfdcId() == null || device.getUsersfid() == null ||
                !mobileUser.getSfdcId().equals(device.getUsersfid())) {
            System.out.println("----------User表中的usersfid：'" + mobileUser.getSfdcId() + "'--------");
            System.out.println("--------Device表中的usersfid：'" + device.getUsersfid() + "'--------");
            responseEntity = DeviceUtils.getCannotPerformResponse(Authorization);
        }
        return responseEntity;
    }

    /**
     * 获得字段错误时返回的对象
     * @return
     */
    public static ResponseEntity getFieldIsWrong() {
        ResultMsg.Error error = new ResultMsg.Error();
        error.setStatus(422);
        error.setTitle("Field data error.");
        error.setMessage(null);
        Map<String, String> source = new HashMap<>();
        source.put("pointer", "");
        error.setSource(source);

        List<ResultMsg.Error> errorList = new ArrayList<>();
        errorList.add(error);
        ResultMsg resultMsg = new ResultMsg(errorList);

        HttpHeaders headers = HttpHeader.HttpHeader();
        return new ResponseEntity<Object>(resultMsg, headers, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
