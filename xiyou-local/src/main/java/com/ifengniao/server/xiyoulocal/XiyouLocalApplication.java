package com.ifengniao.server.xiyoulocal;

import com.ifengniao.common.communication.adapter.mqtt.EnableMqttV5Adapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableMqttV5Adapter
public class XiyouLocalApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiyouLocalApplication.class, args);
    }

}
