package com.ifengniao.server.xiyoucloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class BeaconMqttResultDTO {

    private List<AlarmDTO> alarmDTOList;
    private HashMap<Integer, List<BeaconDTO>> beaconDTOHistoryMap;
    private HashMap<Integer, List<BeaconDTO>> beaconDTOMqttMap;

}
