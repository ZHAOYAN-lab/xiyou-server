package com.ifengniao.server.xiyoucloud.db.postgresql.repository;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BeaconEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface BeaconEntityRepository extends JpaRepositoryImplementation<BeaconEntity, Integer> {

    BeaconEntity findByBeaconMac(String beaconMac);

    List<BeaconEntity> findAllByBeaconLastTimeBefore(Long beaconLastTime);

    @Query(value = "select distinct beacon_product from xy_beacon where beacon_product != ''", nativeQuery = true)
    List<String> fetchDistinctBeaconProduct();

    @Modifying
    @Query(value = "update xy_beacon set beacon_online = false where beacon_last_time < :lastTime", nativeQuery = true)
    void updateBeaconEntitySetOnlineFalseByLastTimeBefore(@Param(value = "lastTime") Long lastTime);

    @Query(value = """
             select beacon_online as online, count(*) as count
             from xy_beacon
             where beacon_location_object is not null
             group by online
            """, nativeQuery = true)
    List<Map<String, Object>> countGroupByOnline();




}