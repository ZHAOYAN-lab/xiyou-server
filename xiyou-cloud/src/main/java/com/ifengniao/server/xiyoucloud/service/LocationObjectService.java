package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.common.message.BaseException;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.LocationObjectEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.MapEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.BeaconEntityRepository;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.LocationObjectEntityRepository;
import com.ifengniao.server.xiyoucloud.dto.LocationObjectDTO;
import com.ifengniao.server.xiyoucloud.dto.MapWithoutBaseStationDTO;
import com.ifengniao.server.xiyoucloud.util.HandleFileUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
public class LocationObjectService {

    @Autowired
    private LocationObjectEntityRepository locationObjectEntityRepository;
    @Autowired
    private BeaconEntityRepository beaconEntityRepository;

    public List<LocationObjectEntity> findAll() {
        return locationObjectEntityRepository.findAll();
    }

    public List<LocationObjectEntity> getAvailableLocationObjectList() {
        return locationObjectEntityRepository.findAllByLocationObjectBeaconIsNotNullOrderByLocationObjectNameAsc();
    }

    public List<LocationObjectEntity> findByType(short type) {
        return locationObjectEntityRepository.findAllByLocationObjectTypeAndLocationObjectBeaconIsNullOrderByLocationObjectNameAsc(type);
    }

    @Transactional(readOnly = true)
    public List<LocationObjectEntity> findByBeaconId(Integer beaconId) throws Exception {
        if (beaconId == null) {
            throw new BaseException("信标信息未找到").setStrCode(Constants.ERR_1303);
        }
        var beacon = beaconEntityRepository.findById(beaconId).orElse(null);
        if (beacon == null) {
            throw new BaseException("信标信息未找到").setStrCode(Constants.ERR_1303);
        }
        if (beacon.getBeaconType() == Constants.BEACON_AND_LOCATION_OBJECT_TYPE_NONE) {
            throw new BaseException("未知类型信标无法配置对象").setStrCode(Constants.ERR_1307);
        }
        var loList = locationObjectEntityRepository.findAllByLocationObjectTypeAndLocationObjectBeaconIsNullOrderByLocationObjectNameAsc(beacon.getBeaconType());
        var result = new ArrayList<LocationObjectEntity>();
        if (beacon.getBeaconLocationObject() != null) {
            result.add(beacon.getBeaconLocationObject());
        }
        result.addAll(loList);
        return result;
    }

    public LocationObjectEntity findById(Integer id) {
        if (id == null) {
            return null;
        }
        return locationObjectEntityRepository.findById(id).orElse(null);
    }

    public LocationObjectEntity findByName(String name) {
        return locationObjectEntityRepository.findByLocationObjectName(name);
    }

    public LocationObjectEntity save(LocationObjectEntity locationObject) {
        return locationObjectEntityRepository.save(locationObject);
    }

    public void delete(LocationObjectEntity locationObject) {
        locationObjectEntityRepository.delete(locationObject);
    }

    public Page<LocationObjectEntity> getLocationObjectByPage(Integer page, Integer size, String locationObjectName, List<Short> locationObjectType,
                                                              String order, Boolean asc) {
        Page<LocationObjectEntity> listPage;
        if (page < 0) {
            page = 0;
        }

        if (StringUtils.isBlank(order)) {
            order = "locationObjectId";
        }
        Sort.Direction direction = Sort.Direction.DESC;
        if (asc != null && asc) {
            direction = Sort.Direction.ASC;
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(
                new Sort.Order(direction, order)
        ));

        if (locationObjectType != null && locationObjectType.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
        }

        if (StringUtils.isNotBlank(locationObjectName) || locationObjectType != null) {
            Specification<LocationObjectEntity> spec = (root, query, cb) -> {
                List<Predicate> andParam = new ArrayList<>();
                if (StringUtils.isNotBlank(locationObjectName)) {
                    andParam.add(cb.like(root.get("locationObjectName"), "%" + locationObjectName + "%"));
                }
                if (locationObjectType != null) {
                    CriteriaBuilder.In<Object> in = cb.in(root.get("locationObjectType"));
                    in.value(locationObjectType);
                    andParam.add(in);
                }
                return cb.and(andParam.toArray(new Predicate[0]));
//                return cb.and(andParam.toArray(new Predicate[andParam.size()]));
            };
            listPage = locationObjectEntityRepository.findAll(spec, pageRequest);
        } else {
            listPage = locationObjectEntityRepository.findAll(pageRequest);
        }

        return listPage;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public LocationObjectEntity saveOrUpdateLocationObject(LocationObjectDTO locationObjectDTO) throws Exception {
        if (StringUtils.isBlank(locationObjectDTO.getLocationObjectName())) {
            throw new BaseException("定位对象名称不能为空").setStrCode(Constants.ERR_1401);
        }
        if (locationObjectDTO.getLocationObjectType() == null) {
            throw new BaseException("定位对象类型不能为空").setStrCode(Constants.ERR_1402);
        }
        var now = System.currentTimeMillis();
        LocationObjectEntity locationObject;
        var byName = findByName(locationObjectDTO.getLocationObjectName());
        HandleFileUtil.FileTransferModel fileTransferModel = null;
        if (locationObjectDTO.getLocationObjectId() == null) {
            //新增
            if (byName != null) {
                throw new BaseException("定位对象名称已存在").setStrCode(Constants.ERR_1403);
            }
            locationObject = new LocationObjectEntity();
            if (StringUtils.isBlank(locationObjectDTO.getLocationObjectImgFileName())) {
                //新上传空icon
                locationObject.setLocationObjectImg("");
            } else {
                //新上传有icon
                if (!locationObjectDTO.getLocationObjectImgSaveName().startsWith(Constants.DIR_TEMP)) {
                    throw new BaseException("上传文件不能为空").setStrCode(Constants.ERR_F100);
                }
                locationObject.setLocationObjectImg(locationObjectDTO.getLocationObjectImgFileName());
                fileTransferModel = new HandleFileUtil.FileTransferModel(locationObjectDTO.getLocationObjectImgSaveName(), null);
            }
            locationObject.setLocationObjectImgUpdateTime(now);
            locationObject.setLocationObjectCreateTime(now);
        } else {
            //修改
            locationObject = findById(locationObjectDTO.getLocationObjectId());
            if (locationObject == null) {
                throw new BaseException("定位对象信息未找到").setStrCode(Constants.ERR_1400);
            }
            if (byName != null && !byName.getLocationObjectId().equals(locationObject.getLocationObjectId())) {
                throw new BaseException("定位对象名称已存在").setStrCode(Constants.ERR_1403);
            }
            if (locationObject.getLocationObjectBeacon() != null) {
                //已绑定信标的，不能变更对象类型
                if (!Objects.equals(locationObject.getLocationObjectType(), locationObjectDTO.getLocationObjectType())) {
                    throw new BaseException("对象已绑定信标，不可修改类型").setStrCode(Constants.ERR_1404);
                }
            }
            var saveName = locationObject.getLocationObjectImgSaveName();
            if (StringUtils.isNotBlank(saveName) || StringUtils.isNotBlank(locationObjectDTO.getLocationObjectImgSaveName())) {
                if (StringUtils.isBlank(locationObjectDTO.getLocationObjectImgSaveName())) {
                    //去掉icon，暂时先不删原图
                    locationObject.setLocationObjectImg("");
                    locationObject.setLocationObjectImgUpdateTime(now);
                } else if (StringUtils.isBlank(saveName) || !Objects.equals(saveName, locationObjectDTO.getLocationObjectImgSaveName())) {
                    //新上传icon  //修改icon
                    if (!locationObjectDTO.getLocationObjectImgSaveName().startsWith(Constants.DIR_TEMP)) {
                        throw new BaseException("上传文件不能为空").setStrCode(Constants.ERR_F100);
                    }
                    locationObject.setLocationObjectImg(locationObjectDTO.getLocationObjectImgFileName());
                    fileTransferModel = new HandleFileUtil.FileTransferModel(locationObjectDTO.getLocationObjectImgSaveName(), null);
                    locationObject.setLocationObjectImgUpdateTime(now);
                }
            }
        }
        locationObject.setLocationObjectName(locationObjectDTO.getLocationObjectName());
        locationObject.setLocationObjectType(locationObjectDTO.getLocationObjectType());
        locationObject = save(locationObject);
        if (fileTransferModel != null) {
            fileTransferModel.setTo(locationObject.getLocationObjectImgSaveName());
            HandleFileUtil.saveTempFile(fileTransferModel, SysUrlService.pathPrefix);
        }
        return locationObject;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public void deleteLocationObject(LocationObjectDTO locationObjectDTO) throws Exception {
        LocationObjectEntity locationObject = findById(locationObjectDTO.getLocationObjectId());
        if (locationObject == null) {
            throw new BaseException("定位对象信息未找到").setStrCode(Constants.ERR_1400);
        }
        if (locationObject.getLocationObjectBeacon() != null) {
            throw new BaseException("已配置信标，不可删除").setStrCode(Constants.ERR_1405);
        }
        delete(locationObject);
        Files.deleteIfExists(Paths.get(SysUrlService.pathPrefix + locationObject.getLocationObjectImgSaveName()));
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public LocationObjectEntity locationObjectBindMap(LocationObjectDTO locationObjectDTO) throws Exception {
        LocationObjectEntity locationObject = findById(locationObjectDTO.getLocationObjectId());
        if (locationObject == null) {
            throw new BaseException("定位对象信息未找到").setStrCode(Constants.ERR_1400);
        }
        locationObject.getMapSet().clear();
        for (MapWithoutBaseStationDTO mapDTO : locationObjectDTO.getMapList()) {
            locationObject.getMapSet().add(new MapEntity(mapDTO.getMapId()));
        }
        locationObject = save(locationObject);
        return locationObject;
    }

}
