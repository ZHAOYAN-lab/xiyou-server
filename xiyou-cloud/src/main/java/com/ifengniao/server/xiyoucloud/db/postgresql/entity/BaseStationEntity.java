package com.ifengniao.server.xiyoucloud.db.postgresql.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import java.util.Collections;

@Entity
@Table(name = "xy_base_station", schema = "public", catalog = "xiyou")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class BaseStationEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "base_station_id")
    private Integer baseStationId;
    @Basic
    @Column(name = "base_station_mac")
    private String baseStationMac;
    @Basic
    @Column(name = "base_station_ip")
    private String baseStationIp;
    @Basic
    @Column(name = "base_station_elevation")
    private String baseStationElevation;
    @Basic
    @Column(name = "base_station_azimuth")
    private String baseStationAzimuth;
    @Basic
    @Column(name = "base_station_rotation")
    private String baseStationRotation;
    @Basic
    @Column(name = "base_station_error_degree")
    private String baseStationErrorDegree;
    @Basic
    @Column(name = "base_station_x")
    private String baseStationX;
    @Basic
    @Column(name = "base_station_y")
    private String baseStationY;
    @Basic
    @Column(name = "base_station_z")
    private String baseStationZ;
    @Basic
    @Column(name = "base_station_online")
    private Boolean baseStationOnline;
    @Basic
    @Column(name = "base_station_last_time")
    private Long baseStationLastTime;
    @Basic
    @Column(name = "base_station_name")
    private String baseStationName;
    @Basic
    @Column(name = "base_station_type")
    private String baseStationType;
    @Basic
    @Column(name = "base_station_product")
    private String baseStationProduct;
    @Type(JsonBinaryType.class)
    @Column(name = "base_station_channels", columnDefinition = "jsonb")
    private Object baseStationChannels;
    @ManyToOne
    @JoinColumn(name = "base_station_map", referencedColumnName = "map_id", nullable = false)
    private MapEntity baseStationMap;

    public Object getBaseStationChannels() {
        if (baseStationChannels == null) {
            baseStationChannels = Collections.emptyList();
        }
        return baseStationChannels;
    }
}
