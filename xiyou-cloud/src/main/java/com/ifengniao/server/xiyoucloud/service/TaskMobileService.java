package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.server.xiyoucloud.dto.MobileTaskDto;
import com.ifengniao.server.xiyoucloud.mapper.TaskMobileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskMobileService {

    @Autowired
    private TaskMobileMapper taskMobileMapper;

    public MobileTaskDto getMyTask(String objectName) {
        return taskMobileMapper.getMyTask(objectName);
    }
}
