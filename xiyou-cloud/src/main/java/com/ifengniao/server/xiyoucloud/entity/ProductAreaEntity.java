package com.ifengniao.server.xiyoucloud.entity;

import lombok.Data;

@Data
public class ProductAreaEntity {

    private Integer areaId;
    private String objectName;
    private String belongType;
    private String iconUrl;
    private String mapIds;

    private String createTime;
    private String updateTime;

    private String mapNames;  // ⭐ 新增：用于显示地图名称
}
