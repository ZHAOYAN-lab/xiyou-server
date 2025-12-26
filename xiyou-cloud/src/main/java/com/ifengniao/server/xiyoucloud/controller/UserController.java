package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.BaseException;
import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.UserEntity;
import com.ifengniao.server.xiyoucloud.dto.AccountCreateParam;
import com.ifengniao.server.xiyoucloud.dto.AccountDTO;
import com.ifengniao.server.xiyoucloud.dto.UserDTO;
import com.ifengniao.server.xiyoucloud.service.AuthorizationService;
import com.ifengniao.server.xiyoucloud.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.Date;
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

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

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

    /**
     * 账号管理列表
     */
    @Operation(summary = "账号管理列表")
    @GetMapping("/account/list")
    public ResultMsg getAccountList(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String accountName) {
        return Resp.exec(() -> {
            List<UserEntity> users = userService.findAll();
            return users.stream()
                    .filter(user -> StringUtils.isBlank(department)
                            || department.equals(user.getUserDepartment()))
                    .filter(user -> StringUtils.isBlank(level)
                            || level.equals(user.getUserLevel()))
                    .filter(user -> StringUtils.isBlank(employeeName)
                            || StringUtils.contains(user.getEmployeeName(), employeeName))
                    .filter(user -> StringUtils.isBlank(accountName)
                            || StringUtils.contains(user.getUserName(), accountName))
                    .sorted(Comparator.comparing(UserEntity::getCreateTime,
                            Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                    .map(AccountDTO::new)
                    .collect(Collectors.toList());
        });
    }

    /**
     * 新增账号
     */
    @Operation(summary = "新增账号")
    @PostMapping("/account/add")
    public ResultMsg addAccount(@RequestBody AccountCreateParam param) {
        return Resp.exec(() -> {
            if (StringUtils.isBlank(param.getAccountName())
                    || StringUtils.isBlank(param.getPassword())
                    || StringUtils.isBlank(param.getDepartment())
                    || StringUtils.isBlank(param.getLevel())
                    || StringUtils.isBlank(param.getEmployeeName())) {
                throw new BaseException("参数缺失").setStrCode(Constants.ERR_F200);
            }

            UserEntity existing = userService.findByUserName(param.getAccountName());
            if (existing != null) {
                throw new BaseException("账号已存在").setStrCode(Constants.ERR_FFFF);
            }

            UserEntity user = new UserEntity()
                    .setUserName(param.getAccountName())
                    .setUserPass(passwordEncoder.encode(param.getPassword()))
                    .setUserDepartment(param.getDepartment())
                    .setUserLevel(param.getLevel())
                    .setEmployeeName(param.getEmployeeName())
                    .setUserRemark(param.getRemark())
                    .setCreateTime(new Date());

            UserEntity saved = userService.save(user);
            return new AccountDTO(saved);
        });
    }

}
