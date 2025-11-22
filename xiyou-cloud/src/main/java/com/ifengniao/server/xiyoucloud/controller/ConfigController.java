package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.ConfigEntity;
import com.ifengniao.server.xiyoucloud.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置相关接口
 */
@Tag(description = "配置相关接口", name = "配置相关接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/config")
@Slf4j
public class ConfigController {

    @Autowired
    private ConfigService configService;

    /**
     * 获取各个配置项
     *
     * @return
     */
    @Operation(summary = "获取各个配置项，configType为配置类型，configValues为配置值，两个都不传时查全部，只传configType时查一类，两个都传时查单项")
    @GetMapping("/find")
    public ResultMsg find(@RequestParam(name = "configType", required = false) List<String> configType,
                          @RequestParam(name = "configValues", required = false) Integer configValues) {
        return Resp.exec(() -> {
            List<ConfigEntity> configList = configService.findByParams(configType, configValues);
            return configList.stream().collect(Collectors.groupingBy(ConfigEntity::getConfigType));
        });
    }

}
