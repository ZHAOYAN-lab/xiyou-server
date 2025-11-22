package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BuildingEntity;
import com.ifengniao.server.xiyoucloud.dto.BuildingDTO;
import com.ifengniao.server.xiyoucloud.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

/**
 * 建筑相关接口
 */
@Tag(description = "建筑相关接口", name = "建筑相关接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/building")
@Slf4j
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    /**
     * 分页查找建筑信息
     *
     * @return
     */
    @Operation(summary = "分页查找建筑信息")
    @GetMapping("/page")
    public ResultMsg page(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                          @RequestParam(value = "size", required = true, defaultValue = "20") Integer size,
                          @RequestParam(name = "buildingName", required = false) String buildingName) {
        return Resp.exec(() -> getBuildingByPage(page, size, buildingName));
    }

    private Page<BuildingDTO> getBuildingByPage(Integer page, Integer size, String buildingName) {
        Page<BuildingEntity> buildingByPage = buildingService.getBuildingByPage(page, size, buildingName);
        return new PageImpl<>(BuildingDTO.covert(buildingByPage.getContent()), buildingByPage.getPageable(), buildingByPage.getTotalElements());
    }

    /**
     * 新增/修改建筑信息
     *
     * @return
     */
    @Operation(summary = "新增/修改建筑信息")
    @PostMapping("/edit")
    public ResultMsg edit(@RequestBody BuildingDTO buildingDTO) {
        return Resp.exec(() -> new BuildingDTO(buildingService.saveOrUpdateBuilding(buildingDTO)));
    }

    /**
     * 删除建筑信息
     *
     * @return
     */
    @Operation(summary = "删除建筑信息")
    @DeleteMapping("/delete")
    public ResultMsg deleteCpa(@RequestBody BuildingDTO buildingDTO) {
        return Resp.exec(() -> buildingService.deleteBuilding(buildingDTO));
    }

}
