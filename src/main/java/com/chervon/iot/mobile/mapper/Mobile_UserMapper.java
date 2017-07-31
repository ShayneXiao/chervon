package com.chervon.iot.mobile.mapper;

import com.chervon.iot.mobile.model.Mobile_User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public interface Mobile_UserMapper {


    void insert(Mobile_User record);

    Mobile_User getUserByEmail(String email);

    void  updateModifyTime(Mobile_User mobile_user);

    Mobile_User getUserSfid(String sfdc_id);

    void updateByPrimaryKey(Mobile_User record);
    void verified(Mobile_User mobile_user);
    void resetPassword(Mobile_User mobile_user);
}