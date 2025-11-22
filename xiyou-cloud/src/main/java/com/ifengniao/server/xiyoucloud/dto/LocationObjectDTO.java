package com.ifengniao.server.xiyoucloud.dto;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.LocationObjectEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.SimpleLocationObjectEntity;
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
public class LocationObjectDTO {
    private Integer locationObjectId;
    private String locationObjectName;
    private Short locationObjectType;
    private String locationObjectImgFileName;
    private String locationObjectImgSaveName;
    private String locationObjectImgViewUrl;
    private Long locationObjectCreateTime;
    private List<MapWithoutBaseStationDTO> mapList;

    public LocationObjectDTO simplify() {
        this.locationObjectImgSaveName = null;
        this.mapList = null;
        return this;
    }

    public static List<LocationObjectDTO> covert(Collection<LocationObjectEntity> list) {
        List<LocationObjectDTO> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new LocationObjectDTO(source));
            }
        }
        return result;
    }

    public LocationObjectDTO(LocationObjectEntity locationObjectEntity) {
        this.locationObjectId = locationObjectEntity.getLocationObjectId();
        this.locationObjectName = locationObjectEntity.getLocationObjectName();
        this.locationObjectType = locationObjectEntity.getLocationObjectType();
        this.locationObjectImgFileName = locationObjectEntity.getLocationObjectImg();
        this.locationObjectCreateTime = locationObjectEntity.getLocationObjectCreateTime();
        var saveName = locationObjectEntity.getLocationObjectImgSaveName();
        if (StringUtils.isBlank(saveName)) {
            this.locationObjectImgSaveName = "";
            this.locationObjectImgViewUrl = "";
        } else {
            this.locationObjectImgSaveName = saveName;
            this.locationObjectImgViewUrl = SysUrlService.urlPrefix + saveName + "?t=" + locationObjectEntity.getLocationObjectImgUpdateTime();
        }
        this.mapList = MapWithoutBaseStationDTO.covert(locationObjectEntity.getMapSet());
    }

    public LocationObjectDTO(SimpleLocationObjectEntity locationObjectEntity) {
        this.locationObjectId = locationObjectEntity.getLocationObjectId();
        this.locationObjectName = locationObjectEntity.getLocationObjectName();
        this.locationObjectType = locationObjectEntity.getLocationObjectType();
        this.locationObjectImgFileName = locationObjectEntity.getLocationObjectImg();
        this.locationObjectCreateTime = locationObjectEntity.getLocationObjectCreateTime();
        var saveName = locationObjectEntity.getLocationObjectImgSaveName();
        if (StringUtils.isBlank(saveName)) {
            this.locationObjectImgSaveName = "";
            this.locationObjectImgViewUrl = "";
        } else {
            this.locationObjectImgSaveName = saveName;
            this.locationObjectImgViewUrl = SysUrlService.urlPrefix + saveName + "?t=" + locationObjectEntity.getLocationObjectImgUpdateTime();
        }
        this.mapList = null;
    }

    public List<MapWithoutBaseStationDTO> getMapList() {
        if (mapList == null) {
            mapList = new ArrayList<>();
        }
        return mapList;
    }

}
