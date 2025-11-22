package com.ifengniao.server.xiyoucloud.dto.export;

import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.dto.BeaconDTO;

public interface IBeaconExportDTO {
    String getMac();

    String getType();

    String getProduct();

    String getRemark();

    default BeaconDTO toBeaconDTO() {
        var beaconDTO = new BeaconDTO();
        beaconDTO.setBeaconMac(this.getMac());
        if (this.getType() != null) {
            beaconDTO.setBeaconType(switch (this.getType()) {
                case Constants.BEACON_AND_LOCATION_OBJECT_TYPE_PERSON + "" ->
                        Constants.BEACON_AND_LOCATION_OBJECT_TYPE_PERSON;
                case Constants.BEACON_AND_LOCATION_OBJECT_TYPE_THING + "" ->
                        Constants.BEACON_AND_LOCATION_OBJECT_TYPE_THING;
                case Constants.BEACON_AND_LOCATION_OBJECT_TYPE_DEVICE + "" ->
                        Constants.BEACON_AND_LOCATION_OBJECT_TYPE_DEVICE;
                case Constants.BEACON_AND_LOCATION_OBJECT_TYPE_CAR + "" ->
                        Constants.BEACON_AND_LOCATION_OBJECT_TYPE_CAR;
                default -> Constants.BEACON_AND_LOCATION_OBJECT_TYPE_NONE;
            });
        } else {
            beaconDTO.setBeaconType(Constants.BEACON_AND_LOCATION_OBJECT_TYPE_NONE);
        }
        beaconDTO.setBeaconProduct(this.getProduct());
        beaconDTO.setBeaconRemark(this.getRemark());
        return beaconDTO;
    }
}
