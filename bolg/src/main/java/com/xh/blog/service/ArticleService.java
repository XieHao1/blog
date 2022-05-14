package com.xh.blog.service;

import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.ArticleParams;
import com.xh.blog.vo.PageParams;

public interface ArticleService {
    /**
     * 分页查询文章列表接口
     * @param pageParams 接收前端传入的页数和展示数量的JSON格式字符串
     * @return 返回JSON
     */
    ResultJSON listArticle(PageParams pageParams);

    /**
     * 查询最热文章
     * @param limit 查询最热文章的个数
     * @return 返回JSON
     */
    ResultJSON hotArticles(int limit);

    /**
     * 查询最新文章
     * @param limit 查询最新文章的个数
     * @return 返回JSON
     */
    ResultJSON newArticles(int limit);

    /**
     * 文章归档
     * @return 返回JSON
     */
    ResultJSON archives();

    /**
     * 通过文章的id查询文章
     * @param id 文章的id
     * @return 返回json
     */
    ResultJSON findArticleById(Long id);

    /**
     * 写文章接口
     * @param articleParams 文章的相关信息
     * @return  "data": {"id":12232323}
     */
    ResultJSON publish(ArticleParams articleParams);
}
