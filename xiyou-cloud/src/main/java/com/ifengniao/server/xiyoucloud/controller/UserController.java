package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.UserEntity;
import com.ifengniao.server.xiyoucloud.dto.UserDTO;
import com.ifengniao.server.xiyoucloud.service.AuthorizationService;
import com.ifengniao.server.xiyoucloud.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户相关接口
 */
@Tag(description = "用户相关接口", name = "用户相关接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationService authorizationService;

    @Value("${application.systemConfig.mqttPageTopicPrefix}")
    private String mqttPageTopicPrefix;

    @Value("${application.systemConfig.mqttPageUrl}")
    private String mqttPageUrl;

    @Value("${application.systemConfig.mqttPageUsername}")
    private String mqttPageUsername;

    @Value("${application.systemConfig.mqttPagePassword}")
    private String mqttPagePassword;

    /**
     * 获取当前登录人 / 权限
     */
    @Operation(summary = "获取当前登录人/权限")
    @GetMapping("/loginer")
    public ResultMsg getLoginer() {
        return Resp.exec(() -> {
            var user = new UserDTO(authorizationService.getCurrentLogin());
            user.setMqttUrl(mqttPageUrl);
            user.setMqttUsername(mqttPageUsername);
            user.setMqttPassword(mqttPagePassword);
            user.setMqttTopicPrefix(mqttPageTopicPrefix);
            return user;
        });
    }

    /**
     * 获取用户简易列表（任务派发用）
     * 返回 user_name 列表
     */
    @Operation(summary = "获取用户简易列表")
    @GetMapping("/simple-list")
    public ResultMsg getUserSimpleList() {
        return Resp.exec(() -> {
            List<UserEntity> users = userService.findAll();
            return users.stream()
                    .map(UserEntity::getUserName) // ← 对应 xy_user.user_name
                    .collect(Collectors.toList());
        });
    }

}
