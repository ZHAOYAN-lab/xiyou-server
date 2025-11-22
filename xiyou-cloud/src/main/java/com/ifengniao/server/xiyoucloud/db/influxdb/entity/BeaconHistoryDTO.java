package com.ifengniao.server.xiyoucloud.db.influxdb.entity;

import com.ifengniao.server.xiyoucloud.dto.BeaconDTO;
import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
@Measurement(name = "beacon_history")
public class BeaconHistoryDTO {
    @Column(tag = true)
    String mapId;
    @Column(tag = true)
    String loId;
    @Column
    String x;
    @Column
    String y;
    @Column(timestamp = true)
    Instant time;

    public static List<BeaconHistoryDTO> covert(Collection<BeaconDTO> list, String mapId) {
        List<BeaconHistoryDTO> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new BeaconHistoryDTO(source, mapId));
            }
        }
        return result;
    }

    public BeaconHistoryDTO(BeaconDTO beaconDTO, String mapId) {
        this.mapId = mapId;
        this.loId = beaconDTO.getBeaconLocationObject().getLocationObjectId().toString();
        this.x = beaconDTO.getBeaconX();
        this.y = beaconDTO.getBeaconY();
        this.time = Instant.ofEpochMilli(beaconDTO.getBeaconLastTime());
    }
}