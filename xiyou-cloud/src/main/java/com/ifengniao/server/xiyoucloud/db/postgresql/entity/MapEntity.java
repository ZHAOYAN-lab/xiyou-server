package com.ifengniao.server.xiyoucloud.db.postgresql.entity;

import com.ifengniao.server.xiyoucloud.config.Constants;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "xy_map", schema = "public", catalog = "xiyou")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class MapEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "map_id")
    private Integer mapId;
    @Basic
    @Column(name = "map_cpa_id")
    private String mapCpaId;
    @Basic
    @Column(name = "map_cpa_name")
    private String mapCpaName;
    @Basic
    @Column(name = "map_model_type")
    private String mapModelType; //（'image' | 'collada' | 'zip'）
    @Basic
    @Column(name = "map_width")
    private String mapWidth;
    @Basic
    @Column(name = "map_height")
    private String mapHeight;
    @Basic
    @Column(name = "map_width_pixel")
    private String mapWidthPixel;
    @Basic
    @Column(name = "map_height_pixel")
    private String mapHeightPixel;
    @Basic
    @Column(name = "map_meters_per_pixel")
    private String mapMetersPerPixel;
    @Basic
    @Column(name = "map_origin_pixel_x")
    private String mapOriginPixelX;
    @Basic
    @Column(name = "map_origin_pixel_y")
    private String mapOriginPixelY;
    @Basic
    @Column(name = "map_update_time")
    private Long mapUpdateTime;
    @Type(JsonBinaryType.class)
    @Column(name = "map_cpa_areas", columnDefinition = "jsonb")
    private Object mapCpaAreas;
    @ManyToOne
    @JoinColumn(name = "map_local_server", referencedColumnName = "local_server_id", nullable = false)
    private LocalServerEntity mapLocalServer;
    @OneToMany(mappedBy = "baseStationMap")
    @OrderBy("baseStationId asc")
    private List<BaseStationEntity> baseStationList;
    @OneToMany(mappedBy = "fenceMap")
    @OrderBy("fenceId asc")
    private List<FenceEntity> fenceList;

    public MapEntity(Integer mapId) {
        this.mapId = mapId;
    }

    @Transient
    public String getMapImgSaveName() {
        if (mapId == null || StringUtils.isBlank(mapModelType)) {
            return null;
        }
//        return Constants.DIR_MAP_IMG + mapId + (mapModelType.equals(Constants.MAP_MODEL_TYPE_IMAGE) ? ".png" : ".dae");
        return Constants.DIR_MAP_IMG + mapId + switch (mapModelType) {
            case Constants.MAP_MODEL_TYPE_COLLADA -> ".dae";
            case Constants.MAP_MODEL_TYPE_ZIP -> ".zip";
            //Constants.MAP_MODEL_TYPE_IMAGE
            default -> ".png";
        };
    }

    public List<BaseStationEntity> getBaseStationList() {
        if (baseStationList == null) {
            baseStationList = new ArrayList<>();
        }
        return baseStationList;
    }

    public List<FenceEntity> getFenceList() {
        if (fenceList == null) {
            fenceList = new ArrayList<>();
        }
        return fenceList;
    }

}
