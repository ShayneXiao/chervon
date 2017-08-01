package com.chervon.iot.mobile.util;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.pagehelper.PageHelper;
/**
 * Created by Admin on 2017/7/20.
 */
@Configuration
public class MybatisConfig2 {
    @Bean
    public PageHelper pageHelper() {
        System.out.println("MyBatisConfiguration.pageHelper()");
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("pageSizeZero", "true");
        p.setProperty("dialect","postgresql");
        pageHelper.setProperties(p);
        return pageHelper;
    }
}
