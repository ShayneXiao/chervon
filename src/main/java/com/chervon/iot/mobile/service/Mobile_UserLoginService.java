package com.chervon.iot.mobile.service;

import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.model.entity.ResponseBody;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by 喷水君 on 2017/6/27.
 */
public interface Mobile_UserLoginService {
    Mobile_User getUserByEmail(String email)throws SQLException;
    ResponseBody loginReturn(String type,String Authorization,Mobile_User mobile_user);
    void modifyTime(Mobile_User mobile_user)throws SQLException;

}
