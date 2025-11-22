package com.ifengniao.server.xiyoucloud.dto;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.AlarmEntity;
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
public class AlarmDTO {
    private Integer alarmId;
    private Short alarmType;
    private Boolean alarmStatus;
    private Long alarmTime;
    private LocationObjectWithBeaconDTO alarmLocationObject;
    private FenceWithoutMapDTO alarmFence;
    private MapWithoutBaseStationDTO alarmMap;

    public static List<AlarmDTO> covert(Collection<AlarmEntity> list) {
        List<AlarmDTO> result = new ArrayList<>();
        if (list != null) {
            for (var source : list) {
                result.add(new AlarmDTO(source));
            }
        }
        return result;
    }

    public AlarmDTO(AlarmEntity alarmEntity) {
        this.alarmId = alarmEntity.getAlarmId();
        this.alarmType = alarmEntity.getAlarmType();
        this.alarmStatus = alarmEntity.getAlarmStatus();
        this.alarmTime = alarmEntity.getAlarmTime();
        this.alarmLocationObject = new LocationObjectWithBeaconDTO(alarmEntity.getAlarmLocationObject());
        this.alarmFence = new FenceWithoutMapDTO(alarmEntity.getAlarmFence());
        this.alarmMap = new MapWithoutBaseStationDTO(alarmEntity.getAlarmMap());
    }
}
