package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.BrowsingHistory;
import com.atguigu.lease.web.app.mapper.BrowsingHistoryMapper;
import com.atguigu.lease.web.app.service.BrowsingHistoryService;
import com.atguigu.lease.web.app.vo.history.HistoryItemVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author liubo
 * @description 针对表【browsing_history(浏览历史)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class BrowsingHistoryServiceImpl extends ServiceImpl<BrowsingHistoryMapper, BrowsingHistory>
        implements BrowsingHistoryService {

    @Autowired
    private BrowsingHistoryMapper browsingHistoryMapper;


    @Override
    public IPage<HistoryItemVo> pageHistoryItemByUserId(Page<HistoryItemVo> page, Long userId) {
        return browsingHistoryMapper.pageHistoryItemByUserId(page, userId);
    }


    /**
     * 根据用户id和房间id，保存浏览记录。
     * 这里的浏览记录即browsing_history表记录。
     * 保存浏览历史的动作不应影响前端获取房间详情信息，故此处采取异步操作。
     * @param userId
     * @param roomId
     */
    @Override
    @Async //在要进行异步处理的方法上添加注解，表示该方法是异步方法。
    public void saveHistory(Long userId, Long roomId) {
        LambdaQueryWrapper<BrowsingHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowsingHistory::getUserId, userId);
        queryWrapper.eq(BrowsingHistory::getRoomId, roomId);
        BrowsingHistory browsingHistory = browsingHistoryMapper.selectOne(queryWrapper);//查询浏览历史。

        //通常每个用户查看的每个房间，标记为一条浏览历史。所以要对浏览历史判断，如果是同一个房间，则更新时间，否则保存新的浏览记录。
        if (browsingHistory != null) {//已经有同一个房间的浏览记录。
            browsingHistory.setBrowseTime(new Date());//更新时间。
            browsingHistoryMapper.updateById(browsingHistory);//更新。
        } else {
            BrowsingHistory newBrowsingHistory = new BrowsingHistory();//新建浏览记录。
            newBrowsingHistory.setUserId(userId);
            newBrowsingHistory.setRoomId(roomId);
            newBrowsingHistory.setBrowseTime(new Date());//当前时间。
            browsingHistoryMapper.insert(newBrowsingHistory);//插入。
        }
    }
}