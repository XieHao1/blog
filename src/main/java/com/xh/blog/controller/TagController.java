package com.xh.blog.controller;

import com.xh.blog.service.TagService;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.TagVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final static int LIMIT = 6;

    @Resource
    private TagService tagService;

    @GetMapping("/hot")
    public ResultJSON listHotTags(){
        List<TagVo> tagHotList = tagService.hots(LIMIT);
        return ResultJSON.success(tagHotList);
    }

    @GetMapping()
    public ResultJSON allTags(){
        return tagService.findAllTags();
    }

    @GetMapping("/detail")
    public ResultJSON findDetail(){
        return tagService.findDetail();
    }

    @GetMapping("/detail/{id}")
    public ResultJSON DetailById(@PathVariable("id") String id){
        return tagService.findTagsByID(id);
    }
}
