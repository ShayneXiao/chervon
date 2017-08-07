package com.chervon.iot.mobile.controller;
import com.chervon.iot.common.common_util.HttpHeader;
import com.chervon.iot.common.exception.ResultMsg;
import com.chervon.iot.common.exception.ResultStatusCode;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.chervon.iot.mobile.service.Mobile_UserCreateService;
import com.chervon.iot.mobile.service.Mobile_UserLoginService;
import com.chervon.iot.mobile.util.ErrorResponseUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.Date;


/**
 * 创建用户，查看用户，更新用户
 * Created by Boy on 2017/6/24.
 */
@Controller
@RequestMapping("/api/v1")
public class Mobile_CreateUserController {

    @Autowired
    private Mobile_UserLoginService mobile_userLoginService;

    @Autowired
    private Mobile_UserCreateService mobile_userCreateService;

    @Autowired
    private  Mobile_User mobileUser;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    //json解析对象
    private static final ObjectMapper mapper = new ObjectMapper();

    //创建用户
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<?> ceateUser(@RequestBody String jsonData, Device device)throws SQLException,Exception{
        HttpHeaders headers = HttpHeader.HttpHeader();
        //json解析
        JsonNode jsonNode = mapper.readTree(jsonData);
        String password=jsonNode.get("data").get("attributes").get("password").asText();
        String type=jsonNode.get("data").get("type").asText();
        String email=jsonNode.get("data").get("attributes").get("email").asText();
        String name=jsonNode.get("data").get("attributes").get("name").asText();
        //通过email查询是否该email已经被注册
        Mobile_User mobile_user=mobile_userLoginService.getUserByEmail(email);
        //已注册
        if (mobile_user!=null){
            ResultMsg resultMsg =  ErrorResponseUtil.conflict();
            return new ResponseEntity(resultMsg,headers, HttpStatus.valueOf(ResultStatusCode.SC_CONFLICT.getErrcode()));
        }
        mobileUser.setCreatedate(new Date());
        mobileUser.setPassword(password);
        mobileUser.setEmail(email);
        mobileUser.setEnabled(true);
        mobileUser.setName(name);
        mobileUser.setStatus("unverified");
        return mobile_userCreateService.createUser(device,type,mobileUser);

    }

    //查询指定用户的的信息
    @RequestMapping(value = "/users/{user_id}", method= RequestMethod.GET )
    public ResponseEntity<?> currentUser(@RequestHeader String Authorization,@PathVariable String user_id)throws SQLException,Exception{
        return   mobile_userCreateService.getCurrentUser(Authorization,user_id);

    }

    //更新信息
    @RequestMapping(value = "/users/{user_id}" ,method= RequestMethod.PATCH)
    public ResponseEntity<?> updateUser(@RequestHeader String Authorization,Device device,@RequestBody String jsonData,@PathVariable String user_id)throws SQLException,Exception {
        HttpHeaders headers = HttpHeader.HttpHeader();
        JsonNode jsonNode = mapper.readTree(jsonData);
        String sf_id=jsonNode.get("data").get("id").asText();
        String password=jsonNode.get("data").get("attributes").get("password").asText();
        String password_confrim=jsonNode.get("data").get("attributes").get("password_confirmation").asText();
        if(!password.equals(password_confrim)){
            ResultMsg  resultMsg =  ErrorResponseUtil.errorFiled();
            headers.add("Authorization",Authorization);
            return new ResponseEntity(resultMsg,headers, HttpStatus.valueOf(ResultStatusCode.SC_BAD_REQUEST.getErrcode()));

        }
        String email=jsonNode.get("data").get("attributes").get("email").asText();
        String name=jsonNode.get("data").get("attributes").get("name").asText();

        mobileUser.setPassword(password);
        mobileUser.setEmail(email);
        mobileUser.setEnabled(true);
        mobileUser.setName(name);
        mobileUser.setSfdcId(sf_id);
        if(!sf_id.equals(user_id)){
            ResultMsg  resultMsg =  ErrorResponseUtil.errorFiled();
            headers.add("Authorization",Authorization);
            return new ResponseEntity(resultMsg,headers, HttpStatus.valueOf(ResultStatusCode.SC_BAD_REQUEST.getErrcode()));
        }
        return mobile_userCreateService.updateUser(Authorization,device,mobileUser);
    }
    //邮箱email链接验证
    @RequestMapping(value = "/users/{Authorization}/email" ,method= RequestMethod.GET)
    public String updateUser(@PathVariable String Authorization)throws SQLException,Exception {

        HttpHeaders headers = HttpHeader.HttpHeader();
        String email = jwtTokenUtil.getEmailFromToken(Authorization.substring(7));
        if(email!=null){
            boolean flag =mobile_userCreateService.verified(email);
            if (flag==true){
                return  "success";
            }
        }
        return  "AuthoirzationDefeat";
    }
}