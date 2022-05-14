package com.xh.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import com.xh.blog.dao.SysUserDao;
import com.xh.blog.domain.SysUser;
import com.xh.blog.service.SysUserService;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.SysUserVo;
import com.xh.blog.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private SysUserDao sysUserDao;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public SysUser findSysUserNameByAuthorId(Long authorID) {
        SysUser sysUser = sysUserDao.selectById(authorID);
        if(sysUser == null){
            sysUser = new SysUser();
            sysUser.setNickname("佚名");
        }
        return sysUser;
    }

    @Override
    public SysUser findUser(String account, String encryptedPassword) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account",account);
        wrapper.eq("password",encryptedPassword);
        wrapper.select("id","account","avatar","nickname");
        wrapper.last("limit 1");
        return sysUserDao.selectOne(wrapper);
    }

    @Override
    public ResultJSON findUserInfoByToken(String token) {
        String JSON = redisTemplate.opsForValue().get("token_" + token);
        SysUserVo sysUserVo = new Gson().fromJson(JSON, SysUserVo.class);
        return ResultJSON.success(sysUserVo);
    }

    @Override
    public UserVo findUserVoByAuthorId(Long authorId) {
        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
        sysUserQueryWrapper.select("id","avatar","nickname").eq("id",authorId);
        SysUser sysUser = sysUserDao.selectOne(sysUserQueryWrapper);
        if(sysUser==null){
            UserVo userVo = new UserVo();
            userVo.setAvatar("/static/img/logo.b3a48c0.png");
            userVo.setId(1L);
            userVo.setNickname("佚名");
            return userVo;
        }
        return copy(sysUser);
    }

    @Override
    public void updateLastLoginTime(Long id) {
        UpdateWrapper<SysUser> userUpdateWrapper = new UpdateWrapper<>();
        SysUser sysUser = new SysUser();
        sysUser.setLastLogin(System.currentTimeMillis());
        userUpdateWrapper.eq("id",id);
        sysUserDao.update(sysUser,userUpdateWrapper);
    }

    private UserVo copy(SysUser sysUser) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(sysUser,userVo);
        return userVo;
    }
}
