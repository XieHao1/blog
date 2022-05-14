package com.xh.blog.service.impl;

import com.google.gson.Gson;
import com.xh.blog.dictionary.ErrorEnum;
import com.xh.blog.domain.SysUser;
import com.xh.blog.service.LoginService;
import com.xh.blog.service.SysUserService;
import com.xh.blog.service.ThreadService;
import com.xh.blog.util.JWTUtils;
import com.xh.blog.util.MD5Util;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.util.ThreadLocalUtils;
import com.xh.blog.vo.LoginParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {

    private final static String SALT = "SAIAWVABGIUAWGASG";

    @Resource
    private SysUserService sysUserService;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public ResultJSON login(LoginParams loginParams) {
        //1.检测参数是否合法
        String account = loginParams.getAccount().trim();
        String password = loginParams.getPassword().trim();
        if(StringUtils.isBlank(account) || StringUtils.isBlank(password)){
            return ResultJSON.fail(ErrorEnum.PARAMS_ERROR.getCode(),ErrorEnum.PARAMS_ERROR.getMsg());
        }
        //对密码进行加密
        String encryptedPassword = MD5Util.getMd5Password(password,SALT);
        //2.根据用户名和密码在user表中查询是否存在
        SysUser sysUser = sysUserService.findUser(account,encryptedPassword);
        //3.如果不存在就登录失败
        if(sysUser == null){
            return ResultJSON.fail(ErrorEnum.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorEnum.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        //4.登录成功。使用jwt生成token返回给前端
        String token = JWTUtils.getToken(sysUser.getId());
        //5.将token放入redis当中 token:user 过期时间
        redisTemplate.opsForValue()
                .set("token_"+token,new Gson().toJson(sysUser),1,TimeUnit.DAYS);
        //（进行了一次缓存，使下一次登录不需要在查询一次数据库）
        //6.使用线程池更新登录时间
        ThreadService.updateLastLoginTime(sysUserService,sysUser.getId());
        return ResultJSON.success(token);
    }

    @Override
    public ResultJSON logout(String token) {
        ThreadLocalUtils.remove();
        redisTemplate.delete("token_"+token);
        return ResultJSON.success(null);
    }
}
