package com.chervon.iot.mobile.service.imp;
import com.chervon.iot.mobile.mapper.Mobile_UserMapper;
import com.chervon.iot.mobile.model.Mobile_User;
import com.chervon.iot.mobile.model.entity.Included;
import com.chervon.iot.mobile.model.entity.Relationship;
import com.chervon.iot.mobile.model.entity.ResponseBody;
import com.chervon.iot.mobile.model.entity.ResponseData;
import com.chervon.iot.mobile.sercuity.JwtTokenUtil;
import com.chervon.iot.mobile.service.Mobile_UserLoginService;
import com.chervon.iot.common.util.GetUTCTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by 喷水君 on 2017/6/27.
 */
@Service
public class Mobile_UserLoginServiceImp implements Mobile_UserLoginService {

    @Autowired
    private Mobile_User mobileUser;
    @Autowired
    private Mobile_UserMapper mobile_userMapper;
    @Autowired
    private Relationship relationship;
    @Autowired
    private ResponseData responseData;
    @Autowired
    private  GetUTCTime getUTime;
    @Autowired
    private Included included;
    @Autowired
    private ResponseBody responseBody;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${relation_BaseLink}")
    private String  egoBaseLink;

    @Override
    @Transactional
    public Mobile_User getUserByEmail(String email) throws SQLException {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Mobile_User  mobile_User = (Mobile_User) operations.get(email);
        if(mobile_User==null){
            mobileUser = mobile_userMapper.getUserByEmail(email);
            if(mobile_User!=null){
                operations.set(email,mobile_User);
            }
        }
        return  mobileUser;
    }

    @Override
    public ResponseBody loginReturn(String type,String Authorization,Mobile_User mobile_user) {
        responseData.setType(type);
        responseData.setId(mobile_user.getSfdcId());
        Map<String,String> attributeMap = new HashMap<String,String>();
        String utcTime =getUTime.getUTCTime(getUTime.getCurrentUTCTimeStr(jwtTokenUtil.getCreatedDateFromToken(Authorization)),"yyyy-MM-dd'T'HH:mm:ssZ");;
        attributeMap.put("created_at",utcTime);
        responseData.setAttributes(attributeMap);
        Map<String,String> links = new HashMap<String, String>();
        Map<String,String> data = new HashMap<String, String>();
        links.put("self", egoBaseLink+"sessions/Bearer "+Authorization+"/relationships/creator");
        links.put("related",egoBaseLink+"sessions/Bearer "+Authorization+"/creator");
        data.put("type","users");
        data.put("id",mobile_user.getSfdcId());
        relationship.setLinks(links);
        relationship.setData(data);
        Map<String,Relationship> creator = new HashMap<String, Relationship>();
        creator.put("creator",relationship);
        responseData.setRelationships(creator);
        Map<String,String> linkMap = new HashMap<String,String>();
        linkMap.put("self",egoBaseLink+"sessions/Bearer "+Authorization);
        responseData.setLinks(linkMap);
        included.setType("users");
        included.setId(mobile_user.getSfdcId());
        Map<String,String> includeAttribute = new HashMap<String,String>();
        includeAttribute.put("name", mobile_user.getName());
        includeAttribute.put("email", mobile_user.getEmail());
        includeAttribute.put("status", mobile_user.getStatus());
        included.setAttributes(includeAttribute);
        Map<String,String> includeLink = new HashMap<String,String>();
        includeLink.put("self", egoBaseLink+"users/"+mobile_user.getSfdcId());
        included.setLinks(includeLink);
        List<Included> includedList = new ArrayList<Included>();
        includedList.add(included);
        responseBody.setData(responseData);
        responseBody.setIncluded(includedList);
        Map<String,String> metaMap = new HashMap<String,String>();
        responseBody.setMeta(metaMap);
        return responseBody;
    }

    @Override
    @Transactional
    public void modifyTime(Mobile_User mobile_user)throws SQLException {
        mobile_userMapper.updateModifyTime(mobile_user);
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(mobile_user.getEmail(),mobile_user);
        operations.set(mobile_user.getSfdcId(),mobile_user);
    }
}
