package com.ifengniao.server.xiyoucloud.db.postgresql.repository;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.SimpleBeaconEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SimpleBeaconEntityRepository extends JpaRepositoryImplementation<SimpleBeaconEntity, Integer> {

    SimpleBeaconEntity findByBeaconMac(String beaconMac);

    List<SimpleBeaconEntity> findAllByBeaconLastTimeBefore(Long beaconLastTime);

    @Modifying
    @Query("update SimpleBeaconEntity be set be.beaconOnline = false where be.beaconLastTime < :lastTime")
    void updateBeaconEntitySetOnlineFalseByLastTimeBefore(@Param(value = "lastTime") Long lastTime);

}