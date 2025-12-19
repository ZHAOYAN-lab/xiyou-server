package com.ifengniao.server.xiyoucloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifengniao.server.xiyoucloud.entity.Task;
import com.ifengniao.server.xiyoucloud.mapper.TaskMapper;
import com.ifengniao.server.xiyoucloud.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl
        extends ServiceImpl<TaskMapper, Task>
        implements TaskService {

    @Override
    public void deleteTaskWithStatusCheck(Integer id) {

        Task task = getById(id);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        if ("已派发".equals(task.getStatus()) || "执行中".equals(task.getStatus())) {
            throw new RuntimeException("任务已派发或执行中，必须先取消后才能删除");
        }

        // 物理删除（你当前设计就是物理删除）
        removeById(id);
    }

    @Override
    public List<Task> listMyTasks(String username) {

        return this.list(
            new QueryWrapper<Task>()
                .eq("is_deleted", 0)
                .like("assigned_to", username)
                .orderByDesc("update_time")
        );
    }
}
