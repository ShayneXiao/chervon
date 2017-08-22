package com.chervon.iot.mobile.sercuity.filter;

import com.chervon.iot.common.exception.ResultMsg;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.chervon.iot.mobile.service.Mobile_UserLoginService;
import com.chervon.iot.mobile.util.ErrorResponseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Shayne on 2017/8/7.
 */
@Component    //首先初始化切面类
@Aspect
public class ApiAuthenticationAspect {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private Mobile_User mobile_user;
    @Autowired
    private Mobile_UserLoginService mobile_userLoginService;
    @Resource
    private HttpServletRequest request;

    @Around("@annotation(com.chervon.iot.mobile.sercuity.filter.ApiAuthentication)")
    public Object beforeAdvice(ProceedingJoinPoint point) throws Throwable{
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/vnd.api+json");

        logger.info("进入过滤器");
        String headerAuthToken = request.getHeader("Authorization");
        String authToken = "";

        if(headerAuthToken==null){
            ResultMsg resultMsg =  ErrorResponseUtil.unauthorized();
            return new ResponseEntity<Object>(resultMsg, headers, HttpStatus.FORBIDDEN);
        }
        //Authorization格式判断，格式不对，return
        if((headerAuthToken != null) && (headerAuthToken.length() > 7) && headerAuthToken.startsWith("Bearer ")){
            authToken = headerAuthToken.substring(7);
            System.out.println(authToken);
            //通过jwt解析拿到email
            String email = jwtTokenUtil.getEmailFromToken(authToken);
            logger.info("checking authentication for user= " + email);
            logger.info("checking authentication for ExpirationDateFromToken= " + jwtTokenUtil.getExpirationDateFromToken(authToken));
            //从数据拿去信息
            if(email != null){
                try {
                    mobile_user = mobile_userLoginService.getUserByEmail(email);
                }
                catch(Exception e){
                    //异常报500错误return
                    ResultMsg  resultMsg =  ErrorResponseUtil.serverError();
                    return new ResponseEntity<Object>(resultMsg, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                //符合条件过滤
                if(mobile_user != null && jwtTokenUtil.validateToken(authToken, mobile_user)){
                    System.out.println(mobile_user.getPassword());
                    System.out.println(mobile_user.getLastpasswordresetdate());
                    System.out.println("JWT放行");
                    return point.proceed();
                }
            }
        }
        //错误返回验证失败
        ResultMsg  resultMsg =  ErrorResponseUtil.unauthorized();
        return new ResponseEntity<Object>(resultMsg, headers, HttpStatus.FORBIDDEN);
    }
}
