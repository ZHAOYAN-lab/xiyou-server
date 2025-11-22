package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.common.communication.adapter.common.base.BaseAdapterType;
import com.ifengniao.common.communication.adapter.common.base.BaseExchangeDataPlus;
import com.ifengniao.common.communication.adapter.mqtt.service.MqttHandler;
import com.ifengniao.common.util.JsonUtil;
import com.ifengniao.common.util.UUIDUtil;
import com.ifengniao.server.xiyoucloud.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class MQTTService extends MqttHandler {

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
    private DataProcessService dataProcessService;
    @Value("${ifengniao.common.adapter.mqtt.subscribeTopic[0]}")
    private String mqttSubTopicNormal; //MQTT监听Topic：内部应用
    @Value("${ifengniao.common.adapter.mqtt.subscribeTopic[1]}")
    private String mqttSubTopicCle; //MQTT监听Topic：CLE
    @Value("${application.systemConfig.mqttPageTopicPrefix}")
    private String mqttPageTopicPrefix; //MQTT监听Topic：Page

/*    @Override
    public void handleMessageSimple(String topic, byte[] payload) throws MessagingException {
        //super.handleMessageSimple(topic, payload);
        //logger.info("接受消息={}", new String(payload, StandardCharsets.UTF_8));
        try {
            if (mqttSubTopicNormal.equals(topic)) {
                var content = new String(payload, StandardCharsets.UTF_8);
                var data = JsonUtil.fromJson(content, BaseExchangeDataPlus.class);
                if (data != null) {
                    switch (data.getType()) {
                        case LOGIN -> {
                            logger.info("MQTT RX << {}", content);
                            var localServer = JsonUtil.covertObj(data.getData(), LocalServerDTO.class, null);
                            if (localServer != null) {
                                localServerService.saveOrUpdateLocalServer(localServer);
                                sendConfigToDevice(data.getMachineCode());
                            }
                        }
                        case HEARTBEAT -> {
                            logger.info("MQTT RX << {}", content);
                            localServerService.updateLastTimeAndOnlineByMac(data.getMachineCode(), System.currentTimeMillis());
                            if (data.getData() != null && ((List) data.getData()).size() > 0) {
                                var baseStationDTOList = JsonUtil.fromJsonWithMultiClass(JsonUtil.toJson(data.getData()), new TypeReference<List<BaseStationDTO>>() {
                                });
                                if (baseStationDTOList != null) {
                                    baseStationService.updateBaseStationByLocalServerMac(data.getMachineCode(), baseStationDTOList);
                                }
                            }
                        }
                        case DEVICE_INFO -> {
//                            Map.of("type", "beacons", "reult", beacons)
                            var beaconDTOList = JsonUtil.fromJsonWithMultiClass(JsonUtil.toJson(((Map) data.getData()).get("reult")), new TypeReference<List<BeaconDTO>>() {
                            });
                            if (beaconDTOList != null) {
                                var beaconList = beaconService.updateBeaconByLocalServerMac(data.getMachineCode(), beaconDTOList);
                                if (beaconList.size() > 0) {
                                    //按地图发送MQTT消息给前端页面，用于实时定位
                                    //TODO：历史数据存储
                                    //TODO: 报警判断
                                    var beaconMap = beaconList.stream()
                                            .filter(beacon -> beacon.getBeaconLocationObject() != null && beacon.getBeaconOnline())
                                            .map(BeaconDTO::simplify)
                                            .collect(Collectors.groupingBy(BeaconDTO::getBeaconMapId));
                                    for (Map.Entry<String, List<BeaconDTO>> entry : beaconMap.entrySet()) {
                                        var map = mapService.findByCpaIdAndLocalServerMac(entry.getKey(), data.getMachineCode());
                                        if (map != null) {
                                            sendBeaconsByMapToPage(map.getMapId(), entry.getValue());
                                        }
                                    }
                                }
                            }
                        }
                        case DEVICE_SET -> {
                            logger.info("MQTT RX << {}", content);
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
        } catch (Exception e) {
            log.error("MQTT消息处理失败：", e);
        }
    }*/

    @Override
    public void handleMessageSimple(String topic, byte[] payload) throws MessagingException {
        dataProcessService.process(topic, payload);
    }

    public static void sendConfigToDevice(String mac) throws Exception {
        sendToDevice(mac, BaseAdapterType.CONFIG, null);
    }

    public static void sendLicenseToDevice(String messageId, String mac, String licenseUrl) throws Exception {
        sendToDevice(messageId, mac, BaseAdapterType.DEVICE_SET, Map.of(
                Constants.MQTT_DEVICE_SET_TYPE, Constants.MQTT_DEVICE_SET_TYPE_LICENSE,
                "url", licenseUrl)
        );
    }

    public static void sendCpaToDevice(String messageId, String mac, String cpaUrl) throws Exception {
        sendToDevice(messageId, mac, BaseAdapterType.DEVICE_SET, Map.of(
                Constants.MQTT_DEVICE_SET_TYPE, Constants.MQTT_DEVICE_SET_TYPE_CPA,
                "url", cpaUrl)
        );
    }

    public static String sendToDevice(String mac, BaseAdapterType type, Object data) throws Exception {
        return sendToDevice(null, mac, type, data);
    }

    public static String sendToDevice(String messageId, String mac, BaseAdapterType type, Object data) throws Exception {
        if (messageId == null) {
            messageId = UUIDUtil.generateUUID();
        }
        sendMqttToDevice(mac, JsonUtil.toJson(new BaseExchangeDataPlus<>(messageId, null, type, data, "")));
        return messageId;
    }

    public String sendBeaconsByMapToPage(Integer mapId, Object data) throws Exception {
        return sendToPage(mqttPageTopicPrefix + mapId, BaseAdapterType.DEVICE_INFO, data);
    }

    public String sendToPage(String topic, BaseAdapterType type, Object data) throws Exception {
        return sendToPage(null, topic, type, data);
    }

    public String sendToPage(String messageId, String topic, BaseAdapterType type, Object data) throws Exception {
//        if (messageId == null) {
//            messageId = UUIDUtil.generateUUID();
//        }
        sendMqtt(topic, null, JsonUtil.toJson(new BaseExchangeDataPlus<>(messageId, null, type, data, "")), false);
        return messageId;
    }
}
