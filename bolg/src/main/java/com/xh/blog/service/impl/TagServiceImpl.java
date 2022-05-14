package com.xh.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xh.blog.dao.TagDao;
import com.xh.blog.domain.Tag;
import com.xh.blog.service.TagService;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagDao tagDao;

    @Override
    public List<TagVo> findTagsByArticleID(Long articleID) {
        List<Tag> tagByArticleIdList = tagDao.findTagByArticleId(articleID);
        return copyList(tagByArticleIdList);
    }

    @Override
    /** 最热标签：
     * 1.标签所拥有的文章数量最多
     * 2.查询 根据tag_id进行分组技术，从大到小排序
     */
    public List<TagVo> hots(int limit) {
        //若一条sql语句过长，可以将其分开进行查询
        List<Long> tagIdList = tagDao.findTagsHot(limit);
        //List<Tag> tagList = tagDao.selectBatchIds(tagIdList);
        List<Tag> tagList = tagDao.findTagsHotNameById(tagIdList);
        return copyList(tagList);
    }

    @Override
    public ResultJSON findAllTags() {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","tag_name");
        List<Tag> tagList = tagDao.selectList(queryWrapper);
        return ResultJSON.success(tagList);
    }

    @Override
    public ResultJSON findDetail() {
        List<Tag> tagList = tagDao.selectList(new QueryWrapper<>());
        return ResultJSON.success(tagList);
    }

    @Override
    public ResultJSON findTagsByID(String id) {
        Tag tag = tagDao.selectById(id);
        return ResultJSON.success(tag);
    }

    private List<TagVo> copyList(List<Tag> tagByArticleIdList) {
        List<TagVo> tagVoList = new ArrayList<>();
        for(Tag record : tagByArticleIdList){
            tagVoList.add(copy(record));
        }
        return tagVoList;
    }

    private TagVo copy(Tag record) {
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(record,tagVo);
        return tagVo;
    }
}
