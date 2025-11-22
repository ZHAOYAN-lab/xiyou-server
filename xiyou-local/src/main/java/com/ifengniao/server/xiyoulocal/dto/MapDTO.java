package com.ifengniao.server.xiyoulocal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class MapDTO {
    private Integer mapId;//
    private String mapCpaId;////
    private String mapCpaName;////
    private String mapModelType;////
    private String mapWidth;////
    private String mapHeight;////
    private String mapWidthPixel;////
    private String mapHeightPixel;////
    private String mapMetersPerPixel;////
    private String mapOriginPixelX;////
    private String mapOriginPixelY;////
    private Object mapCpaAreas;////
    private String mapImgFileName;////
    private String mapImgSaveName;////
    private String mapImgViewUrl;//
    private List<BaseStationDTO> baseStationList;

    public List<BaseStationDTO> getBaseStationList() {
        if (baseStationList == null) {
            baseStationList = new ArrayList<>();
        }
        return baseStationList;
    }
}
