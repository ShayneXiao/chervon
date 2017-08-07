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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
    @Autowired
    private JavaMailUtil sendEmail;
    @Autowired
    private RedisTemplate redisTemplate;

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
    @Value("${relation_BaseLink}")
    private String  egoBaseLink;

    /**
     * 创建用户
     * @param device  移动端/pc端
     * @param type
     * @param user
     * @return
     * @throws SQLException
     * @throws Exception
     */
    @Override
    @Transactional
    public ResponseEntity<?> createUser(Device device, String type, Mobile_User user) throws SQLException, Exception {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        HttpHeaders headers = HttpHeader.HttpHeader();
        sfdc_request = new Sfdc_Request(user.getName(),null,user.getName(),
                user.getEmail(),user.getPassword(),user.getStatus());
        String json = JsonUtils.objectToJson(sfdc_request);
        //调SFDC接口
        String jsonData = HttpClientUtil.doPostJson(sfdcurl, json, "CreateUser", MyUtils.getMD5("CreateUser" + app_key));
        JsonNode jsonNode = mapper.readTree(jsonData);
        //若返回结果为true
        if (!jsonNode.get("success").asText().equals("true")) {
            throw new Exception();
        } else
            user.setSfdcId(jsonNode.get("user").get("sfid").asText());
        // user.setLatitude(BigDecimal.valueOf(jsonNode.get("user").get("address_longitude").doubleValue()));
        //user.setLatitude(BigDecimal.valueOf(jsonNode.get("user").get("address_latitude").doubleValue()));
        jwtTokenUtil.setExpiration(expiration1);
        String token = jwtTokenUtil.generateToken(user, device);
        //发送email
        sendEmail.sendEmail(url+"users/Bearer "+ token+"/email", user.getEmail(), user.getName());

        mobile_userMapper.insert(user);
        //用户信息放入redis
        operations.set(user.getEmail(),user);
        operations.set(user.getSfdcId(),user);
        //构建返回体
        responseData.setType(type);
        responseData.setId(user.getSfdcId());
        responseData.setRelationships(new HashMap<>());
        Map<String, String> attribute = new HashMap();
        attribute.put("name", user.getName());
        attribute.put("email", user.getEmail());
        attribute.put("status", user.getStatus());
        responseData.setAttributes(attribute);
        Map<String, String> link = new HashMap<>();
        link.put("self", egoBaseLink+"users/" + user.getSfdcId());
        responseData.setLinks(link);
        List<Included> includedList = new ArrayList();
        Map<String, String> meta = new HashMap();
        responseBody.setData(responseData);
        responseBody.setIncluded(includedList);
        responseBody.setMeta(meta);
        jwtTokenUtil.setExpiration(expiration);
        token = jwtTokenUtil.generateToken(user, device);
        headers.add("Authorization", "Bearer " + token);
        return new ResponseEntity<Object>(responseBody, headers, HttpStatus.OK);
    }

    /**
     * 获取当前用户信息
     * @param token 会话信息
     * @param user_id //SFDCid
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public ResponseEntity<?> getCurrentUser(String token, String user_id) throws SQLException, Exception {
        HttpHeaders headers = HttpHeader.HttpHeader();
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Mobile_User user = (Mobile_User) operations.get(user_id);
        if (user == null) {
            user = mobile_userMapper.getUserSfid(user_id);
            operations.set("user_id", user);
        }
        if (user != null) {
            String email = jwtTokenUtil.getEmailFromToken(token.substring(7));
            if(!user.getEmail().equals(email)){
                ResultMsg  resultMsg =  ErrorResponseUtil.forbidend();
                return new ResponseEntity(resultMsg,headers, HttpStatus.valueOf(ResultStatusCode.SC_FORBIDDEN.getErrcode()));
            }
            responseData.setType("users");
            responseData.setId(user.getSfdcId());
            responseData.setRelationships(new HashMap<>());
            Map<String, String> attribute = new HashMap();
            attribute.put("name", user.getName());
            attribute.put("email", user.getEmail());
            attribute.put("status", user.getStatus());
            responseData.setAttributes(attribute);
            Map<String, String> link = new HashMap<>();
            link.put("self", egoBaseLink+"users/" + user.getSfdcId());
            responseData.setLinks(link);
            List<Included> includedList = new ArrayList();
            Map<String, String> meta = new HashMap();
            responseBody.setData(responseData);
            responseBody.setIncluded(includedList);
            responseBody.setMeta(meta);
            headers.add("Authorization", token);
            return new ResponseEntity<Object>(responseBody, headers, HttpStatus.OK);
        }
        ResultMsg resultMsg = ErrorResponseUtil.notFound();
        return new ResponseEntity(resultMsg, headers, HttpStatus.valueOf(ResultStatusCode.SC_NOT_FOUND.getErrcode()));
    }

    /**
     * 更新用户的信息，如果更新email 则需要发送email确认
     * @param Authorization
     * @param device
     * @param user
     * @return
     * @throws SQLException
     * @throws Exception
     */
    @Override
    @Transactional
    public ResponseEntity<?> updateUser(String Authorization, Device device, Mobile_User user) throws SQLException, Exception {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        HttpHeaders headers = HttpHeader.HttpHeader();
        headers.add("Authorization", Authorization);
        String email = jwtTokenUtil.getEmailFromToken(Authorization.substring(7));
        Mobile_User mob_User =(Mobile_User)operations.get(user.getEmail());
        if(mob_User==null){
            mob_User = mobile_userMapper.getUserByEmail(user.getEmail());
        }
        if (!email.equals(user.getEmail()) && mob_User!=null) {
            ResultMsg resultMsg = ErrorResponseUtil.errorFiled();
            return new ResponseEntity(resultMsg, headers, HttpStatus.valueOf(ResultStatusCode.SC_BAD_REQUEST.getErrcode()));
        }
        Mobile_User mobile_user =(Mobile_User)operations.get(email);
        if(mobile_user==null){
            mobile_user = mobile_userMapper.getUserByEmail(email);
        }
        Mobile_User mobUser = (Mobile_User) operations.get(user.getSfdcId());
        if(mobUser==null){
            mobUser=mobile_userMapper.getUserSfid(user.getSfdcId());
            if(mobUser==null){
                ResultMsg resultMsg = ErrorResponseUtil.notFound();
                return new ResponseEntity(resultMsg, headers, HttpStatus.valueOf(ResultStatusCode.SC_NOT_FOUND.getErrcode()));
            }
        }
        if (!mobile_user.getSfdcId().equals(user.getSfdcId())) {
            ResultMsg resultMsg = ErrorResponseUtil.forbidend();
            return new ResponseEntity(resultMsg, headers, HttpStatus.valueOf(ResultStatusCode.SC_FORBIDDEN.getErrcode()));
        }
        user.setCreatedate(mobile_user.getCreatedate());
        user.setStatus(mobile_user.getStatus());
        if (!email.equals(user.getEmail())) {

            user.setStatus("unverified");
            sfdc_request = new Sfdc_Request(user.getName(),null,user.getName(),
                    user.getEmail(),user.getPassword(),user.getStatus());
            String json = JsonUtils.objectToJson(sfdc_request);
            String jsonData = HttpClientUtil.doPostJson(sfdcurl, json, "CreateUser", MyUtils.getMD5("CreateUser" + app_key));
            JsonNode jsonNode = mapper.readTree(jsonData);
            if (jsonNode.get("success").asText().equals("true")){
                jwtTokenUtil.setExpiration(expiration1);
                String token = jwtTokenUtil.generateToken(user, device);
                sendEmail.sendEmail(url + "users/Bearer " + token+"/email",user.getEmail(),user.getName() );
                mobile_userMapper.updateByPrimaryKey(user);
                redisTemplate.delete(email);
                redisTemplate.delete(user.getSfdcId());
                jwtTokenUtil.setExpiration(expiration);
                token = jwtTokenUtil.generateToken(user, device);
                headers.add("Authorization", "Bearer " + token);
            }
            else{
                throw  new Exception();
            }
        }
        responseData.setType("users");
        responseData.setId(user.getSfdcId());
        Map<String, String> attribute = new HashMap();
        attribute.put("name", user.getName());
        attribute.put("email", user.getEmail());
        attribute.put("status", user.getStatus());
        responseData.setAttributes(attribute);
        Map<String, String> link = new HashMap<>();
        link.put("self", egoBaseLink+"users/" + user.getSfdcId());
        responseData.setLinks(link);
        responseData.setRelationships(new HashMap<>());
        List<Included> includedList = new ArrayList();
        Map<String, String> meta = new HashMap();
        responseBody.setData(responseData);
        responseBody.setIncluded(includedList);
        responseBody.setMeta(meta);
        return new ResponseEntity<Object>(responseBody, headers, HttpStatus.OK);
    }

    /**
     * 发送email将
     * @param email
     * @return
     * @throws SQLException
     * @throws Exception
     */
    @Override
    @Transactional
    public boolean verified(String email) throws SQLException, Exception {
        mobileUser=mobile_userMapper.getUserByEmail(email);
        if(mobileUser.getStatus().equals("verified")){
            return true;
        }
        mobileUser.setStatus("verified");
        mobileUser.setEmail(email);
        sfdc_request = new Sfdc_Request(null,null,mobileUser.getName(),
                mobileUser.getEmail(),null,mobileUser.getStatus());
        String json = JsonUtils.objectToJson(sfdc_request);
        String jsonData = HttpClientUtil.doPostJson(sfdcurl, json, "CreateUser", MyUtils.getMD5("CreateUser" + app_key));
        JsonNode jsonNode = mapper.readTree(jsonData);
        if (jsonNode.get("success").asText().equals("true")){
            mobile_userMapper.verified(mobileUser);
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(email,mobileUser);
            operations.set(mobileUser.getSfdcId(),mobileUser);
            return true;
        } else
            return false;
    }
}
