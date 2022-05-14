package com.xh.blog.commom.cache;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
    //设置过期时间 默认为1分钟
    long expire() default 60 * 1000;
    //缓存标识
    String name() default "";
}
