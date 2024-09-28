package com.atguigu.lease;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * 开始因为启动类放错位置了，导致：java.lang.ClassNotFoundException: io.r2dbc.spi.ValidationDepth
 * 分析了原因如下：
 * 1.Spring Boot 启动类通常使用 @SpringBootApplication 注解，该注解组合了三个功能：
 * @Configuration: 表明该类是一个配置类。
 * @EnableAutoConfiguration: 启用 Spring Boot 的自动配置功能。
 * @ComponentScan: 默认扫描启动类所在包及其子包中的组件（如 @Component、@Service、@Repository 和 @Controller）。
 * 如果启动类放在 java 目录下，Spring Boot 只会扫描 java 包及其子包，这样就会漏掉 java.atguigu.lease 包中的所有组件和配置，导致应用程序无法找到必要的类和资源。
 * 2.错误表明某个必要的类没有被找到。R2DBC 相关依赖没有被加载，因为相关的自动配置类未被扫描到。
 * 其他依赖未被正确加载，导致整个 Spring Boot 上下文无法正确初始化。
 * 总结：Spring Boot启动流程梳理，深入分析这个问题。
 */
@SpringBootApplication
//@MapperScan("com.atguigu.lease.web.*.mapper")
@EnableAsync //启用异步支持
public class AppWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppWebApplication.class);
    }
}
