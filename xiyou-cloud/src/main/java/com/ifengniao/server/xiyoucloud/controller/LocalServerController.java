package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.BaseException;
import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.LocalServerEntity;
import com.ifengniao.server.xiyoucloud.dto.LocalServerDTO;
import com.ifengniao.server.xiyoucloud.service.LocalServerService;
import com.ifengniao.server.xiyoucloud.service.RedisService;
import com.ifengniao.server.xiyoucloud.util.CovertUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 本地服务相关接口
 */
@Tag(description = "本地服务相关接口", name = "本地服务相关接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/localserver")
@Slf4j
public class LocalServerController {

    @Autowired
    private LocalServerService localServerService;
    @Autowired
    private RedisService redisService;

    /**
     * 分页查找本地服务信息
     *
     * @return
     */
    @Operation(summary = "分页查找本地服务信息")
    @GetMapping("/page")
    public ResultMsg page(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                          @RequestParam(value = "size", required = true, defaultValue = "20") Integer size,
                          @RequestParam(name = "buildingId", required = false) Integer buildingId,
                          @RequestParam(name = "projectName", required = false) String projectName,
                          @RequestParam(name = "cpaFileName", required = false) String cpaFileName,
                          @RequestParam(name = "mac", required = false) String mac) {
        return Resp.exec(() -> getLocalServerByPage(page, size, buildingId, projectName, cpaFileName, mac));
    }

    private Page<LocalServerDTO> getLocalServerByPage(Integer page, Integer size, Integer buildingId, String projectName, String cpaFileName, String mac) {
        if (StringUtils.isNotBlank(mac)) {
            mac = CovertUtil.toNormalMac(mac);
        }
        Page<LocalServerEntity> localServerByPage = localServerService.getLocalServerByPage(page, size, buildingId, projectName, cpaFileName, mac);
        return new PageImpl<>(LocalServerDTO.covert(localServerByPage.getContent()), localServerByPage.getPageable(), localServerByPage.getTotalElements());
    }

    /**
     * 查找可用的本地服务列表
     *
     * @return
     */
    @Operation(summary = "查找可用的本地服务列表")
    @GetMapping("/available")
    public ResultMsg available() {
        return Resp.exec(() -> {
            var available = localServerService.getAvailableLocalServerList();
            return available.stream()
                    .map(ls -> Map.of("localServerId", ls.getLocalServerId(), "localServerMac", ls.getLocalServerMac()))
                    .collect(Collectors.toList());
        });
    }

    /**
     * 修改本地服务信息（备注）
     *
     * @return
     */
    @Operation(summary = "修改本地服务信息（备注）")
    @PostMapping("/edit")
    public ResultMsg edit(@RequestBody LocalServerDTO localServer) {
        return Resp.exec(() -> new LocalServerDTO(localServerService.updateRemarkById(localServer.getLocalServerId(), localServer.getLocalServerRemark())));
    }

    /**
     * 上传授权文件
     *
     * @return
     */
    @Operation(summary = "上传授权文件")
    @PostMapping("/uploadLicense")
    public ResultMsg uploadLicense(@RequestBody LocalServerDTO localServer) {
        return Resp.exec(() -> {
            if (StringUtils.isBlank(localServer.getLocalServerCleLicenseFileName())
                    || StringUtils.isBlank(localServer.getLocalServerCleLicenseSaveName())) {
                throw new BaseException("参数缺失").setStrCode(Constants.ERR_F200);
            }
            return localServerService.updateLicenseById(localServer.getLocalServerId(),
                    localServer.getLocalServerCleLicenseFileName(),
                    localServer.getLocalServerCleLicenseSaveName());
        });
    }

    /**
     * 获取上传授权文件结果
     *
     * @return
     */
    @Operation(summary = "获取上传授权文件结果")
    @GetMapping("/uploadLicenseResult")
    public ResultMsg uploadLicenseResult(@RequestParam(value = "key") String key) {
        return Resp.exec(() -> {
            if (StringUtils.isBlank(key)) {
                throw new BaseException("参数缺失").setStrCode(Constants.ERR_F200);
            }
            return redisService.getSendLicenseResult(key);
        });
    }

    /**
     * 上传CPA文件
     *
     * @return
     */
    @Operation(summary = "上传CPA文件")
    @PostMapping("/uploadCpa")
    public ResultMsg uploadCpa(@RequestBody LocalServerDTO localServer) {
        return Resp.exec(() -> {
            if (StringUtils.isBlank(localServer.getLocalServerCpaFileName())
                    || StringUtils.isBlank(localServer.getLocalServerCpaSaveName())
                    || localServer.getLocalServerBuilding() == null
                    || localServer.getLocalServerBuilding().getBuildingId() == null) {
                throw new BaseException("参数缺失").setStrCode(Constants.ERR_F200);
            }
            return localServerService.updateCpaById(localServer.getLocalServerId(),
                    localServer.getLocalServerCpaFileName(),
                    localServer.getLocalServerCpaSaveName(),
                    localServer.getLocalServerBuilding().getBuildingId());
        });
    }

    /**
     * 获取上传CPA文件结果
     *
     * @return
     */
    @Operation(summary = "获取上传CPA文件结果")
    @GetMapping("/uploadCpaResult")
    public ResultMsg uploadCpaResult(@RequestParam(value = "key") String key) {
        return Resp.exec(() -> {
            if (StringUtils.isBlank(key)) {
                throw new BaseException("参数缺失").setStrCode(Constants.ERR_F200);
            }
            return redisService.getSendCpaResult(key);
        });
    }

    /**
     * 删除CPA文件
     *
     * @return
     */
    @Operation(summary = "删除CPA文件")
    @DeleteMapping("/deleteCpa")
    public ResultMsg deleteCpa(@RequestBody LocalServerDTO localServer) {
        return Resp.exec(() -> localServerService.deleteCpaById(localServer.getLocalServerId()));
    }

}
