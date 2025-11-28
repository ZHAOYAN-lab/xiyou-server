package com.ifengniao.server.xiyoucloud.mapper;

import com.ifengniao.server.xiyoucloud.entity.ProductAreaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductAreaMapper {

    List<ProductAreaEntity> list(ProductAreaEntity query);

    int add(ProductAreaEntity entity);

    int update(ProductAreaEntity entity);

    int delete(@Param("areaId") Integer areaId);

    // ⭐ 新增：根据 mapIds 查地图名称
    List<String> getMapNamesByIds(@Param("ids") List<Integer> ids);
}
