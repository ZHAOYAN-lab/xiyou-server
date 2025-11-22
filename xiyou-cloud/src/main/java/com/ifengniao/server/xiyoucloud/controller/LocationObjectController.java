package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.LocationObjectEntity;
import com.ifengniao.server.xiyoucloud.dto.LocationObjectDTO;
import com.ifengniao.server.xiyoucloud.service.LocationObjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 定位对象相关接口
 */
@Tag(description = "定位对象相关接口", name = "定位对象相关接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/locationobject")
@Slf4j
public class LocationObjectController {

    @Autowired
    private LocationObjectService locationObjectService;

    /**
     * 分页查找定位对象信息
     *
     * @return
     */
    @Operation(summary = "分页查找定位对象信息")
    @GetMapping("/page")
    public ResultMsg page(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                          @RequestParam(value = "size", required = true, defaultValue = "20") Integer size,
                          @RequestParam(name = "locationObjectName", required = false) String locationObjectName,
                          @RequestParam(value = "locationObjectType", required = false) List<Short> locationObjectType,
                          @RequestParam(value = "order", required = false) String order,
                          @RequestParam(value = "asc", required = false) Boolean asc) {
        return Resp.exec(() -> getLocationObjectByPage(page, size, locationObjectName, locationObjectType, order, asc));
    }

    private Page<LocationObjectDTO> getLocationObjectByPage(Integer page, Integer size, String locationObjectName, List<Short> locationObjectType,
                                                            String order, Boolean asc) {
        Page<LocationObjectEntity> locationObjectByPage = locationObjectService.getLocationObjectByPage(page, size, locationObjectName, locationObjectType, order, asc);
        return new PageImpl<>(LocationObjectDTO.covert(locationObjectByPage.getContent()), locationObjectByPage.getPageable(), locationObjectByPage.getTotalElements());
    }

    /**
     * 查找可用的对象列表
     *
     * @return
     */
    @Operation(summary = "查找可用的对象列表")
    @GetMapping("/available")
    public ResultMsg available() {
        return Resp.exec(() -> {
            var available = locationObjectService.getAvailableLocationObjectList();
            return available.stream()
                    .map(locationObject -> Map.of("locationObjectId", locationObject.getLocationObjectId(), "locationObjectName", locationObject.getLocationObjectName()))
                    .collect(Collectors.toList());
        });
    }

    /**
     * 按分类查找定位对象信息
     *
     * @return
     */
    @Operation(summary = "按分类查找定位对象信息")
    @GetMapping("/byType")
    public ResultMsg byType(@RequestParam(name = "locationObjectType") Short locationObjectType) {
        return Resp.exec(() -> LocationObjectDTO.covert(locationObjectService.findByType(locationObjectType)));
    }

    /**
     * 按信标ID查找其允许绑定的定位对象信息
     *
     * @return
     */
    @Operation(summary = "按信标ID查找其允许绑定的定位对象信息")
    @GetMapping("/byBeaconId")
    public ResultMsg byBeaconId(@RequestParam(name = "beaconId") Integer beaconId) {
        return Resp.exec(() -> LocationObjectDTO.covert(locationObjectService.findByBeaconId(beaconId)));
    }

    /**
     * 新增/修改定位对象信息
     *
     * @return
     */
    @Operation(summary = "新增/修改定位对象信息")
    @PostMapping("/edit")
    public ResultMsg edit(@RequestBody LocationObjectDTO locationObjectDTO) {
        return Resp.exec(() -> new LocationObjectDTO(locationObjectService.saveOrUpdateLocationObject(locationObjectDTO)));
    }

    /**
     * 删除定位对象信息
     *
     * @return
     */
    @Operation(summary = "删除定位对象信息")
    @DeleteMapping("/delete")
    public ResultMsg deleteCpa(@RequestBody LocationObjectDTO locationObjectDTO) {
        return Resp.exec(() -> locationObjectService.deleteLocationObject(locationObjectDTO));
    }

    /**
     * 定位对象绑定地图
     *
     * @return
     */
    @Operation(summary = "定位对象绑定地图")
    @PostMapping("/bingMap")
    public ResultMsg bingMap(@RequestBody LocationObjectDTO locationObjectDTO) {
        return Resp.exec(() -> new LocationObjectDTO(locationObjectService.locationObjectBindMap(locationObjectDTO)));
    }

}
