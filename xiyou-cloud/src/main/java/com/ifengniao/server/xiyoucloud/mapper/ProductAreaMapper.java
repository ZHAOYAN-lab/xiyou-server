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

    int delete(@Param("areaId") Integer areaId);  // ★ 关键修复点
}

