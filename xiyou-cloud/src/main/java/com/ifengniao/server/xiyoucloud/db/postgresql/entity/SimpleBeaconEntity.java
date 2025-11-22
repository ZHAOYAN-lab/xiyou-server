package com.ifengniao.server.xiyoucloud.db.postgresql.entity;

import com.ifengniao.server.xiyoucloud.config.Constants;
import io.hypersistence.utils.hibernate.type.array.IntArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "xy_beacon", schema = "public", catalog = "xiyou")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class SimpleBeaconEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "beacon_id")
    private Integer beaconId;

    @Basic
    @Column(name = "beacon_mac")
    private String beaconMac;

    // ✅ 新增字段：beacon_unique_id
    @Basic
    @Column(name = "beacon_unique_id")
    private String beaconUniqueId;

    @Basic
    @Column(name = "beacon_x")
    private String beaconX;

    @Basic
    @Column(name = "beacon_y")
    private String beaconY;

    @Basic
    @Column(name = "beacon_z")
    private String beaconZ;

    @Basic
    @Column(name = "beacon_online")
    private Boolean beaconOnline;

    @Basic
    @Column(name = "beacon_last_time")
    private Long beaconLastTime;

    @Basic
    @Column(name = "beacon_join_time")
    private Long beaconJoinTime;

    @Basic
    @Column(name = "beacon_product")
    private String beaconProduct;

    @Basic
    @Column(name = "beacon_type")
    private Short beaconType;

    @Basic
    @Column(name = "beacon_allow")
    private Boolean beaconAllow;

    @Basic
    @Column(name = "beacon_remark")
    private String beaconRemark;

    @Basic
    @Column(name = "beacon_rssi")
    private String beaconRssi;

    @Basic
    @Column(name = "beacon_channel")
    private String beaconChannel;

    @Basic
    @Column(name = "beacon_frequency")
    private String beaconFrequency;

    @Basic
    @Column(name = "beacon_power_type")
    private String beaconPowerType;

    @Basic
    @Column(name = "beacon_sos")
    private Boolean beaconSos;

    @Basic
    @Column(name = "beacon_sos_time")
    private Long beaconSosTime;

    @Basic
    @Column(name = "beacon_battery")
    private String beaconBattery;

    @Basic
    @Column(name = "beacon_last_gateway")
    private String beaconLastGateway;

    @Basic
    @Column(name = "beacon_nearest_gateway")
    private String beaconNearestGateway;

    @Basic
    @Column(name = "beacon_last_local_server_mac")
    private String beaconLastLocalServerMac;

    @Basic
    @Column(name = "beacon_map_id")
    private String beaconMapId;

    @Basic
    @Column(name = "beacon_zone_id")
    private String beaconZoneId;

    @Basic
    @Column(name = "beacon_zone_name")
    private String beaconZoneName;

    @Type(IntArrayType.class)
    @Column(name = "beacon_last_fence_ids", columnDefinition = "int-array")
    private Integer[] beaconLastFenceIds;

    @OneToOne
    @JoinColumn(name = "beacon_location_object", referencedColumnName = "location_object_id")
    private SimpleLocationObjectEntity beaconLocationObject;

    public SimpleBeaconEntity init() {
        this.beaconId = null;
        this.beaconMac = "";
        this.beaconUniqueId = ""; // ✅ 初始化 uniqueId
        this.beaconX = "";
        this.beaconY = "";
        this.beaconZ = "";
        this.beaconOnline = false;
        this.beaconLastTime = -1L;
        this.beaconJoinTime = System.currentTimeMillis();
        this.beaconProduct = "";
        this.beaconType = Constants.BEACON_AND_LOCATION_OBJECT_TYPE_NONE;
        this.beaconAllow = false;
        this.beaconRemark = "";
        this.beaconRssi = "";
        this.beaconChannel = "";
        this.beaconFrequency = "";
        this.beaconPowerType = "";
        this.beaconSos = false;
        this.beaconSosTime = -1L;
        this.beaconBattery = "";
        this.beaconLastGateway = "";
        this.beaconNearestGateway = "";
        this.beaconLastLocalServerMac = "";
        this.beaconMapId = "";
        this.beaconZoneId = "";
        this.beaconZoneName = "";
        this.beaconLastFenceIds = new Integer[]{};
        this.beaconLocationObject = null;
        return this;
    }

    public Integer[] getBeaconLastFenceIds() {
        if (beaconLastFenceIds == null) {
            beaconLastFenceIds = new Integer[]{};
        }
        return beaconLastFenceIds;
    }
}
