package com.xh.blog.service;


import com.xh.blog.util.ResultJSON;

public interface CategoryService {
    /**
     * 查询所以文章分类
     * @return JSON
     */
    ResultJSON findAllCategories();

    /**
     * 查询文章分类的所有信息
     * @return JSON
     */
    ResultJSON findAllCategoriesMsg();

    /**
     * 通过文章类别id查询类别信息
     * @param id 文章类别id
     * @return JSON
     */
    ResultJSON findCategoriesById(String id);
}
