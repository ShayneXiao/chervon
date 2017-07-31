package com.chervon.iot.ablecloud.util;

import com.chervon.iot.common.util.GetUTCTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static final String ENCODING = "UTF-8";
    private static final String HASH = "HmacSHA256";

    @Value("${ablecloud.developerId}")
    private  long developerId;

    @Value("${ablecloud.accessKey}")
    private  String accessKey;

    @Value("${ablecloud.timeout}")
    private  long timeout;

    @Value("${ableCloud.secretKey}")
    private String secretKey;
    @Value("${ableCloud.subDomain}")
    private String subDomain;

    /**
     * 获取用于签名字符串
     *
     * @param developerId 开发者id
     * @param majorDomain 主域名
     * @param subDomain   子域名，子域名为空字符串即可
     * @param method      接口方法名(即ACMsg里对应的name)
     * @param timestamp   当前时间，单位秒
     * @param timeout     签名有效期，单位秒
     * @param nonce       随机16位字符串
     */
    public static String getSignString(long developerId, String majorDomain,
                                       String subDomain, String method,
                                       long timestamp, long timeout, String nonce) {
        String stringToSign = String.valueOf(timeout) +
                String.valueOf(timestamp) +
                nonce +
                String.valueOf(developerId) +
                method +
                majorDomain+
                subDomain
              ;
        return stringToSign;
    }


    /**
     * 获取X-Zc-Developer-Signature的签名值
     *
     * @param sk           开发密钥对，与ak对应，从控制台-->服务管理-->开发密钥-->Secrety Key获取
     * @param stringToSign 由上面函数获取
     */
    public static String getSignature(String sk, String stringToSign) {
        String signature = "";

        try {
            String encodedSign = URLEncoder.encode(stringToSign, ENCODING);
            try {
                Mac mac = Mac.getInstance(HASH);
                mac.init(new SecretKeySpec(sk.getBytes(ENCODING), HASH));
                byte[] hashData = mac.doFinal(encodedSign.getBytes(ENCODING));

                StringBuilder sb = new StringBuilder(hashData.length * 2);
                for (byte data : hashData) {
                    String hex = Integer.toHexString(data & 0xFF);
                    if (hex.length() == 1) {
                        // append leading zero
                        sb.append("0");
                    }
                    sb.append(hex);
                }
                signature = sb.toString().toLowerCase();
            } catch (Exception e) {
                logger.warn("sha256 exception for[" + sk + "," + stringToSign + "]. e:", e);
            }
        } catch (UnsupportedEncodingException e) {
            logger.warn("encode error, string[" + stringToSign + "] e:" + e);
        }
        return signature;
    }



//其中，timestamp精确到秒；nonce是一个随机字符串（一般选则长度为16个字符）。如：


    public  String getNonce(long seed, int length) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYAC0123456789";
        Random random = new Random();
        random.setSeed(seed);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(base.charAt(random.nextInt(base.length())));
        }
        return sb.toString();
    }

    public  Map<String,String> getHeadMaps(String method){
        GetUTCTime getUTCTime = new GetUTCTime();
        long seconds = getUTCTime.getCurrentUTCTimeStr(new Date())/1000;//=参数正确？====================
      //  long seconds = System.currentTimeMillis()/1000;
        System.out.println("!!!"+System.currentTimeMillis());
        Map<String,String> headMaps = new HashMap<>();
        headMaps.put("Content-Type","application/x-zc-object");
        headMaps.put("X-Zc-Major-Domain",subDomain);
        headMaps.put("X-Zc-Sub-Domain","");
        headMaps.put("X-Zc-Developer-Id",String.valueOf(developerId));
        headMaps.put("X-Zc-Timestamp",String.valueOf(seconds));
        headMaps.put("X-Zc-Timeout",String.valueOf(timeout));
        String nonceStr = getNonce(seconds,16);
        headMaps.put("X-Zc-Nonce",nonceStr);
        headMaps.put("X-Zc-Access-Key",accessKey);

         String signString=getSignString(developerId,  subDomain,
                 "",method ,
                seconds,  timeout,  nonceStr);
        headMaps.put("X-Zc-Developer-Signature",getSignature(secretKey,signString));
        return headMaps;
    }
}