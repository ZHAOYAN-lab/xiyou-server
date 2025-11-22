package com.ifengniao.server.xiyoucloud.db.postgresql.entity;

import com.ifengniao.server.xiyoucloud.config.Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "xy_location_object", schema = "public", catalog = "xiyou")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class SimpleLocationObjectEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "location_object_id")
    private Integer locationObjectId;
    @Basic
    @Column(name = "location_object_name")
    private String locationObjectName;
    @Basic
    @Column(name = "location_object_type")
    private Short locationObjectType;
    @Basic
    @Column(name = "location_object_img")
    private String locationObjectImg;
    @Basic
    @Column(name = "location_object_img_update_time")
    private Long locationObjectImgUpdateTime;
    @Basic
    @Column(name = "location_object_create_time")
    private Long locationObjectCreateTime;

    @Transient
    public String getLocationObjectImgSaveName() {
        if (locationObjectId == null || StringUtils.isBlank(locationObjectImg)) {
            return null;
        }
        return Constants.DIR_LOCATION_OBJECT_IMG + locationObjectId + locationObjectImg.substring(locationObjectImg.lastIndexOf("."));
    }

}
