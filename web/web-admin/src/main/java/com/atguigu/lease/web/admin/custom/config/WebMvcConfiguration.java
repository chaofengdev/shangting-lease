package com.atguigu.lease.web.admin.custom.config;

//import com.atguigu.lease.web.admin.custom.converter.StringToItemTypeConverter;
//import org.springframework.beans.factory.annotation.Autowired;
import com.atguigu.lease.web.admin.custom.converter.StringToBaseEnumConverterFactory;
import com.atguigu.lease.web.admin.custom.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
//import org.springframework.format.FormatterRegistry;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 修改Spring MVC默认墨汁。
 * 自定义 Spring MVC 的配置，特别是注册自定义的 类型转换器工厂 (ConverterFactory)，用于在请求参数绑定或其他场景中，将字符串类型转换为枚举类型。
 * WebMvcConfigurer 接口：实现这个接口可以自定义 Spring MVC 的一些默认配置，例如格式化器、拦截器、视图解析器等。通过实现 addFormatters() 方法，你可以自定义类型转换规则。
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

//    @Autowired
//    private StringToItemTypeConverter stringToItemTypeConverter;//自定义类型转换器。

//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverter(this.stringToItemTypeConverter);//添加自定义类型转换器。
//    }

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;//自定义拦截器

    @Autowired
    private StringToBaseEnumConverterFactory stringToBaseEnumConverterFactory;//自定义类型转换器工厂

    /**
     * 这个方法用来向 Spring MVC 的 FormatterRegistry 中注册自定义的类型转换器工厂。
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(this.stringToBaseEnumConverterFactory);
    }



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加自定义拦截器，配置拦截路径和非拦截路径。拦截web-admin下的所有请求，但是放行登录验证相关接口。
        registry.addInterceptor(this.authenticationInterceptor).addPathPatterns("/admin/**").excludePathPatterns("/admin/login/**");
    }
}
