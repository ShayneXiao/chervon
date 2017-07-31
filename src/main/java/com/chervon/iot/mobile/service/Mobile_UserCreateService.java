package com.chervon.iot.mobile.service;

import com.chervon.iot.mobile.model.Mobile_User;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;

import java.sql.SQLException;

/**
 * Created by Boy on 2017/6/24.
 */
public interface Mobile_UserCreateService {
    ResponseEntity<?> createUser(Device device,String type, Mobile_User user) throws SQLException,Exception;

    ResponseEntity<?> getCurrentUser(String Authorization,String user_id) throws SQLException,Exception;

    ResponseEntity<?> updateUser(String Authorization ,Device device,Mobile_User user) throws SQLException,Exception;
    boolean  verified(String email)throws SQLException,Exception;
   //int userLoginOut(String Authorzation);
}
