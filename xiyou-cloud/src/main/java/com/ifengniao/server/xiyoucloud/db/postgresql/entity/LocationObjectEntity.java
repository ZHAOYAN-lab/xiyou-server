package com.ifengniao.server.xiyoucloud.db.postgresql.entity;

import com.ifengniao.server.xiyoucloud.config.Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "xy_location_object", schema = "public", catalog = "xiyou")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class LocationObjectEntity {
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
    @OneToOne
    @JoinColumn(name = "location_object_beacon", referencedColumnName = "beacon_id")
    private BeaconEntity locationObjectBeacon;
    @ManyToMany
    @JoinTable(name = "xy_bind_lom", catalog = "xiyou", schema = "public", joinColumns = @JoinColumn(name = "bind_lom_location_object", referencedColumnName = "location_object_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "bind_lom_map", referencedColumnName = "map_id", nullable = false))
    private Set<MapEntity> mapSet;

    public LocationObjectEntity(Integer locationObjectId) {
        this.locationObjectId = locationObjectId;
    }

    @Transient
    public String getLocationObjectImgSaveName() {
        if (locationObjectId == null || StringUtils.isBlank(locationObjectImg)) {
            return null;
        }
        return Constants.DIR_LOCATION_OBJECT_IMG + locationObjectId + locationObjectImg.substring(locationObjectImg.lastIndexOf("."));
    }

    public Set<MapEntity> getMapSet() {
        if (mapSet == null) {
            mapSet = new HashSet<>();
        }
        return mapSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationObjectEntity that = (LocationObjectEntity) o;
        return Objects.equals(locationObjectId, that.locationObjectId) && Objects.equals(locationObjectName, that.locationObjectName) && Objects.equals(locationObjectType, that.locationObjectType) && Objects.equals(locationObjectImg, that.locationObjectImg) && Objects.equals(locationObjectImgUpdateTime, that.locationObjectImgUpdateTime) && Objects.equals(locationObjectCreateTime, that.locationObjectCreateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationObjectId, locationObjectName, locationObjectType, locationObjectImg, locationObjectImgUpdateTime, locationObjectCreateTime);
    }
}
