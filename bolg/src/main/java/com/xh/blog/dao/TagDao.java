package com.xh.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xh.blog.domain.Tag;

import java.util.List;

public interface TagDao extends BaseMapper<Tag> {
    /**
     * 通过文章id查询文章标签
     * @param articleID 文章id
     * @return 文章标签列表
     */
    List<Tag> findTagByArticleId(Long articleID);

    /**
     * 通过article和tag关联表查询最热的标签id
     * @param limit 要查询的个数
     * @return 最热标签id列表
     */
    List<Long> findTagsHot(int limit);

    /**
     * 通过最热的标签id查询
     * @param tagIdList 最热标签id列表
     * @return 最热标签列表
     */
    List<Tag> findTagsHotNameById(List<Long> tagIdList);
}
