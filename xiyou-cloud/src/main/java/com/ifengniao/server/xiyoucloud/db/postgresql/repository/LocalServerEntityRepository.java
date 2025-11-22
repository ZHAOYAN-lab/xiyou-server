package com.ifengniao.server.xiyoucloud.db.postgresql.repository;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BuildingEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.LocalServerEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocalServerEntityRepository extends JpaRepositoryImplementation<LocalServerEntity, Integer> {

    LocalServerEntity findByLocalServerMac(String localServerMac);

    List<LocalServerEntity> findAllByLocalServerLastTimeBefore(Long localServerLastTime);

    @Modifying
    @Query(value = "update xy_local_server set local_server_online = false where local_server_last_time < :lastTime", nativeQuery = true)
    void updateLocalServerEntitySetOnlineFalseByLastTimeBefore(@Param(value = "lastTime") Long lastTime);

    List<LocalServerEntity> findAllByLocalServerOnlineIsTrueAndLocalServerCleActivationIsTrueAndLocalServerBuildingIsNull();

    List<LocalServerEntity> findAllByLocalServerBuilding(BuildingEntity localServerBuilding);

}