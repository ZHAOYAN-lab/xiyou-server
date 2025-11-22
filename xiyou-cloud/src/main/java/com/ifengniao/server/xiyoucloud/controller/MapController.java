package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.BaseException;
import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.dto.MapDTO;
import com.ifengniao.server.xiyoucloud.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 地图相关接口
 */
@Tag(description = "地图相关接口", name = "地图相关接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/map")
@Slf4j
public class MapController {

    @Autowired
    private MapService mapService;

    /**
     * 按mapId获取地图及其下基站信息
     *
     * @return
     */
    @Operation(summary = "按mapId获取地图及其下基站信息")
    @GetMapping("/id")
    public ResultMsg id(@RequestParam(value = "mapId") Integer mapId) {
        return Resp.exec(() -> {
            var map = mapService.findById(mapId);
            if (map == null) {
                throw new BaseException("地图信息未找到").setStrCode(Constants.ERR_1200);
            }
            return new MapDTO(map);
        });
    }

    /**
     * 获取地图信息树形结构
     *
     * @return
     */
    @Operation(summary = "获取地图信息树形结构")
    @GetMapping("/tree")
    public ResultMsg tree() {
        return Resp.exec(mapService::findMapTree);
    }

}
