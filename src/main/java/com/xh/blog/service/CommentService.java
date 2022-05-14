package com.xh.blog.service;

import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.CommentParams;

public interface CommentService{
    /**
     * 通过文章id查询评论
     * @param id 文章的id
     * @return JSON
     */
    ResultJSON findCommentsById(String id);

    /**
     * 添加评论
     * @param commentParams 接收参数的VO类
     * @return 返回JSON
     */
    ResultJSON insertComment(CommentParams commentParams);
}
