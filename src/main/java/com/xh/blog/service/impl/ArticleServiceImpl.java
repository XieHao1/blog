package com.xh.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.xh.blog.dao.ArticleBodyDao;
import com.xh.blog.dao.ArticleDao;
import com.xh.blog.dao.ArticleTagDao;
import com.xh.blog.dao.CategoryDao;
import com.xh.blog.domain.*;
import com.xh.blog.service.*;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.util.ThreadLocalUtils;
import com.xh.blog.vo.*;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleDao articleDao;

    @Resource
    private TagService tagService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private ArticleBodyDao articleBodyDao;

    @Resource
    private ThreadService threadService;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Resource
    private ArticleTagDao articleTagDao;

    //使用分页插件+自定义动态sql修改查询文章列表
    @Override
    public ResultJSON listArticle(PageParams pageParams){
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        IPage<Article> iPage = articleDao.findArticles(
                page,
                pageParams.getCategoryId(),
                pageParams.getTagId(),
                pageParams.getYear(),
                pageParams.getMonth());
        List<Article> records = iPage.getRecords();
        return ResultJSON.success(copyList(records,true,true));
    }

//    @Override
//    public ResultJSON listArticle(PageParams pageParams) {
//         // 1.分页查询article表实现分页
//        IPage<Article> iPage = new Page<>(pageParams.getPage(), pageParams.getPageSize());
//        /**
//         * 带lambda的wrapper可以使用lambda表达式，选择列，设置列值，反之不带lambda的就需要手动指定列名
//         * LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
//         * queryWrapper.orderByDesc(Article::getCreateDate);
//         */
//        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
//        //表的字段名
//        //ORDER BY weight DESC,create_date DESC LIMIT ?
//        if(pageParams.getCategoryId()!=null){
//            //对原有查询文章接口的修改
//            queryWrapper.eq("category_id",pageParams.getCategoryId());
//        }
//        if(pageParams.getTagId()!=null){
//            List<Long> list = articleTagService.findArticleIdByTagId(pageParams.getTagId());
//            if(list.size()>0){
//                queryWrapper.in("id",list);
//            }
//        }
//        queryWrapper.orderByDesc("weight").orderByDesc("create_date");
//        IPage<Article> articleIPage = articleDao.selectPage(iPage, queryWrapper);
//        //将分页后的数据转化为list集合
//        List<Article> articlesList = articleIPage.getRecords();
//        //将domain中的数据转换为vo中的数据交给前端，防止直接与数据库字段进行交互
//        List<ArticleVo> articleVoList = copyList(articlesList,true,true);
//        return ResultJSON.success(articleVoList);
//    }

    @Override
    public ResultJSON hotArticles(int limit) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        //不要忘记limit中的空格
        queryWrapper.select("id","title").orderByDesc("view_counts").last("limit " + limit);
        List<Article> articleList = articleDao.selectList(queryWrapper);
        List<ArticleVo> hotArticleVoList = copyList(articleList,false,false);
        return ResultJSON.success(hotArticleVoList);
    }

    @Override
    public ResultJSON newArticles(int limit) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","title").orderByDesc("create_date").last("limit " + limit);
        List<Article> articleList = articleDao.selectList(queryWrapper);
        List<ArticleVo> newArticleVoList = copyList(articleList, false, false);
        return ResultJSON.success(newArticleVoList);
    }

    @Override
    public ResultJSON archives() {
        List<Archives> archivesList = articleDao.newArchives();
        return ResultJSON.success(archivesList);
    }

    @Override
    public ResultJSON findArticleById(Long id) {
        Article article = articleDao.selectById(id);
        ArticleVo articleVo = copy(article,true,true,true,true);
        threadService.updateArticleViewCount(articleDao,article);
        return ResultJSON.success(articleVo);
    }

    @Override
    @Transactional
    public ResultJSON publish(ArticleParams articleParams) {
        Article article = new Article();
        //从本地线程中获取用户的id信息
        String toke = ThreadLocalUtils.get();
        String s = redisTemplate.opsForValue().get("token_" + toke);
        SysUser sysUser = new Gson().fromJson(s, SysUser.class);
        article.setCommentCounts(0);
        article.setCreateDate(System.currentTimeMillis());
        article.setSummary(articleParams.getSummary().trim());
        article.setTitle(articleParams.getTitle().trim());
        article.setViewCounts(0);
        article.setWeight(0);
        article.setAuthorId(sysUser.getId());
        article.setBodyId(-1L);
        article.setCategoryId(articleParams.getCategory().getId());
        articleDao.insert(article);

        //body
        ArticleBody articleBody = new ArticleBody();
        articleBody.setContent(articleParams.getBody().getContent());
        articleBody.setContentHtml(articleParams.getBody().getContentHtml());
        articleBody.setArticleId(article.getId());
        articleBodyDao.insert(articleBody);

        //tags
        List<TagVo> tagVo = articleParams.getTags();
        if(tagVo!=null){
            for (TagVo tagVo1 : tagVo){
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(tagVo1.getId());
                articleTagDao.insert(articleTag);
            }
        }

        Article article1 = new Article();
        article1.setBodyId(articleBody.getId());
        UpdateWrapper<Article> articleUpdateWrapper = new UpdateWrapper<>();
        articleUpdateWrapper.eq("id",article.getId());
        articleDao.update(article1,articleUpdateWrapper);

        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(article.getId());
        return ResultJSON.success(articleVo);
    }

    private List<ArticleVo> copyList(List<Article> articlesList, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        //遍历list集合中的数据，将数据转换为vo类数据
        for(Article record : articlesList){
            articleVoList.add(copy(record,isTag,isAuthor,false,false));
        }
        return articleVoList;
    }

    private ArticleVo copy(Article article , boolean isTag, boolean isAuthor,boolean isArticleBody,
                           boolean isCategory){
        ArticleVo articleVo = new ArticleVo();
        /**
         * BeanUtils是由Apache公司所开发的工具包，
         * 目的是为了简化数据封装，方便Java程序员对JavaBean类进行简便的操作。
         * BeanUtils.copyProperties("转换前的类", "转换后的类");
         */
        BeanUtils.copyProperties(article,articleVo);
        //vo中时间存储为String类型，数据库中的事件为Long类型，需要进行转换
        articleVo.setCreateDate(new DateTime(articleVo.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //并不是所有的集合都需要标签和作者信息,需要经行判断
        if(isTag){
            Long articleID = article.getId();
            articleVo.setTags(tagService.findTagsByArticleID(articleID));
        }
        if(isAuthor){
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findSysUserNameByAuthorId(authorId).getNickname());
        }
        //以下代码对数据库的操作应该放在对应的service中进行
        if(isArticleBody){
            ArticleBodyVo articleBodyVo = new ArticleBodyVo();
            QueryWrapper<ArticleBody> wrapper = new QueryWrapper<>();
            wrapper.select("content").eq("article_id",article.getId());
            ArticleBody articleBody = articleBodyDao.selectOne(wrapper);
            BeanUtils.copyProperties(articleBody,articleBodyVo);
            articleVo.setBody(articleBodyVo);
        }
        if(isCategory){
            QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id","avatar","category_name").eq("id",article.getCategoryId());
            List<Category> categories = categoryDao.selectList(queryWrapper);
            articleVo.setCategorys(categories);
        }
        return articleVo;
    }
}
