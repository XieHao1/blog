package com.xh.blog.interceptor;

import com.google.gson.Gson;
import com.xh.blog.dictionary.ErrorEnum;
import com.xh.blog.util.JWTUtils;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.util.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //需要判断 请求的接口路径 是否为 HandlerMethod (controller方法)
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        String token = request.getHeader("Authorization");

        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");

        if(StringUtils.isBlank(token)){
            result(response);
            return false;
        }
        try {
            JWTUtils.verify(token);
        }catch (RuntimeException e){
            result(response);
            return false;
        }
        ThreadLocalUtils.putLocal(token);
        return true;
    }

    private void result(HttpServletResponse response) throws IOException {
        ResultJSON fail = ResultJSON.fail(ErrorEnum.TOKEN_ERROR.getCode(), ErrorEnum.TOKEN_ERROR.getMsg());
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(new Gson().toJson(fail));
    }
}
