package com.xh.blog.vo;

import lombok.Data;

@Data
public class CommentParams {
    private Long articleId;
    private String content;
    private Long parent;
    private Long toUserId;
}
