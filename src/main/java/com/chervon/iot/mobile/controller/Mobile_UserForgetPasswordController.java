package com.chervon.iot.mobile.controller;

import com.chervon.iot.common.common_util.HttpHeader;
import com.chervon.iot.common.exception.ErrorInfo;
import com.chervon.iot.common.exception.ResultMsg;
import com.chervon.iot.common.exception.ResultStatusCode;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.chervon.iot.mobile.service.Mobile_UserForgetPasswordService;
import com.chervon.iot.mobile.service.imp.Mobile_UserLoginServiceImp;
import com.chervon.iot.mobile.util.ErrorResponseUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import com.chervon.iot.mobile.model.entity.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Boy on 2017/6/26.
 */
@RestController
@RequestMapping("/api/v1")
public class Mobile_UserForgetPasswordController {
    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private  Mobile_User  mobile_user;
    @Autowired
    private  ResponseBody responseBody;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private Mobile_UserForgetPasswordService mobile_userForgetPasswordService;
    @Autowired
    private Mobile_UserLoginServiceImp mobile_userLoginServiceImp;

    @RequestMapping(value = "/resets", method= RequestMethod.POST)
    public ResponseEntity<?> createReset(@RequestBody String jsonData, Device device)throws SQLException,IOException,Exception{
        JsonNode jsonNode = mapper.readTree(jsonData);
        String type = jsonNode.get("data").get("type").asText();
        String  email =jsonNode.get("data").get("attributes").get("email").asText();
        return mobile_userForgetPasswordService.forgetPassword(type, email,device);
    }

    @RequestMapping(value = "/resets/{Authorization}")
    public ModelAndView resetPassword(@PathVariable String Authorization)throws SQLException,Exception{
        String email=jwtTokenUtil.getEmailFromToken(Authorization.substring(7));
        ModelAndView mav = new ModelAndView();
        HttpHeaders httpHeaders = HttpHeader.HttpHeader();
        httpHeaders.add("Authorization",Authorization);
        if(email!=null){
            Mobile_User mobileuser=mobile_userLoginServiceImp.getUserByEmail(email);
            if(mobileuser!=null){
                if(jwtTokenUtil.validateToken(Authorization.substring(7),mobile_user)==true){

                    Map<String,String> resets = new HashMap<>();
                    resets.put("type","resets");
                    resets.put("id",mobileuser.getSfdcId());
                    mav.addObject(resets);
                    mav.setViewName("resetPassword");
                    return mav;
                }
            }
        }
        return null;
    }

    @RequestMapping(value = "/resets" ,method=RequestMethod.PATCH)
    public ResponseEntity<?> resetPassword(@RequestHeader String Authorization, @RequestBody String jsonData)throws SQLException,Exception {
        JsonNode jsonNode = mapper.readTree(jsonData);
        mobile_user.setEmail(jwtTokenUtil.getEmailFromToken(Authorization.substring(7)));
        mobile_user.setPassword(jsonNode.get("data").get("attributes").get("password").asText());
        return mobile_userForgetPasswordService.resetPassword(jsonNode.get("data").get("type").asText(),Authorization.substring(7),mobile_user);
    }
}
