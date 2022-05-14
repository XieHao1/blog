package com.xh.blog.vo;

import lombok.Data;

import java.util.List;

@Data
public class ArticleParams {
    private Long id;
    private String title;
    private ArticleBodyParams body;
    private CategoryVo category;
    private String summary;
    private List<TagVo> tags;
}
