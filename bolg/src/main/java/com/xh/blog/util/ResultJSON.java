package com.xh.blog.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
//Lombok中自动生成一个包含所有字段的构造方法
public class ResultJSON implements Serializable {

    private boolean success;

    private int code;

    private String msg;

    private Object data;

    public ResultJSON() {}

    public static ResultJSON success(Object data){
        return new ResultJSON(true,200,"success",data);
    }

    public static ResultJSON fail(int code,String msg){
        return new ResultJSON(false,code,msg,null);
    }
}
