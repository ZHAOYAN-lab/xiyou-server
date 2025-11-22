package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.common.message.BaseException;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.AlarmEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.AlarmEntityRepository;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.LocationObjectEntityRepository;
import com.ifengniao.server.xiyoucloud.dto.AlarmDTO;
import jakarta.persistence.criteria.Join;
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

@Service
@Slf4j
public class AlarmService {

    @Autowired
    private AlarmEntityRepository alarmEntityRepository;
    @Autowired
    private LocationObjectEntityRepository locationObjectEntityRepository;

    public List<AlarmEntity> findAll() {
        return alarmEntityRepository.findAll();
    }

    public AlarmEntity findById(Integer id) {
        if (id == null) {
            return null;
        }
        return alarmEntityRepository.findById(id).orElse(null);
    }

    public AlarmEntity save(AlarmEntity alarm) {
        return alarmEntityRepository.save(alarm);
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public List<AlarmEntity> saveAll(Iterable<AlarmEntity> alarm) {
        return alarmEntityRepository.saveAll(alarm);
    }

    public void delete(AlarmEntity alarm) {
        alarmEntityRepository.delete(alarm);
    }

    public Page<AlarmEntity> getAlarmByPage(Integer page, Integer size, String beaconMac, String locationObjectName,
                                            Short alarmType, Boolean alarmStatus, Long start, Long end,
                                            String order, Boolean asc) {
        Page<AlarmEntity> listPage;
        if (page < 0) {
            page = 0;
        }

        if (StringUtils.isBlank(order)) {
            order = "alarmId";
        }
        Sort.Direction direction = Sort.Direction.DESC;
        if (asc != null && asc) {
            direction = Sort.Direction.ASC;
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(
                new Sort.Order(direction, order)
        ));

        if (StringUtils.isNotBlank(beaconMac) || StringUtils.isNotBlank(locationObjectName) || alarmType != null || alarmStatus != null || start != null || end != null) {
            Specification<AlarmEntity> spec = (root, query, cb) -> {
                List<Predicate> andParam = new ArrayList<>();
                if (alarmType != null) {
                    andParam.add(cb.equal(root.get("alarmType"), alarmType));
                }
                if (alarmStatus != null) {
                    andParam.add(cb.equal(root.get("alarmStatus"), alarmStatus));
                }
                if (start != null || end != null) {
                    if (start != null && end != null) {
                        andParam.add(cb.between(root.get("alarmTime"), start, end));
                    } else if (start != null) {
                        andParam.add(cb.greaterThanOrEqualTo(root.get("alarmTime"), start));
                    } else {
                        andParam.add(cb.lessThanOrEqualTo(root.get("alarmTime"), end));
                    }
                }
                Join<Object, Object> locationObjectJoin = null;
                if (StringUtils.isNotBlank(locationObjectName)) {
                    locationObjectJoin = root.join("alarmLocationObject");
                    andParam.add(cb.like(locationObjectJoin.get("locationObjectName"), "%" + locationObjectName + "%"));
                }
                if (StringUtils.isNotBlank(beaconMac)) {
                    if (locationObjectJoin == null) {
                        locationObjectJoin = root.join("alarmLocationObject");
                    }
                    andParam.add(cb.like(locationObjectJoin.join("locationObjectBeacon").get("beaconMac"), "%" + beaconMac + "%"));
                }
                return cb.and(andParam.toArray(new Predicate[0]));
//                return cb.and(andParam.toArray(new Predicate[andParam.size()]));
            };
            listPage = alarmEntityRepository.findAll(spec, pageRequest);
        } else {
            listPage = alarmEntityRepository.findAll(pageRequest);
        }

        return listPage;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public AlarmEntity saveNewAlarm(Integer locationObjectId, Short alarmType, Long millis) throws Exception {
        if (locationObjectId == null || alarmType == null) {
            return null;
        }
        var locationObject = locationObjectEntityRepository.findById(locationObjectId).orElse(null);
        if (locationObject == null) {
            return null;
        }
        AlarmEntity alarm = new AlarmEntity();
        alarm.setAlarmType(alarmType);
        alarm.setAlarmStatus(false);
        if (millis == null) {
            millis = System.currentTimeMillis();
        }
        alarm.setAlarmTime(millis);
        alarm.setAlarmLocationObject(locationObject);
        alarm = save(alarm);
        return alarm;
    }

    //合并到了BeaconService的receiveBeaconMsgFromMqtt
    /*@Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public List<AlarmDTO> saveNewAlarmList(BeaconMqttResultDTO result, Long millis) throws Exception {
        if (result == null || result.getAlarmMap() == null || result.getAlarmFenceEnter() == null || result.getAlarmFenceLeave() == null) {
            throw new BaseException("生成报警存储结果错误");
        }
        if (millis == null) {
            millis = System.currentTimeMillis();
        }
        var save = new ArrayList<AlarmEntity>();
        Map<Integer, LocationObjectEntity> loMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : result.getAlarmMap().entrySet()) {
            AlarmEntity alarm = new AlarmEntity();
            alarm.setAlarmType(Constants.WARN_TYPE_FENCE_MAP_ENTER);
            alarm.setAlarmStatus(false);
            alarm.setAlarmTime(millis);
            alarm.setAlarmLocationObject(loMap.computeIfAbsent(entry.getKey(), key -> locationObjectEntityRepository.findById(key).orElse(null)));
            save.add(alarm);
        }
        for (Map.Entry<Integer, Integer> entry : result.getAlarmFenceEnter().entrySet()) {
            AlarmEntity alarm = new AlarmEntity();
            alarm.setAlarmType(Constants.WARN_TYPE_FENCE_ENTER);
            alarm.setAlarmStatus(false);
            alarm.setAlarmTime(millis);
            alarm.setAlarmLocationObject(loMap.computeIfAbsent(entry.getKey(), key -> locationObjectEntityRepository.findById(key).orElse(null)));
            save.add(alarm);
        }
        for (Map.Entry<Integer, Integer> entry : result.getAlarmFenceLeave().entrySet()) {
            AlarmEntity alarm = new AlarmEntity();
            alarm.setAlarmType(Constants.WARN_TYPE_FENCE_LEAVE);
            alarm.setAlarmStatus(false);
            alarm.setAlarmTime(millis);
            alarm.setAlarmLocationObject(loMap.computeIfAbsent(entry.getKey(), key -> locationObjectEntityRepository.findById(key).orElse(null)));
            save.add(alarm);
        }
        if (save.size() > 0) {
            return AlarmDTO.covert(saveAll(save));
        } else {
            return List.of();
        }
    }*/

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public AlarmEntity handleAlarm(AlarmDTO alarmDTO) throws Exception {
        var alarm = findById(alarmDTO.getAlarmId());
        if (alarm == null) {
            throw new BaseException("告警信息未找到").setStrCode(Constants.ERR_1600);
        }
        alarm.setAlarmStatus(true);
        alarm = save(alarm);
        return alarm;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public void deleteAlarm(AlarmDTO alarmDTO) throws Exception {
        AlarmEntity alarm = findById(alarmDTO.getAlarmId());
        if (alarm == null) {
            throw new BaseException("告警信息未找到").setStrCode(Constants.ERR_1600);
        }
        delete(alarm);
    }

}
