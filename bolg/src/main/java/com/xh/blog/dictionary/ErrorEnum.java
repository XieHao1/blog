package com.xh.blog.dictionary;

public enum ErrorEnum {

    PARAMS_ERROR(10001,"参数有误"),
    ACCOUNT_PWD_NOT_EXIST(10002,"用户名或密码不存在"),
    NO_PERMISSION(70001,"无访问权限"),
    SESSION_TIME_OUT(90001,"会话超时"),
    NO_LOGIN(90002,"未登录"),
    TOKEN_ERROR(10003,"token验证失败"),
    REGISTER_PARAMS_ERROR(100004,"注册参数错误"),
    ACCOUNT_REPEAT(10005,"用户名重复"),
    COMMENT_ERROR(10006,"评论失败"),
    FILE_UPLOAD_ERROR(10007,"文件上传失败")
    ;

    private int code;
    private String msg;

    ErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
