package com.xh.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.xh.blog.dao.CommentDao;
import com.xh.blog.dictionary.ErrorEnum;
import com.xh.blog.domain.Comment;
import com.xh.blog.domain.SysUser;
import com.xh.blog.service.CommentService;
import com.xh.blog.service.SysUserService;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.util.ThreadLocalUtils;
import com.xh.blog.vo.CommentParams;
import com.xh.blog.vo.CommentVo;
import com.xh.blog.vo.UserVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentDao commentDao;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public ResultJSON findCommentsById(String id) {
        //1.根据文章id查询评论列表 从comment表中查询
        //2.通过comment表中的作者id查询作者的信息
        //3.判断level=1 要去查询它有没有子评论
        //4.如果有 根据parent_id进行查询
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id",id);
        //等级为1的是主评论
        queryWrapper.eq("level",1);
        List<Comment> commentList = commentDao.selectList(queryWrapper);
        List<CommentVo> commentVoList = copyList(commentList);
        return ResultJSON.success(commentVoList);
    }

    @Override
    public ResultJSON insertComment(CommentParams commentParams) {
        Long parent = commentParams.getParent();
        Long toUserId = commentParams.getToUserId();
        //从本地获取用户的信息
        String token = ThreadLocalUtils.get();
        //从redis中获取用户的信息
        String s = redisTemplate.opsForValue().get("token_" + token);
        SysUser sysUser = new Gson().fromJson(s, SysUser.class);
        Comment comment = new Comment();
        comment.setCreateDate(System.currentTimeMillis());
        comment.setContent(commentParams.getContent().trim());
        comment.setArticleId(commentParams.getArticleId());
        comment.setAuthorId(sysUser.getId());
        if(parent == null || parent ==0){
            comment.setLevel("1");
        }else {
            comment.setLevel("2");
        }
        comment.setParentId(parent==null ? 0 : parent);
        comment.setToUid(toUserId==null ? 0 : toUserId);
        int insert = commentDao.insert(comment);
        if(insert!=1){
            return ResultJSON.fail(ErrorEnum.COMMENT_ERROR.getCode(),ErrorEnum.COMMENT_ERROR.getMsg());
        }
        return ResultJSON.success(null);
    }

    private List<CommentVo> copyList(List<Comment> commentList) {
        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comment comment : commentList){
            commentVoList.add(copy(comment));
        }
        return commentVoList;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        //将时间从long转换为String
        commentVo.setCreateDate(new DateTime(comment.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //作者信息
        UserVo userVo = sysUserService.findUserVoByAuthorId(comment.getAuthorId());
        commentVo.setAuthor(userVo);
        //子评论 level == 1 时有子评论,通过parentID查询子评论
        //level == 2 没有子评论
        String level = comment.getLevel();
        if("1".equals(level)){
            Long id = comment.getId();
            //找与主评论有相同id的parent_id查询子评论
            List<CommentVo> commentVoList = findCommentsByParentId(id);
            commentVo.setChildrens(commentVoList);

        }
        //to User 查询子评论是给谁评论的
        if("2".equals(level)){
            Long id = comment.getToUid();
            UserVo userVoByToUid = sysUserService.findUserVoByAuthorId(id);
            commentVo.setToUser(userVoByToUid);
        }
        return commentVo;
    }

    private List<CommentVo> findCommentsByParentId(Long id) {
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        //查询子评论 当level == 2时没有子评论----子评论的等级都是2
        commentQueryWrapper.eq("parent_id",id).eq("level",2);
        List<Comment> comments = commentDao.selectList(commentQueryWrapper);
        //递归查询 子评论的等级只能为2，所有不会无限查询下去
        return copyList(comments);
    }
}
