package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.server.xiyoucloud.entity.ProductAreaEntity;
import com.ifengniao.server.xiyoucloud.mapper.ProductAreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductAreaService {

    @Autowired
    private ProductAreaMapper productAreaMapper;

    /**
     * 查询列表
     */
    public List<ProductAreaEntity> list(ProductAreaEntity query) {
        return productAreaMapper.list(query);
    }

    /**
     * 新增
     */
    public int add(ProductAreaEntity entity) {
        entity.setCreateTime(LocalDateTime.now().toString());
        entity.setUpdateTime(LocalDateTime.now().toString());
        return productAreaMapper.add(entity);
    }

    /**
     * 修改
     */
    public int update(ProductAreaEntity entity) {
        entity.setUpdateTime(LocalDateTime.now().toString());
        return productAreaMapper.update(entity);
    }

    /**
     * 删除
     */
    public int delete(Integer areaId) {
        return productAreaMapper.delete(areaId);
    }

    /**
     * 所属类型列表（这里写死三条数据，你可随时改成数据库查询）
     */
    public List<String> getTypes() {
        List<String> list = new ArrayList<>();
        list.add("货物种类1");
        list.add("货物种类2");
        list.add("货物种类3");
        return list;
    }
}
