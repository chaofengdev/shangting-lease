package com.atguigu.lease.web.admin.custom.converter;

import com.atguigu.lease.model.enums.ItemType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

// @Component
//public class StringToItemTypeConverter implements Converter<String, ItemType> {//自定义的类型转换器。
//    @Override
//    //用来将请求中的 String 类型的值转换为 ItemType 类型
//    public ItemType convert(String code) {//定义前端传入的code与type具体转换关系。
//
//        for (ItemType value : ItemType.values()) {
//            if (value.getCode().equals(Integer.valueOf(code))) {
//                return value;//返回匹配的type
//            }
//        }
//        throw new IllegalArgumentException("code非法");
//    }
//}
