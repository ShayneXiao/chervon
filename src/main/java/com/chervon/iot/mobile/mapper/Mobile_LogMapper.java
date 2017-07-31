package com.chervon.iot.mobile.mapper;

import com.chervon.iot.mobile.model.Mobile_Log;
import org.springframework.stereotype.Repository;

@Repository
public interface Mobile_LogMapper {
    
    int insert(Mobile_Log record);

    int insertSelective(Mobile_Log record);
}