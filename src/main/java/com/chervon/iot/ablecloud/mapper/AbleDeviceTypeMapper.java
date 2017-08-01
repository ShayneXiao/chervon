package com.chervon.iot.ablecloud.mapper;

import java.util.List;

import com.chervon.iot.ablecloud.model.AbleDeviceType;
import com.chervon.iot.ablecloud.model.AbleDeviceTypeExample;
import org.apache.ibatis.annotations.Param;

public interface AbleDeviceTypeMapper {
    int countByExample(AbleDeviceTypeExample example);

    int deleteByExample(AbleDeviceTypeExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AbleDeviceType record);

    int insertSelective(AbleDeviceType record);

    List<AbleDeviceType> selectByExample(AbleDeviceTypeExample example);

    AbleDeviceType selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AbleDeviceType record, @Param("example") AbleDeviceTypeExample example);

    int updateByExample(@Param("record") AbleDeviceType record, @Param("example") AbleDeviceTypeExample example);

    int updateByPrimaryKeySelective(AbleDeviceType record);

    int updateByPrimaryKey(AbleDeviceType record);
}