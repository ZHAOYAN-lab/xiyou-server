package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.common.message.BaseException;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BuildingEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.BuildingEntityRepository;
import com.ifengniao.server.xiyoucloud.dto.BuildingDTO;
import com.ifengniao.server.xiyoucloud.util.HandleFileUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BuildingService {

    @Autowired
    private BuildingEntityRepository buildingEntityRepository;

    public List<BuildingEntity> findAll() {
        return buildingEntityRepository.findAll();
    }

    public BuildingEntity findById(Integer id) {
        if (id == null) {
            return null;
        }
        return buildingEntityRepository.findById(id).orElse(null);
    }

    public BuildingEntity findByName(String name) {
        return buildingEntityRepository.findByBuildingName(name);
    }

    public BuildingEntity save(BuildingEntity building) {
        return buildingEntityRepository.save(building);
    }

    public void delete(BuildingEntity building) {
        buildingEntityRepository.delete(building);
    }

    public Page<BuildingEntity> getBuildingByPage(Integer page, Integer size, String buildingName) {
        Page<BuildingEntity> listPage;
        if (page < 0) {
            page = 0;
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(
                new Sort.Order(Sort.Direction.DESC, "buildingId")
        ));

        if (StringUtils.isNotBlank(buildingName)) {
            Specification<BuildingEntity> spec = (root, query, cb) -> {
                List<Predicate> andParam = new ArrayList<>();
                andParam.add(cb.like(root.get("buildingName"), "%" + buildingName + "%"));
                return cb.and(andParam.toArray(new Predicate[0]));
//                return cb.and(andParam.toArray(new Predicate[andParam.size()]));
            };
            listPage = buildingEntityRepository.findAll(spec, pageRequest);
        } else {
            listPage = buildingEntityRepository.findAll(pageRequest);
        }

        return listPage;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public BuildingEntity saveOrUpdateBuilding(BuildingDTO buildingDTO) throws Exception {
        if (StringUtils.isBlank(buildingDTO.getBuildingName())) {
            throw new BaseException("建筑名称不能为空").setStrCode(Constants.ERR_1000);
        }
        BuildingEntity building;
        var byName = findByName(buildingDTO.getBuildingName());
        HandleFileUtil.FileTransferModel fileTransferModel = null;
        if (buildingDTO.getBuildingId() == null) {
            //新增
            if (byName != null) {
                throw new BaseException("建筑名称已存在").setStrCode(Constants.ERR_1001);
            }
            building = new BuildingEntity();
            if (StringUtils.isBlank(buildingDTO.getBuildingImgFileName())) {
                //新上传空图片
                building.setBuildingImg("");
            } else {
                //新上传有图片
                if (!buildingDTO.getBuildingImgSaveName().startsWith(Constants.DIR_TEMP)) {
                    throw new BaseException("上传文件不能为空").setStrCode(Constants.ERR_F100);
                }
                building.setBuildingImg(buildingDTO.getBuildingImgFileName());
                fileTransferModel = new HandleFileUtil.FileTransferModel(buildingDTO.getBuildingImgSaveName(), null);
            }
            building.setBuildingImgUpdateTime(System.currentTimeMillis());
        } else {
            //修改
            building = findById(buildingDTO.getBuildingId());
            if (building == null) {
                throw new BaseException("建筑信息未找到").setStrCode(Constants.ERR_1002);
            }
            if (byName != null && !byName.getBuildingId().equals(building.getBuildingId())) {
                throw new BaseException("建筑名称已存在").setStrCode(Constants.ERR_1001);
            }
            var saveName = building.getBuildingImgSaveName();
            if (StringUtils.isNotBlank(saveName) || StringUtils.isNotBlank(buildingDTO.getBuildingImgSaveName())) {
                if (StringUtils.isBlank(buildingDTO.getBuildingImgSaveName())) {
                    //去掉icon，暂时先不删原图
                    building.setBuildingImg("");
                    building.setBuildingImgUpdateTime(System.currentTimeMillis());
                } else if (StringUtils.isBlank(saveName) || !Objects.equals(saveName, buildingDTO.getBuildingImgSaveName())) {
                    //新上传icon   //修改icon
                    if (!buildingDTO.getBuildingImgSaveName().startsWith(Constants.DIR_TEMP)) {
                        throw new BaseException("上传文件不能为空").setStrCode(Constants.ERR_F100);
                    }
                    building.setBuildingImg(buildingDTO.getBuildingImgFileName());
                    fileTransferModel = new HandleFileUtil.FileTransferModel(buildingDTO.getBuildingImgSaveName(), null);
                    building.setBuildingImgUpdateTime(System.currentTimeMillis());
                }
            }
        }
        building.setBuildingName(buildingDTO.getBuildingName());
        building.setBuildingAddress(buildingDTO.getBuildingAddress() == null ? "" : buildingDTO.getBuildingAddress());
        building = save(building);
        if (fileTransferModel != null) {
            fileTransferModel.setTo(building.getBuildingImgSaveName());
            HandleFileUtil.saveTempFile(fileTransferModel, SysUrlService.pathPrefix);
        }
        return building;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public void deleteBuilding(BuildingDTO buildingDTO) throws Exception {
        BuildingEntity building = findById(buildingDTO.getBuildingId());
        if (building == null) {
            throw new BaseException("建筑信息未找到").setStrCode(Constants.ERR_1002);
        }
        delete(building);
        Files.deleteIfExists(Paths.get(SysUrlService.pathPrefix + building.getBuildingImgSaveName()));
    }

}



















