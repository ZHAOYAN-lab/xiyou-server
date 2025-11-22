package com.ifengniao.server.xiyoucloud;

import com.ifengniao.common.communication.adapter.mqtt.EnableMqttV5Adapter;
import com.ifengniao.common.component.security.EnableSecuritySDK;
import com.ifengniao.common.component.token.EnableToken;
import com.ifengniao.common.component.upload.EnableUpload;
import com.ifengniao.common.config.common.ThreadPoolConfig;
import com.ifengniao.common.config.cross.origin.EnableCrossOriginConfig;
import com.ifengniao.common.config.handle.http.error.config.EnableHandleHttpErrorConfig;
import com.ifengniao.common.config.influxdb2.EnableInflux2DS;
import com.ifengniao.common.config.openapi.EnableOpenapiConfig;
import com.ifengniao.common.config.postgresql.jpa.EnablePgSQLDS;
import com.ifengniao.common.notify.mail.EnableNotifyMail;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableMqttV5Adapter
@EnableCrossOriginConfig
@EnableUpload
@EnableOpenapiConfig
@EnableHandleHttpErrorConfig
@EnablePgSQLDS
@EnableInflux2DS
@EnableSecuritySDK
@EnableToken
@EnableNotifyMail
@Import(value = {ThreadPoolConfig.class})
public class XiyouCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiyouCloudApplication.class, args);
    }

}
