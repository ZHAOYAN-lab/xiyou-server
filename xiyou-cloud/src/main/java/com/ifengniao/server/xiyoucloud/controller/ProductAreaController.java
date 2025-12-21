package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.entity.ProductAreaEntity;
import com.ifengniao.server.xiyoucloud.service.ProductAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(description = "商品区域设置接口", name = "商品区域设置接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/productArea")
@Slf4j
public class ProductAreaController {

    @Autowired
    private ProductAreaService productAreaService;

    @Operation(summary = "查询商品区域列表")
    @GetMapping("/list")
    public ResultMsg list(ProductAreaEntity query) {
        return Resp.exec(() -> productAreaService.list(query));
    }

    @Operation(summary = "新增商品区域")
    @PostMapping("/add")
    public ResultMsg add(@RequestBody ProductAreaEntity entity) {
        return Resp.exec(() -> productAreaService.add(entity));
    }

    @Operation(summary = "修改商品区域")
    @PostMapping("/update")
    public ResultMsg update(@RequestBody ProductAreaEntity entity) {
        return Resp.exec(() -> productAreaService.update(entity));
    }

    @Operation(summary = "删除商品区域")
    @PostMapping("/delete")
    public ResultMsg delete(@RequestBody ProductAreaEntity entity) {
        return Resp.exec(() -> productAreaService.delete(entity.getAreaId()));
    }

    @Operation(summary = "获取区域所属类型列表")
    @GetMapping("/types")
    public ResultMsg types() {
        return Resp.exec(() -> productAreaService.getTypes());
    }

    /**
     * ✅【新增】根据 areaId 查询商品区域详情（H5 使用）
     * 返回内容包含 areaContent（POLYGON((...))）
     */
    @Operation(summary = "根据ID查询商品区域详情")
    @GetMapping("/getById")
    public ResultMsg getById(@RequestParam("areaId") Integer areaId) {
        return Resp.exec(() -> productAreaService.getById(areaId));
    }
}
