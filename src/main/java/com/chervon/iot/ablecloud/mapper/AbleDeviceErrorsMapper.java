package com.chervon.iot.ablecloud.mapper;
import java.util.List;

import com.chervon.iot.ablecloud.model.AbleDeviceErrors;
import com.chervon.iot.ablecloud.model.AbleDeviceErrorsExample;
import com.chervon.iot.ablecloud.model.Able_ResponseDeviceError;
import org.apache.ibatis.annotations.Param;

public interface AbleDeviceErrorsMapper {
    int countByExample(AbleDeviceErrorsExample example);

    int deleteByExample(AbleDeviceErrorsExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AbleDeviceErrors record);

    int insertSelective(AbleDeviceErrors record);

    List<AbleDeviceErrors> selectByExample(AbleDeviceErrorsExample example);

    AbleDeviceErrors selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AbleDeviceErrors record, @Param("example") AbleDeviceErrorsExample example);

    int updateByExample(@Param("record") AbleDeviceErrors record, @Param("example") AbleDeviceErrorsExample example);

    int updateByPrimaryKeySelective(AbleDeviceErrors record);

    int updateByPrimaryKey(AbleDeviceErrors record);

    List<Able_ResponseDeviceError> getDeviceErrorByID(String device_id);

    Able_ResponseDeviceError getDeviceErrorByDeviceErrorID(Integer device_error_id);
}