package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.common.message.BaseException;
import com.ifengniao.server.xiyoucloud.config.Constants;
import com.ifengniao.server.xiyoucloud.db.postgresql.entity.ConfigEntity;
import com.ifengniao.server.xiyoucloud.db.postgresql.repository.ConfigEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ConfigService {

    @Autowired
    private ConfigEntityRepository configEntityRepository;

    public List<ConfigEntity> findAll() {
        return configEntityRepository.findAll();
    }

    public List<ConfigEntity> findByParams(String configType, Integer configValues) {
        return findByParams(Collections.singletonList(configType), configValues);
    }

    public List<ConfigEntity> findByParams(List<String> configType, Integer configValues) {
        if (configType == null || configType.size() == 0) {
            return configEntityRepository.findAllByOrderByConfigTypeAscConfigOrderAsc();
        } else if (configValues == null) {
            return configEntityRepository.findAllByConfigTypeInOrderByConfigTypeAscConfigOrderAsc(configType);
        } else {
            return configEntityRepository.findAllByConfigTypeAndConfigValue(configType.get(0), configValues);
        }
    }

    @Transactional(rollbackFor = {Exception.class}, noRollbackFor = {BaseException.class})
    public ConfigEntity saveAlarmSendMailSwitch(ConfigEntity config) {
        var pro = findAlarmSendMailSwitch();
        pro.setConfigValue(config.getConfigValue());
        return save(pro);
    }

    public boolean isMailSend() {
        return findAlarmSendMailSwitch().getConfigValue() == 1;
    }

    public ConfigEntity findAlarmSendMailSwitch() {
        var config = findByParams(Constants.CONFIG_ALARM_SEND_MAIL_SWITCH, null);
        if (config == null || config.size() == 0) {
            return makeAlarmSendMailSwitchConfig();
        }
        return config.get(0);
    }

    public ConfigEntity makeAlarmSendMailSwitchConfig() {
        return new ConfigEntity(null, Constants.CONFIG_ALARM_SEND_MAIL_SWITCH, "报警是否发送邮件", (short) 0, "", (short) 1, "报警是否发送邮件，0不发送，1发送");
    }

    public ConfigEntity findById(Integer id) {
        if (id == null) {
            return null;
        }
        return configEntityRepository.findById(id).orElse(null);
    }

    public ConfigEntity save(ConfigEntity config) {
        return configEntityRepository.save(config);
    }

    public void delete(ConfigEntity config) {
        configEntityRepository.delete(config);
    }

}
