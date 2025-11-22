package com.ifengniao.server.xiyoucloud.service;

import cn.hutool.core.lang.Tuple;
import com.ifengniao.common.util.DateTimeUtil;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.AlarmEntityRepository;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.BaseStationEntityRepository;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.BeaconEntityRepository;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.LocationObjectEntityRepository;
import com.ifengniao.server.xiyoucloud.dto.LocationObjectWithBeaconDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OverviewService {

    @Autowired
    private AlarmEntityRepository alarmEntityRepository;
    @Autowired
    private LocationObjectEntityRepository locationObjectEntityRepository;
    @Autowired
    private BaseStationEntityRepository baseStationEntityRepository;
    @Autowired
    private BeaconEntityRepository beaconEntityRepository;

    @Transactional(readOnly = true)
    public Map<String, List<Map<String, Object>>> locationObjectAndDeviceGroup() throws Exception {
        //type, online, count
        var loGroup = locationObjectEntityRepository.countGroupByTypeAndOnline();
        var tupleMap = new HashMap<Tuple, Long>();
        tupleMap.put(new Tuple(Constants.BEACON_AND_LOCATION_OBJECT_TYPE_PERSON, true), 0L);
        tupleMap.put(new Tuple(Constants.BEACON_AND_LOCATION_OBJECT_TYPE_PERSON, false), 0L);
        tupleMap.put(new Tuple(Constants.BEACON_AND_LOCATION_OBJECT_TYPE_THING, true), 0L);
        tupleMap.put(new Tuple(Constants.BEACON_AND_LOCATION_OBJECT_TYPE_THING, false), 0L);
        tupleMap.put(new Tuple(Constants.BEACON_AND_LOCATION_OBJECT_TYPE_DEVICE, true), 0L);
        tupleMap.put(new Tuple(Constants.BEACON_AND_LOCATION_OBJECT_TYPE_DEVICE, false), 0L);
        tupleMap.put(new Tuple(Constants.BEACON_AND_LOCATION_OBJECT_TYPE_CAR, true), 0L);
        tupleMap.put(new Tuple(Constants.BEACON_AND_LOCATION_OBJECT_TYPE_CAR, false), 0L);
        for (Map<String, Object> loMap : loGroup) {
            tupleMap.put(new Tuple(loMap.get("type"), loMap.get("online")), (Long) loMap.get("count"));
        }
        loGroup = new ArrayList<>();
        for (Map.Entry<Tuple, Long> entry : tupleMap.entrySet()) {
            loGroup.add(Map.of("type", entry.getKey().get(0), "online", entry.getKey().get(1), "count", entry.getValue()));
        }

        var bsGroup = baseStationEntityRepository.countGroupByOnline();
        var baGroup = beaconEntityRepository.countGroupByOnline();

        return Map.of("locationObject", loGroup, "baseStation", bsGroup, "beacon", baGroup);
    }

    @Transactional(readOnly = true)
    public Map<String, List<Map<String, Object>>> alarmGroup() throws Exception {
        //type, status, count
        long now = System.currentTimeMillis();
        Long todayStart = DateTimeUtil.millisToMillis(now, false);
        Long before24Hours = now - 24 * 60 * 60 * 1000L;
        var todayGroup = alarmEntityRepository.countGroupByTypeAndStatus(todayStart);
        var tupleMap = new HashMap<Tuple, Long>();
        tupleMap.put(new Tuple(Constants.WARN_TYPE_FENCE_ENTER, true), 0L);
        tupleMap.put(new Tuple(Constants.WARN_TYPE_FENCE_ENTER, false), 0L);
        tupleMap.put(new Tuple(Constants.WARN_TYPE_FENCE_LEAVE, true), 0L);
        tupleMap.put(new Tuple(Constants.WARN_TYPE_FENCE_LEAVE, false), 0L);
        tupleMap.put(new Tuple(Constants.WARN_TYPE_FENCE_MAP_ENTER, true), 0L);
        tupleMap.put(new Tuple(Constants.WARN_TYPE_FENCE_MAP_ENTER, false), 0L);
        for (Map<String, Object> alMap : todayGroup) {
            tupleMap.put(new Tuple(alMap.get("type"), alMap.get("status")), (Long) alMap.get("count"));
        }
        todayGroup = new ArrayList<>();
        for (Map.Entry<Tuple, Long> entry : tupleMap.entrySet()) {
            todayGroup.add(Map.of("type", entry.getKey().get(0), "status", entry.getKey().get(1), "count", entry.getValue()));
        }

        var alTop5 = alarmEntityRepository.countGroupByLocationObjectTodayTop5(todayStart);
        var todayAlarmRank = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> alMap : alTop5) {
            Map<String, Object> rank = new HashMap<>(alMap);
            var loId = (Integer) rank.get("alarm_location_object");
            var lo = locationObjectEntityRepository.findById(loId).orElse(null);
            LocationObjectWithBeaconDTO loDTO = null;
            if (lo != null) {
                loDTO = new LocationObjectWithBeaconDTO(lo);
            }
            rank.put("alarm_location_object", loDTO);
            todayAlarmRank.add(rank);
        }

        var timeList = alarmEntityRepository.countGroupByLast24Hours(before24Hours);

        return Map.of("todayAlarmGroup", todayGroup, "todayAlarmRank", todayAlarmRank, "time24AlarmList", timeList);
    }

}
