package com.xh.blog.config;

import com.xh.blog.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨域配置，不可设置为*，不安全, 前后端分离项目，可能域名不一致
        //addMapping 跨域路径
        //allowedOrigins 允许的来源
        registry.addMapping("/**").allowedOrigins("http://localhost:8080");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        String[] pathPatterns = {
                "/users/**","/logout",
                "/comments/create/change",
                "/articles/publish"
        };
        registry.addInterceptor(loginInterceptor)
                //拦截的路径
                .addPathPatterns(pathPatterns);
    }
}
