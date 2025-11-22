package com.ifengniao.server.xiyoulocal.config;

import cn.hutool.core.net.NetUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ifengniao.common.communication.adapter.mqtt.service.MqttHandler;
import com.ifengniao.common.message.BaseException;
import com.ifengniao.common.util.DateTimeUtil;
import com.ifengniao.common.util.JsonUtil;
import com.ifengniao.common.util.ShellUtil;
import com.ifengniao.common.util.ThreadUtil;
import com.ifengniao.server.xiyoulocal.dto.*;
import com.ifengniao.server.xiyoulocal.service.MQTTService;
import com.ifengniao.server.xiyoulocal.util.CovertUtil;
import com.ifengniao.server.xiyoulocal.util.UpDownUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class Startup implements CommandLineRunner {

    @Value("${application.systemConfig.networkCardName}")
    private String networkCardName;
    @Value("${application.systemConfig.cleDownloadUrl}")
    private String cleDownloadUrl;
    @Value("${application.systemConfig.cleLicenseQueryUrl}")
    private String cleLicenseQueryUrl;
    @Value("${application.systemConfig.cleStatusQueryUrl}")
    private String cleStatusQueryUrl;
    @Value("${application.systemConfig.cleInfoQueryUrl}")
    private String cleInfoQueryUrl;
    @Value("${application.systemConfig.cleMapQueryUrl}")
    private String cleMapQueryUrl;
    @Value("${application.systemConfig.cleMapModelQueryUrl}")
    private String cleMapModelQueryUrl;
    @Value("${application.systemConfig.cleLocatorsQueryUrl}")
    private String cleLocatorsQueryUrl;
    @Value("${application.systemConfig.cleBeaconsQueryUrl}")
    private String cleBeaconsQueryUrl;
    @Value("${application.systemConfig.commonUploadUrl}")
    private String commonUploadUrl;
    @Value("${application.systemConfig.commonDownloadFilePath}")
    private String commonDownloadFilePath;
    @Value("${application.systemConfig.mqttSubTopicPrefix}")
    private String mqttSubTopicPrefix;
    public static final LocalServerDTO LOCAL_SERVER = new LocalServerDTO();

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            try {
                fetchAllInfor();
                break;
            } catch (Exception e) {
                log.error("本地服务启动失败：", e);
                ThreadUtil.sleep(10 * 1000);
            }
        }
    }

    /**
     * 获取信息，如果出错，则停一段时间再尝试执行
     */
    public void fetchAllInfor() throws Exception {
        fetchNetInfor();
        fetchCleVersion();
        fetchCleLicense();
        if (LOCAL_SERVER.getLocalServerCleActivation() != null && LOCAL_SERVER.getLocalServerCleActivation()) {
            fetchCleStatus();
            //CLE启动成功后，获取地图及基站数据
            if (LOCAL_SERVER.getLocalServerCleStartup() != null && LOCAL_SERVER.getLocalServerCleStartup()) {
                //CLE刚启动时还不能调通接口，需要等待约3s左右，这里预留5s时间
                ThreadUtil.sleep(5 * 1000);
                fetchCleMapInfo();
            }
        } else {
            LOCAL_SERVER.setLocalServerCleStartup(false);
        }
        MQTTService.sendLoginToServer();
    }

    /**
     * 获取本地服务所在工控机网卡对应局域网ip和mac地址，根据mac地址进行监听mqtt的topic
     *
     * @throws Exception
     */
    public void fetchNetInfor() throws Exception {
        //过滤，防止mqtt多次监听
        if (StringUtils.isBlank(LOCAL_SERVER.getLocalServerIp()) || StringUtils.isBlank(LOCAL_SERVER.getLocalServerMac())) {
            //根据网卡名获取网络接口信息
            log.info("获取到networkCardName：{}", networkCardName);
            var net = NetUtil.getNetworkInterface(networkCardName);
            log.info("获取到net：{}", net);
            if (null != net) {
                //从网络接口信息中解析mac地址和ip地址
                var macBytes = net.getHardwareAddress();
                if (null != macBytes) {
                    var sb = new StringBuilder();
                    for (byte macByte : macBytes) {
                        var s = Integer.toHexString(macByte & 255);
                        sb.append(s.length() == 1 ? 0 + s : s);
                    }
                    LOCAL_SERVER.setLocalServerMac(sb.toString());
                }
                var ips = net.getInetAddresses();
                while (ips.hasMoreElements()) {
                    var inet = ips.nextElement();
                    if (inet instanceof Inet4Address) {
                        LOCAL_SERVER.setLocalServerIp(inet.getHostAddress());
                        break;
                    }
                }
            }
            log.info("获取到ip：{}，mac：{}", LOCAL_SERVER.getLocalServerIp(), LOCAL_SERVER.getLocalServerMac());
            if (StringUtils.isBlank(LOCAL_SERVER.getLocalServerIp()) || StringUtils.isBlank(LOCAL_SERVER.getLocalServerMac())) {
                throw new BaseException("获取ip/mac失败，准备重试...");
            }
            //按mac地址监听mqtt的topic
            MqttHandler.subscribe(mqttSubTopicPrefix + LOCAL_SERVER.getLocalServerMac());
        }
    }

    /**
     * 通过shell获取CLE版本号，如果无法执行命令则下载安装包进行安装
     *
     * @throws Exception
     */
    public void fetchCleVersion() throws Exception {
        //过滤
        if (StringUtils.isBlank(LOCAL_SERVER.getLocalServerCleVersion())) {
            //执行shell获取cle版本号
            var commandResult = ShellUtil.execCommand("cle version", true);
            if (commandResult.getResult() != 0 || StringUtils.isBlank(commandResult.getResponseMsg())) {
                //未安装cle，尝试下载安装
                var cleFile = new File(commonDownloadFilePath + cleDownloadUrl.substring(cleDownloadUrl.lastIndexOf("/")));
                if (!cleFile.exists()) {
                    //cle下载文件不存在，尝试下载
                    try {
                        UpDownUtil.simpleDownload(cleDownloadUrl, cleFile);
                    } catch (Exception e) {
                        //文件不存在，下载失败
                        throw new BaseException("cle安装文件下载失败，准备重试...");
                    }
                }
                log.info("cle安装文件已下载：{}", cleFile.getAbsolutePath());
                //执行chmod +x cle-2.9.9.linux.x64.bin
                commandResult = ShellUtil.execCommand("chmod +x " + cleFile.getAbsolutePath(), false, -1);
                if (commandResult.getResult() != 0) {
                    throw new BaseException("cle安装失败，准备重试...");
                }
                //执行安装命令
                commandResult = ShellUtil.execCommand(cleFile.getAbsolutePath(), true, -1);
                if (commandResult.getResult() != 0 || StringUtils.isBlank(commandResult.getResponseMsg()) || !commandResult.getResponseMsg().contains("Done")) {
                    throw new BaseException("cle安装失败，准备重试...");
                }
                log.info("cle安装成功");
                //再次获取cle版本号
                commandResult = ShellUtil.execCommand("cle version", true);
                if (commandResult.getResult() != 0 || StringUtils.isBlank(commandResult.getResponseMsg())) {
                    throw new BaseException("cle版本获取失败，准备重试...");
                }
            }
            //确定安装成功后，记录cle版本号
            LOCAL_SERVER.setLocalServerCleVersion(commandResult.getResponseMsg());
            log.info("cle已安装，版本：{}", LOCAL_SERVER.getLocalServerCleVersion());
        }
    }

    /**
     * 调用CLE授权信息接口获取CLE授权状态信息
     *
     * @throws Exception
     */
    public void fetchCleLicense() throws Exception {
        //过滤
        if (StringUtils.isBlank(LOCAL_SERVER.getLocalServerCleCode())) {
            var licenseResult = HttpUtil.get(cleLicenseQueryUrl);
            log.info("cle license接口返回：{}", licenseResult);
            if (Constants.CLE_ACTIVATE_UBUNTU_ERROR.equals(licenseResult)) {
                licenseResult = HttpUtil.get(cleLicenseQueryUrl);
                log.info("cle license接口返回：{}", licenseResult);
            }
            var license = JsonUtil.fromJson(licenseResult);
            if (license == null) {
                throw new BaseException("cle 授权信息获取失败，准备重试...");
            }
            var lic = (Map<String, Object>) license.get("license");
            if (lic == null) {
                throw new BaseException("cle 授权信息获取失败，准备重试...");
            }
            var mid = (String) lic.get("mid");
            if (StringUtils.isBlank(mid)) {
                throw new BaseException("cle 授权信息获取失败，准备重试...");
            }
            LOCAL_SERVER.setLocalServerCleCode(mid);
            var expires = (String) lic.get("expires");
            if (expires == null) {
                //未激活
                LOCAL_SERVER.setLocalServerCleActivation(false);
                LOCAL_SERVER.setLocalServerCleLicenseExpireTime(-1L);
            } else {
                //已激活
                LOCAL_SERVER.setLocalServerCleActivation(true);
                var exp = -1L;
                try {
                    exp = DateTimeUtil.strToMillis(expires);
                } catch (Exception ignored) {
                }
                LOCAL_SERVER.setLocalServerCleLicenseExpireTime(exp);
            }
        }
    }

    /**
     * 激活CLE服务，已激活的服务，再拿错误的license激活时，激活失败并且服务状态还是已激活
     *
     * @param licensePath
     * @throws Exception
     */
    public void activateCleWithLicensePath(String licensePath) throws Exception {
        if (StringUtils.isBlank(licensePath)) {
            throw new BaseException("cle license 文件路径不能为空");
        }
        var commandResult = ShellUtil.execCommand("cle activate " + licensePath, true, -1);
        if (commandResult.getResult() != 0 || StringUtils.isBlank(commandResult.getResponseMsg()) || commandResult.getResponseMsg().contains("Invalid")) {
            throw new BaseException("cle 服务激活失败：" + commandResult.getErrorMsg());
        }
        var licenseResult = HttpUtil.get(cleLicenseQueryUrl);
        log.info("cle license接口返回：{}", licenseResult);
        if (Constants.CLE_ACTIVATE_UBUNTU_ERROR.equals(licenseResult)) {
            licenseResult = HttpUtil.get(cleLicenseQueryUrl);
            log.info("cle license接口返回：{}", licenseResult);
        }
        var license = JsonUtil.fromJson(licenseResult);
        if (license == null) {
            throw new BaseException("cle 授权信息获取失败");
        }
        var lic = (Map<String, Object>) license.get("license");
        if (lic == null) {
            throw new BaseException("cle 授权信息获取失败");
        }
        var mid = (String) lic.get("mid");
        if (StringUtils.isBlank(mid)) {
            throw new BaseException("cle 授权信息获取失败");
        }
        LOCAL_SERVER.setLocalServerCleCode(mid);
        var expires = (String) lic.get("expires");
        if (expires == null) {
            //未激活
            LOCAL_SERVER.setLocalServerCleActivation(false);
            LOCAL_SERVER.setLocalServerCleLicenseExpireTime(-1L);
            throw new BaseException("cle 服务激活失败");
        } else {
            //已激活
            LOCAL_SERVER.setLocalServerCleActivation(true);
            var exp = -1L;
            try {
                exp = DateTimeUtil.strToMillis(expires);
            } catch (Exception ignored) {
            }
            LOCAL_SERVER.setLocalServerCleLicenseExpireTime(exp);
        }
    }

    /**
     * 调用CLE状态接口获取CLE状态信息
     *
     * @throws Exception
     */
    public void fetchCleStatus() throws Exception {
        //过滤
        if (LOCAL_SERVER.getLocalServerCleStartup() == null || !LOCAL_SERVER.getLocalServerCleStartup()) {
            var statusResult = HttpUtil.get(cleStatusQueryUrl);
            log.info("cle status接口返回：{}", statusResult);
            var status = JsonUtil.fromJson(statusResult);
            if (status == null) {
                throw new BaseException("cle 状态信息获取失败，准备重试...");
            }
            var stat = (Map<String, Object>) status.get("status");
            if (stat == null) {
                throw new BaseException("cle 状态信息获取失败，准备重试...");
            }
            if (Constants.CLE_STATUS_ONLINE.equals(stat.get("status"))) {
                //已启动
                LOCAL_SERVER.setLocalServerCleStartup(true);
            } else if (StringUtils.isBlank((String) stat.get("cpa"))) {
                //还没有上传过cpa文件，未启动并且不用重试
                LOCAL_SERVER.setLocalServerCleStartup(false);
            } else {
                //之前上传过cpa文件，尝试启动，启动失败时重试
                try {
                    restartCleWithCpaPath((String) stat.get("cpaPath"));
                } catch (Exception e) {
                    throw new BaseException("cle 服务启动失败，准备重试...");
                }
            }
        }
    }

    /**
     * 启动、重启CLE服务的CPA工程，已启动的服务，再拿错误的cpa启动时，启动失败并且服务状态变为未启动
     *
     * @param cpaPath
     * @throws Exception
     */
    public void restartCleWithCpaPath(String cpaPath) throws Exception {
        if (StringUtils.isBlank(cpaPath)) {
            throw new BaseException("cpa 文件路径不能为空");
        }
        var commandResult = ShellUtil.execCommand("cle service start " + cpaPath, true, -1);
        if (commandResult.getResult() != 0 || StringUtils.isBlank(commandResult.getResponseMsg())) {
            LOCAL_SERVER.setLocalServerCleStartup(false);
            throw new BaseException("cle 服务启动失败：" + commandResult.getErrorMsg());
        }
        var statusResult = HttpUtil.get(cleStatusQueryUrl);
        log.info("cle status接口返回：{}", statusResult);
        var status = JsonUtil.fromJson(statusResult);
        if (status == null) {
            throw new BaseException("cle 状态信息获取失败");
        }
        var stat = (Map<String, Object>) status.get("status");
        if (stat == null) {
            throw new BaseException("cle 状态信息获取失败");
        }
        if (Constants.CLE_STATUS_ONLINE.equals(stat.get("status"))) {
            //已启动
            LOCAL_SERVER.setLocalServerCleStartup(true);
        } else {
            //启动失败
            LOCAL_SERVER.setLocalServerCleStartup(false);
            throw new BaseException("cle 服务启动失败");
        }
    }

    /**
     * 拉取CPA项目信息
     *
     * @throws Exception
     */
    public void fetchCleMapInfo() throws Exception {
        //启动后才能调用成功
        if (LOCAL_SERVER.getLocalServerCleStartup() != null && LOCAL_SERVER.getLocalServerCleStartup()) {
            var cleInfoResult = HttpUtil.get(cleInfoQueryUrl);
            log.info("cle info接口返回：{}", cleInfoResult);
            var cleInfo = JsonUtil.fromJson(cleInfoResult);
            if (cleInfo == null) {
                throw new BaseException("cle info获取失败，准备重试...");
            }
            var id = (String) cleInfo.get("id");
            var name = (String) cleInfo.get("name");
            if (StringUtils.isBlank(id) || StringUtils.isBlank(name)) {
                throw new BaseException("cle info获取失败，准备重试...");
            }
            var category = (String) cleInfo.get("category");
            if (category == null) {
                category = "";
            }
            var version = (String) cleInfo.get("version");
            if (version == null) {
                version = "";
            }
            LOCAL_SERVER.setLocalServerCpaId(id);
            LOCAL_SERVER.setLocalServerCpaProject(name);
            LOCAL_SERVER.setLocalServerCpaCategory(category);
            LOCAL_SERVER.setLocalServerCpaVersion(version);

            var mapIdList = (List<String>) cleInfo.get("maps");
            var mapList = new ArrayList<MapDTO>();
            for (String mapId : mapIdList) {
                //拉取CPA项目中创建的每一个Map
                mapList.add(fetchCleMap(mapId));
            }
            LOCAL_SERVER.setMapList(mapList);
        }
    }

    /**
     * 拉取CPA下的Map
     *
     * @param mapId CPA文件中的地图ID
     * @return
     * @throws Exception
     */
    private MapDTO fetchCleMap(String mapId) throws Exception {
        var cleMapResult = HttpUtil.get(String.format(cleMapQueryUrl, mapId));
        log.info("cle map接口返回：{}", cleMapResult);
        var cleMap = JsonUtil.fromJson(cleMapResult);
        if (cleMap == null) {
            throw new BaseException("cle map获取失败，准备重试...");
        }
        var id = (String) cleMap.get("id");
        var name = (String) cleMap.get("name");
        var model = (Map<String, Object>) cleMap.get("model");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(name) || model == null) {
            throw new BaseException("cle map获取失败，准备重试...");
        }
        var modelType = (String) model.get("type");
        var width = cleMap.get("width");
        var height = cleMap.get("height");
        var widthInPixels = cleMap.get("widthInPixels");
        var heightInPixels = cleMap.get("heightInPixels");
        var metersPerPixel = cleMap.get("metersPerPixel");
        var originPixelX = cleMap.get("originPixelX");
        var originPixelY = cleMap.get("originPixelY");
        var areas = cleMap.get("areas");

        var map = new MapDTO();

        //不管是否新下发的CPA文件，都需要获取地图图片进行上传，以防通过CLE手动上传，程序获取不到
        var fileName = id;
        //（'image' | 'collada' | 'zip'）
        var contentType = switch (modelType) {
            case Constants.MAP_MODEL_TYPE_COLLADA -> {
                fileName += ".dae";
                yield ContentType.APPLICATION_OCTET_STREAM;
            }
            case Constants.MAP_MODEL_TYPE_ZIP -> {
                fileName += ".zip";
                yield ContentType.APPLICATION_OCTET_STREAM;
            }
            //Constants.MAP_MODEL_TYPE_IMAGE
            default -> {
                fileName += ".png";
                yield ContentType.IMAGE_PNG;
            }
        };

        try {
            var uploadName = UpDownUtil.simpleUploadByUrl(
                    commonUploadUrl, contentType, fileName, String.format(cleMapModelQueryUrl, mapId)
            );
            map.setMapImgFileName(fileName);
            map.setMapImgSaveName(uploadName);
        } catch (Exception e) {
            throw new BaseException("云端文件上传失败，准备重试...");
        }

        map.setMapCpaId(id);
        map.setMapCpaName(name);
        map.setMapModelType(modelType);
        map.setMapWidth(width == null ? "" : width.toString());
        map.setMapHeight(height == null ? "" : height.toString());
        map.setMapWidthPixel(widthInPixels == null ? "" : widthInPixels.toString());
        map.setMapHeightPixel(heightInPixels == null ? "" : heightInPixels.toString());
        map.setMapMetersPerPixel(metersPerPixel == null ? "" : metersPerPixel.toString());
        map.setMapOriginPixelX(originPixelX == null ? "" : originPixelX.toString());
        map.setMapOriginPixelY(originPixelY == null ? "" : originPixelY.toString());
        map.setMapCpaAreas(areas);

        //解析基站数据
        var gateways = (List<Map<String, Object>>) cleMap.get("gateways");
        var baseStationList = new ArrayList<BaseStationDTO>();
        for (var gateway : gateways) {
            var baseStation = new BaseStationDTO();
            var gmac = (String) gateway.get("mac");
            var gname = (String) gateway.get("name");
            var gtype = (String) gateway.get("type");
            var gproduct = (String) gateway.get("product");
            if (StringUtils.isBlank(gmac) || StringUtils.isBlank(gname) || StringUtils.isBlank(gtype) || StringUtils.isBlank(gproduct)) {
//                throw new BaseException("cle gateway获取失败，准备重试...");
                continue;
            }
            var gelevation = gateway.get("elevation");
            var gazimuth = gateway.get("azimuth");
            var grotation = gateway.get("rotation");
            var gerrorDegree = gateway.get("errorDegree");
            var gcoordinate = (List) gateway.get("coordinate");

            baseStation.setBaseStationMac(CovertUtil.toNormalMac(gmac));
            baseStation.setBaseStationName(gname);
            baseStation.setBaseStationType(gtype);
            baseStation.setBaseStationProduct(gproduct);
            baseStation.setBaseStationElevation(gelevation == null ? "" : gelevation.toString());
            baseStation.setBaseStationAzimuth(gazimuth == null ? "" : gazimuth.toString());
            baseStation.setBaseStationRotation(grotation == null ? "" : grotation.toString());
            baseStation.setBaseStationErrorDegree(gerrorDegree == null ? "" : gerrorDegree.toString());
            if (gcoordinate == null || gcoordinate.size() < 3) {
                baseStation.setBaseStationX("0");
                baseStation.setBaseStationY("0");
                baseStation.setBaseStationZ("3");
            } else {
                baseStation.setBaseStationX(gcoordinate.get(0).toString());
                baseStation.setBaseStationY(gcoordinate.get(1).toString());
                baseStation.setBaseStationZ(gcoordinate.get(2).toString());
            }

            baseStationList.add(baseStation);
        }
        map.setBaseStationList(baseStationList);
        return map;
    }

    /**
     * 调用CLE locators接口获取基站信息
     *
     * @throws Exception
     */
    public List<BaseStationDTO> fetchLocators() throws Exception {
        var result = new ArrayList<BaseStationDTO>();
        //过滤
        if (LOCAL_SERVER.getLocalServerCleStartup() != null && LOCAL_SERVER.getLocalServerCleStartup()) {
            var locatorsResult = HttpUtil.get(cleLocatorsQueryUrl);
//            log.info("cle locators接口返回：{}", locatorsResult);
            var locators = JsonUtil.fromJsonWithMultiClass(locatorsResult, new TypeReference<Map<String, CleLocatorDTO>>() {
            });
            if (locators == null) {
                throw new BaseException("cle locators信息获取失败");
            }
            for (Map.Entry<String, CleLocatorDTO> entry : locators.entrySet()) {
                if (entry.getValue().getInfo().getX() != null && entry.getValue().getInfo().getY() != null) {
                    String mac = CovertUtil.toNormalMac(entry.getKey());
                    result.add(new BaseStationDTO(mac, entry.getValue()));
                }
            }
        }
        return result;
    }

    /**
     * 调用CLE beacons接口获取信标信息
     *
     * @throws Exception
     */
    public List<BeaconDTO> fetchBeacons() throws Exception {
        var result = new ArrayList<BeaconDTO>();
        //过滤
        if (LOCAL_SERVER.getLocalServerCleStartup() != null && LOCAL_SERVER.getLocalServerCleStartup()) {
            var beaconsResult = HttpUtil.get(cleBeaconsQueryUrl);
//            log.info("cle beacons接口返回：{}", beaconsResult);
            var beacons = JsonUtil.fromJsonWithMultiClass(beaconsResult, new TypeReference<Map<String, CleBeaconDTO>>() {
            });
            if (beacons == null) {
                throw new BaseException("cle beacons信息获取失败");
            }
            for (Map.Entry<String, CleBeaconDTO> entry : beacons.entrySet()) {
                if (entry.getValue().getUserData().get("0") != null && entry.getValue().getX() != null && entry.getValue().getY() != null) {
                    String mac = CovertUtil.toNormalMac(entry.getKey());
                    result.add(new BeaconDTO(mac, entry.getValue()));
                }
            }
        }
        return result;
    }

}
