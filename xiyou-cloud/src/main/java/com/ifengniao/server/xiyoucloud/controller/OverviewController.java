package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.service.OverviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 概览相关接口
 */
@Tag(description = "概览相关接口", name = "概览相关接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/overview")
@Slf4j
public class OverviewController {

    @Autowired
    private OverviewService overviewService;

    /**
     * 定位对象相关图表：定位概览（人员/物品/设备/车辆）/在线对象分布/基站状态/信标状态
     *
     * @return
     */
    @Operation(summary = "定位对象相关图表：定位概览（人员/物品/设备/车辆）/在线对象分布/基站状态/信标状态")
    @GetMapping("/locationObjectAndDeviceGroup")
    public ResultMsg locationObjectAndDeviceGroup() {
        return Resp.exec(() -> overviewService.locationObjectAndDeviceGroup());
    }

    /**
     * 今日警告/今日告警类别占比/24小时告警变化
     *
     * @return
     */
    @Operation(summary = "今日警告/今日告警类别占比/24小时告警变化")
    @GetMapping("/alarmGroup")
    public ResultMsg alarmGroup() {
        return Resp.exec(() -> overviewService.alarmGroup());
    }

}
