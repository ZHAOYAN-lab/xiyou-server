package com.ifengniao.server.xiyoulocal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class BeaconDTO {
    private Integer beaconId;
    private String beaconMac;
    private String beaconUniqueId; // ❗保留用于兼容，但最终 = beaconMac
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
    private String beaconMapId;
    private String beaconZoneId;
    private String beaconZoneName;

    public BeaconDTO(String mac, CleBeaconDTO cleBeaconDTO) {

        // ❗uniqueId 不再使用，直接同步为 mac
        this.beaconUniqueId = mac;

        // MAC 使用 CLE 的 userData.mac > 传入 mac
        if (cleBeaconDTO.getUserData() != null &&
            cleBeaconDTO.getUserData().get("mac") != null) {
            this.beaconMac = cleBeaconDTO.getUserData().get("mac").toString();
        } else {
            this.beaconMac = mac == null ? "" : mac;
        }

        this.beaconX = cleBeaconDTO.getX() == null ? "" : String.format("%.02f", cleBeaconDTO.getX());
        this.beaconY = cleBeaconDTO.getY() == null ? "" : String.format("%.02f", cleBeaconDTO.getY());
        this.beaconZ = cleBeaconDTO.getZ() == null ? "" : String.format("%.02f", cleBeaconDTO.getZ());
        this.beaconLastTime = cleBeaconDTO.getUpdatedAt() == null ? -1L : cleBeaconDTO.getUpdatedAt();
        this.beaconRssi = cleBeaconDTO.getRssi() == null ? "" : cleBeaconDTO.getRssi().toString();

        var data = (Map<String, Object>) cleBeaconDTO.getUserData().get("0");
        if (data != null) {
            this.beaconChannel = data.get("channel") == null ? "" : data.get("channel").toString();
            this.beaconFrequency = data.get("frequency") == null ? "" : data.get("frequency").toString();
            var power = (Map<String, Object>) data.get("power");
            this.beaconPowerType = power == null || power.get("type") == null ? "" : power.get("type").toString();
            this.beaconSos = data.get("sos") != null && (Boolean) data.get("sos");
            this.beaconSosTime = data.get("lastPressedTS") == null ? -1L : (Long) data.get("lastPressedTS");
            this.beaconBattery = data.get("battery") == null ? "" : data.get("battery").toString();
        } else {
            this.beaconChannel = "";
            this.beaconFrequency = "";
            this.beaconPowerType = "";
            this.beaconSos = false;
            this.beaconSosTime = -1L;
            this.beaconBattery = "";
        }

        this.beaconLastGateway = cleBeaconDTO.getLastGateway() == null ? "" : cleBeaconDTO.getLastGateway();
        this.beaconNearestGateway = cleBeaconDTO.getNearestGateway() == null ? "" : cleBeaconDTO.getNearestGateway();
        this.beaconMapId = cleBeaconDTO.getMapId() == null ? "" : cleBeaconDTO.getMapId();
        this.beaconZoneId = cleBeaconDTO.getZoneId() == null ? "" : cleBeaconDTO.getZoneId();
        this.beaconZoneName = cleBeaconDTO.getZoneName() == null ? "" : cleBeaconDTO.getZoneName();
    }
}
