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
import com.chervon.iot.mobile.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private JavaMailUtil sendEmail;
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
    @Value("${relation_BaseLink}")
    private String  egoBaseLink;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 忘记密码，发送邮件重置密码
     * */
    @Override
    public ResponseEntity<?> forgetPassword(String type, String email, Device device) throws SQLException, Exception {
        HttpHeaders headers =HttpHeader.HttpHeader();
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        //从reids中拿取数据
        mobile_user=(Mobile_User) operations.get(email);
        if(mobile_user==null){
            mobile_user = mobile_userMapper.getUserByEmail(email);
        }
        //设置1小时token
        jwtTokenUtil.setExpiration(expirationhours);
        if (mobile_user != null) {
            final String token = jwtTokenUtil.generateToken(mobile_user, device);
            String url = emailUrl + "resets/Bearer " + token+"/email";
            sendEmail.sendEmail(url,email,mobile_user.getName());
            responData.setType(type);
            responData.setId("Bearer "+token);
            Map<String, String> attribute = new HashMap<>();
            attribute.put("email", email);
            responData.setAttributes(attribute);
            responData.setRelationships(new HashMap<>());
            Map<String, String> link = new HashMap<>();
            link.put("self", egoBaseLink+"resets/Bearer "+token);
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

    /**
     * 重新为用户重置密码
     * @param mobile_user 部分用户信息
     * @return
     * @throws SQLException
     * @throws Exception
     */
    @Override
    @Transactional
    public String resetPassword(String type,String id,Mobile_User mobile_user) throws SQLException, Exception {
        HttpHeaders headers = HttpHeader.HttpHeader();
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        mobile_userMapper.resetPassword(mobile_user);
        mobile_user = mobile_userMapper.getUserByEmail(mobile_user.getEmail());
        //更新redis数据
        operations.set(mobile_user.getEmail(), mobile_user);
        operations.set(mobile_user.getSfdcId(), mobile_user);
        sfdc_request = new Sfdc_Request(mobile_user.getName(),null,mobile_user.getName(),
                mobile_user.getEmail(),mobile_user.getPassword(),mobile_user.getStatus());
        String json = JsonUtils.objectToJson(sfdc_request);
        //调SFDC接口
        String jsonData = HttpClientUtil.doPostJson(sfdcurl, json, "CreateUser", MyUtils.getMD5("CreateUse" + app_key));
        JsonNode jsonNode = mapper.readTree(jsonData);
        //如果成功
        if (jsonNode.get("success").asText().equals("true")) {
           /* ResponseBody responseBody = mobile_userLoginService.loginReturn(type,id,mobile_user);
            headers.add("Authorization","Bearer "+id);
            return new ResponseEntity<Object>(responseBody, headers, HttpStatus.OK);*/
           return "resetsSuccess";
        }
       /* ResultMsg resultMsg = ErrorResponseUtil.serverError();
        return new ResponseEntity(resultMsg, headers, HttpStatus.valueOf(ResultStatusCode.INTERNAL_SERVER_ERROR.getErrcode()));*/
        return "resetsFailure";
    }
}
