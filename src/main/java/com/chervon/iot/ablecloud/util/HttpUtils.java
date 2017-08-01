package com.chervon.iot.ablecloud.util;

import com.chervon.iot.common.util.GetUTCTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static final String ENCODING = "UTF-8";
    private static final String HASH = "HmacSHA256";
	
	//Able 后台开发者的ID
    private static final long DEVELOPER_ID = 482;
    //Able 开发者签名认证
    private static final String ACCESS_KEY = "ce28dbd04048029e80c6f1975765cc80";
    //Able 签名所需的密钥
    private static final String SECRET_KEY = "4b30af824096f78480cb92546b8b7e1b";
    //Able 签名的有效时长
    private static final long TIME_OUT = 31536000;
	//主域
	private static final String SUB_DOMAIN="chervon";
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
                majorDomain +
                subDomain;
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
                System.out.println("sk="+sk+",stringToSign="+stringToSign);
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


    public static String getNonce(long seed, int length) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYAC0123456789";
        Random random = new Random();
        random.setSeed(seed);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(base.charAt(random.nextInt(base.length())));
        }
        return sb.toString();
    }

  

    public static Map<String,String> getHeadMaps(long timesTamp, String method){
      //  GetUTCTime getUTCTime = new GetUTCTime();
//        long mill = getUTCTime.getCurrentUTCTimeStr();

        Map<String,String> headMaps = new HashMap<>();
        headMaps.put("Content-Type","application/x-zc-object");
        headMaps.put("X-Zc-Major-Domain","chervon");
        headMaps.put("X-Zc-Sub-Domain","");
        headMaps.put("X-Zc-Developer-Id",String.valueOf(DEVELOPER_ID));
        headMaps.put("X-Zc-Timestamp",String.valueOf(timesTamp/1000));
        headMaps.put("X-Zc-Timeout",String.valueOf(TIME_OUT));

        String nonceStr = getNonce(timesTamp,16);
        headMaps.put("X-Zc-Nonce",nonceStr);
        headMaps.put("X-Zc-Access-Key",ACCESS_KEY);
       // String signString = "";
       // signString = TIME_OUT + "" + timesTamp/1000 + nonceStr + DEVELOPER_ID + "chervon" + method;
		String signString=getSignString(DEVELOPER_ID,  SUB_DOMAIN,"",method ,timesTamp/1000,  TIME_OUT,  nonceStr);
        headMaps.put("X-Zc-Developer-Signature",getSignature(SECRET_KEY,signString));
        return headMaps;
    }
}