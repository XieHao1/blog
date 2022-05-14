package com.xh.blog.controller;

import com.xh.blog.service.RegisterService;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.RegisterParams;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class RegisterController {

    @Resource
    private RegisterService registerService;

    @PostMapping("/register")
    public ResultJSON register(@RequestBody RegisterParams registerParams){
        return registerService.register(registerParams);
    }
}
