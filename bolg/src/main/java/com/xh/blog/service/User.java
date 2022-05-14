package com.xh.blog.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.WxUserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class User {
    //1.拼接一个url，微信登录凭证校验接口
    //2.发起一个http的调用,获取微信的返回结果
    //3.将凭证存入redis中
    //4.生产一个sessionId，返回给前端，作为当前需要登录的一个标识
    @Autowired
    private RedisTemplate<String,String>  redisTemplate;

    public ResultJSON getSessionID(String cookie){
        String AppID = "wxac8dc8fb46ffaccb";
        String AppSecret = "73c451ca24ef60bde97c60239098426b";
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid={0}&secret={1}&js_code={2}&grant_type=authorization_code";
        String replaceUrl = url.replace("{0}", AppID).replace("{1}", AppSecret).replace("{2}", cookie);
        //微信返回的json
        String res = HttpUtil.get(replaceUrl);
        //redis中的key,和返回给前端的sessionId
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(uuid,res,30, TimeUnit.MINUTES);
        Map<String,String> map = new HashMap<>();
        map.put("sessionId",uuid);
        return ResultJSON.success(map);
    }

    public String wxDecrypt(String encryptedData,String vi,String sessionId) throws Exception{
        //开始解密
        //解密完成后，会获得到微信的用户信息，其中包含openId，性别，昵称，头像等信息
        //openID是唯一的，需要区user表中查询openID是否存在,存在就用该身份登录，不存在执行注册
        //使用jwt生产token返回给前端
        String json = redisTemplate.opsForValue().get(sessionId);
        //wxUserInfoVO 映射类，将解密后的信息映射到对应类中
        WxUserInfoVO wxUserInfoVO = JSON.parseObject(json, WxUserInfoVO.class);
        String openId = wxUserInfoVO.getOpenId();
        //去用户表中查询是否存在,若不存在，则进行注册
        return openId;
    }
}
