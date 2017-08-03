package com.chervon.iot.mobile.util;

import com.chervon.iot.common.exception.ResultStatusCode;
import com.chervon.iot.mobile.mapper.Mobile_UserMapper;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.service.Mobile_UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;


/**
 * Created by Admin on 2017/7/11.
 */
@Component
public class BasicAuthorizeTokenUtil {

    @Autowired
    private Mobile_UserLoginService mobile_userLoginService;
    private Mobile_User user;

    public Mobile_User getUser() {
        return user;
    }

    public void setUser(Mobile_User user) {
        this.user = user;
    }

    public ResultStatusCode checkAuthorizeToken(String token)throws Exception{

        if ((token != null) && (token.length() > 6))
        {
            String HeadStr = token.substring(0, 5).toLowerCase();
            if (HeadStr.compareTo("basic") == 0)
            {
                token = token.substring(6, token.length());
                System.out.println("auth === "+token);
                String decodedAuth = getFromBASE64(token);
                System.out.println("decodedAuth === "+decodedAuth);
                if (decodedAuth != null)
                {
                    String[] UserArray = decodedAuth.split(":");
                    System.out.println("UserArray0 === "+UserArray[0]);
                    System.out.println("UserArray1 === "+UserArray[1]);
                    this.user = mobile_userLoginService.getUserByEmail(UserArray[0]);
                    System.out.println("password==="+user.getPassword());
                    System.out.println("email==="+user.getEmail());
                       /* if(user.getStatus().equals("unverified")){
                            return ResultStatusCode.SC_FORBIDDEN;
                        }*/
                    if(this.user != null && UserArray[1].equals(this.user.getPassword())){
                        System.out.println("===========OK=============");
                        return ResultStatusCode.SC_OK;
                    }
                    else{
                        return ResultStatusCode.SC_BAD_REQUEST;
                    }
                }
            }
        }
        return ResultStatusCode.SC_PERMISSION_DENIED;
    }

    public static String[] parseBasicAuthorize(String token){
        String[] UserArray = new String[2];
        if ((token != null) && (token.length() > 6))
        {
            String HeadStr = token.substring(0, 5).toLowerCase();
            if (HeadStr.compareTo("basic") == 0)
            {
                token = token.substring(6, token.length());
                String decodedAuth = getFromBASE64(token);
                System.out.println("decodedAuth === "+decodedAuth);
                if (decodedAuth != null)
                {
                    UserArray = decodedAuth.split(":");
                }
            }
        }
        return UserArray;
    }
    public static String getFromBASE64(String s) {
        if (s == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }
}
