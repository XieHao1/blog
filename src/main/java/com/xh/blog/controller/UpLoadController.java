package com.xh.blog.controller;

import com.xh.blog.dictionary.ErrorEnum;
import com.xh.blog.util.QINiuYunUtils;
import com.xh.blog.util.ResultJSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UpLoadController {

    @Resource
    private QINiuYunUtils qiNiuYunUtils;

    @PostMapping()
    //@RequestParam接收文件的注解
    public ResultJSON upload(@RequestParam("image") MultipartFile multipartFile){
        //MultipartFile接口是由springMVC提供的一个接口，为我们包装了获取文件类型的数据
        //可以用来接受任何类型的文件

        //获取文件的原始文件名称
        //不同浏览器获取的文件名可能不同：
        //可能包含路径信息，也可能只是文件名取决于所使用的浏览器。
        String originalFilename = multipartFile.getOriginalFilename();

        //在上传文件时不能使用原始文件名称，避免重复
        String fileName =
                UUID.randomUUID().toString() + "." +
                        //获取文件的后缀名
                        StringUtils.substringAfterLast(originalFilename,".");
        //将图片上传到七牛云服务器中
        //一般采用字节流上传
        boolean upload = qiNiuYunUtils.upload(multipartFile, fileName);
        if(upload){
            //返回全路径+文件名
            return ResultJSON.success(QINiuYunUtils.URL+fileName);
        }
        return ResultJSON.fail(ErrorEnum.FILE_UPLOAD_ERROR.getCode(), ErrorEnum.FILE_UPLOAD_ERROR.getMsg());
    }
}
