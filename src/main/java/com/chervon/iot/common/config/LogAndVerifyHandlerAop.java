package com.chervon.iot.common.config;

import com.chervon.iot.mobile.mapper.Mobile_LogMapper;
import com.chervon.iot.mobile.model.Mobile_Log;
import com.chervon.iot.mobile.util.BasicAuthorizeTokenUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Map;
import com.alibaba.fastjson.JSON;



/**
 * @Author: Mike Xu.
 * @Date: Created in 20:58 2017/6/26
 * @Description:
 * @Modified By:
 */
@Component
@Aspect
public class LogAndVerifyHandlerAop {
    /**
     * 打印日志
     * */
    Logger log = LoggerFactory.getLogger(LogAndVerifyHandlerAop.class);

    @Autowired
    private Mobile_LogMapper logMapper;

    /**
     * AOP切面中的同步问题：用于监控业务处理性能
     */
    private ThreadLocal<Long> threadLocal = new ThreadLocal<Long>() ;
    private ThreadLocal<Mobile_Log> tlocal = new ThreadLocal<Mobile_Log>();

    @Pointcut("execution(* com.chervon.iot.mobile.controller..*.*(..))")
    public void logControllerAspect(){}

    //请求method前打印内容
    @Before(value = "logControllerAspect()")
    public void methodBefore(JoinPoint joinPoint) {}
       /* try {
            log.info("进入aop before");
            long beginTime = System.currentTimeMillis();
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpRequest request1;

            HttpServletRequest request = attributes.getRequest();
            System.out.println("request.toString()="+request.toString());
            String beanName = joinPoint.getSignature().getDeclaringTypeName();
            String methodName = joinPoint.getSignature().getName();
            String endPoint = request.getRequestURI();
            String remoteAddr = getIpAddr(request);
            String sessionId = request.getSession().getId();
            String userSfid = (String) request.getSession().getAttribute("user");
            String method = request.getMethod();

            //获取当前用户
            String userEmail = "";
            String authToken = request.getHeader("Authorization");
            System.out.println("authToken="+authToken);
            if(method.equals("POST") && endPoint.equals("/api/v1/sessions")){
                String[] UserArray = BasicAuthorizeTokenUtil.parseBasicAuthorize(authToken);
                System.out.println("UserArray="+UserArray[0]);
                userEmail = UserArray[0];
            }
            System.out.println("userEmail="+userEmail);

            //获取参数
            String params = "";
            if ("POST".equals(method)) {
                Object[] paramsArray = joinPoint.getArgs();
                params = argsArrayToString(paramsArray);
            } else {
                Map<?, ?> paramsMap = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                params = paramsMap.toString();
            }

            log.info("url=" + endPoint + "; beanName=" + beanName + "; remoteAddr=" + remoteAddr + "; user=" + userSfid
                    + "; methodName=" + methodName + "; params=" + params);

            Calendar now = Calendar.getInstance();
            Mobile_Log optLog = new Mobile_Log();
            optLog.setEndPoint(endPoint);
            optLog.setRequestMethod(method);
            optLog.setRemoteAddress(remoteAddr);
            optLog.setRequestStartTime(now.getTime());
            optLog.setParams(params);
            optLog.setSessionId(sessionId);
            optLog.setMethodName(methodName);
            optLog.setBeanName(beanName);
            optLog.setUserEmail(userEmail);
            tlocal.set(optLog);
            threadLocal.set(System.currentTimeMillis());

        } catch (Exception e) {
            log.error("***操作请求日志记录失败doBefore()***", e);
        }
    }

    // @Order(5)
    @AfterReturning(returning = "result", pointcut = "logControllerAspect()")
    public void doAfterReturning(Object result) {
        try {
            log.info("进入aop after");
            ObjectMapper mapper = new ObjectMapper();
            String params = mapper.writeValueAsString(result);

            JsonNode jsonNode = mapper.readTree(params);
            String statusCode = jsonNode.get("statusCode").asText();

            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(params);

            // 处理完请求，返回内容
            Mobile_Log optLog = tlocal.get();
            optLog.setResponseJson(jsonObject);
            optLog.setDuration(System.currentTimeMillis()-threadLocal.get());
            optLog.setRepostStatus(statusCode);
            System.out.println("RESPONSE : " + result);
            logMapper.insert(optLog);
        } catch (Exception e) {
            log.error("***操作请求日志记录失败doAfterReturning()***", e);
        }
    }

    *//**
     * 获取登录用户远程主机ip地址
     *
     * @param request
     * @return
     *//*
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    *//**
     * 请求参数拼装
     *
     * @param paramsArray
     * @return
     *//*
    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (int i = 0; i < paramsArray.length; i++) {
                Object jsonObj = JSON.toJSON(paramsArray[i]);
                params += jsonObj.toString() + " ";
            }
        }
        return params.trim();
    }*/
}
