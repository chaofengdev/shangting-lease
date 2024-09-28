package com.atguigu.lease.web.admin.controller.system;

import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.SystemPost;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.admin.service.SystemPostService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Tag(name = "后台用户岗位管理")
@RequestMapping("/admin/system/post")
public class SystemPostController {

    @Autowired
    private SystemPostService systemPostService;

    @Operation(summary = "分页获取岗位信息")
    @GetMapping("page")
    private Result<IPage<SystemPost>> page(@RequestParam long current, @RequestParam long size) {
        //只涉及到单表，这里都比较简单。直接使用mybatis plus提供的接口就可以了。
        IPage<SystemPost> page = new Page<>(current, size);
        IPage<SystemPost> systemPostPage = systemPostService.page(page);
        return Result.ok(systemPostPage);
    }

    @Operation(summary = "保存或更新岗位信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody SystemPost systemPost) {
        //只涉及到单表，这里都比较简单。直接使用mybatis plus提供的接口就可以了。
        systemPostService.saveOrUpdate(systemPost);
        return Result.ok();
    }

    @DeleteMapping("deleteById")
    @Operation(summary = "根据id删除岗位")
    public Result removeById(@RequestParam Long id) {
        //只涉及到单表，这里都比较简单。直接使用mybatis plus提供的接口就可以了。
        systemPostService.removeById(id);
        return Result.ok();
    }

    @GetMapping("getById")
    @Operation(summary = "根据id获取岗位信息")
    public Result<SystemPost> getById(@RequestParam Long id) {
        //只涉及到单表，这里都比较简单。直接使用mybatis plus提供的接口就可以了。
        systemPostService.removeById(id);
        return Result.ok();
    }

    @Operation(summary = "获取全部岗位列表")
    @GetMapping("list")
    public Result<List<SystemPost>> list() {
        //只涉及到单表，这里都比较简单。直接使用mybatis plus提供的接口就可以了。
        List<SystemPost> list = systemPostService.list();
        return Result.ok(list);
    }

    @Operation(summary = "根据岗位id修改状态")
    @PostMapping("updateStatusByPostId")
    public Result updateStatusByPostId(@RequestParam Long id, @RequestParam BaseStatus status) {
        //只涉及到单表，这里都比较简单。直接使用mybatis plus提供的接口就可以了。
        SystemPost systemPost = systemPostService.getById(id);
        return Result.ok(systemPost);
    }
}
