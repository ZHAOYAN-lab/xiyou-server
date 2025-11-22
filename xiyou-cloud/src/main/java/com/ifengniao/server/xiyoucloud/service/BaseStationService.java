package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.common.message.BaseException;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.BaseStationEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.MapEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.BaseStationEntityRepository;
import com.ifengniao.server.xiyoucloud.dto.BaseStationDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BaseStationService {

    @Autowired
    private BaseStationEntityRepository baseStationEntityRepository;

    public List<BaseStationEntity> findAll() {
        return baseStationEntityRepository.findAll();
    }

    public BaseStationEntity findById(Integer id) {
        if (id == null) {
            return null;
        }
        return baseStationEntityRepository.findById(id).orElse(null);
    }

    public BaseStationEntity findByMacAndLocalServerMac(String baseStationMac, String localServerMac) {
        return baseStationEntityRepository.findByBaseStationMacAndBaseStationMap_MapLocalServer_LocalServerMac(baseStationMac, localServerMac);
    }

    public BaseStationEntity save(BaseStationEntity baseStation) {
        return baseStationEntityRepository.save(baseStation);
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public List<BaseStationEntity> saveAll(Iterable<BaseStationEntity> baseStation) {
        return baseStationEntityRepository.saveAll(baseStation);
    }

    public void delete(BaseStationEntity baseStation) {
        baseStationEntityRepository.delete(baseStation);
    }

    public Page<BaseStationEntity> getBaseStationByPage(Integer page, Integer size, Integer mapId, String mac, String ip, Boolean online) {
        Page<BaseStationEntity> listPage;
        if (page < 0) {
            page = 0;
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(
                new Sort.Order(Sort.Direction.DESC, "baseStationId")
        ));

        if (mapId != null || StringUtils.isNotBlank(mac) || StringUtils.isNotBlank(ip) || online != null) {
            Specification<BaseStationEntity> spec = (root, query, cb) -> {
                List<Predicate> andParam = new ArrayList<>();
                if (mapId != null) {
                    andParam.add(cb.equal(root.get("baseStationMap"), new MapEntity(mapId)));
                }
                if (StringUtils.isNotBlank(mac)) {
                    andParam.add(cb.like(root.get("baseStationMac"), "%" + mac + "%"));
                }
                if (StringUtils.isNotBlank(ip)) {
                    andParam.add(cb.like(root.get("baseStationIp"), "%" + ip + "%"));
                }
                if (online != null) {
                    andParam.add(cb.equal(root.get("baseStationOnline"), online));
                }
                return cb.and(andParam.toArray(new Predicate[0]));
//                return cb.and(andParam.toArray(new Predicate[andParam.size()]));
            };
            listPage = baseStationEntityRepository.findAll(spec, pageRequest);
        } else {
            listPage = baseStationEntityRepository.findAll(pageRequest);
        }

        return listPage;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public void updateOnlineStatus(long millisAgo) throws Exception {
        baseStationEntityRepository.updateBaseStationEntitySetOnlineFalseByLastTimeBefore(System.currentTimeMillis() - millisAgo);
//        var baseStationList = baseStationEntityRepository.findAllByBaseStationLastTimeBefore(System.currentTimeMillis() - millisAgo);
//        if (baseStationList != null && baseStationList.size() > 0) {
//            for (BaseStationEntity localServer : baseStationList) {
//                localServer.setBaseStationOnline(false);
//            }
//            baseStationList = saveAll(baseStationList);
//        }
//        return baseStationList;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public List<BaseStationEntity> updateBaseStationByLocalServerMac(String mac, List<BaseStationDTO> baseStationDTOList) throws Exception {
        List<BaseStationEntity> result = new ArrayList<>();
        for (BaseStationDTO baseStationDTO : baseStationDTOList) {
            var baseStation = findByMacAndLocalServerMac(baseStationDTO.getBaseStationMac(), mac);
            if (baseStation == null) {
                continue;
            }
            baseStation.setBaseStationIp(baseStationDTO.getBaseStationIp());
            baseStation.setBaseStationElevation(baseStationDTO.getBaseStationElevation());
            baseStation.setBaseStationAzimuth(baseStationDTO.getBaseStationAzimuth());
            baseStation.setBaseStationRotation(baseStationDTO.getBaseStationRotation());
            baseStation.setBaseStationErrorDegree(baseStationDTO.getBaseStationErrorDegree());
            baseStation.setBaseStationX(baseStationDTO.getBaseStationX());
            baseStation.setBaseStationY(baseStationDTO.getBaseStationY());
            baseStation.setBaseStationZ(baseStationDTO.getBaseStationZ());
            baseStation.setBaseStationOnline(baseStationDTO.getBaseStationOnline());
            if (baseStation.getBaseStationOnline()) {
                baseStation.setBaseStationLastTime(System.currentTimeMillis());
            }
            baseStation.setBaseStationName(baseStationDTO.getBaseStationName());
            baseStation.setBaseStationType(baseStationDTO.getBaseStationType());
            baseStation.setBaseStationProduct(baseStationDTO.getBaseStationProduct());
            baseStation.setBaseStationChannels(baseStationDTO.getBaseStationChannels());
            result.add(baseStation);
        }
        if (result.size() > 0) {
            result = saveAll(result);
        }
        return result;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public BaseStationEntity tryUpdateByMqtt(String ip, Map<String, Object> locator) throws Exception {

        return null;
    }

}
