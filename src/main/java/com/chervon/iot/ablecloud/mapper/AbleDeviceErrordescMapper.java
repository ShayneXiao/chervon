package com.chervon.iot.ablecloud.mapper;

import com.chervon.iot.ablecloud.model.AbleDeviceErrordesc;
import com.chervon.iot.ablecloud.model.AbleDeviceErrordescExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AbleDeviceErrordescMapper {
    int countByExample(AbleDeviceErrordescExample example);

    int deleteByExample(AbleDeviceErrordescExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AbleDeviceErrordesc record);

    int insertSelective(AbleDeviceErrordesc record);

    List<AbleDeviceErrordesc> selectByExample(AbleDeviceErrordescExample example);

    AbleDeviceErrordesc selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AbleDeviceErrordesc record, @Param("example") AbleDeviceErrordescExample example);

    int updateByExample(@Param("record") AbleDeviceErrordesc record, @Param("example") AbleDeviceErrordescExample example);

    int updateByPrimaryKeySelective(AbleDeviceErrordesc record);

    int updateByPrimaryKey(AbleDeviceErrordesc record);
}