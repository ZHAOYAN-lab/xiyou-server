package com.ifengniao.server.xiyoulocal.util;

public class CovertUtil {

    public static String toNormalMac(String mac) {
        return mac.replaceAll("[- :.：]", "").toLowerCase();
    }

}
