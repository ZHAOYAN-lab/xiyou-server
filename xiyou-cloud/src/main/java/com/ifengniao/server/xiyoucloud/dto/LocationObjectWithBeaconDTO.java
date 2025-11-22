package com.ifengniao.server.xiyoucloud.dto;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.LocationObjectEntity;
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
public class LocationObjectWithBeaconDTO {
    private Integer locationObjectId;
    private String locationObjectName;
    private Short locationObjectType;
    private String locationObjectImgFileName;
    private String locationObjectImgSaveName;
    private String locationObjectImgViewUrl;
    private Long locationObjectCreateTime;
    private BeaconWithoutLocationObjectDTO locationObjectBeacon;

    public LocationObjectWithBeaconDTO simplify() {
        this.locationObjectImgSaveName = null;
        if (this.locationObjectBeacon != null) {
            this.locationObjectBeacon.simplify();
        }
        return this;
    }

    public static List<LocationObjectWithBeaconDTO> covert(Collection<LocationObjectEntity> list) {
        List<LocationObjectWithBeaconDTO> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new LocationObjectWithBeaconDTO(source));
            }
        }
        return result;
    }

    public LocationObjectWithBeaconDTO(LocationObjectEntity locationObjectEntity) {
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
        if (locationObjectEntity.getLocationObjectBeacon() != null) {
            this.locationObjectBeacon = new BeaconWithoutLocationObjectDTO(locationObjectEntity.getLocationObjectBeacon());
        }
    }

}
