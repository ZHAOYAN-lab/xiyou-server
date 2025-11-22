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
public class LocalServerDTO {
    private Integer localServerId;//
    private String localServerMac;////
    private String localServerIp;////
    private Boolean localServerOnline;//
    private Boolean localServerCleStartup;////
    private Boolean localServerCleActivation;////
    private String localServerCleCode;////
    private String localServerCleLicenseFileName;//
    private String localServerCleLicenseSaveName;//
    private String localServerCleLicenseViewUrl;//
    private String localServerRemark;//
    private String localServerCpaId;////
    private String localServerCpaProject;////
    private String localServerCpaCategory;////
    private String localServerCpaVersion;////
    private String localServerCpaFileName;//
    private String localServerCpaSaveName;//
    private String localServerCpaViewUrl;//
    private Long localServerLastTime;//
    private Long localServerCleLicenseExpireTime;////
    private String localServerCleVersion;////
    private Long localServerCleLicenseUpdateTime;//
    private List<MapDTO> mapList;

    public List<MapDTO> getMapList() {
        if (mapList == null) {
            mapList = new ArrayList<>();
        }
        return mapList;
    }

}
