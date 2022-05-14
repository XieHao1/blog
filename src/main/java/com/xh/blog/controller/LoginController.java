package com.xh.blog.controller;

import com.xh.blog.service.LoginService;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.LoginParams;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class LoginController {

    @Resource
    private LoginService loginService;

    @PostMapping("/login")
    public ResultJSON login(@RequestBody LoginParams loginParams){
        return loginService.login(loginParams);
    }

    @GetMapping("/logout")
    public ResultJSON logout(@RequestHeader("Authorization") String token){
        return loginService.logout(token);
    }
}
