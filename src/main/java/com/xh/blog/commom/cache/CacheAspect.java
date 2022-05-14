package com.xh.blog.commom.cache;

import com.google.gson.Gson;
import com.xh.blog.util.ResultJSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;

@Aspect
@Component
@Slf4j
public class CacheAspect {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Pointcut("@annotation(com.xh.blog.commom.cache.Cache)")
    public void pt(){}
    @Around("pt()")
    //使用缓存的标识+类名+方法名+参数类型MD5加密后的字符串作为redis的key
    public Object cache(ProceedingJoinPoint pjp) throws Throwable{
        //获取方法的完整定义
        Signature signature = pjp.getSignature();
        //获取类名
        String className = pjp.getTarget().getClass().getSimpleName();
        //获取调用的方法名
        String methodName = signature.getName();

        //获取方法的参数类型
        Object[] args = pjp.getArgs();
        StringBuffer params = new StringBuffer();
        for (int i=0;i<args.length;i++){
            //将方法的参数类型转换为JSON字符串
            if(args[i] != null){
                params.append(new Gson().toJson(args[i]));
                //将参数的类型放入数组中储存
            }
        }
        String MD5Params = "";
        if(StringUtils.isNoneBlank(params)){
            //加密，防止出现key过长以及字符转义获取不到的情况
            MD5Params = DigestUtils.md5Hex(String.valueOf(params));
        }

        //获取方法操作对象
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        //获取Cache注解
        Cache annotation = method.getAnnotation(Cache.class);
        //获取缓存的过期时间
        long expire = annotation.expire();
        //获取缓存的标识
        String name = annotation.name();

        //先从redis中获取
        String redisKey = name + "::" + className + "::" + methodName + "::" +MD5Params;
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        if(StringUtils.isNoneEmpty(redisKey)){
            log.info("走了缓存~~~~~~~~~~,{},{}",className,methodName);
            return new Gson().fromJson(redisValue, ResultJSON.class);
        }

        //如果redis没有缓存，则设置缓存
        //先执行目标方法，得到返回的对象
        Object proceed = pjp.proceed();
        redisTemplate.opsForValue().set(redisKey,new Gson().toJson(proceed), Duration.ofMillis(expire));
        log.info("存入缓存~~~~~~~~~~,{},{}",className,methodName);
        return proceed;
    }
}
