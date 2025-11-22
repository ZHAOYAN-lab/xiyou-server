package com.ifengniao.server.xiyoucloud.dto;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BuildingEntity;
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
public class BuildingDTO {
    private Integer buildingId;
    private String buildingName;
    private String buildingAddress;
    private String buildingImgFileName;
    private String buildingImgSaveName;
    private String buildingImgViewUrl;

    public static List<BuildingDTO> covert(Collection<BuildingEntity> list) {
        List<BuildingDTO> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new BuildingDTO(source));
            }
        }
        return result;
    }

    public BuildingDTO(BuildingEntity buildingEntity) {
        this.buildingId = buildingEntity.getBuildingId();
        this.buildingName = buildingEntity.getBuildingName();
        this.buildingAddress = buildingEntity.getBuildingAddress();
        this.buildingImgFileName = buildingEntity.getBuildingImg();
        var saveName = buildingEntity.getBuildingImgSaveName();
        if (StringUtils.isBlank(saveName)) {
            this.buildingImgSaveName = "";
            this.buildingImgViewUrl = "";
        } else {
            this.buildingImgSaveName = saveName;
            this.buildingImgViewUrl = SysUrlService.urlPrefix + saveName + "?t=" + buildingEntity.getBuildingImgUpdateTime();
        }
    }

}
