package com.ifengniao.server.xiyoucloud.util;

public class CovertUtil {

    public static String toNormalMac(String mac) {
        return mac.replaceAll("[- :.：]", "").toLowerCase();
    }

}
