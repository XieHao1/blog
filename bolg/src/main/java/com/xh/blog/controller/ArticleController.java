package com.xh.blog.controller;

import com.xh.blog.commom.aop.LogAnnotation;
import com.xh.blog.service.ArticleService;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.ArticleParams;
import com.xh.blog.vo.PageParams;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    /**
     * 显示最大热门/最新文章数
     */
    private static final int LIMIT = 5;

    @Resource
    private ArticleService articleService;

    /**
     * 首页文章列表
     * @param pageParams 接收前端传入的页数和展示数量的JSON格式字符串
     * @return 返回JSON
     */
    @PostMapping
    //POST请求时
    //@RequestBody --> JSON字符串部分
    //@RequestParam --> 请求参数部分
    public ResultJSON listArticles(@RequestBody PageParams pageParams){
        return articleService.listArticle(pageParams);
    }

    @PostMapping("/hot")
    public ResultJSON listHotArticle(){
        return articleService.hotArticles(LIMIT);
    }

    @PostMapping("/new")
    public ResultJSON newArticle(){
        return articleService.newArticles(LIMIT);
    }

    @PostMapping("/listArchives")
    public ResultJSON archives(){
        return articleService.archives();
    }

    @PostMapping("/view/{id}")
    public ResultJSON view(@PathVariable("id") Long id){
        return articleService.findArticleById(id);
    }

    @PostMapping("/publish")
    //加上此注解，代表要对此接口记录日志
    @LogAnnotation(module = "文章",operator="写文章")
    public ResultJSON publish(@RequestBody ArticleParams articleParams){
        return articleService.publish(articleParams);
    }
}
