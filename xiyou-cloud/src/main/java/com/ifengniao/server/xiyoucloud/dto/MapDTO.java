package com.ifengniao.server.xiyoucloud.dto;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.MapEntity;
import com.ifengniao.server.xiyoucloud.service.SysUrlService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class MapDTO {
    private Integer mapId;
    private String mapCpaId;
    private String mapCpaName;
    private String mapModelType;
    private String mapWidth;
    private String mapHeight;
    private String mapWidthPixel;
    private String mapHeightPixel;
    private String mapMetersPerPixel;
    private String mapOriginPixelX;
    private String mapOriginPixelY;
    private Object mapCpaAreas;
    private String mapImgFileName;
    private String mapImgSaveName;
    private String mapImgViewUrl;
    private List<BaseStationDTO> baseStationList;

    public static List<MapDTO> covert(Collection<MapEntity> list) {
        List<MapDTO> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new MapDTO(source));
            }
        }
        return result;
    }

    public MapDTO(MapEntity mapEntity) {
        this.mapId = mapEntity.getMapId();
        this.mapCpaId = mapEntity.getMapCpaId();
        this.mapCpaName = mapEntity.getMapCpaName();
        this.mapModelType = mapEntity.getMapModelType();
        this.mapWidth = mapEntity.getMapWidth();
        this.mapHeight = mapEntity.getMapHeight();
        this.mapWidthPixel = mapEntity.getMapWidthPixel();
        this.mapHeightPixel = mapEntity.getMapHeightPixel();
        this.mapMetersPerPixel = mapEntity.getMapMetersPerPixel();
        this.mapOriginPixelX = mapEntity.getMapOriginPixelX();
        this.mapOriginPixelY = mapEntity.getMapOriginPixelY();
        this.mapCpaAreas = mapEntity.getMapCpaAreas();
        this.mapImgFileName = "";
        var mapSaveName = mapEntity.getMapImgSaveName();
        if (StringUtils.isBlank(mapSaveName)) {
            this.mapImgSaveName = "";
            this.mapImgViewUrl = "";
        } else {
            this.mapImgSaveName = mapSaveName;
            this.mapImgViewUrl = SysUrlService.urlPrefix + mapSaveName + "?t=" + mapEntity.getMapUpdateTime();
        }
        this.baseStationList = BaseStationDTO.covert(mapEntity.getBaseStationList());
    }

    public List<BaseStationDTO> getBaseStationList() {
        if (baseStationList == null) {
            baseStationList = new ArrayList<>();
        }
        return baseStationList;
    }

}
