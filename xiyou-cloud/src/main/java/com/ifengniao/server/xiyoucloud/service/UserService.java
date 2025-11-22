package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.server.xiyoucloud.db.postgresql.entity.UserEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.UserEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserEntityRepository userEntityRepository;

    public List<UserEntity> findAll() {
        return userEntityRepository.findAll();
    }

    public UserEntity findById(Integer id) {
        if (id == null) {
            return null;
        }
        return userEntityRepository.findById(id).orElse(null);
    }

    public UserEntity findByUserName(String userName) {
        return userEntityRepository.findByUserName(userName);
    }

    public UserEntity save(UserEntity user) {
        return userEntityRepository.save(user);
    }

    public void delete(UserEntity user) {
        userEntityRepository.delete(user);
    }

}
