package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BaseStationEntity;
import com.ifengniao.server.xiyoucloud.dto.BaseStationDTO;
import com.ifengniao.server.xiyoucloud.service.BaseStationService;
import com.ifengniao.server.xiyoucloud.util.CovertUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基站相关接口
 */
@Tag(description = "基站相关接口", name = "基站相关接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/baseStation")
@Slf4j
public class BaseStationController {

    @Autowired
    private BaseStationService baseStationService;

    /**
     * 分页查找基站信息
     *
     * @return
     */
    @Operation(summary = "分页查找基站信息")
    @GetMapping("/page")
    public ResultMsg page(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                          @RequestParam(value = "size", required = true, defaultValue = "20") Integer size,
                          @RequestParam(name = "mapId", required = false) Integer mapId,
                          @RequestParam(name = "mac", required = false) String mac,
                          @RequestParam(name = "ip", required = false) String ip,
                          @RequestParam(name = "online", required = false) Boolean online) {
        return Resp.exec(() -> getBaseStationByPage(page, size, mapId, mac, ip, online));
    }

    private Page<BaseStationDTO> getBaseStationByPage(Integer page, Integer size, Integer mapId, String mac, String ip, Boolean online) {
        if (StringUtils.isNotBlank(mac)) {
            mac = CovertUtil.toNormalMac(mac);
        }
        Page<BaseStationEntity> baseStationByPage = baseStationService.getBaseStationByPage(page, size, mapId, mac, ip, online);
        return new PageImpl<>(BaseStationDTO.covert(baseStationByPage.getContent()), baseStationByPage.getPageable(), baseStationByPage.getTotalElements());
    }

}
