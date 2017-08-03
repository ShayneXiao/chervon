package com.chervon.iot.common.config;

import com.chervon.iot.mobile.sercuity.filter.HTTPBearerAuthorizeAttribute;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * @author Jonsy
 *
 */
//@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
public class WebSecurityConfig {

    @Bean
    public FilterRegistrationBean jwtFilterRegistrationBean(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        HTTPBearerAuthorizeAttribute httpBearerFilter = new HTTPBearerAuthorizeAttribute();
        registrationBean.setFilter(httpBearerFilter);
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/api/v1/*");
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
    }



}