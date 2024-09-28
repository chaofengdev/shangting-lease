package com.atguigu.lease.web.app.controller.apartment;

import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentDetailVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "公寓信息")
@RequestMapping("/app/apartment")
public class ApartmentController {


    @Autowired
    private ApartmentInfoService apartmentInfoService;


    @Operation(summary = "根据id获取公寓信息")
    @GetMapping("getDetailById")
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        //公寓详情页面有两个接口，其中一个是根据id查询所有房间信息，另外一个就是根据id查询公寓信息，
        //这里的公寓信息是ApartmentDetailVo，内容稍微多一点，所以单独写个接口来查询。
        ApartmentDetailVo apartmentDetailVo = apartmentInfoService.getApartmentDetailById(id);
        return Result.ok(apartmentDetailVo);
    }
}
