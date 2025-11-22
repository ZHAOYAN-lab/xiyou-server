package com.ifengniao.server.xiyoulocal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class CleLocatorDTO {
    private List<Integer> angles;
    private LocatorInfo info;
    private String ip;
    private Boolean online;
    private Long updatedAt;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Accessors(chain = true)
    public static class LocatorInfo {
        private Double azimuth;
        private Double elevation;
        private Double rotation;
        private Double errorDegree;
        private Object channel;
        private Object channels;
        private String mapId;
        private String modelName;
        private String name;
        private String type;
        private String version;
        private Double x;
        private Double y;
        private Double z;
        private Object zoneId;
    }

    public LocatorInfo getInfo() {
        if (info == null) {
            info = new LocatorInfo();
        }
        return info;
    }
}