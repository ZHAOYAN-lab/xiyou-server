package com.ifengniao.server.xiyoucloud.db.postgresql.repository;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.SimpleLocationObjectEntity;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;

public interface SimpleLocationObjectEntityRepository extends JpaRepositoryImplementation<SimpleLocationObjectEntity, Integer> {

    SimpleLocationObjectEntity findByLocationObjectName(String locationObjectName);

    List<SimpleLocationObjectEntity> findAllByLocationObjectType(Short locationObjectType);

}