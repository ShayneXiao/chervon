package com.chervon.iot.mobile.controller;

import com.chervon.iot.common.common_util.HttpHeader;
import com.chervon.iot.common.exception.ResultMsg;
import com.chervon.iot.common.exception.ResultStatusCode;
import com.chervon.iot.mobile.mapper.Mobile_UserMapper;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.model.entity.*;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.chervon.iot.mobile.sercuity.filter.HTTPBearerAuthorizeAttribute;
import com.chervon.iot.mobile.service.Mobile_UserLoginService;
import com.chervon.iot.mobile.util.BasicAuthorizeTokenUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.*;


import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Boy on 2017/6/26.
 */
@RestController
@RequestMapping("/api/v1")
public class Mobile_LoginUserController {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Log logger = LogFactory.getLog(this.getClass());
    @Autowired
    private BasicAuthorizeTokenUtil basicTokenUtil;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private  Mobile_User mobile_user;
    @Autowired
    private Mobile_UserLoginService mobile_userLoginService;
    @Autowired
    private  RelationCreator relationCreator;
    @Value("${jwt.expiration}")
    private  Long expiration;
    @Autowired
    private com.chervon.iot.mobile.model.entity.ResponseBody responseBody;

    /**
     * 登陆
     * @param Authorization
     * @param device
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sessions",method= RequestMethod.POST)
    public ResponseEntity<?> login(@RequestHeader String Authorization,Device device )throws Exception{
        HttpHeaders headers= HttpHeader.HttpHeader();
        headers.add("Content-Type","application/vnd.api+json");
        ResultStatusCode resultStatusCode = basicTokenUtil.checkAuthorizeToken(Authorization);
        if(resultStatusCode==ResultStatusCode.SC_OK){
            mobile_user=basicTokenUtil.getUser();
            responseBody= mobile_userLoginService.loginReturn(mobile_user);
            jwtTokenUtil.setExpiration(expiration);
            final String token = jwtTokenUtil.generateToken(mobile_user, device);
            logger.info("checking authentication for ExpirationDateFromToken= " + jwtTokenUtil.getExpirationDateFromToken(token));
            headers.add("Authorization","Bearer "+token);
            return new ResponseEntity(responseBody,headers, HttpStatus.OK);
        }
        else{
            List<ResultMsg.Error> errors = new ArrayList<ResultMsg.Error>();
            ResultMsg.Error error = new ResultMsg.Error(resultStatusCode.getErrcode(),resultStatusCode.getTitle(),null);
            errors.add(error);
            ResultMsg resultMsg = new ResultMsg(errors);
            HttpStatus.valueOf(resultStatusCode.getErrcode());
            return new ResponseEntity(resultMsg, headers,HttpStatus.valueOf(resultStatusCode.getErrcode()));
        }
    }

    /**
     * 读取个人信息
     * @param Authorization
     * @return
     * @throws SQLException
     * @throws Exception
     */
    @GetMapping("/sessions")
    public  ResponseEntity<?> read(@RequestHeader String Authorization)throws SQLException,Exception{
        HttpHeaders headers=HttpHeader.HttpHeader();
        headers.add("Content-Type","application/vnd.api+json");
        String email = jwtTokenUtil.getEmailFromToken(Authorization.substring(7));
        mobile_user = mobile_userLoginService.getUserByEmail(email);
        responseBody= mobile_userLoginService.loginReturn(mobile_user);
        headers.add("Authorization",Authorization);
        return new ResponseEntity(responseBody,headers, HttpStatus.OK);
    }

    /**
     * 退出
     * @param Authorization
     * @return
     * @throws SQLException
     * @throws Exception
     */
    @DeleteMapping("/sessions")
    public  ResponseEntity<?> delete(@RequestHeader String Authorization)throws SQLException,Exception{
        HttpHeaders headers=HttpHeader.HttpHeader();
        headers.add("Content-Type","application/vnd.api+json");
       //String email = jwtTokenUtil.getEmailFromToken(Authorization.substring(7));
       String authToken = Authorization.substring(7);
        System.out.println(authToken);
        //通过jwt解析拿到email
        String email = jwtTokenUtil.getEmailFromToken(authToken);
        mobile_user = mobile_userLoginService.getUserByEmail(email);
        mobile_user.setLastpasswordresetdate(new Date());
        mobile_userLoginService.modifyTime(mobile_user);
        return new ResponseEntity(headers, HttpStatus.NO_CONTENT);
        }

    /**
     * 关系
      * @param Authorization
     * @param session_id
     * @return
     * @throws SQLException
     * @throws Exception
     */
    @RequestMapping(value="/sessions/{session_id}/relationships/creator", method = RequestMethod.GET)
    public   ResponseEntity<?> relationship(@RequestHeader String Authorization,@PathVariable String session_id)throws SQLException,Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/vnd.api+json");
        Map<String,String> links = new HashMap<>();
        Map<String,String> data = new HashMap<>();
        links.put("self","https://private-b1af72-egoapi.apiary-mock.com/api/v1/sessions/"+session_id+"/relationships/creator");
        links.put("related","https://private-b1af72-egoapi.apiary-mock.com/api/v1/sessions/"+session_id+"/creator");
        data.put("type","users");
        data.put("id",session_id);
        relationCreator.setLinks(links);
        relationCreator.setData(data);
        headers.add("Authorization",Authorization);
        return new ResponseEntity(relationCreator,headers, HttpStatus.OK);

    }
}
