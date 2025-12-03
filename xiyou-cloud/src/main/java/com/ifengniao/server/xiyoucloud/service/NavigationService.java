package com.ifengniao.server.xiyoucloud.service;

import java.util.List;
import java.util.Map;

public interface NavigationService {

    List<Map<String, Object>> routeToProductArea(
            Long mapId,
            Double startX,
            Double startY,
            Long areaId
    );
}
