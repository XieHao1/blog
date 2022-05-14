package com.xh.blog.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysUserVo implements Serializable {

    private Long id;

    private String account;

    private Boolean admin;

    private String avatar;

    private Long createDate;

    private Boolean deleted;

    private String email;

    private Long lastLogin;

    private String mobilePhoneNumber;

    private String nickname;

    private String password;

    private String salt;

    private String status;

}
