package com.ifengniao.server.xiyoucloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifengniao.server.xiyoucloud.entity.Task;
import com.ifengniao.server.xiyoucloud.mapper.TaskMapper;
import com.ifengniao.server.xiyoucloud.service.TaskService;
import org.springframework.stereotype.Service;

/**
 * 任务 Service 实现类
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    // 继承 ServiceImpl，自动实现基础方法
}