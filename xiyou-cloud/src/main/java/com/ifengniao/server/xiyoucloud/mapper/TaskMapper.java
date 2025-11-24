package com.ifengniao.server.xiyoucloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifengniao.server.xiyoucloud.entity.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务 Mapper 接口
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    // 继承 BaseMapper 后，自动拥有 CRUD 能力
}