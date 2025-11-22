package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.component.security.model.LoginParam;
import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.service.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 鉴权相关接口
 */
@Tag(description = "鉴权相关接口", name = "鉴权相关接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/token")
@Slf4j
public class TokenController {

    @Autowired
    private AuthorizationService authorizationService;

    @Operation(summary = "获取访问token，获取到的token需要带在大部分请求接口的Header中，Authorization字段")
    @PostMapping("/request")
    public ResultMsg requestToken(@RequestBody LoginParam user) {
        return Resp.exec(() -> authorizationService.requestToken(user.getUserName(), user.getPassword()));
    }

    @Operation(summary = "删除访问token，使token失效，登出时使用，需要带上Authorization头")
    @DeleteMapping
    public ResultMsg deleteToken(HttpServletRequest request) {
        return Resp.exec(() -> authorizationService.deleteToken(request));
    }

}
