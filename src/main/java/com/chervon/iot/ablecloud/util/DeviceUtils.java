package com.chervon.iot.ablecloud.util;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by Admin on 2017/7/31.
 */
public class DeviceUtils {
    /**
     * 获得状态
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
     * 获得剩余电量
     * @param jsonNode
     * @return
     */
    public static Integer getDumpEnergy(JsonNode jsonNode) {
        int bat1DumpEnergy = jsonNode.get("bat1").get("dumpEnergy").asInt();
        int bat2DumpEnergy = jsonNode.get("bat2").get("dumpEnergy").asInt();
        int bat3DumpEnergy = jsonNode.get("bat3").get("dumpEnergy").asInt();
        int bat4DumpEnergy = jsonNode.get("bat4").get("dumpEnergy").asInt();
        Integer dumpEnergy = bat1DumpEnergy + bat2DumpEnergy + bat3DumpEnergy + bat4DumpEnergy;
        return dumpEnergy;
    }

    /**
     * 获得输出瓦特
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
    public static Double getDumpEnergyPercent(JsonNode jsonNode) {
        double dumpEnergyPercent1 = jsonNode.get("bat1").get("dumpEnergyPercent").asDouble();
        double dumpEnergyPercent2 = jsonNode.get("bat2").get("dumpEnergyPercent").asDouble();
        double dumpEnergyPercent3 = jsonNode.get("bat3").get("dumpEnergyPercent").asDouble();
        double dumpEnergyPercent4 = jsonNode.get("bat4").get("dumpEnergyPercent").asDouble();
        Double dumpEnergyPercent = (dumpEnergyPercent1 + dumpEnergyPercent2 + dumpEnergyPercent3 + dumpEnergyPercent4) / 4.0;
        return dumpEnergyPercent;
    }
}
