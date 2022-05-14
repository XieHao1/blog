package com.xh.blog.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
//使用@Component注解来将密钥注入
public class QINiuYunUtils {

    //域名 注意在URL最后加“/”方便拼接全路径
    public static final String URL = "http://r86cjajtz.hn-bkt.clouddn.com/";

    //...生成上传凭证，然后准备上传
    //accessKey,secretKey来自七牛云的密钥管理
    //将密钥信息放入配置文件中
    @Value("${qiniuyun.accessKey}")
    private String accessKey;
    @Value("${qiniuyun.secretKey}")
    private String secretKey;
    //上传到七牛云的那个空间中
    @Value("${qiniuyun.bucket}")
    private String bucket;

    public boolean upload(MultipartFile multipartFile,String fileName) {
        //构造一个带指定 Region 对象的配置类
        //Region.region2()根据地区选择，详情看七牛云官方文档
        Configuration cfg = new Configuration(Region.region2());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        //String localFilePath = "/home/qiniu/test.png";
        try {
            byte[] uploadBytes = multipartFile.getBytes();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            //默认不指定key的情况下，以文件内容的hash值作为文件名
            //String key = null ---- fileName;
            Response response = uploadManager.put(uploadBytes, fileName, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            //上传成功返回true
            return true;
        } catch (QiniuException ex) {
            Response r = ex.response;
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
