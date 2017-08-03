package com.chervon.iot.common.util;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 修改了其中的方法,全部替换
 */
@Component
public class GetUTCTime {

    // 取得本地时间：
   private Calendar  cal = Calendar.getInstance();
    // 取得时间偏移量：
    private int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
    // 取得夏令时差：
    private int dstOffset = cal.get(Calendar.DST_OFFSET);

    public long getCurrentUTCTimeStr() {
        System.out.println("local="+cal.getTime());
        System.out.println("扣除前 = " + cal.getTimeInMillis());// 等效System.currentTimeMillis() , 统一值，不分时区
        // 从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        System.out.println("扣除后 = " + cal.getTimeInMillis());
        long mills = cal.getTimeInMillis();
        System.out.println("UTC = " + mills);

        return mills;
    }

    public long getCurrentUTCTimeStr(Date date) {

        cal.setTime(date);
        System.out.println("local="+cal.getTime());
        System.out.println("扣除前 = " + cal.getTimeInMillis());// 等效System.currentTimeMillis() , 统一值，不分时区
        // 从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        System.out.println("扣除后 = " + cal.getTimeInMillis());
        long mills = cal.getTimeInMillis();
        System.out.println("UTC = " + mills);

        return mills;
    }

    public String getUTCTime(long utcTimestr,String utcTimePatten) {
        cal.setTimeInMillis(utcTimestr);
        SimpleDateFormat dataFormat = new SimpleDateFormat(utcTimePatten);
        String utcTime = dataFormat.format(cal.getTime());
        System.out.println("GMT time= " + utcTime);
        return utcTime;
    }

    public String getLocalTime(long utcTimestr,String utcTimePatten){
        cal.setTimeInMillis(utcTimestr);
        //从本地时间里扣除这些差量
        cal.add(Calendar.MILLISECOND, (zoneOffset + dstOffset));
        SimpleDateFormat dataFormat = new SimpleDateFormat(utcTimePatten);
        String localTime = dataFormat.format(cal.getTime());
        System.out.println("Local time= " + localTime);
        return localTime;
    }
}