package com.xh.blog.commom.aop;

import com.google.gson.Gson;
import com.xh.blog.util.HttpContextUtils;
import com.xh.blog.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Component
@Aspect //切面，定义了通知和切点的关系
@Slf4j
public class LogAspect {
    //切点
    @Pointcut("@annotation(com.xh.blog.commom.aop.LogAnnotation)")
    public void pt(){}

    //通知类 环绕通知
    @Around("pt()")
    //方法的定义要求：
    // 1.公共方法
    // 2.必须有一个返回值，推荐使用Object
    // 3.方法名称自定义
    // 4.方法有参数 ,固定参数：ProceedingJoinPoint接口，继承了JoinPoint
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable{
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = joinPoint.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        recordLog(joinPoint, time);
        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAnnotation annotation = method.getAnnotation(LogAnnotation.class);
        log.info("======================log start==================");
        log.info("module:{}",annotation.module());
        log.info("operation:{}",annotation.operator());

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method:{}",className + "." + methodName + "()");

        //请求参数
        Object[] args = joinPoint.getArgs();
        String params = new Gson().toJson(args[0]);
        log.info("params:{}",params);

        //获取request
        HttpServletRequest httpServletRequest = HttpContextUtils.getHttpServletRequest();
        //获取ip地址
        String ipAddr = IpUtils.getIpAddr(httpServletRequest);
        log.info("ip:{}",ipAddr);

        log.info("excute time : {} ms",time);
        log.info("=====================log end====================");
    }

}
