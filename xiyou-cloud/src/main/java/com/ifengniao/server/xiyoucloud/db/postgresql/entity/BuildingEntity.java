package com.ifengniao.server.xiyoucloud.db.postgresql.entity;

import com.ifengniao.server.xiyoucloud.config.Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "xy_building", schema = "public", catalog = "xiyou")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class BuildingEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "building_id")
    private Integer buildingId;
    @Basic
    @Column(name = "building_name")
    private String buildingName;
    @Basic
    @Column(name = "building_address")
    private String buildingAddress;
    @Basic
    @Column(name = "building_img")
    private String buildingImg;
    @Basic
    @Column(name = "building_img_update_time")
    private Long buildingImgUpdateTime;

    public BuildingEntity(Integer buildingId) {
        this.buildingId = buildingId;
    }

    @Transient
    public String getBuildingImgSaveName() {
        if (buildingId == null || StringUtils.isBlank(buildingImg)) {
            return null;
        }
        return Constants.DIR_BUILDING_IMG + buildingId + buildingImg.substring(buildingImg.lastIndexOf("."));
    }
}
