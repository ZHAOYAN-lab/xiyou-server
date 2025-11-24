package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.FenceEntity;
import com.ifengniao.server.xiyoucloud.dto.FenceDTO;
import com.ifengniao.server.xiyoucloud.service.FenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

/**
 * 围栏相关接口
 */
@Tag(description = "围栏相关接口", name = "围栏相关接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/fence")
@Slf4j
public class FenceController {

    @Autowired
    private FenceService fenceService;

    /**
     * 分页查找围栏信息
     *
     * @return
     */
    @Operation(summary = "分页查找围栏信息")
    @GetMapping("/page")
    public ResultMsg page(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                          @RequestParam(value = "size", required = true, defaultValue = "20") Integer size,
                          @RequestParam(value = "mapCpaName", required = false) String mapCpaName,
                          @RequestParam(value = "fenceName", required = false) String fenceName,
                          @RequestParam(value = "fenceType", required = false) Short fenceType,
                          @RequestParam(value = "fenceStatus", required = false) Boolean fenceStatus) {
        return Resp.exec(() -> getFenceByPage(page, size, mapCpaName, fenceName, fenceType, fenceStatus));
    }

    private Page<FenceDTO> getFenceByPage(Integer page, Integer size, String mapCpaName, String fenceName, Short fenceType, Boolean fenceStatus) {
        Page<FenceEntity> fenceByPage = fenceService.getFenceByPage(page, size, mapCpaName, fenceName, fenceType, fenceStatus);
        return new PageImpl<>(FenceDTO.covert(fenceByPage.getContent()), fenceByPage.getPageable(), fenceByPage.getTotalElements());
    }

    /**
     * 按地图ID查找围栏信息
     *
     * @return
     */
    @Operation(summary = "按地图ID查找围栏信息")
    @GetMapping("/byMap")
    public ResultMsg byMap(@RequestParam(value = "mapId") Integer mapId) {
        return Resp.exec(() -> FenceDTO.covert(fenceService.findAllByFenceMap(mapId)));
    }


    /**
     * 新增/修改围栏信息 (包含新增的商品区域属性)
     *
     * @return
     */
    @Operation(summary = "新增/修改围栏信息 (含商品区域属性)") // 仅修改了 Operation 描述
    @PostMapping("/edit")
    public ResultMsg edit(@RequestBody FenceDTO fenceDTO) {
        // 由于 Spring 框架会自动将请求体 JSON 映射到 FenceDTO，所以无需修改内部代码
        return Resp.exec(() -> new FenceDTO(fenceService.saveOrUpdateFence(fenceDTO)));
    }

    /**
     * 围栏启用禁用按钮
     *
     * @return
     */
    @Operation(summary = "围栏启用禁用按钮")
    @PostMapping("/changeStatus")
    public ResultMsg changeStatus(@RequestBody FenceDTO fenceDTO) {
        return Resp.exec(() -> new FenceDTO(fenceService.changeStatus(fenceDTO)));
    }

    /**
     * 删除围栏信息
     *
     * @return
     */
    @Operation(summary = "删除围栏信息")
    @DeleteMapping("/delete")
    public ResultMsg delete(@RequestBody FenceDTO fenceDTO) {
        return Resp.exec(() -> fenceService.deleteFence(fenceDTO));
    }

}