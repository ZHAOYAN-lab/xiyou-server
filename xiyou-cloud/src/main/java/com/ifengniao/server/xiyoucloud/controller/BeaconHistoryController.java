package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.db.influxdb.service.BeaconHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 历史轨迹相关接口
 */
@Tag(description = "历史轨迹相关接口", name = "历史轨迹相关接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/beaconHistory")
@Slf4j
public class BeaconHistoryController {

    @Autowired
    private BeaconHistoryService beaconHistoryService;

    /**
     * 获取历史轨迹
     *
     * @return
     */
    @Operation(summary = "获取历史轨迹")
    @GetMapping("/find")
    public ResultMsg find(@RequestParam(name = "mapId", required = true) String mapId,
                          @RequestParam(name = "locationObjectId", required = false) String locationObjectId,
                          @RequestParam(name = "start", required = true) Long start,
                          @RequestParam(name = "end", required = true) Long end) {
        return Resp.exec(() -> beaconHistoryService.queryBeaconHistory(mapId, locationObjectId, start, end));
    }

}
