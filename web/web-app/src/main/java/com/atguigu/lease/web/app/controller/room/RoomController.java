package com.atguigu.lease.web.app.controller.room;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.web.app.service.RoomInfoService;
import com.atguigu.lease.web.app.vo.room.RoomDetailVo;
import com.atguigu.lease.web.app.vo.room.RoomItemVo;
import com.atguigu.lease.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "房间信息")
@RestController
@RequestMapping("/app/room")
public class RoomController {


    @Autowired
    private RoomInfoService roomInfoService;



    @Operation(summary = "分页查询房间列表")
    @GetMapping("pageItem")
    public Result<IPage<RoomItemVo>> pageItem(@RequestParam long current, @RequestParam long size, RoomQueryVo queryVo) {
        //分页查询，因为查询条件RoomQueryVo众多，且返回结果RoomItemVo字段有大量列表，注意sql书写，本项目最难的sql！！！
        //写这中代码，务必梳理清楚请求和响应的数据结构。
        Page<RoomItemVo> page = new Page<>(current, size);
        IPage<RoomItemVo> list = roomInfoService.pageRoomItemByQuery(page, queryVo);
        return Result.ok(list);
    }

    @Operation(summary = "根据id获取房间的详细信息")
    @GetMapping("getDetailById")
    public Result<RoomDetailVo> getDetailById(@RequestParam Long id) {
        //响应数据结构是RoomDetailVo，内容挺多的，但是都比较简单，因为是一条root_info的相关记录，直接在service中写即可。
        RoomDetailVo roomInfo = roomInfoService.getDetailById(id);
        return Result.ok(roomInfo);
    }

    @Operation(summary = "根据公寓id分页查询房间列表")
    @GetMapping("pageItemByApartmentId")
    public Result<IPage<RoomItemVo>> pageItemByApartmentId(@RequestParam long current, @RequestParam long size, @RequestParam Long id) {
        //专业浏览房间，找到一个合适的房间，在房间里看到该房间所在公寓，然后再从公寓里找到该公寓的所有房间？是个合理的需求。
        IPage<RoomItemVo> page = new Page<>(current, size);
        IPage<RoomItemVo> result = roomInfoService.pageItemByApartmentId(page, id);
        return Result.ok(result);
    }
}
