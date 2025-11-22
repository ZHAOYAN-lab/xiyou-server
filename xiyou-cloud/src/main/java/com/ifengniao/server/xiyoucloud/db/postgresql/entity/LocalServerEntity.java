package com.ifengniao.server.xiyoucloud.db.postgresql.entity;

import com.ifengniao.server.xiyoucloud.config.Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "xy_local_server", schema = "public", catalog = "xiyou")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class LocalServerEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "local_server_id")
    private Integer localServerId;
    @Basic
    @Column(name = "local_server_mac")
    private String localServerMac;
    @Basic
    @Column(name = "local_server_ip")
    private String localServerIp;
    @Basic
    @Column(name = "local_server_online")
    private Boolean localServerOnline;
    @Basic
    @Column(name = "local_server_cle_startup")
    private Boolean localServerCleStartup;
    @Basic
    @Column(name = "local_server_cle_activation")
    private Boolean localServerCleActivation;
    @Basic
    @Column(name = "local_server_cle_code")
    private String localServerCleCode;
    @Basic
    @Column(name = "local_server_cle_license")
    private String localServerCleLicense;
    @Basic
    @Column(name = "local_server_remark")
    private String localServerRemark;
    @Basic
    @Column(name = "local_server_cpa_id")
    private String localServerCpaId;
    @Basic
    @Column(name = "local_server_cpa_project")
    private String localServerCpaProject;
    @Basic
    @Column(name = "local_server_cpa_category")
    private String localServerCpaCategory;
    @Basic
    @Column(name = "local_server_cpa_version")
    private String localServerCpaVersion;
    @Basic
    @Column(name = "local_server_cpa_file")
    private String localServerCpaFile;
    @Basic
    @Column(name = "local_server_last_time")
    private Long localServerLastTime;
    @Basic
    @Column(name = "local_server_cle_license_expire_time")
    private Long localServerCleLicenseExpireTime;
    @Basic
    @Column(name = "local_server_cle_version")
    private String localServerCleVersion;
    @Basic
    @Column(name = "local_server_cle_license_update_time")
    private Long localServerCleLicenseUpdateTime;
    @Basic
    @Column(name = "local_server_cpa_file_update_time")
    private Long localServerCpaFileUpdateTime;
    @ManyToOne
    @JoinColumn(name = "local_server_building", referencedColumnName = "building_id")
    private BuildingEntity localServerBuilding;
    @OneToMany(mappedBy = "mapLocalServer")
    @OrderBy("mapId asc")
    private List<MapEntity> mapList;

    @Transient
    public String getLocalServerCleLicenseSaveName() {
        if (localServerId == null || StringUtils.isBlank(localServerCleLicense)) {
            return null;
        }
        return Constants.DIR_LOCAL_SERVER_LICENSE + localServerId + localServerCleLicense.substring(localServerCleLicense.lastIndexOf("."));
    }

    @Transient
    public String getLocalServerCpaFileSaveName() {
        if (localServerId == null || StringUtils.isBlank(localServerCpaFile)) {
            return null;
        }
        return Constants.DIR_LOCAL_SERVER_CPA + localServerId + localServerCpaFile.substring(localServerCpaFile.lastIndexOf("."));
    }

    public List<MapEntity> getMapList() {
        if (mapList == null) {
            mapList = new ArrayList<>();
        }
        return mapList;
    }

}
