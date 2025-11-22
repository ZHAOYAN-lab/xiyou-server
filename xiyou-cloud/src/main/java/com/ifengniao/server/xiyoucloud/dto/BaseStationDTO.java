package com.ifengniao.server.xiyoucloud.dto;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BaseStationEntity;
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
public class BaseStationDTO {
    private Integer baseStationId;
    private String baseStationMac;
    private String baseStationIp;
    private String baseStationElevation;
    private String baseStationAzimuth;
    private String baseStationRotation;
    private String baseStationErrorDegree;
    private String baseStationX;
    private String baseStationY;
    private String baseStationZ;
    private Boolean baseStationOnline;
    private Long baseStationLastTime;
    private String baseStationName;
    private String baseStationType;
    private String baseStationProduct;
    private Object baseStationChannels;

    public static List<BaseStationDTO> covert(Collection<BaseStationEntity> list) {
        List<BaseStationDTO> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new BaseStationDTO(source));
            }
        }
        return result;
    }

    public BaseStationDTO(BaseStationEntity baseStationEntity) {
        this.baseStationId = baseStationEntity.getBaseStationId();
        this.baseStationMac = baseStationEntity.getBaseStationMac();
        this.baseStationIp = baseStationEntity.getBaseStationIp();
        this.baseStationElevation = baseStationEntity.getBaseStationElevation();
        this.baseStationAzimuth = baseStationEntity.getBaseStationAzimuth();
        this.baseStationRotation = baseStationEntity.getBaseStationRotation();
        this.baseStationErrorDegree = baseStationEntity.getBaseStationErrorDegree();
        this.baseStationX = baseStationEntity.getBaseStationX();
        this.baseStationY = baseStationEntity.getBaseStationY();
        this.baseStationZ = baseStationEntity.getBaseStationZ();
        this.baseStationOnline = baseStationEntity.getBaseStationOnline();
        this.baseStationLastTime = baseStationEntity.getBaseStationLastTime();
        this.baseStationName = baseStationEntity.getBaseStationName();
        this.baseStationType = baseStationEntity.getBaseStationType();
        this.baseStationProduct = baseStationEntity.getBaseStationProduct();
        this.baseStationChannels = baseStationEntity.getBaseStationChannels();
    }

}
