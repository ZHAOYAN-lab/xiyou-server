package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BeaconEntity;
import com.ifengniao.server.xiyoucloud.dto.BatchUploadBeaconParam;
import com.ifengniao.server.xiyoucloud.dto.BeaconDTO;
import com.ifengniao.server.xiyoucloud.service.BeaconService;
import com.ifengniao.server.xiyoucloud.util.CovertUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 信标相关接口
 */
@Tag(description = "信标相关接口", name = "信标相关接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/beacon")
@Slf4j
public class BeaconController {

    @Autowired
    private BeaconService beaconService;

    /**
     * 分页查找信标信息
     *
     * @return
     */
    @Operation(summary = "分页查找信标信息")
    @GetMapping("/page")
    public ResultMsg page(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                          @RequestParam(value = "size", required = true, defaultValue = "20") Integer size,
                          @RequestParam(value = "beaconMac", required = false) String beaconMac,
                          @RequestParam(value = "locationObjectName", required = false) String locationObjectName,
                          @RequestParam(value = "beaconOnline", required = false) Boolean beaconOnline,
                          @RequestParam(value = "beaconAllow", required = false) Boolean beaconAllow,
                          @RequestParam(value = "beaconType", required = false) List<Short> beaconType,
                          @RequestParam(value = "beaconProduct", required = false) String beaconProduct,
                          @RequestParam(value = "order", required = false) String order,
                          @RequestParam(value = "asc", required = false) Boolean asc) {
        return Resp.exec(() -> getBeaconByPage(page, size, beaconMac, locationObjectName, beaconOnline, beaconAllow, beaconType, beaconProduct, order, asc));
    }

    private Page<BeaconDTO> getBeaconByPage(Integer page, Integer size, String beaconMac, String locationObjectName,
                                            Boolean beaconOnline, Boolean beaconAllow, List<Short> beaconType,
                                            String beaconProduct, String order, Boolean asc) {
        if (StringUtils.isNotBlank(beaconMac)) {
            beaconMac = CovertUtil.toNormalMac(beaconMac);
        }
        Page<BeaconEntity> beaconByPage = beaconService.getBeaconByPage(page, size, beaconMac, locationObjectName, beaconOnline, beaconAllow, beaconType, beaconProduct, order, asc);
        return new PageImpl<>(BeaconDTO.covert(beaconByPage.getContent()), beaconByPage.getPageable(), beaconByPage.getTotalElements());
    }

    /**
     * 查询所有信标的产品型号数组
     *
     * @return
     */
    @Operation(summary = "查询所有信标的产品型号数组")
    @GetMapping("/allProduct")
    public ResultMsg allProduct() {
        return Resp.exec(() -> beaconService.fetchDistinctBeaconProduct());
    }

    /**
     * 新增/修改信标信息
     *
     * @return
     */
    @Operation(summary = "新增/修改信标信息")
    @PostMapping("/edit")
    public ResultMsg edit(@RequestBody BeaconDTO beaconDTO) {
        return Resp.exec(() -> new BeaconDTO(beaconService.saveOrUpdateBeacon(beaconDTO)));
    }

    /**
     * 批量导入信标信息
     *
     * @return
     */
    @Operation(summary = "批量导入信标信息")
    @PostMapping("/batch")
    public ResultMsg batch(@RequestBody BatchUploadBeaconParam param) {
        return Resp.exec(() -> BeaconDTO.covert(beaconService.batchUploadBeaconByCsv(param.getLangType(), param.getSaveName())));
    }

    /**
     * 信标录入按钮
     *
     * @return
     */
    @Operation(summary = "信标录入按钮")
    @PostMapping("/allow")
    public ResultMsg allow(@RequestBody BeaconDTO beaconDTO) {
        return Resp.exec(() -> new BeaconDTO(beaconService.allow(beaconDTO)));
    }

    /**
     * 信标配置对象
     *
     * @return
     */
    @Operation(summary = "信标配置对象")
    @PostMapping("/bind")
    public ResultMsg bind(@RequestBody BeaconDTO beaconDTO) {
        return Resp.exec(() -> new BeaconDTO(beaconService.bindLocationObject(beaconDTO)));
    }

    /**
     * 删除信标信息
     *
     * @return
     */
    @Operation(summary = "删除信标信息")
    @DeleteMapping("/delete")
    public ResultMsg deleteCpa(@RequestBody BeaconDTO beaconDTO) {
        return Resp.exec(() -> beaconService.deleteBeacon(beaconDTO));
    }

}
