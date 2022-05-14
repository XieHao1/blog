package com.xh.blog.util;

import org.springframework.util.DigestUtils;

public class MD5Util {

    private MD5Util(){}

    public static String getMd5Password(String password, String salt) {
        for (int i = 0; i < 3; i++) {
            password = DigestUtils.md5DigestAsHex((salt + password + salt).getBytes()).toUpperCase();
        }
        return password;
    }
}
