package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.common.message.BaseException;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.FenceEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.MapEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.FenceEntityRepository;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.MapEntityRepository;
import com.ifengniao.server.xiyoucloud.dto.FenceDTO;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FenceService {

    @Autowired
    private FenceEntityRepository fenceEntityRepository;
    @Autowired
    private MapEntityRepository mapEntityRepository;

    public List<FenceEntity> findAll() {
        return fenceEntityRepository.findAll();
    }

    public List<FenceEntity> findAllByFenceMap(int mapId) {
        return fenceEntityRepository.findAllByFenceMapAndFenceStatusIsTrue(new MapEntity(mapId));
    }

    //按Postgis方法查询，每次查询一个点，需要与数据库交互多次，于是改成了上方findAllByFenceMap，然后调用Polygon.contains()进行循环判断
    public List<FenceEntity> findAllByContainsPoint(int mapId, double x, double y) {
        return fenceEntityRepository.findAllByMapIdAndContainsPoint(mapId, new GeometryFactory().createPoint(new Coordinate(x, y)));
    }

    public FenceEntity findById(Integer id) {
        if (id == null) {
            return null;
        }
        return fenceEntityRepository.findById(id).orElse(null);
    }

    public FenceEntity save(FenceEntity fence) {
        return fenceEntityRepository.save(fence);
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public List<FenceEntity> saveAll(Iterable<FenceEntity> fence) {
        return fenceEntityRepository.saveAll(fence);
    }

    public void delete(FenceEntity fence) {
        fenceEntityRepository.delete(fence);
    }

    public Page<FenceEntity> getFenceByPage(Integer page, Integer size, String mapCpaName, String fenceName, Short fenceType, Boolean fenceStatus) {
        Page<FenceEntity> listPage;
        if (page < 0) {
            page = 0;
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(
                new Sort.Order(Sort.Direction.DESC, "fenceId")
        ));

        if (StringUtils.isNotBlank(mapCpaName) || StringUtils.isNotBlank(fenceName) || fenceType != null || fenceStatus != null) {
            Specification<FenceEntity> spec = (root, query, cb) -> {
                List<Predicate> andParam = new ArrayList<>();
                if (fenceType != null) {
                    andParam.add(cb.equal(root.get("fenceType"), fenceType));
                }
                if (fenceStatus != null) {
                    andParam.add(cb.equal(root.get("fenceStatus"), fenceStatus));
                }
                if (StringUtils.isNotBlank(fenceName)) {
                    andParam.add(cb.like(root.get("fenceName"), "%" + fenceName + "%"));
                }
                if (StringUtils.isNotBlank(mapCpaName)) {
                    andParam.add(cb.like(root.join("fenceMap").get("mapCpaName"), "%" + mapCpaName + "%"));
                }
                return cb.and(andParam.toArray(new Predicate[0]));
//                return cb.and(andParam.toArray(new Predicate[andParam.size()]));
            };
            listPage = fenceEntityRepository.findAll(spec, pageRequest);
        } else {
            listPage = fenceEntityRepository.findAll(pageRequest);
        }

        return listPage;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public FenceEntity saveOrUpdateFence(FenceDTO fenceDTO) throws Exception {
        if (StringUtils.isBlank(fenceDTO.getFenceName())) {
            throw new BaseException("围栏名称不能为空").setStrCode(Constants.ERR_1500);
        }
        if (fenceDTO.getFenceMap() == null || fenceDTO.getFenceMap().getMapId() == null) {
            throw new BaseException("围栏所属地图不能为空").setStrCode(Constants.ERR_1501);
        }
        if (fenceDTO.getFenceType() == null) {
            throw new BaseException("围栏类型不能为空").setStrCode(Constants.ERR_1502);
        }
        // 注意：FenceDTO中的 covertXyListToPolygon() 包含了边界校验逻辑，无需在此重复
        var polygon = fenceDTO.covertXyListToPolygon(); 
        var mapEntity = mapEntityRepository.findById(fenceDTO.getFenceMap().getMapId()).orElse(null);
        if (mapEntity == null) {
            throw new BaseException("围栏所属地图不能为空").setStrCode(Constants.ERR_1501);
        }
        FenceEntity fence;
        if (fenceDTO.getFenceId() == null) {
            //新增
            fence = new FenceEntity();
            fence.setFenceStatus(false);
        } else {
            //修改
            fence = findById(fenceDTO.getFenceId());
            if (fence == null) {
                throw new BaseException("围栏信息未找到").setStrCode(Constants.ERR_1503);
            }
        }
        
        // ==========================================================
        // DTO 到 Entity 的属性映射：新增的商品区域属性
        // ==========================================================
        fence.setObjectName(fenceDTO.getObjectName());
        fence.setBelongType(fenceDTO.getBelongType());
        fence.setIconUrl(fenceDTO.getIconUrl());
        // ==========================================================
        
        fence.setFenceName(fenceDTO.getFenceName());
        fence.setFenceType(fenceDTO.getFenceType());
        fence.setFenceContent(polygon);
        fence.setFenceMap(mapEntity);
        fence = save(fence);
        return fence;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public FenceEntity changeStatus(FenceDTO fenceDTO) throws Exception {
        var fence = findById(fenceDTO.getFenceId());
        if (fence == null) {
            throw new BaseException("围栏信息未找到").setStrCode(Constants.ERR_1503);
        }
        fence.setFenceStatus(fenceDTO.getFenceStatus());
        fence = save(fence);
        return fence;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public void deleteFence(FenceDTO fenceDTO) throws Exception {
        FenceEntity fence = findById(fenceDTO.getFenceId());
        if (fence == null) {
            throw new BaseException("围栏信息未找到").setStrCode(Constants.ERR_1503);
        }
        delete(fence);
    }

}