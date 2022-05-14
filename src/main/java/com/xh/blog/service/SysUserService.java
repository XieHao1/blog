package com.xh.blog.service;

import com.xh.blog.domain.SysUser;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.UserVo;

public interface SysUserService {
    /**
     * 通过作者id查询作者的姓名
     * @param authorID 作者的id
     * @return 作者的姓名
     */
    SysUser findSysUserNameByAuthorId(Long authorID);

    /**
     * 通过用户名和密码查询用户信息
     * @param account 用户名
     * @param encryptedPassword 加密后的密码
     * @return 用户的信息
     */
    SysUser findUser(String account, String encryptedPassword);

    /**
     * 通过token查询用户的信息
     * @param token token字符串
     * @return 用户 的信息
     */
    ResultJSON findUserInfoByToken(String token);

    /**
     * 评论接口中通过作者id查询作者的信息
     * @param authorId 作者id
     * @return UserVo中所需要的id
     */
    UserVo findUserVoByAuthorId(Long authorId);

    void updateLastLoginTime(Long id);
}
