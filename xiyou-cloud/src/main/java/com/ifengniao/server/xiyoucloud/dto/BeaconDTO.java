package com.ifengniao.server.xiyoucloud.dto;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BeaconEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.SimpleBeaconEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class BeaconDTO {
    private Integer beaconId;
    private String beaconMac;
    private String beaconUniqueId;   // ❗保留字段用于兼容旧前端，但不入库
    private String beaconX;
    private String beaconY;
    private String beaconZ;
    private Boolean beaconOnline;
    private Long beaconLastTime;
    private Long beaconJoinTime;
    private String beaconProduct;
    private Short beaconType;
    private Boolean beaconAllow;
    private String beaconRemark;
    private String beaconRssi;
    private String beaconChannel;
    private String beaconFrequency;
    private String beaconPowerType;
    private Boolean beaconSos;
    private Long beaconSosTime;
    private String beaconBattery;
    private String beaconLastGateway;
    private String beaconNearestGateway;
    private String beaconLastLocalServerMac;
    private String beaconMapId;
    private String beaconZoneId;
    private String beaconZoneName;
    private LocationObjectDTO beaconLocationObject;

    public BeaconDTO simplify() {
        this.beaconOnline = null;
        this.beaconJoinTime = null;
        this.beaconProduct = null;
        this.beaconAllow = null;
        this.beaconRemark = null;
        this.beaconRssi = null;
        this.beaconChannel = null;
        this.beaconFrequency = null;
        this.beaconPowerType = null;
        this.beaconSos = null;
        this.beaconSosTime = null;
        this.beaconBattery = null;
        this.beaconLastGateway = null;
        this.beaconNearestGateway = null;
        this.beaconLastLocalServerMac = null;
        this.beaconZoneId = null;
        this.beaconZoneName = null;
        if (this.beaconLocationObject != null) {
            this.beaconLocationObject.simplify();
        }
        return this;
    }

    public static List<BeaconDTO> covert(Collection<BeaconEntity> list) {
        List<BeaconDTO> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new BeaconDTO(source));
            }
        }
        return result;
    }

    public static List<BeaconDTO> covertSimplify(Collection<BeaconEntity> list) {
        List<BeaconDTO> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new BeaconDTO(source).simplify());
            }
        }
        return result;
    }

    public static List<BeaconDTO> covertSimple(Collection<SimpleBeaconEntity> list) {
        List<BeaconDTO> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new BeaconDTO(source));
            }
        }
        return result;
    }

    public BeaconDTO(BeaconEntity beaconEntity) {
        this.beaconId = beaconEntity.getBeaconId();
        this.beaconMac = beaconEntity.getBeaconMac();
        this.beaconUniqueId = this.beaconMac; // ❗uniqueId = mac（兼容使用）

        this.beaconX = beaconEntity.getBeaconX();
        this.beaconY = beaconEntity.getBeaconY();
        this.beaconZ = beaconEntity.getBeaconZ();
        this.beaconOnline = beaconEntity.getBeaconOnline();
        this.beaconLastTime = beaconEntity.getBeaconLastTime();
        this.beaconJoinTime = beaconEntity.getBeaconJoinTime();
        this.beaconProduct = beaconEntity.getBeaconProduct();
        this.beaconType = beaconEntity.getBeaconType();
        this.beaconAllow = beaconEntity.getBeaconAllow();
        this.beaconRemark = beaconEntity.getBeaconRemark();
        this.beaconRssi = beaconEntity.getBeaconRssi();
        this.beaconChannel = beaconEntity.getBeaconChannel();
        this.beaconFrequency = beaconEntity.getBeaconFrequency();
        this.beaconPowerType = beaconEntity.getBeaconPowerType();
        this.beaconSos = beaconEntity.getBeaconSos();
        this.beaconSosTime = beaconEntity.getBeaconSosTime();
        this.beaconBattery = beaconEntity.getBeaconBattery();
        this.beaconLastGateway = beaconEntity.getBeaconLastGateway();
        this.beaconNearestGateway = beaconEntity.getBeaconNearestGateway();
        this.beaconLastLocalServerMac = beaconEntity.getBeaconLastLocalServerMac();
        this.beaconMapId = beaconEntity.getBeaconMapId();
        this.beaconZoneId = beaconEntity.getBeaconZoneId();
        this.beaconZoneName = beaconEntity.getBeaconZoneName();

        if (beaconEntity.getBeaconLocationObject() != null) {
            this.beaconLocationObject = new LocationObjectDTO(beaconEntity.getBeaconLocationObject());
        }
    }

    public BeaconDTO(SimpleBeaconEntity beaconEntity) {
        this.beaconId = beaconEntity.getBeaconId();
        this.beaconMac = beaconEntity.getBeaconMac();
        this.beaconUniqueId = this.beaconMac; // ❗uniqueId = mac

        this.beaconX = beaconEntity.getBeaconX();
        this.beaconY = beaconEntity.getBeaconY();
        this.beaconZ = beaconEntity.getBeaconZ();
        this.beaconOnline = beaconEntity.getBeaconOnline();
        this.beaconLastTime = beaconEntity.getBeaconLastTime();
        this.beaconJoinTime = beaconEntity.getBeaconJoinTime();
        this.beaconProduct = beaconEntity.getBeaconProduct();
        this.beaconType = beaconEntity.getBeaconType();
        this.beaconAllow = beaconEntity.getBeaconAllow();
        this.beaconRemark = beaconEntity.getBeaconRemark();
        this.beaconRssi = beaconEntity.getBeaconRssi();
        this.beaconChannel = beaconEntity.getBeaconChannel();
        this.beaconFrequency = beaconEntity.getBeaconFrequency();
        this.beaconPowerType = beaconEntity.getBeaconPowerType();
        this.beaconSos = beaconEntity.getBeaconSos();
        this.beaconSosTime = beaconEntity.getBeaconSosTime();
        this.beaconBattery = beaconEntity.getBeaconBattery();
        this.beaconLastGateway = beaconEntity.getBeaconLastGateway();
        this.beaconNearestGateway = beaconEntity.getBeaconNearestGateway();
        this.beaconLastLocalServerMac = beaconEntity.getBeaconLastLocalServerMac();
        this.beaconMapId = beaconEntity.getBeaconMapId();
        this.beaconZoneId = beaconEntity.getBeaconZoneId();
        this.beaconZoneName = beaconEntity.getBeaconZoneName();

        if (beaconEntity.getBeaconLocationObject() != null) {
            this.beaconLocationObject = new LocationObjectDTO(beaconEntity.getBeaconLocationObject());
        }
    }
}
