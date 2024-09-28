package com.atguigu.lease.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Schema(description = "房间&基本属性值关联表")
@TableName(value = "room_attr_value")
@Data
@Builder //lombok提供的简化构造器模式的实现。提供了链式方法来设置对象的属性，最后通过调用 build() 方法创建对象。
public class RoomAttrValue extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "房间id")
    @TableField(value = "room_id")
    private Long roomId;

    @Schema(description = "属性值id")
    @TableField(value = "attr_value_id")
    private Long attrValueId;
}