package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.PaymentType;
import com.atguigu.lease.web.admin.service.PaymentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "支付方式管理")
@RequestMapping("/admin/payment")
@RestController //序列化结果对象为json字符串，返回给前端。默认是jackson实现。
public class PaymentTypeController {

    @Autowired
    private PaymentTypeService paymentTypeService;
    @Operation(summary = "查询全部支付方式列表")
    @GetMapping("list")
    public Result<List<PaymentType>> listPaymentType() {
        //查询时，需要限制is_deleted字段为0，同时返回对象后序列化时忽略特定字段。
        //两者都可以使用注解解决，前者在实体类字段上添加@TableLogic，后者在实体类字段上添加@JsonIgnore
        List<PaymentType> list = paymentTypeService.list();//查出所有PaymentType记录。
        return Result.ok(list);//返回给前端。
    }

    @Operation(summary = "保存或更新支付方式")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdatePaymentType(@RequestBody PaymentType paymentType) {
        //保存和更新时注意，前端通常不会传入create_time、update_time、is_deleted，
        //但数据表有此字段，手动赋值比较繁琐，所以采取以下措施：
        //is_deleted字段：插入数据库，默认值为0，表示未删除
        //create_time/update_time字段：`@TableField`注解和实现MetaObjectHandler的配置类，自动为某些字段赋值。
        //我个人觉得，和框架强相关并不是一个好事，可能方便了大厂的稳定开发，但是缺乏迁移性。被框架绑定太深。
        boolean flag = paymentTypeService.saveOrUpdate(paymentType);//保存或更新
        return Result.ok();
    }

    @Operation(summary = "根据ID删除支付方式")
    @DeleteMapping("deleteById")
    public Result deletePaymentById(@RequestParam Long id) {
        //这里是逻辑删除，mybatis plus框架实际执行的是更新操作，将is_delete字段更新为1，表示删除。
        //注意：这里没有校验id是否存在，导致删除不在的id也能返回ok。改进！
        boolean flag = paymentTypeService.removeById(id);
        return Result.ok();
    }

}















