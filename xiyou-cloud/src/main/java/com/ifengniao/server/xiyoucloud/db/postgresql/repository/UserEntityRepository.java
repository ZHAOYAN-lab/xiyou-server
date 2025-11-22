package com.ifengniao.server.xiyoucloud.db.postgresql.repository;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.UserEntity;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

public interface UserEntityRepository extends JpaRepositoryImplementation<UserEntity, Integer> {

    UserEntity findByUserName(String userName);

}