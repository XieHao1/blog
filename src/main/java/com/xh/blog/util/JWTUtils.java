package com.xh.blog.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;

public class JWTUtils {

    //token的密钥
    private static final String TOKEN_SIGN = "^BX(!C(G&af(0ds8g0@)(@ASV^^!!";

    //token的过期时间（天）
    private static final int TIME = 7;

    private JWTUtils(){}

    /**
     * 生成token
     * @param id 要在token中储存的用户的id
     * @return 生成的token
     */
    public static String getToken(Long id){

        JWTCreator.Builder builder = JWT.create();

        //在payload中存储用户的id
        builder.withClaim("id",id);

        //设置过期时间和sigNature
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,TIME);
        return builder.withExpiresAt(calendar.getTime()).sign(Algorithm.HMAC256(TOKEN_SIGN));
    }

    /**
     * 核实token是否正确
     * @param token token
     * @return 核实之后的Token对象
     */
    public static DecodedJWT verify(String token){
        return JWT.require(Algorithm.HMAC256(TOKEN_SIGN)).build().verify(token);
    }
}
