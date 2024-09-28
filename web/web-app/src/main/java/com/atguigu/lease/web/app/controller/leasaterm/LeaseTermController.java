package com.atguigu.lease.web.app.controller.leasaterm;

import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.LeaseTerm;
import com.atguigu.lease.web.app.service.LeaseTermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app/term/")
@Tag(name = "租期信息")
public class LeaseTermController {

    @Autowired
    private LeaseTermService service;

    @GetMapping("listByRoomId")
    @Operation(summary = "根据房间id获取可选获取租期列表")
    public Result<List<LeaseTerm>> list(@RequestParam Long id) {
        //注意，这里是用在个人中心-租约管理的接口，负责新增修改租约时选择租期。
        List<LeaseTerm> list = service.listByRoomId(id);
        return Result.ok(list);
    }
}
