package com.ifengniao.server.xiyoucloud.db.postgresql.repository;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BaseStationEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface BaseStationEntityRepository extends JpaRepositoryImplementation<BaseStationEntity, Integer> {

    BaseStationEntity findByBaseStationMacAndBaseStationMap_MapLocalServer_LocalServerMac(String baseStationMac, String baseStationMap_mapLocalServer_localServerMac);

    List<BaseStationEntity> findAllByBaseStationLastTimeBefore(Long baseStationLastTime);

    @Modifying
    @Query(value = "update xy_base_station set base_station_online = false where base_station_last_time < :lastTime", nativeQuery = true)
    void updateBaseStationEntitySetOnlineFalseByLastTimeBefore(@Param(value = "lastTime") Long lastTime);

    @Query(value = """
             select base_station_online as online, count(*) as count
             from xy_base_station
             group by online
            """, nativeQuery = true)
    List<Map<String, Object>> countGroupByOnline();

}