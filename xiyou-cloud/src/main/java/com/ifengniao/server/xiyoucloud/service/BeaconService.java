package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.common.message.BaseException;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.*;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.*;
import com.ifengniao.server.xiyoucloud.dto.AlarmDTO;
import com.ifengniao.server.xiyoucloud.dto.BeaconDTO;
import com.ifengniao.server.xiyoucloud.dto.BeaconMqttResultDTO;
import com.ifengniao.server.xiyoucloud.dto.export.BeaconExportDTO;
import com.ifengniao.server.xiyoucloud.dto.export.BeaconExportJPDTO;
import com.ifengniao.server.xiyoucloud.dto.export.IBeaconExportDTO;
import com.ifengniao.server.xiyoucloud.util.CovertUtil;
import com.ifengniao.server.xiyoucloud.util.HandleFileUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BeaconService {

    @Autowired
    private BeaconEntityRepository beaconEntityRepository;
    @Autowired
    private LocationObjectEntityRepository locationObjectEntityRepository;
    @Autowired
    private SimpleBeaconEntityRepository simpleBeaconEntityRepository;
    @Autowired
    private LocalServerEntityRepository localServerEntityRepository;
    @Autowired
    private FenceEntityRepository fenceEntityRepository;
    @Autowired
    private AlarmEntityRepository alarmEntityRepository;

    public List<BeaconEntity> findAll() {
        return beaconEntityRepository.findAll();
    }

    public BeaconEntity findById(Integer id) {
        if (id == null) {
            return null;
        }
        return beaconEntityRepository.findById(id).orElse(null);
    }

    public BeaconEntity findByMac(String mac) {
        return beaconEntityRepository.findByBeaconMac(mac);
    }

    public SimpleBeaconEntity findByMacSimple(String mac) {
        return simpleBeaconEntityRepository.findByBeaconMac(mac);
    }

    public BeaconEntity save(BeaconEntity beacon) {
        return beaconEntityRepository.save(beacon);
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public List<BeaconEntity> saveAll(Iterable<BeaconEntity> beacon) {
        return beaconEntityRepository.saveAll(beacon);
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public List<SimpleBeaconEntity> saveAllSimple(Iterable<SimpleBeaconEntity> beacon) {
        return simpleBeaconEntityRepository.saveAll(beacon);
    }

    public void delete(BeaconEntity beacon) {
        beaconEntityRepository.delete(beacon);
    }

    public List<String> fetchDistinctBeaconProduct() {
        return beaconEntityRepository.fetchDistinctBeaconProduct();
    }

    public Page<BeaconEntity> getBeaconByPage(Integer page, Integer size, String beaconMac, String locationObjectName,
                                              Boolean beaconOnline, Boolean beaconAllow, List<Short> beaconType,
                                              String beaconProduct, String order, Boolean asc) {
        Page<BeaconEntity> listPage;
        if (page < 0) {
            page = 0;
        }
        if (StringUtils.isBlank(order)) {
            order = "beaconId";
        }
        Sort.Direction direction = Sort.Direction.DESC;
        if (asc != null && asc) {
            direction = Sort.Direction.ASC;
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(
                new Sort.Order(direction, order)
        ));

        if (beaconType != null && beaconType.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
        }

        if (StringUtils.isNotBlank(beaconMac) || StringUtils.isNotBlank(locationObjectName) || beaconOnline != null
                || beaconAllow != null || beaconType != null || StringUtils.isNotBlank(beaconProduct)) {
            Specification<BeaconEntity> spec = (root, query, cb) -> {
                List<Predicate> andParam = new ArrayList<>();
                if (StringUtils.isNotBlank(beaconProduct)) {
                    andParam.add(cb.equal(root.get("beaconProduct"), beaconProduct));
                }
                if (beaconOnline != null) {
                    andParam.add(cb.equal(root.get("beaconOnline"), beaconOnline));
                }
                if (beaconAllow != null) {
                    andParam.add(cb.equal(root.get("beaconAllow"), beaconAllow));
                }
                if (StringUtils.isNotBlank(beaconMac)) {
                    andParam.add(cb.like(root.get("beaconMac"), "%" + beaconMac + "%"));
                }
                if (beaconType != null) {
                    CriteriaBuilder.In<Object> in = cb.in(root.get("beaconType"));
                    in.value(beaconType);
                    andParam.add(in);
                }
                if (StringUtils.isNotBlank(locationObjectName)) {
                    andParam.add(cb.like(root.join("beaconLocationObject").get("locationObjectName"), "%" + locationObjectName + "%"));
                }
                return cb.and(andParam.toArray(new Predicate[0]));
//                return cb.and(andParam.toArray(new Predicate[andParam.size()]));
            };
            listPage = beaconEntityRepository.findAll(spec, pageRequest);
        } else {
            listPage = beaconEntityRepository.findAll(pageRequest);
        }

        return listPage;
    }

    private static boolean matchMac(String mac) {
        return Pattern.compile("([a-f0-9]){12}", Pattern.CASE_INSENSITIVE).matcher(mac).matches();
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public BeaconEntity saveOrUpdateBeacon(BeaconDTO beaconDTO) throws Exception {
        if (StringUtils.isBlank(beaconDTO.getBeaconMac())) {
            throw new BaseException("信标MAC不能为空").setStrCode(Constants.ERR_1300);
        }
        beaconDTO.setBeaconMac(CovertUtil.toNormalMac(beaconDTO.getBeaconMac()));
        if (!matchMac(beaconDTO.getBeaconMac())) {
            throw new BaseException("信标MAC地址格式：12位，字母+数字，字母a至f").setStrCode(Constants.ERR_1308);
        }
        if (beaconDTO.getBeaconType() == null) {
            throw new BaseException("信标类型不能为空").setStrCode(Constants.ERR_1301);
        }
        BeaconEntity beacon;
        var byMac = findByMac(beaconDTO.getBeaconMac());
        if (beaconDTO.getBeaconId() == null) {
            //新增
            if (byMac != null) {
                throw new BaseException("信标MAC已存在").setStrCode(Constants.ERR_1302);
            }
            beacon = new BeaconEntity().init();
            beacon.setBeaconAllow(true);
        } else {
            //修改
            beacon = findById(beaconDTO.getBeaconId());
            if (beacon == null) {
                throw new BaseException("信标信息未找到").setStrCode(Constants.ERR_1303);
            }
            if (byMac != null && !byMac.getBeaconId().equals(beacon.getBeaconId())) {
                throw new BaseException("信标MAC已存在").setStrCode(Constants.ERR_1302);
            }
            if (beacon.getBeaconLocationObject() != null) {
                //已绑定对象的，不能变更信标类型
                if (!Objects.equals(beacon.getBeaconType(), beaconDTO.getBeaconType())) {
                    throw new BaseException("信标已绑定对象，不可修改类型").setStrCode(Constants.ERR_1304);
                }
            }
        }
        beacon.setBeaconMac(beaconDTO.getBeaconMac());
        beacon.setBeaconProduct(beaconDTO.getBeaconProduct() == null ? "" : beaconDTO.getBeaconProduct());
        beacon.setBeaconType(beaconDTO.getBeaconType());
        beacon.setBeaconRemark(beaconDTO.getBeaconRemark() == null ? "" : beaconDTO.getBeaconRemark());
        beacon = save(beacon);
        return beacon;
    }

    //    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    @Transactional(rollbackFor = {Exception.class})
    public List<BeaconEntity> batchUploadBeaconByCsv(int langType, String saveName) throws Exception {
        if (StringUtils.isBlank(saveName)) {
            throw new BaseException("上传文件不能为空").setStrCode(Constants.ERR_F100);
        }
        List<? extends IBeaconExportDTO> beaconExportDTOList = null;
        try {
            beaconExportDTOList = switch (langType) {
                case Constants.LANGUAGE_JP ->
                        HandleFileUtil.readFromCsv(SysUrlService.pathPrefix + saveName, BeaconExportJPDTO.class);
                default -> HandleFileUtil.readFromCsv(SysUrlService.pathPrefix + saveName, BeaconExportDTO.class);
            };
        } catch (Exception e) {
            log.error("读取CVS文件失败：", e);
            throw new BaseException("上传文件格式错误").setStrCode(Constants.ERR_F101);
        }
        if (beaconExportDTOList == null || beaconExportDTOList.isEmpty()) {
            throw new BaseException("上传文件不能为空").setStrCode(Constants.ERR_F100);
        }
        var result = new ArrayList<BeaconEntity>();
        for (var iBeaconExportDTO : beaconExportDTOList) {
            result.add(saveOrUpdateBeacon(iBeaconExportDTO.toBeaconDTO()));
        }
        return result;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public BeaconEntity allow(BeaconDTO beaconDTO) throws Exception {
        var beacon = findById(beaconDTO.getBeaconId());
        if (beacon == null) {
            throw new BaseException("信标信息未找到").setStrCode(Constants.ERR_1303);
        }
        beacon.setBeaconAllow(true);
        beacon = save(beacon);
        return beacon;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public BeaconEntity bindLocationObject(BeaconDTO beaconDTO) throws Exception {
        var beacon = findById(beaconDTO.getBeaconId());
        if (beacon == null) {
            throw new BaseException("信标信息未找到").setStrCode(Constants.ERR_1303);
        }
        var proLocationObjectId = beacon.getBeaconLocationObject() == null ? null : beacon.getBeaconLocationObject().getLocationObjectId();
        var nowLocationObjectId = beaconDTO.getBeaconLocationObject() == null ? null : beaconDTO.getBeaconLocationObject().getLocationObjectId();
        //全null或不变就不用修改beacon
        if (!Objects.equals(proLocationObjectId, nowLocationObjectId)) {
            LocationObjectEntity proLocationObject = null;
            LocationObjectEntity nowLocationObject = null;
            if (proLocationObjectId == null) {
                nowLocationObject = locationObjectEntityRepository.findById(nowLocationObjectId).orElse(null);
                if (nowLocationObject == null) {
                    throw new BaseException("定位对象信息未找到").setStrCode(Constants.ERR_1400);
                }
                if (nowLocationObject.getLocationObjectBeacon() != null) {
                    throw new BaseException("定位对象已配置其他信标，不可重复配置").setStrCode(Constants.ERR_1406);
                }
                nowLocationObject.setLocationObjectBeacon(beacon);
                beacon.setBeaconLocationObject(nowLocationObject);
            } else if (nowLocationObjectId == null) {
                proLocationObject = beacon.getBeaconLocationObject();
                proLocationObject.setLocationObjectBeacon(null);
                beacon.setBeaconLocationObject(null);
            } else {
                nowLocationObject = locationObjectEntityRepository.findById(nowLocationObjectId).orElse(null);
                if (nowLocationObject == null) {
                    throw new BaseException("定位对象信息未找到").setStrCode(Constants.ERR_1400);
                }
                if (nowLocationObject.getLocationObjectBeacon() != null) {
                    throw new BaseException("定位对象已配置其他信标，不可重复配置").setStrCode(Constants.ERR_1406);
                }
                proLocationObject = beacon.getBeaconLocationObject();
                proLocationObject.setLocationObjectBeacon(null);
                nowLocationObject.setLocationObjectBeacon(beacon);
                beacon.setBeaconLocationObject(nowLocationObject);
            }
            if (nowLocationObject != null) {
                //不能绑定与信标类型不同的对象
                if (!Objects.equals(beacon.getBeaconType(), nowLocationObject.getLocationObjectType())) {
                    throw new BaseException("不能绑定与信标类型不同的对象").setStrCode(Constants.ERR_1305);
                }
                locationObjectEntityRepository.save(nowLocationObject);
            }
            if (proLocationObject != null) {
                locationObjectEntityRepository.save(proLocationObject);
            }
            beacon = save(beacon);
        }
        return beacon;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public void deleteBeacon(BeaconDTO beaconDTO) throws Exception {
        BeaconEntity beacon = findById(beaconDTO.getBeaconId());
        if (beacon == null) {
            throw new BaseException("信标信息未找到").setStrCode(Constants.ERR_1303);
        }
        if (beacon.getBeaconLocationObject() != null) {
            throw new BaseException("信标已绑定对象，不可删除").setStrCode(Constants.ERR_1306);
        }
        delete(beacon);
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public void updateOnlineStatus(long millisAgo) throws Exception {
        beaconEntityRepository.updateBeaconEntitySetOnlineFalseByLastTimeBefore(System.currentTimeMillis() - millisAgo);
//        var beaconList = beaconEntityRepository.findAllByBeaconLastTimeBefore(System.currentTimeMillis() - millisAgo);
//        if (beaconList != null && beaconList.size() > 0) {
//            for (BeaconEntity beacon : beaconList) {
//                beacon.setBeaconOnline(false);
//            }
//            beaconList = saveAll(beaconList);
//        }
//        return beaconList;
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public List<BeaconDTO> updateOnlineBeaconByLocalServerMac(String mac, List<BeaconDTO> beaconDTOList) throws Exception {
        List<SimpleBeaconEntity> result = new ArrayList<>();
        for (BeaconDTO beaconDTO : beaconDTOList) {
            var beacon = findByMacSimple(beaconDTO.getBeaconMac());
            if (beacon == null) {
                beacon = new SimpleBeaconEntity().init();
                beacon.setBeaconMac(beaconDTO.getBeaconMac());
            }
            beacon.setBeaconX(beaconDTO.getBeaconX());
            beacon.setBeaconY(beaconDTO.getBeaconY());
            beacon.setBeaconZ(beaconDTO.getBeaconZ());
            beacon.setBeaconLastTime(beaconDTO.getBeaconLastTime());
            beacon.setBeaconOnline(true);
            beacon.setBeaconRssi(beaconDTO.getBeaconRssi());
            beacon.setBeaconChannel(beaconDTO.getBeaconChannel());
            beacon.setBeaconFrequency(beaconDTO.getBeaconFrequency());
            beacon.setBeaconPowerType(beaconDTO.getBeaconPowerType());
            beacon.setBeaconSos(beaconDTO.getBeaconSos());
            if (beacon.getBeaconSos()) {
                beacon.setBeaconSosTime(beaconDTO.getBeaconSosTime());
            }
            beacon.setBeaconBattery(beaconDTO.getBeaconBattery());
            beacon.setBeaconLastGateway(beaconDTO.getBeaconLastGateway());
            beacon.setBeaconNearestGateway(beaconDTO.getBeaconNearestGateway());
            beacon.setBeaconLastLocalServerMac(mac);
            beacon.setBeaconMapId(beaconDTO.getBeaconMapId());
            beacon.setBeaconZoneId(beaconDTO.getBeaconZoneId());
            beacon.setBeaconZoneName(beaconDTO.getBeaconZoneName());
            result.add(beacon);
        }
        if (result.size() > 0) {
            result = saveAllSimple(result);
        }

        return BeaconDTO.covertSimple(result);
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public BeaconMqttResultDTO receiveBeaconMsgFromMqtt(String mac, List<BeaconDTO> beaconDTOList) throws Exception {
        var now = System.currentTimeMillis();
        var offlineTime = now - Constants.OFFLINE_TIME_MILLIS;
        var localServer = localServerEntityRepository.findByLocalServerMac(mac);
        if (localServer == null) {
            throw new BaseException("本地服务未找到：" + mac).setStrCode(Constants.ERR_1100);
        }
        var alarmMap = new HashMap<LocationObjectEntity, MapEntity>();
        var alarmFenceEnter = new HashMap<LocationObjectEntity, FenceEntity>();
        var alarmFenceLeave = new HashMap<LocationObjectEntity, FenceEntity>();
        var beaconEntityHistoryMap = new HashMap<Integer, List<BeaconEntity>>();
        var beaconEntityMqttMap = new HashMap<Integer, List<BeaconEntity>>();
        var beaconList = beaconDTOList.stream()
                .filter(beaconDTO -> beaconDTO.getBeaconLastTime() >= offlineTime) //筛掉离线信标
                .map(beaconDTO -> {
                    var beacon = findByMac(beaconDTO.getBeaconMac());
                    if (beacon == null) {
                        beacon = new BeaconEntity().init();
                        beacon.setBeaconMac(beaconDTO.getBeaconMac());
                    } else {
                        //只有绑定了对象的信标才有报警/存储历史数据/发送MQTT定位消息给前端的必要
                        if (beacon.getBeaconLocationObject() != null) {

                            //先找出新的所属地图
                            var findMap = localServer.getMapList().stream()
                                    .filter(mapEntity -> mapEntity.getMapCpaId().equals(beaconDTO.getBeaconMapId()))
                                    .findAny().orElse(null);

                            if (findMap != null) {
                                var lo = beacon.getBeaconLocationObject();
                                boolean move = true;

                                //报警判断，只有触发的一次进行报警，只要没离开都只算一次
                                //地图进入报警判断
                                if (beacon.getBeaconLastLocalServerMac().equals(mac)
                                        && beacon.getBeaconMapId().equals(beaconDTO.getBeaconMapId())) {
                                    //如果在四舍五入到厘米级别后信标坐标不变，则不存储历史
                                    //MQTT还是要推送的，不然前端页面重新刷新渲染就缺少信标对象了
                                    if (beacon.getBeaconX().equals(beaconDTO.getBeaconX()) && beacon.getBeaconY().equals(beaconDTO.getBeaconY())) {
                                        move = false;
                                    }
                                } else {
                                    //地图发生变化，需要判断新地图是否在信标绑定的对象绑定的地图列表中，如果不在则报警
                                    if (beacon.getBeaconLocationObject().getMapSet().stream()
                                            .noneMatch(mapEntity -> mapEntity.getMapCpaId().equals(beaconDTO.getBeaconMapId())
                                                    && mapEntity.getMapLocalServer().getLocalServerMac().equals(mac))) {
                                        //确实不包含当前地图，添加到报警列表
                                        alarmMap.put(lo, findMap);
                                    }
                                }

                                //围栏进出报警判断
                                //判断地图内包含当前点的所有已启用的围栏，这些就是最后要写入beaconLastFenceIds的围栏
                                var endIds = new ArrayList<Integer>();
                                //将包含当前点的所有围栏与之前的beaconLastFenceIds做对比
                                var proIds = new ArrayList<>(Arrays.asList(beacon.getBeaconLastFenceIds()));
                                //找到的说明没变，从beaconLastFenceIds中去除
                                //没找到说明新增，判断是否是进入报警，如果是则报警
                                //beaconLastFenceIds中剩余的是减少的，判断是否是离开报警，如果是则报警
                                //最后将新的beaconLastFenceIds写入beacon
                                findMap.getFenceList().stream()
                                        .filter(fenceEntity -> fenceEntity.getFenceStatus() && fenceEntity.fenceContainPoint(
                                                Double.parseDouble(beaconDTO.getBeaconX()),
                                                Double.parseDouble(beaconDTO.getBeaconY())
                                        )).forEach(fenceEntity -> {
                                            endIds.add(fenceEntity.getFenceId());
                                            if (proIds.contains(fenceEntity.getFenceId())) {
                                                proIds.remove(fenceEntity.getFenceId());
                                            } else {
                                                if (Constants.WARN_TYPE_FENCE_ENTER == fenceEntity.getFenceType()) {
                                                    alarmFenceEnter.put(lo, fenceEntity);
                                                }
                                            }
                                        });
                                if (!proIds.isEmpty()) {
                                    var leave = fenceEntityRepository.findAllByFenceIdInAndFenceType(proIds, Constants.WARN_TYPE_FENCE_LEAVE);
                                    leave.forEach(fenceEntity -> alarmFenceLeave.put(lo, fenceEntity));
                                }
                                beacon.setBeaconLastFenceIds(endIds.toArray(Integer[]::new));

                                //如果移动了
                                if (move) {
                                    //准备用于历史存储的数据，这里的beacon先添加进去，后面会修改数据
                                    beaconEntityHistoryMap.computeIfAbsent(findMap.getMapId(), key -> new ArrayList<>()).add(beacon);
                                }
                                //准备用于MQTT的数据，这里的beacon先添加进去，后面会修改数据
                                beaconEntityMqttMap.computeIfAbsent(findMap.getMapId(), key -> new ArrayList<>()).add(beacon);
                            }
                        }
                    }

                    beacon.setBeaconX(beaconDTO.getBeaconX());
                    beacon.setBeaconY(beaconDTO.getBeaconY());
                    beacon.setBeaconZ(beaconDTO.getBeaconZ());
                    beacon.setBeaconLastTime(beaconDTO.getBeaconLastTime());
                    beacon.setBeaconOnline(true);
                    beacon.setBeaconRssi(beaconDTO.getBeaconRssi());
                    beacon.setBeaconChannel(beaconDTO.getBeaconChannel());
                    beacon.setBeaconFrequency(beaconDTO.getBeaconFrequency());
                    beacon.setBeaconPowerType(beaconDTO.getBeaconPowerType());
                    beacon.setBeaconSos(beaconDTO.getBeaconSos());
                    if (beacon.getBeaconSos()) {
                        beacon.setBeaconSosTime(beaconDTO.getBeaconSosTime());
                    }
                    beacon.setBeaconBattery(beaconDTO.getBeaconBattery());
                    beacon.setBeaconLastGateway(beaconDTO.getBeaconLastGateway());
                    beacon.setBeaconNearestGateway(beaconDTO.getBeaconNearestGateway());
                    beacon.setBeaconLastLocalServerMac(mac);
                    beacon.setBeaconMapId(beaconDTO.getBeaconMapId());
                    beacon.setBeaconZoneId(beaconDTO.getBeaconZoneId());
                    beacon.setBeaconZoneName(beaconDTO.getBeaconZoneName());
                    return beacon;
                })
                .collect(Collectors.toList());
        saveAll(beaconList);

        var beaconDTOHistoryMap = new HashMap<Integer, List<BeaconDTO>>();
        for (Map.Entry<Integer, List<BeaconEntity>> entry : beaconEntityHistoryMap.entrySet()) {
            beaconDTOHistoryMap.put(entry.getKey(), BeaconDTO.covertSimplify(entry.getValue()));
        }
        var beaconDTOMqttMap = new HashMap<Integer, List<BeaconDTO>>();
        for (Map.Entry<Integer, List<BeaconEntity>> entry : beaconEntityMqttMap.entrySet()) {
            beaconDTOMqttMap.put(entry.getKey(), BeaconDTO.covertSimplify(entry.getValue()));
        }

        //存储报警信息
        var save = new ArrayList<AlarmEntity>();
        for (var entry : alarmMap.entrySet()) {
            AlarmEntity alarm = new AlarmEntity();
            alarm.setAlarmType(Constants.WARN_TYPE_FENCE_MAP_ENTER);
            alarm.setAlarmStatus(false);
            alarm.setAlarmTime(now);
            alarm.setAlarmLocationObject(entry.getKey());
            alarm.setAlarmFence(null);
            alarm.setAlarmMap(entry.getValue());
            save.add(alarm);
        }
        for (var entry : alarmFenceEnter.entrySet()) {
            AlarmEntity alarm = new AlarmEntity();
            alarm.setAlarmType(Constants.WARN_TYPE_FENCE_ENTER);
            alarm.setAlarmStatus(false);
            alarm.setAlarmTime(now);
            alarm.setAlarmLocationObject(entry.getKey());
            alarm.setAlarmFence(entry.getValue());
            alarm.setAlarmMap(entry.getValue().getFenceMap());
            save.add(alarm);
        }
        for (var entry : alarmFenceLeave.entrySet()) {
            AlarmEntity alarm = new AlarmEntity();
            alarm.setAlarmType(Constants.WARN_TYPE_FENCE_LEAVE);
            alarm.setAlarmStatus(false);
            alarm.setAlarmTime(now);
            alarm.setAlarmLocationObject(entry.getKey());
            alarm.setAlarmFence(entry.getValue());
            alarm.setAlarmMap(entry.getValue().getFenceMap());
            save.add(alarm);
        }
        List<AlarmDTO> alarmDTOList;
        if (!save.isEmpty()) {
            alarmDTOList = AlarmDTO.covert(alarmEntityRepository.saveAll(save));
        } else {
            alarmDTOList = List.of();
        }

        return new BeaconMqttResultDTO(alarmDTOList, beaconDTOHistoryMap, beaconDTOMqttMap);
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public BeaconEntity tryUpdateByMqtt(String mac, Map<String, Object> sensor) throws Exception {

        return null;
    }

}
