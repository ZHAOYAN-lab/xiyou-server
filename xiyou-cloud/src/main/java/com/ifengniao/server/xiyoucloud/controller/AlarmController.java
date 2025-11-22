package com.ifengniao.server.xiyoucloud.controller;

import com.ifengniao.common.message.ResultMsg;
import com.ifengniao.common.util.DateTimeUtil;
import com.ifengniao.common.util.Resp;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.AlarmEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.ConfigEntity;
import com.ifengniao.server.xiyoucloud.dto.AlarmDTO;
import com.ifengniao.server.xiyoucloud.service.AlarmService;
import com.ifengniao.server.xiyoucloud.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

/**
 * 告警相关接口
 */
@Tag(description = "告警相关接口", name = "告警相关接口")
@RestController
@RequestMapping("/ifengniao/cloud/server/xiyou/alarm")
@Slf4j
public class AlarmController {

    @Autowired
    private AlarmService alarmService;
    @Autowired
    private ConfigService configService;

    /**
     * 分页查找告警信息
     *
     * @return
     */
    @Operation(summary = "分页查找告警信息")
    @GetMapping("/page")
    public ResultMsg page(@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
                          @RequestParam(value = "size", required = true, defaultValue = "20") Integer size,
                          @RequestParam(value = "beaconMac", required = false) String beaconMac,
                          @RequestParam(value = "locationObjectName", required = false) String locationObjectName,
                          @RequestParam(value = "alarmType", required = false) Short alarmType,
                          @RequestParam(value = "alarmStatus", required = false) Boolean alarmStatus,
                          @RequestParam(value = "start", required = false) Long start,
                          @RequestParam(value = "end", required = false) Long end,
                          @RequestParam(value = "order", required = false) String order,
                          @RequestParam(value = "asc", required = false) Boolean asc) {
        return Resp.exec(() -> getAlarmByPage(page, size, beaconMac, locationObjectName, alarmType, alarmStatus, start, end, order, asc));
    }

    private Page<AlarmDTO> getAlarmByPage(Integer page, Integer size, String beaconMac, String locationObjectName,
                                          Short alarmType, Boolean alarmStatus, Long start, Long end,
                                          String order, Boolean asc) {
        if (end != null) {
            try {
                end = DateTimeUtil.millisToMillis(end, true);
            } catch (Exception e) {
                log.warn("时间格式转换失败：", e);
            }
        }
        Page<AlarmEntity> alarmByPage = alarmService.getAlarmByPage(page, size, beaconMac, locationObjectName, alarmType, alarmStatus, start, end, order, asc);
        return new PageImpl<>(AlarmDTO.covert(alarmByPage.getContent()), alarmByPage.getPageable(), alarmByPage.getTotalElements());
    }

    /**
     * 告警处理按钮
     *
     * @return
     */
    @Operation(summary = "告警处理按钮")
    @PostMapping("/handle")
    public ResultMsg handle(@RequestBody AlarmDTO alarmDTO) {
        return Resp.exec(() -> new AlarmDTO(alarmService.handleAlarm(alarmDTO)));
    }

    /**
     * 删除告警信息
     *
     * @return
     */
    @Operation(summary = "删除告警信息")
    @DeleteMapping("/delete")
    public ResultMsg delete(@RequestBody AlarmDTO alarmDTO) {
        return Resp.exec(() -> alarmService.deleteAlarm(alarmDTO));
    }

    /**
     * 获取邮件发送开关
     *
     * @return
     */
    @Operation(summary = "获取邮件发送开关")
    @GetMapping("/mailStatus")
    public ResultMsg mailStatus() {
        return Resp.exec(configService::findAlarmSendMailSwitch);
    }

    /**
     * 修改邮件发送开关
     *
     * @return
     */
    @Operation(summary = "修改邮件发送开关")
    @PostMapping("/mailStatus")
    public ResultMsg aa(@RequestBody ConfigEntity config) {
        return Resp.exec(() -> configService.saveAlarmSendMailSwitch(config));
    }

}
