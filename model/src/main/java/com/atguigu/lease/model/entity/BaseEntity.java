package com.atguigu.lease.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT) //自动填充字段，配置时机，插入数据时填充。
    @JsonIgnore //标记序列化被忽略的字段
    private Date createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.UPDATE) //自动填充字段，配置时机，更新数据时填充
    @JsonIgnore //标记序列化被忽略的字段
    private Date updateTime;

    @Schema(description = "逻辑删除")
    @TableField("is_deleted")
    @TableLogic //标记逻辑删除字段，使用MyBatis Plus的删除方法时，逻辑删除会自动生效。会将该字段更新为1，而不是实际删除记录。
    @JsonIgnore //标记序列化被忽略的字段
    private Byte isDeleted;

}