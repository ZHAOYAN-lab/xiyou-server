package com.ifengniao.server.xiyoucloud.db.influxdb.service;

import com.ifengniao.common.config.influxdb2.service.Influx2Sample;
import com.ifengniao.server.xiyoucloud.db.influxdb.entity.BeaconHistoryDTO;
import com.influxdb.client.domain.WritePrecision;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BeaconHistoryService {

    @Autowired
    private Influx2Sample influx2Sample;

    public void saveBeaconHistory(List<BeaconHistoryDTO> objs) {
        try {
            influx2Sample.writeByPOJOs(WritePrecision.NS, objs);
        } catch (Exception e) {
            log.error("influxDB写入数据失败：", e);
        }
    }

    public Map<String, List<Map<String, Object>>> queryBeaconHistory(@NonNull String mapId, String loId, Long start, Long end) {
        try {
            var map = new HashMap<String, String>();
            map.put("mapId", mapId);
            if (StringUtils.isBlank(loId)) {
                map.put("loId", null);
            } else {
                map.put("loId", loId);
            }
            String window = "3s";
//            if (end - start >= 15 * 24 * 60 * 60 * 1000L) {
//                window = "60s";
//            } else if (end - start >= 7 * 24 * 60 * 60 * 1000L) {
//                window = "30s";
//            } else if (end - start >= 3 * 24 * 60 * 60 * 1000L) {
//                window = "15s";
//            } else if (end - start >= 24 * 60 * 60 * 1000L) {
//                window = "6s";
//            } else if (end - start >= 6 * 60 * 60 * 1000L) {
//                window = "3s";
//            } else if (end - start >= 60 * 60 * 1000L) {
//                window = "1s";
//            } else {
//                window = null;
//            }
            Instant si = null;
            Instant ei = null;
            if (start != null) {
                si = Instant.ofEpochMilli(start);
            }
            if (end != null) {
                if (end.equals(start)) {
                    end++;
                }
                ei = Instant.ofEpochMilli(end);
            }
            var list = influx2Sample.query("beacon_history", map, null, si, ei, window);
            var result = new HashMap<String, List<Map<String, Object>>>();
            for (Map<String, Object> objectMap : list) {
                String id = null;
                if (StringUtils.isBlank(loId)) {
                    id = objectMap.get("loId").toString();
                } else {
                    id = loId;
                }
                objectMap.remove("mapId");
                objectMap.remove("loId");
                objectMap.remove("time");
                result.computeIfAbsent(id, key -> new ArrayList<>()).add(objectMap);
            }
            return result;
        } catch (Exception e) {
            log.error("influxDB读取数据失败：", e);
        }
        return Map.of();
    }

}
