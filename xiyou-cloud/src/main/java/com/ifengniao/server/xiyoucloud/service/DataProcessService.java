package com.ifengniao.server.xiyoucloud.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ifengniao.common.communication.adapter.common.base.BaseExchangeDataPlus;
import com.ifengniao.common.notify.mail.service.MailService;
import com.ifengniao.common.util.DateTimeUtil;
import com.ifengniao.common.util.JsonUtil;
import com.ifengniao.common.util.ZipUtil;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.db.influxdb.entity.BeaconHistoryDTO;
import com.ifengniao.server.xiyoucloud.db.influxdb.service.BeaconHistoryService;
import com.ifengniao.server.xiyoucloud.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@Lazy
@Slf4j
public class DataProcessService {
    @Autowired
    private LocalServerService localServerService;
    @Autowired
    private MapService mapService;
    @Autowired
    private BaseStationService baseStationService;
    @Autowired
    private BeaconService beaconService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BeaconHistoryService beaconHistoryService;
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private MQTTService mqttService;
    @Autowired
    private UserService userService;
    @Autowired
    private ConfigService configService;

    @Value("${ifengniao.common.adapter.mqtt.subscribeTopic[0]}")
    private String mqttSubTopicNormal; //MQTT监听Topic：内部应用
    @Value("${ifengniao.common.adapter.mqtt.subscribeTopic[1]}")
    private String mqttSubTopicCle; //MQTT监听Topic：CLE
    @Value("${application.systemConfig.mqttPageTopicPrefix}")
    private String mqttPageTopicPrefix; //MQTT监听Topic：Page

    @Async("asyncTaskPool")
    public void process(String topic, byte[] payload) {
        try {
            if (mqttSubTopicNormal.equals(topic)) {
                var content = new String(payload, StandardCharsets.UTF_8);
                var data = JsonUtil.fromJson(content, BaseExchangeDataPlus.class);
                if (data != null) {
                    switch (data.getType()) {
                        case LOGIN -> {
                            log.info("MQTT RX << {}", content);
                            var localServer = JsonUtil.covertObj(data.getData(), LocalServerDTO.class, null);
                            if (localServer != null) {
                                localServerService.saveOrUpdateLocalServer(localServer);
                                MQTTService.sendConfigToDevice(data.getMachineCode());
                            }
                        }
                        case HEARTBEAT -> {
                            log.info("MQTT RX << {}", content);
                            if (data.getData() != null) {
                                localServerService.updateLastTimeAndOnlineByMac(data.getMachineCode(), true, System.currentTimeMillis());
                                var baseStationDTOList = JsonUtil.fromJsonWithMultiClass(
                                        ZipUtil.gzipStringDecompress(data.getData().toString()),
                                        new TypeReference<List<BaseStationDTO>>() {
                                        }
                                );
                                if (baseStationDTOList != null && !baseStationDTOList.isEmpty()) {
                                    baseStationService.updateBaseStationByLocalServerMac(data.getMachineCode(), baseStationDTOList);
                                }
                            } else {
                                localServerService.updateLastTimeAndOnlineByMac(data.getMachineCode(), false, System.currentTimeMillis());
                            }
                        }
                        case DEVICE_INFO -> {
//                            Map.of("type", "beacons", "reult", beacons)
                            var beaconDTOList = JsonUtil.fromJsonWithMultiClass(
                                    ZipUtil.gzipStringDecompress(((Map) data.getData()).get("reult").toString()),
                                    new TypeReference<List<BeaconDTO>>() {
                                    }
                            );
                            if (beaconDTOList != null && !beaconDTOList.isEmpty()) {
                                var result = beaconService.receiveBeaconMsgFromMqtt(data.getMachineCode(), beaconDTOList);
                                try {
                                    //合并到了BeaconService的receiveBeaconMsgFromMqtt
//                                    var saveAlarmList = alarmService.saveNewAlarmList(result, null);
                                    if (!result.getAlarmDTOList().isEmpty() && configService.isMailSend()) {
                                        var admin = userService.findById(1);
                                        if (admin != null && StringUtils.isNotBlank(admin.getUserEmail())) {
                                            StringBuilder total = new StringBuilder();
                                            for (AlarmDTO alarm : result.getAlarmDTOList()) {
                                                String mailTitle = "";
                                                String mailContent = "";
                                                switch (alarm.getAlarmType()) {
                                                    case Constants.WARN_TYPE_FENCE_ENTER -> {
                                                        mailTitle = "围栏进入";
                                                        mailContent = "非法闯入围栏" + alarm.getAlarmFence().getFenceName();
                                                    }
                                                    case Constants.WARN_TYPE_FENCE_LEAVE -> {
                                                        mailTitle = "围栏离开";
                                                        mailContent = "非法越界围栏" + alarm.getAlarmFence().getFenceName();
                                                    }
                                                    default -> {
                                                        mailTitle = "地图进入";
                                                        mailContent = alarm.getAlarmMap().getMapCpaName() + "地图中进入非关联的对象";
                                                    }
                                                }
                                                String beaconMac = "";
                                                if (alarm.getAlarmLocationObject().getLocationObjectBeacon() != null) {
                                                    beaconMac = alarm.getAlarmLocationObject().getLocationObjectBeacon().getBeaconMac();
                                                }
                                                total.append(String.format(Constants.SEND_MAIL_CONTENT,
                                                        beaconMac,
                                                        alarm.getAlarmLocationObject().getLocationObjectName(),
                                                        DateTimeUtil.millisToStr(alarm.getAlarmTime(), "yyyy-MM-dd HH:mm:ss"),
                                                        mailContent
                                                )).append("\n");
//                                                MailService.sendMail(
//                                                        admin.getUserEmail(),
//                                                        String.format(Constants.SEND_MAIL_TITLE, mailTitle),
//                                                        String.format(Constants.SEND_MAIL_CONTENT,
//                                                                beaconMac,
//                                                                alarm.getAlarmLocationObject().getLocationObjectName(),
//                                                                DateTimeUtil.millisToStr(alarm.getAlarmTime(), "yyyy-MM-dd HH:mm:ss"),
//                                                                mailContent
//                                                        )
//                                                );
                                            }
                                            MailService.sendMail(
                                                    admin.getUserEmail(),
                                                    "对象报警",
                                                    total.toString()
                                            );
                                        }
                                    }
                                } catch (Exception e) {
                                    log.error("邮件发送失败：", e);
                                }
                                for (Map.Entry<Integer, List<BeaconDTO>> entry : result.getBeaconDTOHistoryMap().entrySet()) {
                                    try {
                                        //历史数据存储
                                        beaconHistoryService.saveBeaconHistory(BeaconHistoryDTO.covert(entry.getValue(), entry.getKey().toString()));
                                    } catch (Exception e) {
                                        log.error("历史数据存储失败：", e);
                                    }
                                }
                                for (Map.Entry<Integer, List<BeaconDTO>> entry : result.getBeaconDTOMqttMap().entrySet()) {
                                    try {
                                        //按地图发送MQTT消息给前端页面，用于实时定位
                                        mqttService.sendBeaconsByMapToPage(entry.getKey(), entry.getValue());
                                    } catch (Exception e) {
                                        log.error("发送MQTT消息给前端页面失败：", e);
                                    }
                                }
                            }
                        }
                        case DEVICE_SET -> {
                            log.info("MQTT RX << {}", content);
                            var map = (Map<String, Object>) data.getData();
                            var type = (String) map.get(Constants.MQTT_DEVICE_SET_TYPE);
                            switch (type) {
                                case Constants.MQTT_DEVICE_SET_TYPE_LICENSE -> {
                                    var result = (Boolean) map.get("result");
                                    var expireTime = (Long) map.get("expireTime");
                                    if (result != null && result) {
                                        localServerService.updateLicenseResultByMac(data.getMachineCode(), expireTime);
                                        redisService.saveSendLicenseResult(data.getMessageId(), Constants.REDIS_SEND_RESULT_SUCCESS);
                                    } else {
                                        redisService.saveSendLicenseResult(data.getMessageId(), Constants.REDIS_SEND_RESULT_FAIL);
                                    }
                                }
                                case Constants.MQTT_DEVICE_SET_TYPE_CPA -> {
                                    var result = (Boolean) map.get("result");
                                    if (result != null && result) {
                                        redisService.saveSendCpaResult(data.getMessageId(), Constants.REDIS_SEND_RESULT_SUCCESS);
                                    } else {
                                        redisService.saveSendCpaResult(data.getMessageId(), Constants.REDIS_SEND_RESULT_FAIL);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (mqttSubTopicCle.equals(topic)) {
                var content = new String(ZipUtil.inflaterDecompress(payload), StandardCharsets.UTF_8);
                var data = JsonUtil.fromJson(content, LocatorOrSensorDTO.class);
                if (data != null && data.getData() != null) {
                    var params = data.getData();
                    switch (data.getType()) {
                        case Constants.LOCATOR_OR_SENSOR_TYPE_LOCATORS -> {
                            for (var entry : params.entrySet()) {
                                try {
                                    baseStationService.tryUpdateByMqtt(entry.getKey(), entry.getValue());
                                } catch (Exception e) {
                                    log.error("失败：", e);
                                }
                            }
                        }
                        case Constants.LOCATOR_OR_SENSOR_TYPE_SENSORS -> {
                            for (var entry : params.entrySet()) {
                                try {
                                    beaconService.tryUpdateByMqtt(entry.getKey(), entry.getValue());
                                } catch (Exception e) {
                                    log.error("失败：", e);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            log.error("MQTT消息处理失败：", e);
        }
    }

}
