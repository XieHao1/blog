package com.xh.blog.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xh.blog.dao.ArticleDao;
import com.xh.blog.domain.Article;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Async("taskExecutor")
public class ThreadService {
    //更新最后登录时间
    public static void updateLastLoginTime(SysUserService sysUserService,Long id) {
       sysUserService.updateLastLoginTime(id);
    }
    //希望在此处开启线程池执行，不会影响原有的主线程
    public void updateArticleViewCount(ArticleDao articleDao, Article article){
        Integer viewCounts = article.getViewCounts();
        //创建一个新的对象，使更新的数据最少
        Article updateArticleViewCount = new Article();
        updateArticleViewCount.setViewCounts(viewCounts+1);
        UpdateWrapper<Article> articleUpdateWrapper = new UpdateWrapper<>();
        articleUpdateWrapper.eq("id",article.getId());
        //在设置一个，为了保证在多线程的情况下线程安全
        //防止进行错误的更新，加两个判断条件
        articleUpdateWrapper.eq("view_counts",viewCounts);
        //sql: update xh_article set view_counts = 100 where id = ? and view_counts = 99
        articleDao.update(updateArticleViewCount,articleUpdateWrapper);
    }
}
