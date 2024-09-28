package com.atguigu.lease.common.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * yml文件中aliyun.sms配置想的映射类。
 */
@ConfigurationProperties(prefix = "aliyun.sms")
@Data
public class AliyunSMSProperties {

    private String accessKeyId;

    private String accessKeySecret;

    private String endpoint;
}
