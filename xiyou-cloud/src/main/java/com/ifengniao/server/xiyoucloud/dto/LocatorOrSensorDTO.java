package com.ifengniao.server.xiyoucloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class LocatorOrSensorDTO {
    private String type;
    private Map<String, Map<String, Object>> data;
}
