package com.ifengniao.server.xiyoulocal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Collections;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class BaseStationDTO {
    private Integer baseStationId;//
    private String baseStationMac;////
    private String baseStationIp;//
    private String baseStationElevation;////
    private String baseStationAzimuth;////
    private String baseStationRotation;////
    private String baseStationErrorDegree;////
    private String baseStationX;////
    private String baseStationY;////
    private String baseStationZ;////
    private Boolean baseStationOnline;//
    private Long baseStationLastTime;//
    private String baseStationName;////
    private String baseStationType;////
    private String baseStationProduct;////
    private Object baseStationChannels;//

    public BaseStationDTO(String mac, CleLocatorDTO cleLocatorDTO) {
        this.baseStationMac = mac == null ? "" : mac;
        this.baseStationIp = cleLocatorDTO.getIp() == null ? "" : cleLocatorDTO.getIp();
        this.baseStationElevation = cleLocatorDTO.getInfo().getElevation() == null ? "" : cleLocatorDTO.getInfo().getElevation().toString();
        this.baseStationAzimuth = cleLocatorDTO.getInfo().getAzimuth() == null ? "" : cleLocatorDTO.getInfo().getAzimuth().toString();
        this.baseStationRotation = cleLocatorDTO.getInfo().getRotation() == null ? "" : cleLocatorDTO.getInfo().getRotation().toString();
        this.baseStationErrorDegree = cleLocatorDTO.getInfo().getErrorDegree() == null ? "" : cleLocatorDTO.getInfo().getErrorDegree().toString();
        this.baseStationX = cleLocatorDTO.getInfo().getX() == null ? "" : cleLocatorDTO.getInfo().getX().toString();
        this.baseStationY = cleLocatorDTO.getInfo().getY() == null ? "" : cleLocatorDTO.getInfo().getY().toString();
        this.baseStationZ = cleLocatorDTO.getInfo().getZ() == null ? "" : cleLocatorDTO.getInfo().getZ().toString();
        this.baseStationOnline = cleLocatorDTO.getOnline() != null && cleLocatorDTO.getOnline();
        this.baseStationName = cleLocatorDTO.getInfo().getName() == null ? "" : cleLocatorDTO.getInfo().getName();
        this.baseStationType = cleLocatorDTO.getInfo().getType() == null ? "" : cleLocatorDTO.getInfo().getType();
        this.baseStationProduct = cleLocatorDTO.getInfo().getModelName() == null ? "" : cleLocatorDTO.getInfo().getModelName();
        this.baseStationChannels = cleLocatorDTO.getInfo().getChannels() == null ? Collections.emptyList() : cleLocatorDTO.getInfo().getChannels();
    }
}
