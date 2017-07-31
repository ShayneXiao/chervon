package com.chervon.iot.common.exception.exceptionHandle;

import com.chervon.iot.common.exception.ErrorInfo;
import com.chervon.iot.common.exception.ResultMsg;
import com.chervon.iot.common.exception.ResultStatusCode;
import com.chervon.iot.mobile.util.ErrorResponseUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Mike Xu.
 * @Date: Created in 20:58 2017/6/26
 * @Description:
 * @Modified By:
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {SQLException.class, IOException.class,GeneralSecurityException.class})
    @ResponseBody
  public ResponseEntity<?> partException(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/vnd.api+json");
        ResultMsg  resultMsg =  ErrorResponseUtil.serverError();
        return new ResponseEntity(resultMsg,headers, HttpStatus.valueOf(ResultStatusCode.INTERNAL_SERVER_ERROR.getErrcode()));
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<?> GlobalException(Exception e){
      e.printStackTrace();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/vnd.api+json");
        ResultMsg  resultMsg =  ErrorResponseUtil.serverError();
        return new ResponseEntity(resultMsg,headers, HttpStatus.valueOf(ResultStatusCode.INTERNAL_SERVER_ERROR.getErrcode()));

    }
}
