package com.chervon.iot.common.common_util;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Created by 喷水君 on 2017/7/26.
 */
public class HttpHeader {
    public static HttpHeaders HttpHeader(){
        HttpHeaders  httpHeader = new HttpHeaders();
        httpHeader.add("Content-Type","application/vnd.api+json");
        return httpHeader;
    }
}
