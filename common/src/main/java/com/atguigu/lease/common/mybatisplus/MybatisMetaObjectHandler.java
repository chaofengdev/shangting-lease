package com.atguigu.lease.common.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

//配置自动填充的内容：插入数据或写入数据时，create_time和update_time的字段不需要前端传入，自动填充。
//当写入数据时，Mybatis-Plus会自动将实体对象的`create_time`字段填充为当前时间，
//当更新数据时，则会自动将实体对象的`update_time`字段填充为当前时间。
@Component
public class MybatisMetaObjectHandler implements MetaObjectHandler {
    @Override //配置自动填充的内容
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
    }

    @Override //配置自动填充的内容
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}
