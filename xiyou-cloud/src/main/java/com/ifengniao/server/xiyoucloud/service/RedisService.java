package com.ifengniao.server.xiyoucloud.service;

import com.ifengniao.common.config.redis.service.RedisOperator;
import com.ifengniao.server.xiyoucloud.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisService {

    @Autowired
    private RedisOperator redisOperator;
    @Value("${spring.application.name}")
    private String serverName;

    public void saveSendLicenseResult(String messageId, int result) {
        var key = String.format(Constants.REDIS_SEND_LICENSE_RESULT, serverName, messageId);
        redisOperator.setValue(key, result, 5 * 1000);
    }

    public int getSendLicenseResult(String messageId) {
        var key = String.format(Constants.REDIS_SEND_LICENSE_RESULT, serverName, messageId);
        var result = redisOperator.getValue(key);
        return result == null ? Constants.REDIS_SEND_RESULT_TIMEOUT : (int) result;
    }

    public void saveSendCpaResult(String messageId, int result) {
        var key = String.format(Constants.REDIS_SEND_CPA_RESULT, serverName, messageId);
        redisOperator.setValue(key, result, 60 * 1000);
    }

    public int getSendCpaResult(String messageId) {
        var key = String.format(Constants.REDIS_SEND_CPA_RESULT, serverName, messageId);
        var result = redisOperator.getValue(key);
        return result == null ? Constants.REDIS_SEND_RESULT_TIMEOUT : (int) result;
    }

}
