package com.ifengniao.server.xiyoucloud.db.postgresql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "xy_alarm", schema = "public", catalog = "xiyou")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class AlarmEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "alarm_id")
    private Integer alarmId;
    @Basic
    @Column(name = "alarm_type")
    private Short alarmType;
    @Basic
    @Column(name = "alarm_status")
    private Boolean alarmStatus;
    @Basic
    @Column(name = "alarm_time")
    private Long alarmTime;
    @ManyToOne
    @JoinColumn(name = "alarm_location_object", referencedColumnName = "location_object_id", nullable = false)
    private LocationObjectEntity alarmLocationObject;
    @ManyToOne
    @JoinColumn(name = "alarm_fence", referencedColumnName = "fence_id", nullable = true)
    private FenceEntity alarmFence;
    @ManyToOne
    @JoinColumn(name = "alarm_map", referencedColumnName = "map_id", nullable = false)
    private MapEntity alarmMap;
}
