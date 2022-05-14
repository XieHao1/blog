package com.xh.blog.service;

import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.TagVo;

import java.util.List;

public interface TagService {
     /**
      * 根据文章id查询标签列表
      * @param id 文章ID
      * @return  标签列表
      */
     List<TagVo> findTagsByArticleID(Long id);

     /**
      * 查询热门标签
      * @param limit 热门标签的个数
      * @return 热门标签列表
      */
     List<TagVo> hots(int limit);

    /**
     * 查询所以标签
     * @return 所以的文章标签
     */
    ResultJSON findAllTags();

    /**
     * 查询标签的所有信息
     * @return 标签的所有信息
     */
    ResultJSON findDetail();

    /**
     * 通过标签的id查询标签信息
     * @param id 标签id
     * @return 标签的相关信息
     */
    ResultJSON findTagsByID(String id);
}
