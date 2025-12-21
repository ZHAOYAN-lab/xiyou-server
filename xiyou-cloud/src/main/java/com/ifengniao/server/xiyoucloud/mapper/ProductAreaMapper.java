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

    // ★ 根据 mapIds 查询地图名称
    List<String> getMapNamesByIds(@Param("ids") List<Integer> ids);

    // ✅【新增】根据 areaId 查询商品区域详情（H5 使用）
    ProductAreaEntity getById(@Param("areaId") Integer areaId);
}
