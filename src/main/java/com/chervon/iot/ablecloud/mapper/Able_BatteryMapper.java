package com.chervon.iot.ablecloud.mapper;

import com.chervon.iot.ablecloud.model.Able_Battery;

import java.util.List;

/**
 * Created by 喷水君 on 2017/7/28.
 */
public interface Able_BatteryMapper {
   List<Able_Battery> selectListBattery(String device_id);
}
