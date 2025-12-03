package com.ifengniao.server.xiyoucloud.mapper;

import com.ifengniao.server.xiyoucloud.dto.MobileTaskDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TaskMobileMapper {

    MobileTaskDto getMyTask(@Param("objectName") String objectName);
}
