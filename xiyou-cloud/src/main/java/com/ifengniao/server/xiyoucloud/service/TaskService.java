package com.ifengniao.server.xiyoucloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifengniao.server.xiyoucloud.entity.Task;

/**
 * 任务 Service 接口
 */
public interface TaskService extends IService<Task> {

    /**
     * 按状态安全删除任务
     * 仅「待派发」允许删除
     */
    void deleteTaskWithStatusCheck(Long id);
}
