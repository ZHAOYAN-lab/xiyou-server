package com.ifengniao.server.xiyoucloud.db.postgresql.repository;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.AlarmEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface AlarmEntityRepository extends JpaRepositoryImplementation<AlarmEntity, Integer> {

    @Query(value = """
             select alarm_type as type, alarm_status as status, count(*) as count
             from xy_alarm
             where alarm_time >= :startTime
             group by type, status
            """, nativeQuery = true)
    List<Map<String, Object>> countGroupByTypeAndStatus(@Param(value = "startTime") Long startTime);

    @Query(value = """
             select alarm_location_object, count(*) as count
             from xy_alarm
             where alarm_time >= :startTime
             group by alarm_location_object
             order by count desc, alarm_location_object
             limit 5
            """, nativeQuery = true)
    List<Map<String, Object>> countGroupByLocationObjectTodayTop5(@Param(value = "startTime") Long startTime);

    @Query(value = """
             select (alarm_time / 60000 * 60000) as time, count(*) as count
             from xy_alarm
             where alarm_time >= :startTime
             group by time
             order by time
            """, nativeQuery = true)
    List<Map<String, Object>> countGroupByLast24Hours(@Param(value = "startTime") Long startTime);

}