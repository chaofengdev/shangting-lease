package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.enums.ReleaseStatus;
import com.atguigu.lease.web.admin.service.ApartmentInfoService;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "公寓信息管理")
@RestController
@RequestMapping("/admin/apartment")
public class ApartmentController {

    @Autowired
    private ApartmentInfoService service;

    @Operation(summary = "保存或更新公寓信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody ApartmentSubmitVo apartmentSubmitVo) {
        //传入的apartmentSubmitVo对象比较复杂，需要在service层中仔细些新增或更新的操作。
        service.saveOrUpdateApartment(apartmentSubmitVo);//具体逻辑写在ApartmentInfoService实现类中。
        return Result.ok();
    }

    @Operation(summary = "根据条件分页查询公寓列表")
    @GetMapping("pageItem")
    public Result<IPage<ApartmentItemVo>> pageItem(@RequestParam long current, @RequestParam long size, ApartmentQueryVo queryVo) {
        //新建page对象，泛型为ApartmentItemVo，对应前端表记录。
        IPage<ApartmentItemVo> page = new Page<>(current, size);
        //调用service的pageApartmentItemByQuery方法，传入分页信息和地址信息，查询符合条件的对象列表
        IPage<ApartmentItemVo> list = service.pageApartmentItemByQuery(page, queryVo);//分页信息、地址信息
        //返回
        return Result.ok(list);
    }

    @Operation(summary = "根据ID获取公寓详细信息")
    @GetMapping("getDetailById")
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        //在service层中拼接apartmentDetailVo对象。
        ApartmentDetailVo apartmentDetailVo = service.getApartmentDetailById(id);
        return Result.ok(apartmentDetailVo);
    }

    @Operation(summary = "根据id删除公寓信息")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id) {
        service.removeApartmentById(id);
        return Result.ok();
    }

    @Operation(summary = "根据id修改公寓发布状态")
    @PostMapping("updateReleaseStatusById")
    public Result updateReleaseStatusById(@RequestParam Long id, @RequestParam ReleaseStatus status) {
        //根据公寓id找到apartmentInfo，并修改is_release
        LambdaUpdateWrapper<ApartmentInfo> apartmentInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        apartmentInfoLambdaUpdateWrapper.eq(ApartmentInfo::getId, id);
        apartmentInfoLambdaUpdateWrapper.set(ApartmentInfo::getIsRelease, status);//条件里不要忘记更新字段
        service.update(apartmentInfoLambdaUpdateWrapper);
        return Result.ok();
    }

    @Operation(summary = "根据区县id查询公寓信息列表")
    @GetMapping("listInfoByDistrictId")
    public Result<List<ApartmentInfo>> listInfoByDistrictId(@RequestParam Long id) {
        //这里是在房间管理页面用到的，用来从区县id查询公寓信息，只需要apartmentInfo表就可以了。很简单。
        LambdaQueryWrapper<ApartmentInfo> apartmentInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apartmentInfoLambdaQueryWrapper.eq(ApartmentInfo::getDistrictId, id);//idea很只能，可以直接补充。
        List<ApartmentInfo> list = service.list(apartmentInfoLambdaQueryWrapper);
        return Result.ok(list);
    }
}














