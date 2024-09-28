package com.atguigu.lease.model.enums;

// 所有枚举类型都实现了此接口，用来统一String到具体枚举类转换的逻辑。
// 具体参考spring官方文档：converter相关。
public interface BaseEnum {

    Integer getCode();

    String getName();
}
