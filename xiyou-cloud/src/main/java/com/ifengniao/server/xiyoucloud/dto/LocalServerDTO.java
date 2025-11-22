package com.ifengniao.server.xiyoucloud.dto;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.LocalServerEntity;
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
public class LocalServerDTO {
    private Integer localServerId;
    private String localServerMac;
    private String localServerIp;
    private Boolean localServerOnline;
    private Boolean localServerCleStartup;
    private Boolean localServerCleActivation;
    private String localServerCleCode;
    private String localServerCleLicenseFileName;
    private String localServerCleLicenseSaveName;
    private String localServerCleLicenseViewUrl;
    private String localServerRemark;
    private String localServerCpaId;
    private String localServerCpaProject;
    private String localServerCpaCategory;
    private String localServerCpaVersion;
    private String localServerCpaFileName;
    private String localServerCpaSaveName;
    private String localServerCpaViewUrl;
    private Long localServerLastTime;
    private Long localServerCleLicenseExpireTime;
    private String localServerCleVersion;
    private Long localServerCleLicenseUpdateTime;
    private BuildingDTO localServerBuilding;
    private List<MapDTO> mapList;

    public static List<LocalServerDTO> covert(Collection<LocalServerEntity> list) {
        List<LocalServerDTO> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new LocalServerDTO(source));
            }
        }
        return result;
    }

    public LocalServerDTO(LocalServerEntity localServerEntity) {
        this.localServerId = localServerEntity.getLocalServerId();
        this.localServerMac = localServerEntity.getLocalServerMac();
        this.localServerIp = localServerEntity.getLocalServerIp();
        this.localServerOnline = localServerEntity.getLocalServerOnline();
        this.localServerCleStartup = localServerEntity.getLocalServerCleStartup();
        this.localServerCleActivation = localServerEntity.getLocalServerCleActivation();
        this.localServerCleCode = localServerEntity.getLocalServerCleCode();
        this.localServerCleLicenseFileName = localServerEntity.getLocalServerCleLicense();
        var licenseSaveName = localServerEntity.getLocalServerCleLicenseSaveName();
        if (StringUtils.isBlank(licenseSaveName)) {
            this.localServerCleLicenseSaveName = "";
            this.localServerCleLicenseViewUrl = "";
        } else {
            this.localServerCleLicenseSaveName = licenseSaveName;
            this.localServerCleLicenseViewUrl = SysUrlService.urlPrefix + licenseSaveName + "?t=" + localServerEntity.getLocalServerCleLicenseUpdateTime();
        }
        this.localServerRemark = localServerEntity.getLocalServerRemark();
        this.localServerCpaId = localServerEntity.getLocalServerCpaId();
        this.localServerCpaProject = localServerEntity.getLocalServerCpaProject();
        this.localServerCpaCategory = localServerEntity.getLocalServerCpaCategory();
        this.localServerCpaVersion = localServerEntity.getLocalServerCpaVersion();
        this.localServerCpaFileName = localServerEntity.getLocalServerCpaFile();
        var cpaSaveName = localServerEntity.getLocalServerCpaFileSaveName();
        if (StringUtils.isBlank(cpaSaveName)) {
            this.localServerCpaSaveName = "";
            this.localServerCpaViewUrl = "";
        } else {
            this.localServerCpaSaveName = cpaSaveName;
            this.localServerCpaViewUrl = SysUrlService.urlPrefix + cpaSaveName + "?t=" + localServerEntity.getLocalServerCpaFileUpdateTime();
        }
        this.localServerLastTime = localServerEntity.getLocalServerLastTime();
        this.localServerCleLicenseExpireTime = localServerEntity.getLocalServerCleLicenseExpireTime();
        this.localServerCleVersion = localServerEntity.getLocalServerCleVersion();
        this.localServerCleLicenseUpdateTime = localServerEntity.getLocalServerCleLicenseUpdateTime();
        if (localServerEntity.getLocalServerBuilding() != null) {
            this.localServerBuilding = new BuildingDTO(localServerEntity.getLocalServerBuilding());
        }
        this.mapList = MapDTO.covert(localServerEntity.getMapList());
    }

    public List<MapDTO> getMapList() {
        if (mapList == null) {
            mapList = new ArrayList<>();
        }
        return mapList;
    }

}
