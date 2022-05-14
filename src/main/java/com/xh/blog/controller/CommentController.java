package com.xh.blog.controller;

import com.xh.blog.service.CommentService;
import com.xh.blog.util.ResultJSON;
import com.xh.blog.vo.CommentParams;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Resource
    private CommentService commentService;

    @GetMapping("/article/{id}")
    public ResultJSON article(@PathVariable("id") String id){
        return commentService.findCommentsById(id);
    }

    @PostMapping("/create/change")
    public ResultJSON comment(@RequestBody CommentParams commentParams){
        return commentService.insertComment(commentParams);
    }
}
