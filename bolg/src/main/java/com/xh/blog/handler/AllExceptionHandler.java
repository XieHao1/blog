package com.xh.blog.handler;

import com.xh.blog.util.ResultJSON;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
//对加了@Controller注解的方法进行拦截处理 aop的实现
public class AllExceptionHandler {
    //进行异常处理
    @ExceptionHandler(Exception.class)
    //将异常消息发送给前端
    @ResponseBody
    public ResultJSON doException(Exception e){
        e.printStackTrace();
        return ResultJSON.fail(-999,"系统异常");
    }
}
