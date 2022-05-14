package com.xh.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.xh.blog.dao.SysUserDao;
import com.xh.blog.dictionary.ErrorEnum;
import com.xh.blog.domain.SysUser;
import com.xh.blog.service.RegisterService;
import com.xh.blog.util.JWTUtils;
import com.xh.blog.util.MD5Util;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.RegisterParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class RegisterServiceImpl implements RegisterService {

    private static final String SALT = "SAIAWVABGIUAWGASG";

    @Resource
    private SysUserDao sysUserDao;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public ResultJSON register(RegisterParams registerParams) {
        //1.判断参数的合法性
        String account = registerParams.getAccount().trim();
        String password = registerParams.getPassword().trim();
        String nickname = registerParams.getNickname().trim();
        if(StringUtils.isBlank(account) || StringUtils.isBlank(password) || StringUtils.isBlank(nickname)){
           return ResultJSON.fail(ErrorEnum.REGISTER_PARAMS_ERROR.getCode(),ErrorEnum.REGISTER_PARAMS_ERROR.getMsg());
        }
        //2.判断用户名是否重复
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account",account);
        wrapper.last("limit 1");
        SysUser sysUser = sysUserDao.selectOne(wrapper);
        if(sysUser != null){
            return ResultJSON.fail(ErrorEnum.ACCOUNT_REPEAT.getCode(),ErrorEnum.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        //3.对密码经行加盐
        String encryptedPassword = MD5Util.getMd5Password(password,SALT);
        //4.注册
        SysUser insertSysUser = new SysUser();
        insertSysUser.setAdmin(true);
        insertSysUser.setDeleted(false);
        insertSysUser.setSalt(SALT);
        insertSysUser.setNickname(nickname);
        insertSysUser.setAccount(account);
        insertSysUser.setPassword(encryptedPassword);
        insertSysUser.setCreateDate(System.currentTimeMillis());
        insertSysUser.setLastLogin(System.currentTimeMillis());
        insertSysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUserDao.insert(insertSysUser);
        //5.将数据存储到token和redis中
        String token = JWTUtils.getToken(insertSysUser.getId());
        redisTemplate.opsForValue().set("token_"+token,new Gson().toJson(insertSysUser),1, TimeUnit.DAYS);
        return ResultJSON.success(token);
    }
}
