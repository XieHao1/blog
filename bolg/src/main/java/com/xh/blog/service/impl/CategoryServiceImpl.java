package com.xh.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xh.blog.dao.CategoryDao;
import com.xh.blog.domain.Category;
import com.xh.blog.service.CategoryService;
import com.xh.blog.util.ResultJSON;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryDao categoryDao;

    @Override
    public ResultJSON findAllCategories() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","avatar","category_name");
        List<Category> categories = categoryDao.selectList(queryWrapper);
        return ResultJSON.success(categories);
    }

    @Override
    public ResultJSON findAllCategoriesMsg() {
        List<Category> categories = categoryDao.selectList(new QueryWrapper<>());
        return ResultJSON.success(categories);
    }

    @Override
    public ResultJSON findCategoriesById(String id) {
        Category category = categoryDao.selectById(id);
        return ResultJSON.success(category);
    }
}
