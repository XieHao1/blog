package com.xh.blog.service;

import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.LoginParams;

public interface LoginService {

    /**
     * 登录功能
     * @param loginParams 用户的账号和密码
     * @return 返回JSON
     */
    ResultJSON login(LoginParams loginParams);

    /**
     * 退出登录，将redis中储存的token删除
     * @param token token信息
     * @return JSON
     */
    ResultJSON logout(String token);
}
