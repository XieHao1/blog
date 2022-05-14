package com.xh.blog.controller;

import com.xh.blog.service.SysUserService;
import com.xh.blog.util.ResultJSON;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Resource
    private SysUserService sysUserService;

    @GetMapping("/currentUser")
    public ResultJSON currentUser(@RequestHeader("Authorization") String token){
        return sysUserService.findUserInfoByToken(token);
    }
}
