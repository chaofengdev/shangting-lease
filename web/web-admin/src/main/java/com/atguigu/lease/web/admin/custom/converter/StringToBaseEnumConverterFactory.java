package com.atguigu.lease.web.admin.custom.converter;

import com.atguigu.lease.model.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

/**
 * 自定义一个工厂，用于将字符串转换为枚举类型。
 * 它实现了 Spring 的 ConverterFactory<String, BaseEnum> 接口，将来自前端传入的字符串转换为实现了 BaseEnum 接口的枚举类型。
 */
@Component
public class StringToBaseEnumConverterFactory implements ConverterFactory<String, BaseEnum> {//自定义`ConverterFactory`，集中编写各枚举类的转换逻辑

    /**
     * 这个方法用于返回一个具体的 Converter<String, T>，即一个将 String 转换为目标枚举类型 T 的转换器。
     * @param targetType the target type to convert to
     * @return
     * @param <T>
     */
    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {  //目标枚举类型：targetType。具体的工厂方法。
        /**
         * 具体的转换逻辑，前端传入的字符串转换为目标枚举类的枚举常量。
         * 具体逻辑：通过 targetType.getEnumConstants() 获取目标枚举类的所有枚举常量，然后遍历这些常量，
         *          查找其 getCode() 方法返回的值是否与前端传入的字符串（转换为整数）匹配。
         */
        return new Converter<String, T>() {
            @Override
            public T convert(String source) {//前端传入的字符串

                for (T enumConstant : targetType.getEnumConstants()) {
                    if (enumConstant.getCode().equals(Integer.valueOf(source))) {
                        return enumConstant;
                    }
                }
                throw new IllegalArgumentException("非法的枚举值:" + source);
            }
        };
    }
}
