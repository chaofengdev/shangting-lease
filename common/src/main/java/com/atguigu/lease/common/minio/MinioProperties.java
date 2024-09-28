package com.atguigu.lease.common.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * minio配置项对应的配置类，使得yml配置项和业务代码解耦。
 */
@ConfigurationProperties(prefix = "minio") //标记是属性映射类
@Data
public class MinioProperties {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;
}
