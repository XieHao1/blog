package com.xh.blog.util;

public class ThreadLocalUtils {

    private ThreadLocalUtils(){};

    private static final ThreadLocal<String> LOCAL = new ThreadLocal<>();

    public static void putLocal(String token){
        LOCAL.set(token);
    }

    public static String get(){
        return LOCAL.get();
    }
    //在用户退出登录后，一定要将本地储存的信息删除
    public static void remove(){
        LOCAL.remove();
    }
}
