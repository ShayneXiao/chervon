package com.chervon.iot.common.exception;

/**
 * @Author: Mike Xu.
 * @Date: Created in 20:58 2017/6/26
 * @Description:
 * @Modified By:
 */
public class ErrorInfo<T> {
    public static final Integer OK = 200;
    public static final Integer ERROR = 201;


    private Integer code;
    private String message;
    private String url;
    private T Data;

    public static Integer getOK() {
        return OK;
    }

    public static Integer getERROR() {
        return ERROR;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

}
