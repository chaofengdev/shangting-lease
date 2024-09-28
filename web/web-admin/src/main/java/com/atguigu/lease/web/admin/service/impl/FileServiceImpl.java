package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.minio.MinioProperties;
import com.atguigu.lease.web.admin.service.FileService;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioProperties properties;

    @Autowired
    private MinioClient client;

    @Override
    public String upload(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {//具体上传文件的方法。
        //判断bucket是否存在 这里的minio-api的设计是[建造器]模式，设计模式。
        boolean bucketExists = client.bucketExists(BucketExistsArgs.builder()
                .bucket(properties.getBucketName())
                .build());
        //如果bucket不存在，新建bucket
        if (!bucketExists) {
            //创建bucket
            client.makeBucket(MakeBucketArgs.builder()
                    .bucket(properties.getBucketName())
                    .build());
            //设置bucket访问属性
            client.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(properties.getBucketName())
                    .config(createBucketPolicyConfig(properties.getBucketName()))
                    .build());
        }
        //拼接文件名称
        String fileName = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        //将文件传入minio 这里其实就是minio的api，无需深究。
        client.putObject(PutObjectArgs.builder()
                .bucket(properties.getBucketName())
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
        //返回url 按照minio的图片访问路径规则拼接
        String url = String.join("/", properties.getEndpoint(), properties.getBucketName(), fileName);
        return url;
    }

    //辅助方法：封装了bucket访问权限的配置信息。
    //生成用于描述指定bucket访问权限的JSON字符串。最终生成的字符串格式如下，其表示，允许(`Allow`)所有人(`*`)获取(`s3:GetObject`)指定桶(`<bucket-name>`)的内容。
    private String createBucketPolicyConfig(String bucketName) {
        //具体规则，详见minio官网文档。
        return """
            {
              "Statement" : [ {
                "Action" : "s3:GetObject",
                "Effect" : "Allow",
                "Principal" : "*",
                "Resource" : "arn:aws:s3:::%s/*"
              } ],
              "Version" : "2012-10-17"
            }
            """.formatted(bucketName);
    }
}
