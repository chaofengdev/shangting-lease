package com.atguigu.lease.common.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioProperties.class) //扫描配置映射类，注入到spring。
@ConditionalOnProperty(name = "minio.endpoint") //条件注解，只有当`minio.endpoint`属性存在时，该配置类才会生效。
public class MinioConfiguration {

    @Autowired
    private MinioProperties properties;

    //利用配置信息类获得MinioClient对象，并注入到spring中。
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey()).build();
    }
}
