package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.common.message.BaseException;
import com.ifengniao.common.util.UUIDUtil;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BaseStationEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BuildingEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.LocalServerEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.MapEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.BaseStationEntityRepository;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.BuildingEntityRepository;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.LocalServerEntityRepository;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.MapEntityRepository;
import com.ifengniao.server.xiyoucloud.dto.BaseStationDTO;
import com.ifengniao.server.xiyoucloud.dto.LocalServerDTO;
import com.ifengniao.server.xiyoucloud.dto.MapDTO;
import com.ifengniao.server.xiyoucloud.util.HandleFileUtil;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class LocalServerService {

    @Autowired
    private BuildingEntityRepository buildingEntityRepository;
    @Autowired
    private LocalServerEntityRepository localServerEntityRepository;
    @Autowired
    private MapEntityRepository mapEntityRepository;
    @Autowired
    private BaseStationEntityRepository baseStationEntityRepository;
    @Autowired
    private RedisService redisService;

    public List<LocalServerEntity> findAll() {
        return localServerEntityRepository.findAll();
    }

    public List<LocalServerEntity> findAllByLocalServerBuilding(BuildingEntity localServerBuilding) {
        return localServerEntityRepository.findAllByLocalServerBuilding(localServerBuilding);
    }

    public LocalServerEntity findById(Integer id) {
        if (id == null) {
            return null;
        }
        return localServerEntityRepository.findById(id).orElse(null);
    }

    public LocalServerEntity findByMac(String mac) {
        return localServerEntityRepository.findByLocalServerMac(mac);
    }

    public LocalServerEntity save(LocalServerEntity localServer) {
        return localServerEntityRepository.save(localServer);
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public List<LocalServerEntity> saveAll(Iterable<LocalServerEntity> localServer) {
        return localServerEntityRepository.saveAll(localServer);
    }

    public void delete(LocalServerEntity localServer) {
        localServerEntityRepository.delete(localServer);
    }

    public List<LocalServerEntity> getAvailableLocalServerList() {
        return localServerEntityRepository.findAllByLocalServerOnlineIsTrueAndLocalServerCleActivationIsTrueAndLocalServerBuildingIsNull();
    }

    public Page<LocalServerEntity> getLocalServerByPage(Integer page, Integer size, Integer buildingId, String projectName, String cpaFileName, String mac) {
        Page<LocalServerEntity> listPage;
        if (page < 0) {
            page = 0;
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(
                new Sort.Order(Sort.Direction.DESC, "localServerId")
        ));

        if (buildingId != null || StringUtils.isNotBlank(projectName) || StringUtils.isNotBlank(cpaFileName) || StringUtils.isNotBlank(mac)) {
            Specification<LocalServerEntity> spec = (root, query, cb) -> {
                List<Predicate> andParam = new ArrayList<>();
                if (buildingId != null) {
                    andParam.add(cb.equal(root.get("localServerBuilding"), new BuildingEntity(buildingId)));
                }
                if (StringUtils.isNotBlank(projectName)) {
                    andParam.add(cb.like(root.get("localServerCpaProject"), "%" + projectName + "%"));
                }
                if (StringUtils.isNotBlank(cpaFileName)) {
                    andParam.add(cb.like(root.get("localServerCpaFile"), "%" + cpaFileName + "%"));
                }
                if (StringUtils.isNotBlank(mac)) {
                    andParam.add(cb.like(root.get("localServerMac"), "%" + mac + "%"));
                }
                return cb.and(andParam.toArray(new Predicate[0]));
//                return cb.and(andParam.toArray(new Predicate[andParam.size()]));
            };
            listPage = localServerEntityRepository.findAll(spec, pageRequest);
        } else {
            listPage = localServerEntityRepository.findAll(pageRequest);
        }

        return listPage;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public LocalServerEntity saveOrUpdateLocalServer(LocalServerDTO localServerDTO) throws Exception {
        var localServer = findByMac(localServerDTO.getLocalServerMac());
        if (localServer == null) {
            localServer = new LocalServerEntity();
            localServer.setLocalServerMac(localServerDTO.getLocalServerMac());
            localServer.setLocalServerCleLicense("");
            localServer.setLocalServerRemark("");
            localServer.setLocalServerCpaFile("");
            localServer.setLocalServerCleLicenseUpdateTime(-1L);
            localServer.setLocalServerCpaFileUpdateTime(-1L);
            localServer.setLocalServerBuilding(null);
        }
        localServer.setLocalServerIp(localServerDTO.getLocalServerIp());
        localServer.setLocalServerOnline(true);
        localServer.setLocalServerCleStartup(localServerDTO.getLocalServerCleStartup());
        localServer.setLocalServerCleActivation(localServerDTO.getLocalServerCleActivation());
        localServer.setLocalServerCleCode(localServerDTO.getLocalServerCleCode());
        localServer.setLocalServerCpaId(localServerDTO.getLocalServerCpaId() == null ? "" : localServerDTO.getLocalServerCpaId());
        localServer.setLocalServerCpaProject(localServerDTO.getLocalServerCpaProject() == null ? "" : localServerDTO.getLocalServerCpaProject());
        localServer.setLocalServerCpaCategory(localServerDTO.getLocalServerCpaCategory() == null ? "" : localServerDTO.getLocalServerCpaCategory());
        localServer.setLocalServerCpaVersion(localServerDTO.getLocalServerCpaVersion() == null ? "" : localServerDTO.getLocalServerCpaVersion());
        localServer.setLocalServerLastTime(System.currentTimeMillis());
        localServer.setLocalServerCleLicenseExpireTime(localServerDTO.getLocalServerCleLicenseExpireTime());
        localServer.setLocalServerCleVersion(localServerDTO.getLocalServerCleVersion());

        localServer = save(localServer);

        var fileTransferModelList = updateMapList(localServer, localServerDTO.getMapList());
        HandleFileUtil.saveTempFile(fileTransferModelList, SysUrlService.pathPrefix);

        return localServer;
    }

    private List<HandleFileUtil.FileTransferModel> updateMapList(LocalServerEntity localServer, List<MapDTO> mapDTOList) throws Exception {
        var fileTransferModelList = new ArrayList<HandleFileUtil.FileTransferModel>();
        var deleteMap = new ArrayList<MapEntity>();
        var updateMap = new ArrayList<MapEntity>();
        for (var mapEntity : localServer.getMapList()) {
            var index = -1;
            for (var i = 0; i < mapDTOList.size(); i++) {
                var mapDTO = mapDTOList.get(i);
                if (mapEntity.getMapCpaId().equals(mapDTO.getMapCpaId())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                //找到了，更新
                var mapDTO = mapDTOList.get(index);
                mapDTOList.remove(index);
                mapEntity.setMapCpaName(mapDTO.getMapCpaName());
                mapEntity.setMapModelType(mapDTO.getMapModelType());
                mapEntity.setMapWidth(mapDTO.getMapWidth());
                mapEntity.setMapHeight(mapDTO.getMapHeight());
                mapEntity.setMapWidthPixel(mapDTO.getMapWidthPixel());
                mapEntity.setMapHeightPixel(mapDTO.getMapHeightPixel());
                mapEntity.setMapMetersPerPixel(mapDTO.getMapMetersPerPixel());
                mapEntity.setMapOriginPixelX(mapDTO.getMapOriginPixelX());
                mapEntity.setMapOriginPixelY(mapDTO.getMapOriginPixelY());
                mapEntity.setMapCpaAreas(mapDTO.getMapCpaAreas());
                mapEntity.setMapUpdateTime(System.currentTimeMillis());
                //updateMap.add(mapEntity);
                mapEntity = mapEntityRepository.save(mapEntity);
                updateBaseStationList(mapEntity, mapDTO.getBaseStationList());

                fileTransferModelList.add(new HandleFileUtil.FileTransferModel(mapDTO.getMapImgSaveName(), mapEntity.getMapImgSaveName()));
            } else {
                //没找到，删除
                deleteMap.add(mapEntity);

                fileTransferModelList.add(new HandleFileUtil.FileTransferModel(mapEntity.getMapImgSaveName(), null));
            }
        }
        //剩下的新增
        for (var mapDTO : mapDTOList) {
            var mapEntity = new MapEntity();
            mapEntity.setMapCpaId(mapDTO.getMapCpaId());
            mapEntity.setMapCpaName(mapDTO.getMapCpaName());
            mapEntity.setMapModelType(mapDTO.getMapModelType());
            mapEntity.setMapWidth(mapDTO.getMapWidth());
            mapEntity.setMapHeight(mapDTO.getMapHeight());
            mapEntity.setMapWidthPixel(mapDTO.getMapWidthPixel());
            mapEntity.setMapHeightPixel(mapDTO.getMapHeightPixel());
            mapEntity.setMapMetersPerPixel(mapDTO.getMapMetersPerPixel());
            mapEntity.setMapOriginPixelX(mapDTO.getMapOriginPixelX());
            mapEntity.setMapOriginPixelY(mapDTO.getMapOriginPixelY());
            mapEntity.setMapCpaAreas(mapDTO.getMapCpaAreas());
            mapEntity.setMapLocalServer(localServer);
            mapEntity.setMapUpdateTime(System.currentTimeMillis());

            //updateMap.add(mapEntity);
            mapEntity = mapEntityRepository.save(mapEntity);
            updateBaseStationList(mapEntity, mapDTO.getBaseStationList());

            fileTransferModelList.add(new HandleFileUtil.FileTransferModel(mapDTO.getMapImgSaveName(), mapEntity.getMapImgSaveName()));
        }
        if (deleteMap.size() > 0) {
            mapEntityRepository.deleteAllInBatch(deleteMap);
        }
        return fileTransferModelList;
    }

    private void updateBaseStationList(MapEntity mapEntity, List<BaseStationDTO> baseStationDTOList) throws Exception {
        var deleteBaseStation = new ArrayList<BaseStationEntity>();
        var updateBaseStation = new ArrayList<BaseStationEntity>();
        for (var baseStationEntity : mapEntity.getBaseStationList()) {
            var index = -1;
            for (var i = 0; i < baseStationDTOList.size(); i++) {
                var baseStationDTO = baseStationDTOList.get(i);
                if (baseStationEntity.getBaseStationMac().equals(baseStationDTO.getBaseStationMac())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                //找到了，更新
                var baseStationDTO = baseStationDTOList.get(index);
                baseStationDTOList.remove(index);
                baseStationEntity.setBaseStationElevation(baseStationDTO.getBaseStationElevation());
                baseStationEntity.setBaseStationAzimuth(baseStationDTO.getBaseStationAzimuth());
                baseStationEntity.setBaseStationRotation(baseStationDTO.getBaseStationRotation());
                baseStationEntity.setBaseStationErrorDegree(baseStationDTO.getBaseStationErrorDegree());
                baseStationEntity.setBaseStationX(baseStationDTO.getBaseStationX());
                baseStationEntity.setBaseStationY(baseStationDTO.getBaseStationY());
                baseStationEntity.setBaseStationZ(baseStationDTO.getBaseStationZ());
                baseStationEntity.setBaseStationName(baseStationDTO.getBaseStationName());
                baseStationEntity.setBaseStationType(baseStationDTO.getBaseStationType());
                baseStationEntity.setBaseStationProduct(baseStationDTO.getBaseStationProduct());
                updateBaseStation.add(baseStationEntity);
            } else {
                //没找到，删除
                deleteBaseStation.add(baseStationEntity);
            }
        }
        //剩下的新增
        for (var baseStationDTO : baseStationDTOList) {
            var baseStationEntity = new BaseStationEntity();
            baseStationEntity.setBaseStationMac(baseStationDTO.getBaseStationMac());
            baseStationEntity.setBaseStationIp("");
            baseStationEntity.setBaseStationElevation(baseStationDTO.getBaseStationElevation());
            baseStationEntity.setBaseStationAzimuth(baseStationDTO.getBaseStationAzimuth());
            baseStationEntity.setBaseStationRotation(baseStationDTO.getBaseStationRotation());
            baseStationEntity.setBaseStationErrorDegree(baseStationDTO.getBaseStationErrorDegree());
            baseStationEntity.setBaseStationX(baseStationDTO.getBaseStationX());
            baseStationEntity.setBaseStationY(baseStationDTO.getBaseStationY());
            baseStationEntity.setBaseStationZ(baseStationDTO.getBaseStationZ());
            baseStationEntity.setBaseStationOnline(false);
            baseStationEntity.setBaseStationLastTime(-1L);
            baseStationEntity.setBaseStationName(baseStationDTO.getBaseStationName());
            baseStationEntity.setBaseStationType(baseStationDTO.getBaseStationType());
            baseStationEntity.setBaseStationProduct(baseStationDTO.getBaseStationProduct());
            baseStationEntity.setBaseStationChannels(Collections.emptyList());
            baseStationEntity.setBaseStationMap(mapEntity);
            updateBaseStation.add(baseStationEntity);
        }
        if (deleteBaseStation.size() > 0) {
            baseStationEntityRepository.deleteAllInBatch(deleteBaseStation);
        }
        if (updateBaseStation.size() > 0) {
            baseStationEntityRepository.saveAll(updateBaseStation);
        }
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public LocalServerEntity updateLastTimeAndOnlineByMac(String mac, boolean startup, long millis) throws Exception {
        var localServer = findByMac(mac);
        if (localServer == null) {
            throw new BaseException("本地服务未找到").setStrCode(Constants.ERR_1100);
        }
        localServer.setLocalServerLastTime(millis);
        localServer.setLocalServerCleStartup(startup);
        localServer.setLocalServerOnline(true);
        return save(localServer);
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public LocalServerEntity updateRemarkById(Integer id, String remark) throws Exception {
        var localServer = findById(id);
        if (localServer == null) {
            throw new BaseException("本地服务未找到").setStrCode(Constants.ERR_1100);
        }
        localServer.setLocalServerRemark(remark == null ? "" : remark);
        return save(localServer);
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public String updateLicenseById(Integer id, @NotNull String fileName, @NotNull String saveName) throws Exception {
        var localServer = findById(id);
        if (localServer == null) {
            throw new BaseException("本地服务未找到").setStrCode(Constants.ERR_1100);
        }
        if (!localServer.getLocalServerOnline()) {
            throw new BaseException("本地服务已离线").setStrCode(Constants.ERR_1101);
        }
        if (!saveName.startsWith(Constants.DIR_TEMP)) {
            //没上传/异常访问
//            return localServer;
            throw new BaseException("上传文件不能为空").setStrCode(Constants.ERR_F100);
        }
        //新上传的
        localServer.setLocalServerCleLicense(fileName);
        localServer.setLocalServerCleLicenseUpdateTime(System.currentTimeMillis());
        localServer = save(localServer);
        HandleFileUtil.saveTempFile(saveName, localServer.getLocalServerCleLicenseSaveName(), SysUrlService.pathPrefix);
        String messageId = UUIDUtil.generateUUID();
        try {
            MQTTService.sendLicenseToDevice(messageId, localServer.getLocalServerMac(), SysUrlService.urlPrefix + localServer.getLocalServerCleLicenseSaveName() + "?t=" + localServer.getLocalServerCleLicenseUpdateTime());
            redisService.saveSendLicenseResult(messageId, Constants.REDIS_SEND_RESULT_WAIT);
        } catch (Exception e) {
            log.error("下发本地服务更新License失败：", e);
            redisService.saveSendLicenseResult(messageId, Constants.REDIS_SEND_RESULT_FAIL);
        }
        return messageId;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public void updateLicenseResultByMac(String mac, Long expireTime) throws Exception {
        var localServer = findByMac(mac);
        if (localServer == null) {
            throw new BaseException("本地服务未找到").setStrCode(Constants.ERR_1100);
        }
        localServer.setLocalServerCleActivation(true);
        localServer.setLocalServerCleLicenseExpireTime(expireTime == null ? -1L : expireTime);
        save(localServer);
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public String updateCpaById(Integer id, @NotNull String fileName, @NotNull String saveName, @NotNull Integer buildId) throws Exception {
        var localServer = findById(id);
        if (localServer == null) {
            throw new BaseException("本地服务未找到").setStrCode(Constants.ERR_1100);
        }
        if (!localServer.getLocalServerOnline()) {
            throw new BaseException("本地服务已离线").setStrCode(Constants.ERR_1101);
        }
        if (!localServer.getLocalServerCleActivation()) {
            throw new BaseException("本地服务未激活").setStrCode(Constants.ERR_1102);
        }
        if (!saveName.startsWith(Constants.DIR_TEMP)) {
            //没上传/异常访问
//            return localServer;
            throw new BaseException("上传文件不能为空").setStrCode(Constants.ERR_F100);
        }
        var building = buildingEntityRepository.findById(buildId).orElse(null);
        if (building == null) {
            throw new BaseException("建筑信息未找到").setStrCode(Constants.ERR_1002);
        }
        //新上传的
        localServer.setLocalServerCpaFile(fileName);
        localServer.setLocalServerCpaFileUpdateTime(System.currentTimeMillis());
        localServer.setLocalServerBuilding(building);
        localServer = save(localServer);
        HandleFileUtil.saveTempFile(saveName, localServer.getLocalServerCpaFileSaveName(), SysUrlService.pathPrefix);
        String messageId = UUIDUtil.generateUUID();
        try {
            MQTTService.sendCpaToDevice(messageId, localServer.getLocalServerMac(), SysUrlService.urlPrefix + localServer.getLocalServerCpaFileSaveName() + "?t=" + localServer.getLocalServerCpaFileUpdateTime());
            redisService.saveSendCpaResult(messageId, Constants.REDIS_SEND_RESULT_WAIT);
        } catch (Exception e) {
            log.error("下发本地服务更新CPA失败：", e);
            redisService.saveSendCpaResult(messageId, Constants.REDIS_SEND_RESULT_FAIL);
        }
        return messageId;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public void deleteCpaById(Integer id) throws Exception {
        var localServer = findById(id);
        if (localServer == null) {
            throw new BaseException("本地服务未找到").setStrCode(Constants.ERR_1100);
        }
        localServer.setLocalServerBuilding(null);
        save(localServer);
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public void updateOnlineStatus(long millisAgo) throws Exception {
        localServerEntityRepository.updateLocalServerEntitySetOnlineFalseByLastTimeBefore(System.currentTimeMillis() - millisAgo);
//        var localServerList = localServerEntityRepository.findAllByLocalServerLastTimeBefore(System.currentTimeMillis() - millisAgo);
//        if (localServerList != null && localServerList.size() > 0) {
//            for (LocalServerEntity localServer : localServerList) {
//                localServer.setLocalServerOnline(false);
//            }
//            localServerList = saveAll(localServerList);
//        }
//        return localServerList;
    }

}
