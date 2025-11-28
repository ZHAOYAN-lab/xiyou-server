package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.server.xiyoucloud.entity.ProductAreaEntity;
import com.ifengniao.server.xiyoucloud.mapper.ProductAreaMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductAreaService {

    @Autowired
    private ProductAreaMapper productAreaMapper;

    /**
     * 查询列表 + 自动补 mapNames
     */
    public List<ProductAreaEntity> list(ProductAreaEntity query) {

        List<ProductAreaEntity> list = productAreaMapper.list(query);

        for (ProductAreaEntity item : list) {

            if (item.getMapIds() != null && !item.getMapIds().trim().isEmpty()) {
                try {
                    List<Integer> ids = Arrays.stream(item.getMapIds().split(","))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());

                    List<String> names = productAreaMapper.getMapNamesByIds(ids);

                    item.setMapNames(
                            names.isEmpty() ? "未绑定" : String.join(", ", names)
                    );

                } catch (Exception e) {
                    log.error("mapIds 解析失败: {}", item.getMapIds(), e);
                    item.setMapNames("未绑定");
                }
            } else {
                item.setMapNames("未绑定");
            }
        }

        return list;
    }

    @Transactional
    public int add(ProductAreaEntity entity) {
        entity.setCreateTime(LocalDateTime.now().toString());
        entity.setUpdateTime(LocalDateTime.now().toString());
        return productAreaMapper.add(entity);
    }

    @Transactional
    public int update(ProductAreaEntity entity) {
        entity.setUpdateTime(LocalDateTime.now().toString());
        return productAreaMapper.update(entity);
    }

    @Transactional
    public int delete(Integer areaId) {
        if (areaId == null) {
            throw new RuntimeException("areaId 不能为空");
        }
        int rows = productAreaMapper.delete(areaId);
        if (rows == 0) {
            throw new RuntimeException("删除失败，记录不存在");
        }
        return rows;
    }

    public List<String> getTypes() {
        return Arrays.asList("货物种类1", "货物种类2", "货物种类3");
    }
}
