package com.ifengniao.server.xiyoucloud.db.postgresql.repository;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BuildingEntity;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface BuildingEntityRepository extends JpaRepositoryImplementation<BuildingEntity, Integer> {

    BuildingEntity findByBuildingName(String buildingName);

}