package com.chervon.iot.mobile.service.imp;

import com.chervon.iot.common.common_util.HttpHeader;
import com.chervon.iot.common.exception.ResultMsg;
import com.chervon.iot.common.exception.ResultStatusCode;
import com.chervon.iot.common.util.HttpClientUtil;
import com.chervon.iot.mobile.mapper.Mobile_UserMapper;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.model.Sfdc_Request;
import com.chervon.iot.mobile.model.entity.Included;
import com.chervon.iot.mobile.model.entity.ResponseBody;
import com.chervon.iot.mobile.model.entity.ResponseData;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.chervon.iot.mobile.service.Mobile_UserCreateService;
import com.chervon.iot.mobile.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Boy on 2017/6/24.
 */
@Service
public class Mobile_UserCreateServiceImp implements Mobile_UserCreateService {
    private static final ObjectMapper mapper = new ObjectMapper();
    public Object[] included = new Object[0];
    @Autowired
    private Mobile_UserMapper mobile_userMapper;
    @Autowired
    private ResponseBody responseBody;
    @Autowired
    private ResponseData responseData;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private Sfdc_Request sfdc_request;
    @Autowired
    private Mobile_User mobileUser;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.expirationhours}")
    private Long expiration1;

    @Value("${sfdc.url}")
    private String sfdcurl;
    @Value("${app_key}")
    private String app_key;
    @Value("${email.url}")
    private String url;
    @Autowired
    private SendEmail sendEmail;

    @Override
    @Transactional
    public ResponseEntity<?> createUser(Device device, String type, Mobile_User user) throws SQLException, Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/vnd.api+json");
        sfdc_request.setEmail(user.getEmail());
        sfdc_request.setName(user.getName());
        sfdc_request.setFirstname(null);
        sfdc_request.setLastname(user.getName());
        sfdc_request.setPassword(user.getPassword());
        sfdc_request.setStatus(user.getStatus());
        System.out.println("+++" + sfdc_request);
        String json = JsonUtils.objectToJson(sfdc_request);
        String jsonData = HttpClientUtil.doPostJson(sfdcurl, json, "CreateUser", MyUtils.getMD5("CreateUser" + app_key));
        JsonNode jsonNode = mapper.readTree(jsonData);
        if (!jsonNode.get("success").asText().equals("true")) {
            throw new Exception();
        } else
            user.setSfdcId(jsonNode.get("user").get("sfid").asText());
        // user.setLatitude(BigDecimal.valueOf(jsonNode.get("user").get("address_longitude").doubleValue()));
        //user.setLatitude(BigDecimal.valueOf(jsonNode.get("user").get("address_latitude").doubleValue()));
        mobile_userMapper.insert(user);
        responseData.setType(type);
        responseData.setId(user.getSfdcId());
        Map<String, String> attribute = new HashMap();
        attribute.put("name", user.getName());
        attribute.put("email", user.getEmail());
        attribute.put("status", user.getStatus());
        responseData.setAttributes(attribute);
        Map<String, String> link = new HashMap<>();
        link.put("self", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/users/" + user.getSfdcId());
        responseData.setLinks(link);
        List<Included> includedList = new ArrayList();
        Map<String, String> meta = new HashMap();
        responseBody.setData(responseData);
        responseBody.setIncluded(includedList);
        responseBody.setMeta(meta);
        jwtTokenUtil.setExpiration(expiration1);
        final String token = jwtTokenUtil.generateToken(user, device);
        sendEmail.sendAttachmentsMail(user.getEmail(), url + "Bearer " + token);
        headers.add("Authorization", "Bearer " + token);
        return new ResponseEntity<Object>(responseBody, headers, HttpStatus.OK);
    }

    public ResponseEntity<?> getCurrentUser(String token, String user_id) throws SQLException, Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/vnd.api+json");
        Mobile_User user = mobile_userMapper.getUserSfid(user_id);
        if (user != null) {
            responseData.setType("users");
            responseData.setId(user.getSfdcId());
            Map<String, String> attribute = new HashMap();
            attribute.put("name", user.getName());
            attribute.put("email", user.getEmail());
            attribute.put("status", user.getStatus());
            responseData.setAttributes(attribute);
            Map<String, String> link = new HashMap<>();
            link.put("self", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/users/" + user.getSfdcId());
            responseData.setLinks(link);
            List<Included> includedList = new ArrayList();
            Map<String, String> meta = new HashMap();
            responseBody.setData(responseData);
            responseBody.setIncluded(includedList);
            responseBody.setMeta(meta);
            headers.add("Authorization", token);
            return new ResponseEntity<Object>(responseBody, headers, HttpStatus.OK);
        }
       /* else if(!user.getEmail().equals(email)){
            ResultMsg  resultMsg =  ErrorResponseUtil.forbidend();
            return new ResponseEntity(resultMsg,headers, HttpStatus.valueOf(ResultStatusCode.SC_FORBIDDEN.getErrcode()));
        }*/
        ResultMsg resultMsg = ErrorResponseUtil.notFound();
        return new ResponseEntity(resultMsg, headers, HttpStatus.valueOf(ResultStatusCode.SC_NOT_FOUND.getErrcode()));
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateUser(String Authorization, Device device, Mobile_User user) throws SQLException, Exception {
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization", Authorization);
        String email = jwtTokenUtil.getEmailFromToken(Authorization.substring(7));
        Mobile_User mobile_user = mobile_userMapper.getUserByEmail(email);
        if (!mobile_user.getSfdcId().equals(user.getSfdcId())) {
            ResultMsg resultMsg = ErrorResponseUtil.forbidend();
            return new ResponseEntity(resultMsg, headers, HttpStatus.valueOf(ResultStatusCode.SC_FORBIDDEN.getErrcode()));
        } else if (!email.equals(user.getEmail())) {
            user.setStatus("unverified");
            mobile_userMapper.updateByPrimaryKey(user);
            jwtTokenUtil.setExpiration(expiration1);
            final String token = jwtTokenUtil.generateToken(user, device);
            sendEmail.sendAttachmentsMail(user.getEmail(), url + "Bearer " + token);
            headers.add("Authorization", token);
        }
        responseData.setType("users");
        responseData.setId(user.getSfdcId());
        Map<String, String> attribute = new HashMap();
        attribute.put("name", user.getName());
        attribute.put("email", user.getEmail());
        attribute.put("status", mobile_user.getStatus());
        responseData.setAttributes(attribute);
        Map<String, String> link = new HashMap<>();
        link.put("self", "https://private-b1af72-egoapi.apiary-mock.com/api/v1/users/" + user.getSfdcId());
        responseData.setLinks(link);
        responseData.setRelationships(new HashMap<>());
        List<Included> includedList = new ArrayList();
        Map<String, String> meta = new HashMap();
        responseBody.setData(responseData);
        responseBody.setIncluded(includedList);
        responseBody.setMeta(meta);

        return new ResponseEntity<Object>(responseBody, headers, HttpStatus.OK);
    }

    @Override
    @Transactional
    public boolean verified(String email) throws SQLException, Exception {
        mobileUser.setStatus("verified");
        mobileUser.setEmail(email);
        mobile_userMapper.verified(mobileUser);
        mobileUser = mobile_userMapper.getUserByEmail(email);
        sfdc_request = new Sfdc_Request(mobileUser.getName(),null,mobileUser.getName(),
                mobileUser.getEmail(),mobileUser.getPassword(),mobileUser.getStatus());
        String json = JsonUtils.objectToJson(sfdc_request);
        String jsonData = null;
        jsonData = HttpClientUtil.doPostJson(sfdcurl, json, "CreateUser", MyUtils.getMD5("CreateUser" + app_key));
        JsonNode jsonNode = mapper.readTree(jsonData);
        if (jsonNode.get("success").asText().equals("true")) {
            return true;
        } else
            return false;
    }
}
