package com.chervon.iot.common.exception;

/**
 * Created by Shayne on 2017/8/4.
 */
public class Bad_RequestException extends Exception{
    public Bad_RequestException() {
    }

    public Bad_RequestException(String message) {
        super(message);
    }
}
