package com.chervon.iot.mobile.service;
import javax.mail.MessagingException;

import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.model.entity.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;

import java.security.GeneralSecurityException;
import java.sql.SQLException;

/**
 * Created by 喷水君 on 2017/6/27.
 */
public interface  Mobile_UserForgetPasswordService {
    ResponseEntity<?> forgetPassword(String type, String email, Device device)throws SQLException,Exception;
    ResponseEntity<?> resetPassword(String type,String id,Mobile_User mobile_user)throws SQLException,Exception;
}
