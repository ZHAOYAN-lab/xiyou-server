package com.ifengniao.server.xiyoucloud.config;

import java.io.File;

public interface Constants {

    //临时文件夹前缀
    String DIR_TEMP = "tmp" + File.separator;
    //建筑物相关文件夹
    String DIR_BUILDING = "building" + File.separator;
    //建筑物示意图相关文件夹
    String DIR_BUILDING_IMG = DIR_BUILDING + "img" + File.separator;
    //本地服务相关文件夹
    String DIR_LOCAL_SERVER = "localserver" + File.separator;
    //本地服务授权文件相关文件夹
    String DIR_LOCAL_SERVER_LICENSE = DIR_LOCAL_SERVER + "license" + File.separator;
    //本地服务CPA项目文件相关文件夹
    String DIR_LOCAL_SERVER_CPA = DIR_LOCAL_SERVER + "cpa" + File.separator;
    //地图相关文件夹
    String DIR_MAP = "map" + File.separator;
    //地图底图相关文件夹
    String DIR_MAP_IMG = DIR_MAP + "img" + File.separator;
    //基站相关文件夹
    String DIR_BASE_STATION = "basestation" + File.separator;
    //信标相关文件夹
    String DIR_BEACON = "beacon" + File.separator;
    //定位对象相关文件夹
    String DIR_LOCATION_OBJECT = "locationobject" + File.separator;
    //定位对象图标相关文件夹
    String DIR_LOCATION_OBJECT_IMG = DIR_LOCATION_OBJECT + "img" + File.separator;


    //MQTT DEVICE_SET type
    String MQTT_DEVICE_SET_TYPE = "type";
    //MQTT DEVICE_SET type license
    String MQTT_DEVICE_SET_TYPE_LICENSE = "license";
    //MQTT DEVICE_SET type CPA
    String MQTT_DEVICE_SET_TYPE_CPA = "cpa";


    //地图类型（'image' | 'collada' | 'zip'）
    String MAP_MODEL_TYPE_IMAGE = "image";
    String MAP_MODEL_TYPE_COLLADA = "collada";
    String MAP_MODEL_TYPE_ZIP = "zip";


    //redis发送授权文件结果
    String REDIS_SEND_LICENSE_RESULT = "%s:server:sendDeviceResult:license:%s";
    String REDIS_SEND_CPA_RESULT = "%s:server:sendDeviceResult:cpa:%s";
    int REDIS_SEND_RESULT_TIMEOUT = -1; //执行超时
    int REDIS_SEND_RESULT_WAIT = 0; //执行中
    int REDIS_SEND_RESULT_SUCCESS = 1; //执行成功
    int REDIS_SEND_RESULT_FAIL = 2; //执行失败

    short BEACON_AND_LOCATION_OBJECT_TYPE_NONE = 0; //未知
    short BEACON_AND_LOCATION_OBJECT_TYPE_PERSON = 1; //人员
    short BEACON_AND_LOCATION_OBJECT_TYPE_THING = 2; //物品
    short BEACON_AND_LOCATION_OBJECT_TYPE_DEVICE = 3; //设备
    short BEACON_AND_LOCATION_OBJECT_TYPE_CAR = 4; //车辆

    short WARN_TYPE_FENCE_ENTER = 1; //围栏进入
    short WARN_TYPE_FENCE_LEAVE = 2; //围栏离开
    short WARN_TYPE_FENCE_MAP_ENTER = 3; //地图进入
    String SEND_MAIL_TITLE = "%s报警";
    String SEND_MAIL_CONTENT = "信标编号%s，告警对象%s，在%s触发%s报警";

    int LANGUAGE_ZH = 1; //中文
    int LANGUAGE_JP = 2; //日文

    //CLE上报MQTT消息类型
    String LOCATOR_OR_SENSOR_TYPE_LOCATORS = "locators";
    String LOCATOR_OR_SENSOR_TYPE_SENSORS = "sensors";

    //离线判断时间间隔
    long OFFLINE_TIME_MILLIS = 12 * 1000;

    String CONFIG_ALARM_SEND_MAIL_SWITCH = "ALARM_SEND_MAIL_SWITCH";

    String SUC_0000 = "0000"; //成功
    String ERR_1000 = "1000"; //建筑名称不能为空
    String ERR_1001 = "1001"; //建筑名称已存在
    String ERR_1002 = "1002"; //建筑信息未找到
    String ERR_1100 = "1100"; //本地服务未找到
    String ERR_1101 = "1101"; //本地服务已离线
    String ERR_1102 = "1102"; //本地服务未激活
    String ERR_1200 = "1200"; //地图信息未找到
    String ERR_1300 = "1300"; //信标MAC不能为空
    String ERR_1301 = "1301"; //信标类型不能为空
    String ERR_1302 = "1302"; //信标MAC已存在
    String ERR_1303 = "1303"; //信标信息未找到
    String ERR_1304 = "1304"; //信标已绑定对象，不可修改类型
    String ERR_1305 = "1305"; //不能绑定与信标类型不同的对象
    String ERR_1306 = "1306"; //信标已绑定对象，不可删除
    String ERR_1307 = "1307"; //未知类型信标无法绑定对象
    String ERR_1308 = "1308"; //信标MAC地址格式：12位，字母+数字，字母a至f
    String ERR_1400 = "1400"; //定位对象信息未找到
    String ERR_1401 = "1401"; //定位对象名称不能为空
    String ERR_1402 = "1402"; //定位对象类型不能为空
    String ERR_1403 = "1403"; //定位对象名称已存在
    String ERR_1404 = "1404"; //对象已绑定信标，不可修改类型
    String ERR_1405 = "1405"; //已配置信标，不可删除
    String ERR_1406 = "1406"; //定位对象已配置其他信标，不可重复配置
    String ERR_1500 = "1500"; //围栏名称不能为空
    String ERR_1501 = "1501"; //围栏所属地图不能为空
    String ERR_1502 = "1502"; //围栏类型不能为空
    String ERR_1503 = "1503"; //围栏信息未找到
    String ERR_1504 = "1504"; //围栏需要是一个封闭图形
    String ERR_1600 = "1600"; //告警信息未找到

    String ERR_F000 = "F000"; //账号/密码错误
    String ERR_F001 = "F001"; //失败，未找到登录信息
    String ERR_F002 = "F002"; //权限校验失败
    String ERR_F100 = "F100"; //上传文件不能为空
    String ERR_F101 = "F101"; //上传文件格式错误
    String ERR_F200 = "F200"; //参数缺失
    String ERR_FFFF = "FFFF"; //失败

}
