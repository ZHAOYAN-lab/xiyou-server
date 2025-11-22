package com.ifengniao.server.xiyoulocal.service;

import com.ifengniao.common.communication.adapter.common.base.BaseAdapterType;
import com.ifengniao.common.communication.adapter.common.base.BaseExchangeDataPlus;
import com.ifengniao.common.communication.adapter.mqtt.service.MqttHandler;
import com.ifengniao.common.util.JsonUtil;
import com.ifengniao.common.util.ThreadUtil;
import com.ifengniao.common.util.UUIDUtil;
import com.ifengniao.common.util.ZipUtil;
import com.ifengniao.server.xiyoulocal.config.Constants;
import com.ifengniao.server.xiyoulocal.config.Startup;
import com.ifengniao.server.xiyoulocal.util.UpDownUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class MQTTService extends MqttHandler {

    @Autowired
    private Startup startup;
    @Value("${application.systemConfig.commonDownloadFilePath}")
    private String commonDownloadFilePath;
    private ScheduledFuture<?> heartbeatScheduled;
    private ScheduledFuture<?> beaconScheduled;

    @Override
    public void handleMessageSimple(String topic, byte[] payload) throws MessagingException {
        //super.handleMessageSimple(topic, payload);
        //logger.info("接受消息={}", new String(payload, StandardCharsets.UTF_8));
        try {
            var content = new String(payload, StandardCharsets.UTF_8);
            logger.info("MQTT RX << {}", content);
            var data = JsonUtil.fromJson(content, BaseExchangeDataPlus.class);
            if (data != null) {
                switch (data.getType()) {
                    case CONFIG -> {
                        startHeartbeat();
                        startBeacon();
                    }
                    case DEVICE_SET -> {
                        var map = (Map<String, Object>) data.getData();
                        var type = (String) map.get(Constants.MQTT_DEVICE_SET_TYPE);
                        switch (type) {
                            case Constants.MQTT_DEVICE_SET_TYPE_LICENSE -> {
                                var url = (String) map.get("url");
                                try {
                                    var end = url.lastIndexOf("?");
                                    if (end < 0) {
                                        end = url.length();
                                    }
                                    var licenseFile = new File(commonDownloadFilePath + File.separator + "xiyou" + url.substring(url.lastIndexOf("."), end));
                                    UpDownUtil.simpleDownload(url, licenseFile);
                                    logger.info("license文件已下载：{}", licenseFile.getAbsolutePath());
                                    startup.activateCleWithLicensePath(licenseFile.getAbsolutePath());
                                    sendLicenseResultToServer(
                                            data.getMessageId(),
                                            Startup.LOCAL_SERVER.getLocalServerCleActivation(),
                                            Startup.LOCAL_SERVER.getLocalServerCleLicenseExpireTime()
                                    );
                                } catch (Exception e) {
                                    logger.error("license激活失败：", e);
                                    sendLicenseResultToServer(data.getMessageId(), false, null);
                                }
                            }
                            case Constants.MQTT_DEVICE_SET_TYPE_CPA -> {
                                if (Startup.LOCAL_SERVER.getLocalServerCleActivation() != null && Startup.LOCAL_SERVER.getLocalServerCleActivation()) {
                                    var url = (String) map.get("url");
                                    try {
                                        var end = url.lastIndexOf("?");
                                        if (end < 0) {
                                            end = url.length();
                                        }
                                        var cpaFile = new File(commonDownloadFilePath + File.separator + "xiyou" + url.substring(url.lastIndexOf("."), end));
                                        UpDownUtil.simpleDownload(url, cpaFile);
                                        logger.info("cpa文件已下载：{}", cpaFile.getAbsolutePath());
                                        startup.restartCleWithCpaPath(cpaFile.getAbsolutePath());
                                        startup.fetchAllInfor();
                                        sendCpaResultToServer(data.getMessageId(), true);
                                    } catch (Exception e) {
                                        logger.error("cpa装载失败：", e);
                                        sendCpaResultToServer(data.getMessageId(), false);
                                    }
                                } else {
                                    sendCpaResultToServer(data.getMessageId(), false);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            logger.error("本地服务MQTT消息处理失败：", e);
        }
    }

    public static void sendLoginToServer() throws Exception {
        sendToServer(BaseAdapterType.LOGIN, Startup.LOCAL_SERVER, true);
    }

    public void stopHeartbeat() {
        if (heartbeatScheduled != null) {
            ThreadUtil.cancel(heartbeatScheduled, true);
            heartbeatScheduled = null;
        }
    }

    public void startHeartbeat() throws Exception {
        stopHeartbeat();
        heartbeatScheduled = ThreadUtil.execute(() -> {
            try {
                String result = null;
                try {
                    var locators = startup.fetchLocators();
                    result = ZipUtil.gzipStringCompress(JsonUtil.toJson(locators));
                } catch (Exception e) {
                    logger.error("获取CLE信标信息失败：", e);
                }
                sendToServer(BaseAdapterType.HEARTBEAT, result, true);
            } catch (Exception e) {
                logger.error("CLE信标信息发送MQTT失败：", e);
            }
        }, 500, 5 * 1000);
    }

    public void stopBeacon() {
        if (beaconScheduled != null) {
            ThreadUtil.cancel(beaconScheduled, true);
            beaconScheduled = null;
        }
    }

    public void startBeacon() throws Exception {
        stopBeacon();
        beaconScheduled = ThreadUtil.execute(() -> {
            try {
                var beacons = startup.fetchBeacons();
                if (beacons != null && !beacons.isEmpty()) {
                    var result = ZipUtil.gzipStringCompress(JsonUtil.toJson(beacons));
                    sendToServer(BaseAdapterType.DEVICE_INFO, Map.of("type", "beacons", "reult", result), false);
                }
            } catch (Exception e) {
                logger.error("获取信标状态信息失败：" + e.getMessage());
            }
        }, 500, 300);
    }

    public static String sendLicenseResultToServer(String messageId, boolean success, Long expireTime) throws Exception {
        var map = new HashMap<>();
        map.put(Constants.MQTT_DEVICE_SET_TYPE, Constants.MQTT_DEVICE_SET_TYPE_LICENSE);
        map.put("result", success);
        map.put("expireTime", expireTime);
        return sendToServer(messageId, BaseAdapterType.DEVICE_SET, map, true);
    }

    public static String sendCpaResultToServer(String messageId, boolean success) throws Exception {
        var map = new HashMap<>();
        map.put(Constants.MQTT_DEVICE_SET_TYPE, Constants.MQTT_DEVICE_SET_TYPE_CPA);
        map.put("result", success);
        return sendToServer(messageId, BaseAdapterType.DEVICE_SET, map, true);
    }

    public static String sendToServer(BaseAdapterType type, Object data, boolean log) throws Exception {
        return sendToServer(null, type, data, log);
    }

    public static String sendToServer(String messageId, BaseAdapterType type, Object data, boolean log) throws Exception {
        if (messageId == null) {
            messageId = UUIDUtil.generateUUID();
        }
        sendMqttToDevice("", null, JsonUtil.toJson(new BaseExchangeDataPlus<>(messageId, Startup.LOCAL_SERVER.getLocalServerMac(), type, data, "")), log);
        return messageId;
    }

}
