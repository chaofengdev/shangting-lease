package com.atguigu.lease.common.sms;

import com.aliyun.dysmsapi20170525.Client;
//import com.aliyun.teaopenapi.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AliyunSMSProperties.class) //扫描配置映射类，注入到spring。
@ConditionalOnProperty(name = "aliyun.sms.endpoint") //条件注解，只有当`aliyun.sms.endpoint`属性存在时，该配置类才会生效。
public class AliyunSmsConfiguration {

    @Autowired
    private AliyunSMSProperties properties;


    /**
     * 获取properties保存的yml属性，利用阿里云api，构建发送短信的Client对象。
     * @return
     */
    @Bean
    public Client smsClient() {
        //阿里云提供的api，配置相关信息，返回客户端对象。
        //官方称这里为配置请求客户端的过程。
        Config config = new Config();
        config.setAccessKeyId(properties.getAccessKeyId());
        config.setAccessKeySecret(properties.getAccessKeySecret());
        config.setEndpoint(properties.getEndpoint());
        try {
            return new Client(config);//不要导错Client包，查看官方演示代码。
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
