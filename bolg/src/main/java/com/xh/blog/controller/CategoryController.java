package com.xh.blog.controller;

import com.xh.blog.service.CategoryService;
import com.xh.blog.util.ResultJSON;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/categorys")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("")
    public ResultJSON categories(){
        return categoryService.findAllCategories();
    }

    @GetMapping("/detail")
    public ResultJSON detail(){
        return  categoryService.findAllCategoriesMsg();
    }

    @GetMapping("/detail/{id}")
    public ResultJSON detailId(@PathVariable String id){
        return  categoryService.findCategoriesById(id);
    }
}
