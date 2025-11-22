package com.ifengniao.server.xiyoucloud.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SysUrlService {

    public static String urlPrefix;
    public static String pathPrefix;

    @Value("${application.systemConfig.getUrl}")
    public void setUrlPrefix(String urlPrefix) {
        SysUrlService.urlPrefix = urlPrefix;
    }

    @Value("${application.systemConfig.uploadPath}")
    public void setPathPrefix(String pathPrefix) {
        SysUrlService.pathPrefix = pathPrefix;
    }
}
