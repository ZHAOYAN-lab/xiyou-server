package com.ifengniao.server.xiyoucloud.db.postgresql.repository;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.MapEntity;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface MapEntityRepository extends JpaRepositoryImplementation<MapEntity, Integer> {

    MapEntity findByMapCpaIdAndMapLocalServer_LocalServerMac(String mapCpaId, String mapLocalServer_localServerMac);

}