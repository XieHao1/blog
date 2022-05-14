package com.xh.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xh.blog.domain.Article;
import com.xh.blog.vo.Archives;

import java.util.List;


public interface ArticleDao extends BaseMapper<Article> {

    List<Archives> newArchives();

    IPage<Article> findArticles(Page<Article> page, Long categoryId, Long tagId, String year, String month);
}
