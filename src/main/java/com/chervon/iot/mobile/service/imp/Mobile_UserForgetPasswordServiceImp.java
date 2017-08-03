package com.chervon.iot.mobile.service.imp;

import com.chervon.iot.common.common_util.HttpHeader;
import com.chervon.iot.common.exception.ResultMsg;
import com.chervon.iot.common.exception.ResultStatusCode;
import com.chervon.iot.common.util.HttpClientUtil;
import com.chervon.iot.mobile.mapper.Mobile_UserMapper;
import com.chervon.iot.mobile.model.Mobile_User;
import javax.mail.MessagingException;

import com.chervon.iot.mobile.model.Sfdc_Request;
import com.chervon.iot.mobile.model.entity.Included;
import com.chervon.iot.mobile.model.entity.ResponseBody;
import com.chervon.iot.mobile.model.entity.ResponseData;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.chervon.iot.mobile.service.Mobile_UserForgetPasswordService;
import com.chervon.iot.mobile.service.Mobile_UserLoginService;
import com.chervon.iot.mobile.util.ErrorResponseUtil;
import com.chervon.iot.mobile.util.JsonUtils;
import com.chervon.iot.mobile.util.MyUtils;
import com.chervon.iot.mobile.util.SendEmail;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sun.audio.AudioDevice.device;
/**
 * Created by 喷水君 on 2017/6/27.
 */
@Service
public class Mobile_UserForgetPasswordServiceImp implements Mobile_UserForgetPasswordService {
    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private Mobile_UserMapper mobile_userMapper;
    @Autowired
    private Mobile_UserLoginService mobile_userLoginService;
    @Autowired
    private Mobile_User mobile_user;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ResponseData responData;
    @Autowired
    private ResponseBody responseBody;
    @Autowired
    private SendEmail sendEmail;
    @Autowired
    private Sfdc_Request sfdc_request;
    @Value("${email.url}")
    private String emailUrl;
    @Value("${sfdc.url}")
    private String sfdcurl;
    @Value("${app_key}")
    private String app_key;
    @Value("${jwt.expirationhours}")
    private Long expirationhours;

    @Override
    public ResponseEntity<?> forgetPassword(String type, String email, Device device) throws SQLException, Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/vnd.api+json");
        mobile_user = mobile_userMapper.getUserByEmail(email);
        jwtTokenUtil.setExpiration(expirationhours);
        if (mobile_user != null) {
            final String token = jwtTokenUtil.generateToken(mobile_user, device);
            String url = emailUrl + mobile_user.getSfdcId() + "/" + token;
            sendEmail.sendAttachmentsMail(email, url);
            System.out.println("sendEmail");
            responData.setType(type);
            responData.setId(mobile_user.getSfdcId());
            Map<String, String> attribute = new HashMap<>();
            attribute.put("email", email);
            responData.setAttributes(attribute);
            Map<String, String> link = new HashMap<>();
            link.put("self", "//private-b1af72-egoapi.apiary-mock.com/api/v1/resets/" + mobile_user.getSfdcId());
            responData.setLinks(link);
            List<Included> includedList = new ArrayList<>();
            responseBody.setIncluded(includedList);
            Map<String, String> meta = new HashMap<>();
            meta.put("message", "An email has been sent with your password recovery instructions!");
            responseBody.setData(responData);
            responseBody.setMeta(meta);
            return new ResponseEntity<Object>(responseBody, headers, HttpStatus.OK);
        }
        ResultMsg resultMsg = ErrorResponseUtil.errorFiled();
        return new ResponseEntity(resultMsg, headers, HttpStatus.valueOf(ResultStatusCode.SC_BAD_REQUEST.getErrcode()));
    }

    @Override
    public ResponseEntity<?> resetPassword(Mobile_User mobile_user) throws SQLException, Exception {
        HttpHeaders headers = HttpHeader.HttpHeader();
        mobile_userMapper.resetPassword(mobile_user);
        mobile_user = mobile_userMapper.getUserByEmail(mobile_user.getEmail());
        sfdc_request = new Sfdc_Request(mobile_user.getName(),null,mobile_user.getName(),
                mobile_user.getEmail(),mobile_user.getPassword(),mobile_user.getStatus());
        String json = JsonUtils.objectToJson(sfdc_request);
        String jsonData = HttpClientUtil.doPostJson(sfdcurl, json, "CreateUser", MyUtils.getMD5("CreateUser" + app_key));
        JsonNode jsonNode = mapper.readTree(jsonData);
        if (jsonNode.get("success").asText().equals("true")) {
            ResponseBody responseBody = mobile_userLoginService.loginReturn(mobile_user);
            return new ResponseEntity<Object>(responseBody, headers, HttpStatus.OK);
        }
        ResultMsg resultMsg = ErrorResponseUtil.serverError();
        return new ResponseEntity(resultMsg, headers, HttpStatus.valueOf(ResultStatusCode.INTERNAL_SERVER_ERROR.getErrcode()));
    }
}
