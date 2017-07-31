package com.chervon.iot.mobile.util;

import com.chervon.iot.common.exception.ErrorInfo;
import com.chervon.iot.common.exception.ResultMsg;
import com.chervon.iot.common.exception.ResultStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Boy on 2017/7/20.
 */
public class ErrorResponseUtil {
    private   List<ResultMsg.Error> errors = new ArrayList<ResultMsg.Error>();
    public static ResultMsg errorFiled(){
        ResultMsg.Error error = new ResultMsg.Error(ResultStatusCode.SC_BAD_REQUEST.getErrcode(),ResultStatusCode.SC_BAD_REQUEST.getTitle(),null);
        List<ResultMsg.Error> errors = new ArrayList<ResultMsg.Error>();
        errors.add(error);
        return new ResultMsg(errors);
    }
    public static ResultMsg unauthorized(){
        ResultMsg.Error error = new ResultMsg.Error(ResultStatusCode.SC_PERMISSION_DENIED.getErrcode(),ResultStatusCode.SC_PERMISSION_DENIED.getTitle(),null);
        List<ResultMsg.Error> errors = new ArrayList<ResultMsg.Error>();
        errors.add(error);
        return new ResultMsg(errors);
    }
    public static ResultMsg noContent(){
        List<ResultMsg.Error> errors = new ArrayList<ResultMsg.Error>();
        ResultMsg.Error error = new ResultMsg.Error(ResultStatusCode.SC_NO_CONTENT.getErrcode(),ResultStatusCode.SC_NO_CONTENT.getTitle(),null);
        errors.add(error);
        return new ResultMsg(errors);
    }
    public static ResultMsg serverError(){
        List<ResultMsg.Error> errors = new ArrayList<ResultMsg.Error>();
        ResultMsg.Error error = new ResultMsg.Error(ResultStatusCode.INTERNAL_SERVER_ERROR.getErrcode(),ResultStatusCode.INTERNAL_SERVER_ERROR.getTitle(),null);
        errors.add(error);
        return new ResultMsg(errors);
    }
    public static ResultMsg notFound(){
        List<ResultMsg.Error> errors = new ArrayList<ResultMsg.Error>();
        ResultMsg.Error error = new ResultMsg.Error(ResultStatusCode.SC_NOT_FOUND.getErrcode(),ResultStatusCode.SC_NOT_FOUND.getTitle(),null);
        errors.add(error);
        return new ResultMsg(errors);
    }
    public static ResultMsg forbidend(){
        List<ResultMsg.Error> errors = new ArrayList<ResultMsg.Error>();
        ResultMsg.Error error = new ResultMsg.Error(ResultStatusCode.SC_FORBIDDEN.getErrcode(),ResultStatusCode.SC_FORBIDDEN.getTitle(),null);
        errors.add(error);
       return new ResultMsg(errors);
    }
    public static ResultMsg conflict(){
        List<ResultMsg.Error> errors = new ArrayList<ResultMsg.Error>();
        ResultMsg.Error error = new ResultMsg.Error(ResultStatusCode.SC_CONFLICT.getErrcode(),ResultStatusCode.SC_CONFLICT.getTitle(),null);
        errors.add(error);
        return new ResultMsg(errors);
    }
}
