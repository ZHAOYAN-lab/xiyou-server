package com.ifengniao.server.xiyoucloud.db.postgresql.repository;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.ConfigEntity;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.Collection;
import java.util.List;

public interface ConfigEntityRepository extends JpaRepositoryImplementation<ConfigEntity, Integer> {

    List<ConfigEntity> findAllByOrderByConfigTypeAscConfigOrderAsc();

    List<ConfigEntity> findAllByConfigType(String configType);

    List<ConfigEntity> findAllByConfigTypeOrderByConfigOrderAsc(String configType);

    List<ConfigEntity> findAllByConfigTypeIn(Collection<String> configType);

    List<ConfigEntity> findAllByConfigTypeInOrderByConfigTypeAscConfigOrderAsc(Collection<String> configType);

    List<ConfigEntity> findAllByConfigTypeAndConfigValue(String configType, Integer configValues);

}