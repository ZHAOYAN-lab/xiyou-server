package com.ifengniao.server.xiyoucloud.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface NavigationMapper {

    // 使用 Long，不要使用 Integer
    Map<String, Object> getProductAreaGeom(@Param("areaId") Long areaId);

    // mapId 也是 Long
    List<Map<String, Object>> getMapWalkable(@Param("mapId") Long mapId);
}
