package com.xh.blog.service;

import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.RegisterParams;

public interface RegisterService {
    /**
     * 注册
     * @param registerParams 注册的信息
     * @return 返回token信息
     */
    ResultJSON register(RegisterParams registerParams);
}
