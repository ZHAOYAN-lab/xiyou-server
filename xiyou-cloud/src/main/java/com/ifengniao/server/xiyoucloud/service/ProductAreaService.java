package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.server.xiyoucloud.entity.ProductAreaEntity;
import com.ifengniao.server.xiyoucloud.mapper.ProductAreaMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j  // ⭐ 加日志
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
    @Transactional  // ⭐ 加事务
    public int add(ProductAreaEntity entity) {
        entity.setCreateTime(LocalDateTime.now().toString());
        entity.setUpdateTime(LocalDateTime.now().toString());
        return productAreaMapper.add(entity);
    }

    /**
     * 修改
     */
    @Transactional  // ⭐ 加事务
    public int update(ProductAreaEntity entity) {
        entity.setUpdateTime(LocalDateTime.now().toString());
        return productAreaMapper.update(entity);
    }

    /**
     * 删除
     */
    @Transactional  // ⭐ 加事务
    public int delete(Integer areaId) {
        log.info("🔴 Service: 准备删除 areaId = {}", areaId);
        
        if (areaId == null) {
            log.error("🔴 Service: areaId 是 null！");
            throw new RuntimeException("areaId 不能为空");
        }
        
        int rows = productAreaMapper.delete(areaId);
        log.info("🔴 Service: 删除影响行数 = {}", rows);
        
        if (rows == 0) {
            log.error("🔴 Service: 删除失败，未找到 areaId = {} 的记录", areaId);
            throw new RuntimeException("删除失败，未找到该记录");
        }
        
        log.info("🔴 Service: 删除成功！");
        return rows;
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