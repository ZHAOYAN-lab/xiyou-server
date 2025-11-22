package com.ifengniao.server.xiyoulocal.config;

public interface Constants {

    //MQTT DEVICE_SET type
    String MQTT_DEVICE_SET_TYPE = "type";
    //MQTT DEVICE_SET type license
    String MQTT_DEVICE_SET_TYPE_LICENSE = "license";
    //MQTT DEVICE_SET type CPA
    String MQTT_DEVICE_SET_TYPE_CPA = "cpa";

    //CLE服务状态
    String CLE_STATUS_ONLINE = "online";
    String CLE_STATUS_OFFLINE = "offline";

    //地图类型（'image' | 'collada' | 'zip'）
    String MAP_MODEL_TYPE_IMAGE = "image";
    String MAP_MODEL_TYPE_COLLADA = "collada";
    String MAP_MODEL_TYPE_ZIP = "zip";

    //HTTP接口请求code
    String HTTP_SUCCESS = "0000";

    //CLE在Ubuntu环境下安装后第一次激活，调用激活接口后会固定报此错误，无论等待多长时间，之后就正常了
    String CLE_ACTIVATE_UBUNTU_ERROR = "{\"error\":{}}";
}
