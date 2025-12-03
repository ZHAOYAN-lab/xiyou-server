package com.ifengniao.server.xiyoucloud.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MobileTaskDto {
    private Long taskId;
    private Long mapId;

    private List<Map<String, Object>> areaPoints;
    private List<Double> areaCenter;
}
